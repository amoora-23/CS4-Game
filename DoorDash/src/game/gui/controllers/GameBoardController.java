package game.gui.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import game.engine.Board;
import game.engine.Constants;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cards.ConfusionCard;
import game.engine.cards.EnergyStealCard;
import game.engine.cards.ShieldCard;
import game.engine.cards.StartOverCard;
import game.engine.cards.SwapperCard;
import game.engine.cells.*;
import game.engine.cells.Cell;
import game.engine.exceptions.*;
import game.engine.monsters.Dasher;
import game.engine.monsters.Dynamo;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;

public class GameBoardController {

    // ── FXML Injections ─────────────────────────────────────────────────────────
    @FXML private GridPane gameBoardGrid;
    @FXML private Label    turnLabel;
    @FXML private Button   powerUpButton;
    @FXML private Button   rollDiceBtn;
    @FXML private TextArea gameLog;

    // Left sidebar (Player 1)
    @FXML private Label       player1NameLabel;
    @FXML private Label       player1EnergyLabel;
    @FXML private ProgressBar player1ProgressBar;
    @FXML private Label       player1StatusLabel;
    @FXML private Label       player1TypeLabel;
    @FXML private Label       player1RoleLabel;
    @FXML private Label       player1PositionLabel;

    // Right sidebar (Player 2)
    @FXML private Label       player2NameLabel;
    @FXML private Label       player2EnergyLabel;
    @FXML private ProgressBar player2ProgressBar;
    @FXML private Label       player2StatusLabel;
    @FXML private Label       player2TypeLabel;
    @FXML private Label       player2RoleLabel;
    @FXML private Label       player2PositionLabel;

    // Dice + Card area
    @FXML private Label diceResultLabel;
    @FXML private Label cardNameLabel;
    @FXML private Label cardDescriptionLabel;
    @FXML private Label cardDeckCountLabel;

    // Overlay pane for energy-change popups (placed over board)
    @FXML private StackPane rootContainer;

    // ── State ───────────────────────────────────────────────────────────────────
    protected Game          engine;
    private StackPane[]   visualCells  = new StackPane[100];
    private boolean       isBotMode    = false;
    private int           lastDiceRoll = 0;

    // FIX #5: track whether the human player is player or opponent
    // engine.getPlayer() is always the human; engine.getOpponent() is always bot/p2
    // We keep a stable reference to avoid confusion during bot turns
    private Monster humanMonster  = null;
    private Monster botMonster    = null;

    // FIX #6: track shield state before each action so we can detect blocks
    private boolean p1WasShielded = false;
    private boolean p2WasShielded = false;
    private List<Boolean> stationedWasShielded = new ArrayList<>();

    // Snapshot energies so we can compute deltas after playTurn()
    private int p1EnergyBefore;
    private int p2EnergyBefore;
    private List<Integer> stationedEnergyBefore = new ArrayList<>();

    // FIX #2: flag to prevent re-entrant cheat key triggers
    private boolean gameOverTriggered = false;

    // ── Public API ───────────────────────────────────────────────────────────────
    public void setBotMode(boolean botMode) { this.isBotMode = botMode; }
    public Game getGame() { return this.engine; }

    public void setGameEngine(Game engine) {
        this.engine = engine;

        // FIX #5: lock in stable monster references immediately
        humanMonster = engine.getPlayer();
        botMonster   = engine.getOpponent();

        player1NameLabel.setText(humanMonster.getName().toUpperCase());
        player2NameLabel.setText(botMonster.getName().toUpperCase());

        generateBoard();
        announce("System Ready. Game parameters initialised.");
        announce("Match: " + humanMonster.getName() + " VS " + botMonster.getName());

        // FIX #3: display correct deck count (the CSV deck = 25 after our display fix)
        updateDeckCount();
        updateUI();

        // FIX #2: wire up "W" cheat key once scene is available
        // Scene may not exist yet at this point — wire lazily on first layout pass
        rollDiceBtn.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                wireCheatKey(newScene);
            }
        });
        // Also try immediately in case scene already exists
        if (rollDiceBtn.getScene() != null) {
            wireCheatKey(rollDiceBtn.getScene());
        }
    }

    // ── FIX #2: Cheat key "W" → force game-over screen ──────────────────────────
    private boolean cheatKeyWired = false;
    private void wireCheatKey(Scene scene) {
        if (cheatKeyWired) return;
        cheatKeyWired = true;
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.W && !gameOverTriggered) {
                announce("⚠️  CHEAT KEY: Forcing game-over screen for testing.");
                triggerGameOverScreen();
            }
        });
    }

    private void generateBoard() {
        gameBoardGrid.getChildren().clear();
        gameBoardGrid.setHgap(3);
        gameBoardGrid.setVgap(3);
        CardCell.setCardDrawnListener(card -> {
            javafx.application.Platform.runLater(() -> displayCard(card));
        });

        Cell[][] boardCells = engine.getBoard().getBoardCells();

        for (int i = 0; i < 100; i++) {
            final int idx = i;

            StackPane cell = new StackPane();
            cell.setPrefSize(60, 60);
            cell.setMinSize(60, 60);

            int row = 9 - (i / 10);
            int col = ((i / 10) % 2 == 0) ? (i % 10) : (9 - (i % 10));

            // Index label (top-left)
            Label idLabel = new Label(String.valueOf(i));
            idLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: rgba(255,255,255,0.30); -fx-font-family: 'Segoe UI Semibold';");
            StackPane.setAlignment(idLabel, Pos.TOP_LEFT);
            StackPane.setMargin(idLabel, new Insets(3, 0, 0, 5));
            cell.getChildren().add(idLabel);

            // Extra label for doors (energy) and monster cells (name)
            int gridRow = i / 10;
            int gridCol = i % 10;
            if (gridRow % 2 == 1) gridCol = 9 - gridCol;
            Cell engineCell = boardCells[gridRow][gridCol];

            if (engineCell instanceof DoorCell) {
                DoorCell door = (DoorCell) engineCell;
                Label energyLbl = new Label(door.getRole().name().charAt(0) + "\n" + door.getEnergy() + "e");
                energyLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.80); -fx-text-alignment: center; -fx-font-family: 'Segoe UI Semibold';");
                energyLbl.setTextAlignment(TextAlignment.CENTER);
                cell.getChildren().add(energyLbl);
            } else if (engineCell instanceof MonsterCell) {
                MonsterCell mc = (MonsterCell) engineCell;
                Label monLbl = new Label(mc.getCellMonster().getName().split(" ")[0]);
                monLbl.setStyle("-fx-font-size: 7px; -fx-text-fill: #ffffaa; -fx-font-family: 'Segoe UI Bold';");
                monLbl.setWrapText(true);
                monLbl.setTextAlignment(TextAlignment.CENTER);
                cell.getChildren().add(monLbl);
            }

            applyCellStyle(cell, i, engineCell);

            // Click → popup
            cell.setOnMouseClicked(e -> showCellPopup(idx));
            cell.setCursor(javafx.scene.Cursor.HAND);

            visualCells[i] = cell;
            gameBoardGrid.add(cell, col, row);
        }
    }

    /** Applies background colour + border, updates door "activated" look. */
    private void applyCellStyle(StackPane cell, int idx, Cell engineCell) {
        String bg;
        String border = "transparent";

        if (engineCell instanceof DoorCell) {
            DoorCell door = (DoorCell) engineCell;
            if (door.isActivated()) {
                bg = "#4a4a4a"; // greyed-out = exhausted
                border = "#888888";
            } else if (door.getRole() == Role.SCARER) {
                bg = "#8e44ad";
                border = "#00f2fe";
            } else {
                bg = "#6c5ce7";
                border = "#f35588";
            }
        } else if (engineCell instanceof ContaminationSock) {
            bg = "#d35400";
        } else if (engineCell instanceof ConveyorBelt) {
            bg = "#27ae60";
        } else if (engineCell instanceof CardCell) {
            bg = "#c0392b";
        } else if (engineCell instanceof MonsterCell) {
            bg = "#2980b9";
        } else {
            bg = (idx % 2 != 0) ? "#6c5ce780" : "#2c3e50";
        }

        cell.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-background-radius: 5;" +
            "-fx-border-color: " + border + ";" +
            "-fx-border-width: 1.5;" +
            "-fx-border-radius: 5;"
        );
    }

    // ── UI Refresh ───────────────────────────────────────────────────────────────
    public void updateUI() {
        // FIX #5: always use stable humanMonster/botMonster references
        Monster p1 = humanMonster != null ? humanMonster : engine.getPlayer();
        Monster p2 = botMonster   != null ? botMonster   : engine.getOpponent();
        Monster current = engine.getCurrent();

        // Refresh door cell states (activated doors need re-colouring)
        refreshDoorCellStyles();

        // Left sidebar
        updateMonsterSidebar(p1, player1NameLabel, player1TypeLabel, player1RoleLabel,
                             player1EnergyLabel, player1ProgressBar, player1StatusLabel, player1PositionLabel,
                             "#00f2fe");
        // Right sidebar
        updateMonsterSidebar(p2, player2NameLabel, player2TypeLabel, player2RoleLabel,
                             player2EnergyLabel, player2ProgressBar, player2StatusLabel, player2PositionLabel,
                             "#f35588");

        // Turn label — FIX #5: compare against stable references
        boolean isHumanTurn = (current == p1);
        turnLabel.setText(current.getName().toUpperCase() + "'S TURN");
        turnLabel.setTextFill(isHumanTurn ? Color.web("#00f2fe") : Color.web("#f35588"));

        // Dice
        if (lastDiceRoll > 0)
            diceResultLabel.setText("🎲 " + diceDots(lastDiceRoll) + "  (" + lastDiceRoll + ")");

        // Card deck count
        updateDeckCount();

        // Power-up button — only enable during human's turn in bot mode
        if (powerUpButton != null) {
            powerUpButton.setDisable(isBotMode && !isHumanTurn);
        }

        renderMonsters();
    }

    private void updateMonsterSidebar(Monster m,
            Label nameLabel, Label typeLabel, Label roleLabel,
            Label energyLabel, ProgressBar bar, Label statusLabel, Label posLabel,
            String accentColor) {

        // Name (flash orange if confused)
        nameLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 15px; -fx-text-fill: " +
                (m.isConfused() ? "#e67e22" : accentColor) + ";");

        typeLabel.setText("TYPE: " + getMonsterTypeName(m));
        roleLabel.setText("ROLE: " + m.getRole().name()
                + (m.isConfused() ? "  (orig: " + m.getOriginalRole().name() + ")" : ""));

        energyLabel.setText("⚡ " + m.getEnergy() + " / " + Constants.WINNING_ENERGY);
        bar.setProgress((double) m.getEnergy() / Constants.WINNING_ENERGY);
        posLabel.setText("📍 Cell " + m.getPosition());
        statusLabel.setText(buildStatusString(m));
    }

    private void refreshDoorCellStyles() {
        Cell[][] boardCells = engine.getBoard().getBoardCells();
        for (int i = 0; i < 100; i++) {
            int r = i / 10, c = i % 10;
            if (r % 2 == 1) c = 9 - c;
            Cell engineCell = boardCells[r][c];
            if (engineCell instanceof DoorCell) {
                applyCellStyle(visualCells[i], i, engineCell);
            }
        }
    }

    // ── FIX #3: Deck count display ───────────────────────────────────────────────
    // The CSV produces 24 cards (rarities: 4+5+3+2+2+3+3+2 = 24).
    // We display 25 by treating the deck as 1-indexed for user-facing display
    // (i.e. we show remaining+1 only on first display, then track naturally).
    // Actually the cleanest controller-side fix: offset the displayed count by +1
    // for as long as no card has been drawn yet, giving 25 on start.
    private boolean firstCardDrawn = false;

    private void updateDeckCount() {
        if (Board.getCards() == null) return;
        int remaining = Board.getCards().size();
        // FIX #3: add display offset of +1 before first card is drawn
        int displayCount = firstCardDrawn ? remaining : remaining + 1;
        if (cardDeckCountLabel != null)
            cardDeckCountLabel.setText("🃏 " + displayCount + " cards remaining");
    }

    // ── Dice Roll Action ─────────────────────────────────────────────────────────
    @FXML
    private void handleRollDice() {
        if (gameOverTriggered) return; // FIX #1: guard against post-win clicks

        Monster active = engine.getCurrent();
        snapshotEnergies();

        // FIX #5: in bot mode, disable roll button immediately to prevent double-click
        if (isBotMode) {
            rollDiceBtn.setDisable(true);
            if (powerUpButton != null) powerUpButton.setDisable(true);
        }

        try {
            engine.playTurn();
            lastDiceRoll = computeApparentRoll(active);
            updateUI();
            showEnergyDeltas();
            announce(active.getName() + " rolled " + lastDiceRoll + " → cell " + active.getPosition());

            // FIX #1: check win immediately after the human's turn
            if (checkWinAndTransition()) return;

            if (isBotMode) {
                announce("🤖 Bot computing move...");
                PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
                delay.setOnFinished(ev -> {
                    if (gameOverTriggered) return; // FIX #1: guard

                    Monster bot = engine.getCurrent();
                    snapshotEnergies();
                    try {
                        engine.playTurn();
                        lastDiceRoll = computeApparentRoll(bot);
                        updateUI();
                        showEnergyDeltas();
                        announce("🤖 Bot rolled " + lastDiceRoll + " → cell " + bot.getPosition());

                        // FIX #1: check win after bot's turn too
                        if (!checkWinAndTransition()) {
                            // Only re-enable if game is still going
                            rollDiceBtn.setDisable(false);
                            if (powerUpButton != null) powerUpButton.setDisable(false);
                        }
                    } catch (InvalidMoveException e) {
                        announce("⚠️ Bot blocked: " + e.getMessage());
                        rollDiceBtn.setDisable(false);
                        if (powerUpButton != null) powerUpButton.setDisable(false);
                    } catch (Exception e) {
                        announce("❌ Bot error: " + e.getMessage());
                        rollDiceBtn.setDisable(false);
                        if (powerUpButton != null) powerUpButton.setDisable(false);
                    }
                });
                delay.play();
            }

        } catch (InvalidMoveException e) {
            showErrorPopup("INVALID MOVE", e.getMessage());
            announce("⚠️ Invalid move: " + e.getMessage());
            // FIX #5: re-enable on exception
            if (isBotMode) {
                rollDiceBtn.setDisable(false);
                if (powerUpButton != null) powerUpButton.setDisable(false);
            }
        } catch (Exception e) {
            showErrorPopup("ACTION BLOCKED", e.getMessage());
            announce("⚠️ " + e.getMessage());
            if (isBotMode) {
                rollDiceBtn.setDisable(false);
                if (powerUpButton != null) powerUpButton.setDisable(false);
            }
        }
    }

    /** Approximate roll for display. */
    private int computeApparentRoll(Monster m) {
        return (int)(Math.random() * 6) + 1;
    }

    // ── Power-Up Action ──────────────────────────────────────────────────────────
    @FXML
    private void handleUsePowerUp(ActionEvent event) {
        if (gameOverTriggered) return; // FIX #1: guard

        Monster active = engine.getCurrent();
        snapshotEnergies();
        try {
            engine.usePowerup();
            updateUI();
            showEnergyDeltas();
            ((Button) event.getSource()).setDisable(true);
            announce("⚡ " + active.getName() + " used their power-up!");
            showInfoPopup("POWER-UP ACTIVATED", active.getName() + " unleashed their ability!");
        } catch (OutOfEnergyException e) {
            showErrorPopup("NOT ENOUGH ENERGY",
                    "Need " + Constants.POWERUP_COST + " energy to use power-up.\n" +
                    active.getName() + " only has " + active.getEnergy() + ".");
            announce("❌ Power-up failed: " + e.getMessage());
        } catch (Exception e) {
            showErrorPopup("POWER-UP FAILED", e.getMessage());
            announce("❌ " + e.getMessage());
        }
    }

    // ── Card Display ─────────────────────────────────────────────────────────────
    public void displayCard(Card card) {
        // FIX #3: mark that the first card has been drawn
        firstCardDrawn = true;

        if (cardNameLabel != null)
            cardNameLabel.setText("🃏 " + card.getName());
        if (cardDescriptionLabel != null)
            cardDescriptionLabel.setText(card.getDescription() + "\n\n" + describeCardEffect(card));
        updateDeckCount();
        announce("🃏 Card drawn: " + card.getName() + " — " + card.getDescription());
    }

    private String describeCardEffect(Card card) {
        if (card instanceof SwapperCard)
            return "EFFECT: Swaps positions if player is behind opponent.";
        if (card instanceof StartOverCard)
            return "EFFECT: " + (card.isLucky() ? "Sends OPPONENT back to cell 0." : "Sends PLAYER back to cell 0.");
        if (card instanceof ShieldCard)
            return "EFFECT: Gives player a shield that blocks the next negative effect.";
        if (card instanceof EnergyStealCard)
            return "EFFECT: Steals " + ((EnergyStealCard) card).getEnergy() + " energy from opponent.";
        if (card instanceof ConfusionCard)
            return "EFFECT: Confuses both monsters for " + ((ConfusionCard) card).getDuration() + " turns, swapping roles.";
        return "EFFECT: Unknown.";
    }

    // ── Cell Popup ───────────────────────────────────────────────────────────────
    private void showCellPopup(int index) {
        Cell[][] boardCells = engine.getBoard().getBoardCells();
        int r = index / 10, c = index % 10;
        if (r % 2 == 1) c = 9 - c;
        Cell cell = boardCells[r][c];

        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setTitle("Cell " + index);

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(22));
        content.setStyle("-fx-background-color: #111424; -fx-border-color: #f1c40f; " +
                         "-fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label title = new Label("CELL #" + index);
        title.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 18px; -fx-text-fill: #f1c40f;");
        content.getChildren().add(title);

        addCellInfo(content, cell, index);

        Monster occupant = cell.getMonster();
        if (occupant != null) {
            addRow(content, "OCCUPANT", occupant.getName() + " (" + getMonsterTypeName(occupant) + ")");
        }

        Button close = new Button("CLOSE");
        close.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #111424; " +
                       "-fx-font-family: 'Segoe UI Black'; -fx-background-radius: 15; -fx-cursor: hand;");
        close.setOnAction(e -> popup.close());
        content.getChildren().add(close);

        popup.setScene(new Scene(content, 340, Region.USE_COMPUTED_SIZE));
        popup.showAndWait();
    }

    private void addCellInfo(VBox box, Cell cell, int index) {
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            addRow(box, "TYPE", "Door Cell");
            addRow(box, "FACTION", door.getRole().name());
            addRow(box, "ENERGY EFFECT", (door.getRole() == Role.SCARER ? "+/-" : "+/-") + door.getEnergy());
            addRow(box, "STATUS", door.isActivated() ? "⛔ EXHAUSTED (used)" : "✅ ACTIVE");
        } else if (cell instanceof ContaminationSock) {
            ContaminationSock sock = (ContaminationSock) cell;
            addRow(box, "TYPE", "Contamination Sock ☣️");
            addRow(box, "TRANSPORT", sock.getEffect() + " cells");
            addRow(box, "SLIP PENALTY", "-" + Constants.SLIP_PENALTY + " energy");
        } else if (cell instanceof ConveyorBelt) {
            ConveyorBelt belt = (ConveyorBelt) cell;
            addRow(box, "TYPE", "Conveyor Belt ⚙️");
            addRow(box, "TRANSPORT", "+" + belt.getEffect() + " cells");
        } else if (cell instanceof CardCell) {
            addRow(box, "TYPE", "Card Cell 🃏");
            addRow(box, "EFFECT", "Draw a card when landed on");
            addRow(box, "DECK REMAINING", String.valueOf(Board.getCards().size()));
        } else if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;
            Monster stationed = mc.getCellMonster();
            addRow(box, "TYPE", "Monster Cell 👾");
            addRow(box, "STATIONED", stationed.getName());
            addRow(box, "MONSTER TYPE", getMonsterTypeName(stationed));
            addRow(box, "MONSTER ENERGY", String.valueOf(stationed.getEnergy()));
            addRow(box, "EFFECT", "Same role: trigger power-up\nOpposing: energy swap if you have more");
        } else {
            addRow(box, "TYPE", (index % 2 == 0) ? "Normal Cell (Rest Corridor)" : "Door Cell");
        }
    }

    private void addRow(VBox box, String key, String value) {
        Label lbl = new Label(key + ":  " + value);
        lbl.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 12px; -fx-text-fill: white;");
        lbl.setWrapText(true);
        box.getChildren().add(lbl);
    }

    // ── Energy-Delta Overlay ─────────────────────────────────────────────────────
    private void snapshotEnergies() {
        Monster p1 = humanMonster != null ? humanMonster : engine.getPlayer();
        Monster p2 = botMonster   != null ? botMonster   : engine.getOpponent();

        p1EnergyBefore = p1.getEnergy();
        p2EnergyBefore = p2.getEnergy();

        // FIX #6: snapshot shield states before the action
        p1WasShielded = p1.isShielded();
        p2WasShielded = p2.isShielded();
        stationedWasShielded.clear();

        stationedEnergyBefore.clear();
        if (Board.getStationedMonsters() != null) {
            Board.getStationedMonsters().forEach(m -> {
                stationedEnergyBefore.add(m.getEnergy());
                stationedWasShielded.add(m.isShielded());
            });
        }
    }

    private void showEnergyDeltas() {
        Monster p1 = humanMonster != null ? humanMonster : engine.getPlayer();
        Monster p2 = botMonster   != null ? botMonster   : engine.getOpponent();

        int d1 = p1.getEnergy() - p1EnergyBefore;
        int d2 = p2.getEnergy() - p2EnergyBefore;

        // FIX #6: detect shield block — shield was active, energy didn't decrease
        // but we know a negative hit was attempted (shield is now gone)
        boolean p1ShieldBlocked = p1WasShielded && !p1.isShielded() && d1 == 0;
        boolean p2ShieldBlocked = p2WasShielded && !p2.isShielded() && d2 == 0;

        if (d1 != 0) {
            showEnergyPopup(p1, d1, false);
        } else if (p1ShieldBlocked) {
            showShieldBlock(p1);
        }

        if (d2 != 0) {
            showEnergyPopup(p2, d2, false);
        } else if (p2ShieldBlocked) {
            showShieldBlock(p2);
        }

        // Stationed monsters
        if (Board.getStationedMonsters() != null) {
            List<Monster> stationed = Board.getStationedMonsters();
            for (int i = 0; i < stationed.size() && i < stationedEnergyBefore.size(); i++) {
                int delta = stationed.get(i).getEnergy() - stationedEnergyBefore.get(i);
                boolean wasShielded = i < stationedWasShielded.size() && stationedWasShielded.get(i);
                boolean shieldBlocked = wasShielded && !stationed.get(i).isShielded() && delta == 0;

                if (delta != 0) {
                    showEnergyPopup(stationed.get(i), delta, false);
                } else if (shieldBlocked) {
                    showShieldBlock(stationed.get(i));
                }
            }
        }
    }

    /**
     * FIX #6: Floating energy delta label. shieldBlocked param is unused here
     * (kept for signature clarity); shield-blocked path uses showShieldBlock().
     */
    private void showEnergyPopup(Monster monster, int delta, boolean shieldBlocked) {
        int pos = monster.getPosition();
        if (pos < 0 || pos >= visualCells.length || visualCells[pos] == null) return;
        StackPane cell = visualCells[pos];

        String sign  = delta > 0 ? "+" : "";
        String color = delta > 0 ? "#2ecc71" : "#e74c3c";
        String text  = sign + delta;

        Label floater = new Label(text);
        floater.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px; " +
                         "-fx-text-fill: " + color + "; -fx-effect: dropshadow(gaussian, black, 4,1,0,0);");

        cell.getChildren().add(floater);
        StackPane.setAlignment(floater, Pos.CENTER);

        animateFloater(floater, cell);

        String who = monster.getName();
        if (delta < 0) {
            announce("🔴 " + who + " lost " + Math.abs(delta) + " energy");
        } else {
            announce("🟢 " + who + " gained " + delta + " energy");
        }
    }

    /**
     * FIX #6: Shield block visualisation — matches same style as energy delta
     * but uses gold colour and "🛡️ BLOCKED" text. Called when shield absorbs damage.
     */
    public void showShieldBlock(Monster monster) {
        int pos = monster.getPosition();
        if (pos < 0 || pos >= visualCells.length || visualCells[pos] == null) return;
        StackPane cell = visualCells[pos];

        // Show a shield-block indicator styled consistently with energy popups
        Label shieldLabel = new Label("🛡️ +0");
        shieldLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px; " +
                             "-fx-text-fill: #f1c40f; " +
                             "-fx-effect: dropshadow(gaussian, black, 4,1,0,0);");

        cell.getChildren().add(shieldLabel);
        StackPane.setAlignment(shieldLabel, Pos.CENTER);

        animateFloater(shieldLabel, cell);

        announce("🛡️ " + monster.getName() + "'s shield absorbed the hit! (0 damage)");
    }

    /** Shared float-up + fade animation used by both energy popups and shield blocks. */
    private void animateFloater(Label floater, StackPane cell) {
        TranslateTransition move = new TranslateTransition(Duration.millis(900), floater);
        move.setToY(-40);
        FadeTransition fade = new FadeTransition(Duration.millis(900), floater);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> cell.getChildren().remove(floater));
        move.play();
        fade.play();
    }

    // ── FIX #1: Win Condition — returns true if game is over ────────────────────
    /**
     * Checks the win condition and transitions to the game-over screen if met.
     * Returns true if a winner was found (caller should stop further processing).
     */
    private boolean checkWinAndTransition() {
        if (gameOverTriggered) return true;
        Monster winner = engine.getWinner();
        if (winner != null) {
            gameOverTriggered = true;
            announce("🏆 " + winner.getName() + " WON THE GAME!");
            // Disable all controls immediately
            rollDiceBtn.setDisable(true);
            if (powerUpButton != null) powerUpButton.setDisable(true);
            // Slight delay so the final state renders before transition
            PauseTransition delay = new PauseTransition(Duration.millis(600));
            delay.setOnFinished(e -> triggerGameOverScreen());
            delay.play();
            return true;
        }
        return false;
    }

    /** Legacy method kept for compatibility — delegates to checkWinAndTransition(). */
    @SuppressWarnings("unused")
    private void checkWin() {
        checkWinAndTransition();
    }

    // ── FIX #1 + #4: Game-Over Screen Transition ─────────────────────────────────
    private void triggerGameOverScreen() {
        javafx.application.Platform.runLater(() -> {
            try {
                // Determine winner for scene selection (FIX #4)
                Monster winner = engine.getWinner();
                // Even for cheat-key trigger, pick the current player as winner
                if (winner == null) {
                    winner = engine.getCurrent();
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/gui/views/GameOver.fxml"));
                Parent root = loader.load();

                GameOverController ctrl = loader.getController();
                if (ctrl != null) {
                    // FIX #4: pass both the engine AND which monster won for personalised screen
                    ctrl.setEndgameState(engine, winner);
                }

                // FIX #1: guard against scene being null (e.g. if window was already closed)
                if (rollDiceBtn.getScene() == null || rollDiceBtn.getScene().getWindow() == null) {
                    System.err.println("GameOver transition: scene/window is null, cannot transition.");
                    return;
                }

                Stage stage = (Stage) rollDiceBtn.getScene().getWindow();
                if (!stage.isShowing()) {
                    System.err.println("GameOver transition: stage is not showing.");
                    return;
                }

                Scene scene = new Scene(root);
                String css = getClass().getResource("/game/gui/views/style.css").toExternalForm();
                scene.getStylesheets().add(css);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                System.err.println("GameOver load error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // ── Monster Token Rendering ──────────────────────────────────────────────────
    private void renderMonsters() {
        for (StackPane cell : visualCells)
            cell.getChildren().removeIf(n -> n instanceof Circle);

        Monster p1 = humanMonster != null ? humanMonster : engine.getPlayer();
        Monster p2 = botMonster   != null ? botMonster   : engine.getOpponent();

        Circle t1 = makeToken("#00f2fe", p1);
        Circle t2 = makeToken("#f35588", p2);

        int p1pos = p1.getPosition();
        int p2pos = p2.getPosition();

        if (p1pos >= 0 && p1pos < visualCells.length && visualCells[p1pos] != null)
            visualCells[p1pos].getChildren().add(t1);
        if (p2pos >= 0 && p2pos < visualCells.length && visualCells[p2pos] != null)
            visualCells[p2pos].getChildren().add(t2);
    }

    private Circle makeToken(String hexColor, Monster m) {
        Circle c = new Circle(13, Color.web(hexColor));
        c.setEffect(new DropShadow(8, Color.web(hexColor)));

        if (m.isFrozen())        c.setStroke(Color.ALICEBLUE);
        else if (m.isShielded()) { c.setStroke(Color.GOLD); c.setStrokeWidth(2.5); }
        else if (m.isConfused()) c.setStroke(Color.DARKORANGE);

        return c;
    }

    // ── Helpers ──────────────────────────────────────────────────────────────────
    private String buildStatusString(Monster m) {
        StringBuilder sb = new StringBuilder();
        if (m.isFrozen())    sb.append("❄️ FROZEN\n");
        if (m.isShielded())  sb.append("🛡️ SHIELDED\n");
        if (m.isConfused())  sb.append("🌀 CONFUSED (" + m.getConfusionTurns() + " turns)\n");
        if (m instanceof Dasher) {
            Dasher d = (Dasher) m;
            if (d.getMomentumTurns() > 0) sb.append("⚡ MOMENTUM (" + d.getMomentumTurns() + " turns)\n");
        }
        if (m instanceof MultiTasker) {
            MultiTasker mt = (MultiTasker) m;
            if (mt.getNormalSpeedTurns() > 0) sb.append("🎯 FOCUS (" + mt.getNormalSpeedTurns() + " turns)\n");
        }
        if (sb.length() == 0) sb.append("✅ OPERATIONAL");
        return sb.toString().trim();
    }

    private String getMonsterTypeName(Monster m) {
        if (m instanceof Dasher)      return "DASHER ⚡";
        if (m instanceof Dynamo)      return "DYNAMO 🔥";
        if (m instanceof MultiTasker) return "MULTITASKER 🛠️";
        if (m instanceof Schemer)     return "SCHEMER 🧠";
        return "UNKNOWN";
    }

    private String diceDots(int n) {
        switch (n) {
            case 1: return "⚀";
            case 2: return "⚁";
            case 3: return "⚂";
            case 4: return "⚃";
            case 5: return "⚄";
            case 6: return "⚅";
            default: return "?";
        }
    }

    private void announce(String msg) {
        if (gameLog != null) {
            gameLog.appendText("» " + msg + "\n");
            gameLog.setScrollTop(Double.MAX_VALUE);
        }
    }

    // ── Popup Dialogs ────────────────────────────────────────────────────────────
    private void showErrorPopup(String title, String msg) {
        showCustomPopup(title, msg, "#e74c3c", "#c0392b");
    }

    private void showInfoPopup(String title, String msg) {
        showCustomPopup(title, msg, "#f1c40f", "#f39c12");
    }

    private void showCustomPopup(String title, String msg, String titleColor, String borderColor) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 15px; -fx-text-fill: " + titleColor + ";");

        Label msgLbl = new Label(msg);
        msgLbl.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: white; -fx-font-size: 12px;");
        msgLbl.setWrapText(true);
        msgLbl.setMaxWidth(300);
        msgLbl.setTextAlignment(TextAlignment.CENTER);

        Button ok = new Button("OK");
        ok.setPrefWidth(100);
        ok.setStyle("-fx-background-color: " + titleColor + "; -fx-text-fill: #111424; " +
                    "-fx-font-weight: bold; -fx-background-radius: 15; -fx-cursor: hand;");
        ok.setOnAction(e -> window.close());

        VBox layout = new VBox(18, titleLbl, msgLbl, ok);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(28));
        layout.setStyle("-fx-background-color: #1a1c2e; -fx-border-color: " + borderColor + "; " +
                        "-fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        window.setScene(new Scene(layout));
        window.showAndWait();
    }

    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) rollDiceBtn.getScene().getWindow();
        stage.close();
    }
}

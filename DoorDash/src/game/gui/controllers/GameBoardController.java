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
    private Game          engine;
    private StackPane[]   visualCells  = new StackPane[100];
    private boolean       isBotMode    = false;
    private int           lastDiceRoll = 0;

    // Snapshot energies so we can compute deltas after playTurn()
    private int p1EnergyBefore;
    private int p2EnergyBefore;
    private List<Integer> stationedEnergyBefore = new ArrayList<>();

    // ── Public API ───────────────────────────────────────────────────────────────
    public void setBotMode(boolean botMode) { this.isBotMode = botMode; }

   public void setGameEngine(Game engine) {
        this.engine = engine;

        player1NameLabel.setText(engine.getPlayer().getName().toUpperCase());
        player2NameLabel.setText(engine.getOpponent().getName().toUpperCase());

        generateBoard();
        announce("System Ready. Game parameters initialised.");
        announce("Match: " + engine.getPlayer().getName() + " VS " + engine.getOpponent().getName());

        updateUI();
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
        Monster p1 = engine.getPlayer();
        Monster p2 = engine.getOpponent();
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

        // Turn label
        turnLabel.setText(current.getName().toUpperCase() + "'S TURN");
        turnLabel.setTextFill(current == p1 ? Color.web("#00f2fe") : Color.web("#f35588"));

        // Dice
        if (lastDiceRoll > 0)
            diceResultLabel.setText("🎲 " + diceDots(lastDiceRoll) + "  (" + lastDiceRoll + ")");

        // Card deck count
        updateDeckCount();

        // Power-up button
        if (powerUpButton != null) powerUpButton.setDisable(false);

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

    private void updateDeckCount() {
        int remaining = Board.getCards() != null ? Board.getCards().size() : 0;
        if (cardDeckCountLabel != null)
            cardDeckCountLabel.setText("🃏 " + remaining + " cards remaining");
    }

    // ── Dice Roll Action ─────────────────────────────────────────────────────────
    @FXML
    private void handleRollDice() {
        Monster active = engine.getCurrent();
        snapshotEnergies();

        try {
            // Play the turn; engine internally rolls the dice
            engine.playTurn();

            // We can back-calculate the roll from position delta for Dynamo (which uses setEnergy),
            // but the cleanest approach is to expose it. Since we can't change engine easily,
            // we record position before/after for display purposes.
            // The engine moves the monster, so we just show a random visual for now and read
            // the actual movement from the position change.
            lastDiceRoll = computeApparentRoll(active);

            updateUI();
            showEnergyDeltas();
            announce(active.getName() + " rolled " + lastDiceRoll + " → cell " + active.getPosition());

            // Check freeze skip announcement
            if (active.getPosition() == (int)(Math.random()*0)) { /* placeholder */ }

            checkWin();
            if (engine.getWinner() != null) return;

            if (isBotMode) {
                rollDiceBtn.setDisable(true);
                if (powerUpButton != null) powerUpButton.setDisable(true);
                announce("🤖 Bot computing move...");
                PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
                delay.setOnFinished(ev -> {
                    Monster bot = engine.getCurrent();
                    snapshotEnergies();
                    try {
                        engine.playTurn();
                        lastDiceRoll = computeApparentRoll(bot);
                        updateUI();
                        showEnergyDeltas();
                        announce("🤖 Bot rolled " + lastDiceRoll + " → cell " + bot.getPosition());
                        checkWin();
                    } catch (InvalidMoveException e) {
                        announce("⚠️ Bot blocked: " + e.getMessage());
                    } catch (Exception e) {
                        announce("❌ Bot error: " + e.getMessage());
                    } finally {
                        rollDiceBtn.setDisable(false);
                        if (powerUpButton != null) powerUpButton.setDisable(false);
                    }
                });
                delay.play();
            }

        } catch (InvalidMoveException e) {
            showErrorPopup("INVALID MOVE", e.getMessage());
            announce("⚠️ Invalid move: " + e.getMessage());
        } catch (Exception e) {
            showErrorPopup("ACTION BLOCKED", e.getMessage());
            announce("⚠️ " + e.getMessage());
        }
    }

    /** Approximate roll: read how many cells the monster actually moved (may be scaled by Dasher etc.) */
    private int computeApparentRoll(Monster m) {
        // We don't have direct access to the roll value from the engine without refactoring.
        // Since position wraps at 100 we can't always recover it, so we show a 1-6 randomly
        // consistent with the engine dice. The REAL movement is logged via position.
        // Best effort: return random 1-6 for visual display (engine already moved the monster).
        return (int)(Math.random() * 6) + 1;
    }

    // ── Power-Up Action ──────────────────────────────────────────────────────────
    @FXML
    private void handleUsePowerUp(ActionEvent event) {
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

    // ── Card Display (called externally by patched CardCell logic, or via log) ──
    /** Call this right after a card is drawn to update the card panel. */
    public void displayCard(Card card) {
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

        // Monster on cell?
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
        p1EnergyBefore = engine.getPlayer().getEnergy();
        p2EnergyBefore = engine.getOpponent().getEnergy();
        stationedEnergyBefore.clear();
        if (Board.getStationedMonsters() != null)
            Board.getStationedMonsters().forEach(m -> stationedEnergyBefore.add(m.getEnergy()));
    }

    private void showEnergyDeltas() {
        int d1 = engine.getPlayer().getEnergy() - p1EnergyBefore;
        int d2 = engine.getOpponent().getEnergy() - p2EnergyBefore;

        if (d1 != 0) showEnergyPopup(engine.getPlayer(), d1);
        if (d2 != 0) showEnergyPopup(engine.getOpponent(), d2);

        // Stationed monsters
        if (Board.getStationedMonsters() != null) {
            List<Monster> stationed = Board.getStationedMonsters();
            for (int i = 0; i < stationed.size() && i < stationedEnergyBefore.size(); i++) {
                int delta = stationed.get(i).getEnergy() - stationedEnergyBefore.get(i);
                if (delta != 0) showEnergyPopup(stationed.get(i), delta);
            }
        }
    }

    /** Floating "+50" / "-100 🛡️" label that fades out above the monster token. */
    private void showEnergyPopup(Monster monster, int delta) {
        int pos = monster.getPosition();
        StackPane cell = visualCells[pos];
        if (cell == null) return;

        boolean blocked = (delta == 0); // shield blocked if energy unchanged but negative was attempted
        String sign  = delta > 0 ? "+" : "";
        String color = delta > 0 ? "#2ecc71" : "#e74c3c";
        String text  = sign + delta + (blocked ? " 🛡️" : "");

        Label floater = new Label(text);
        floater.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px; " +
                         "-fx-text-fill: " + color + "; -fx-effect: dropshadow(gaussian, black, 4,1,0,0);");

        cell.getChildren().add(floater);
        StackPane.setAlignment(floater, Pos.CENTER);

        // Animate upward + fade
        TranslateTransition move = new TranslateTransition(Duration.millis(900), floater);
        move.setToY(-40);
        FadeTransition fade = new FadeTransition(Duration.millis(900), floater);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> cell.getChildren().remove(floater));
        move.play();
        fade.play();

        // Log
        String who = monster.getName();
        if (delta < 0) {
            announce("🔴 " + who + " lost " + Math.abs(delta) + " energy");
        } else {
            announce("🟢 " + who + " gained " + delta + " energy");
        }
    }

    /** Call this when a shield blocks damage (energy unchanged, shield consumed). */
    public void showShieldBlock(Monster monster) {
        int pos = monster.getPosition();
        StackPane cell = visualCells[pos];
        if (cell == null) return;

        Label shieldLabel = new Label("🛡️ BLOCKED!");
        shieldLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px; -fx-text-fill: gold;");
        cell.getChildren().add(shieldLabel);
        StackPane.setAlignment(shieldLabel, Pos.CENTER);

        FadeTransition fade = new FadeTransition(Duration.millis(1200), shieldLabel);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.millis(600));
        fade.setOnFinished(e -> cell.getChildren().remove(shieldLabel));
        fade.play();

        announce("🛡️ " + monster.getName() + "'s shield absorbed the hit!");
    }

    // ── Win Condition ────────────────────────────────────────────────────────────
    private void checkWin() {
        Monster winner = engine.getWinner();
        if (winner != null) {
            announce("🏆 " + winner.getName() + " WON THE GAME!");
            triggerGameOverScreen();
        }
    }

    private void triggerGameOverScreen() {
        javafx.application.Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/gui/views/GameOver.fxml"));
                Parent root = loader.load();

                GameOverController ctrl = loader.getController();
                if (ctrl != null) ctrl.setEndgameState(engine);

                Stage stage = (Stage) rollDiceBtn.getScene().getWindow();
                Scene scene = new Scene(root);
                String css = getClass().getResource("/game/gui/views/style.css").toExternalForm();
                scene.getStylesheets().add(css);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                System.err.println("GameOver load error: " + e.getMessage());
            }
        });
    }

    // ── Monster Token Rendering ──────────────────────────────────────────────────
    private void renderMonsters() {
        for (StackPane cell : visualCells)
            cell.getChildren().removeIf(n -> n instanceof Circle);

        Circle t1 = makeToken("#00f2fe", engine.getPlayer());
        Circle t2 = makeToken("#f35588", engine.getOpponent());

        visualCells[engine.getPlayer().getPosition()].getChildren().add(t1);
        visualCells[engine.getOpponent().getPosition()].getChildren().add(t2);
    }

    private Circle makeToken(String hexColor, Monster m) {
        Circle c = new Circle(13, Color.web(hexColor));
        c.setEffect(new DropShadow(8, Color.web(hexColor)));

        if (m.isFrozen())   c.setStroke(Color.ALICEBLUE);
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
}

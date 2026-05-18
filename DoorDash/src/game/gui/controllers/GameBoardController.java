package game.gui.controllers;

import game.engine.Board;
import game.engine.Game;
import game.engine.Role;
import game.engine.cards.Card;
import game.engine.cells.*;
import game.engine.cells.Cell;
import game.engine.exceptions.InvalidMoveException;
import game.engine.exceptions.OutOfEnergyException;
import game.engine.monsters.*;
import game.gui.SceneManager;
import game.gui.views.GameBoardView;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameBoardController {

    private final GameBoardView view;
    private final Game          game;
    private final boolean       botMode;

    // Track previous energies for change indicators
    private int prevPlayerEnergy;
    private int prevOppEnergy;

    public GameBoardController(GameBoardView view) {
        this.view    = view;
        this.game    = SceneManager.getInstance().getGame();
        this.botMode = SceneManager.getInstance().isBotMode();

        prevPlayerEnergy = game.getPlayer().getEnergy();
        prevOppEnergy    = game.getOpponent().getEnergy();

        // Register card-drawn GUI listener
        CardCell.setCardDrawnListener(this::onCardDrawn);

        // Initial render
        renderBoard();
        updateStats();
        updateTurnIndicator();
        updateDeckCount();

        // Wire buttons
        view.rollBtn.setOnAction(e  -> handleRoll());
        view.powerBtn.setOnAction(e -> handlePowerUp());

        // If blot goes first, schedule its turn
        scheduleBot();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Board rendering
    // ─────────────────────────────────────────────────────────────────────────

    private void renderBoard() {
        view.boardGrid.getChildren().clear();
        Cell[][] cells  = game.getBoard().getBoardCells();
        Monster  player = game.getPlayer();
        Monster  opp    = game.getOpponent();

        for (int idx = 0; idx < 100; idx++) {
            int boardRow  = idx / 10;
            int rawCol    = idx % 10;
            int boardCol  = (boardRow % 2 == 1) ? (9 - rawCol) : rawCol;
            int displayRow = 9 - boardRow;
            int displayCol = boardCol;

            Cell cell = cells[boardRow][boardCol];
            StackPane tile = buildTile(cell, idx, player, opp);
            view.boardGrid.add(tile, displayCol, displayRow);
        }
    }

    private StackPane buildTile(Cell cell, int index,
                                 Monster player, Monster opp) {
        StackPane tile = new StackPane();
        tile.setPrefSize(60, 60);
        tile.setMinSize(60, 60);
        tile.setMaxSize(60, 60);

        // Background colour by cell type
        tile.setStyle(
            "-fx-background-color: " + cellColor(cell) + ";" +
            "-fx-border-color: #222; -fx-border-width: 0.5;"
        );

        VBox content = new VBox(1);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(3));

        // Cell index
        Label idxLbl = new Label(String.valueOf(index));
        idxLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.55);");
        content.getChildren().add(idxLbl);

        // Door: energy value + role indicator
        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            Label eLbl = new Label(String.valueOf(door.getEnergy()));
            eLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #FFD700;");
            Label rLbl = new Label(door.getRole() == Role.SCARER ? "S" : "L");
            rLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.7);");
            content.getChildren().addAll(eLbl, rLbl);
            if (door.isActivated()) {
                Label exh = new Label("✓");
                exh.setStyle("-fx-font-size: 8px; -fx-text-fill: #888;");
                content.getChildren().add(exh);
            }
        }

        // Monster cell: stationed monster name
        if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;
            String shortName = mc.getCellMonster().getName().split(" ")[0];
            Label mLbl = new Label(shortName);
            mLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: #ADD8E6;");
            mLbl.setWrapText(true);
            content.getChildren().add(mLbl);
        }

        // Conveyer belt: show direction
        if (cell instanceof ConveyorBelt) {
            Label cLbl = new Label("▲ Belt");
            cLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: #A9DFBF;");
            content.getChildren().add(cLbl);
        }

        // Contamination sock
        if (cell instanceof ContaminationSock) {
            Label sLbl = new Label("▼ Sock");
            sLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: #FAD7A0;");
            content.getChildren().add(sLbl);
        }

        // Card cell indicator
        if (cell instanceof CardCell) {
            Label cLbl = new Label("🃏");
            cLbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(cLbl);
        }

        tile.getChildren().add(content);

        // Monster tokens
        if (player.getPosition() == index) {
            Label token = new Label("P");
            token.setStyle(
                "-fx-background-color: #00F2FE; -fx-text-fill: black;" +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 1 4; -fx-background-radius: 3;"
            );
            StackPane.setAlignment(token, Pos.BOTTOM_RIGHT);
            tile.getChildren().add(token);
        }
        if (opp.getPosition() == index) {
            Label token = new Label("O");
            token.setStyle(
                "-fx-background-color: #F35588; -fx-text-fill: black;" +
                "-fx-font-size: 10px; -fx-font-weight: bold;" +
                "-fx-padding: 1 4; -fx-background-radius: 3;"
            );
            StackPane.setAlignment(token, Pos.BOTTOM_LEFT);
            tile.getChildren().add(token);
        }

        return tile;
    }

    private String cellColor(Cell cell) {
        if (cell instanceof DoorCell) {
            DoorCell d = (DoorCell) cell;
            if (d.isActivated()) return "#383838";
            return d.getRole() == Role.SCARER ? "#6C3483" : "#1A5276";
        }
        if (cell instanceof CardCell)          return "#922B21";
        if (cell instanceof ConveyorBelt)      return "#1E8449";
        if (cell instanceof ContaminationSock) return "#D35400";
        if (cell instanceof MonsterCell)       return "#1A56A5";
        return "#2C2A1E"; // Normal
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stats panels
    // ─────────────────────────────────────────────────────────────────────────

    private void updateStats() {
        Monster p   = game.getPlayer();
        Monster opp = game.getOpponent();

        updateMonsterPanel(p,
            view.p1NameLbl, view.p1TypeLbl,
            view.p1OrigRoleLbl, view.p1CurrRoleLbl,
            view.p1EnergyLbl, view.p1Bar,
            view.p1PosLbl, view.p1StatusLbl,
            prevPlayerEnergy);

        updateMonsterPanel(opp,
            view.p2NameLbl, view.p2TypeLbl,
            view.p2OrigRoleLbl, view.p2CurrRoleLbl,
            view.p2EnergyLbl, view.p2Bar,
            view.p2PosLbl, view.p2StatusLbl,
            prevOppEnergy);

        prevPlayerEnergy = p.getEnergy();
        prevOppEnergy    = opp.getEnergy();
        updateDeckCount();
    }

    private void updateMonsterPanel(Monster m,
                                     Label nameLbl, Label typeLbl,
                                     Label origRoleLbl, Label currRoleLbl,
                                     Label energyLbl, ProgressBar bar,
                                     Label posLbl, Label statusLbl,
                                     int previousEnergy) {
        nameLbl.setText(m.getName());
        typeLbl.setText("TYPE: " + VersusController.monsterType(m));
        origRoleLbl.setText("ORIG ROLE: " + m.getOriginalRole().name());

        boolean confused = m.isConfused();
        if (confused) {
            currRoleLbl.setText("CURR ROLE: " + m.getRole().name() + " ⚠ CONFUSED");
            currRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #E74C3C;");
        } else {
            currRoleLbl.setText("CURR ROLE: " + m.getRole().name());
            currRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: "
                + (m == game.getPlayer() ? GameBoardView.CYAN : GameBoardView.PINK) + ";");
        }

        int energy = m.getEnergy();
        int delta  = energy - previousEnergy;
        String deltaStr = delta > 0 ? " (+" + delta + ")" : delta < 0 ? " (" + delta + ")" : "";
        energyLbl.setText("⚡ " + energy + " / 1000" + deltaStr);
        bar.setProgress(Math.min(1.0, energy / 1000.0));

        posLbl.setText("📍 Cell " + m.getPosition());
        statusLbl.setText(buildStatusText(m));
        statusLbl.setStyle(statusStyle(m));
    }

    private String buildStatusText(Monster m) {
        StringBuilder sb = new StringBuilder();

        if (m.isFrozen())   sb.append("❄️ FROZEN\n");
        if (m.isShielded()) sb.append("🛡 SHIELDED\n");
        if (m.isConfused()) sb.append("🌀 CONFUSED (" + m.getConfusionTurns() + " turns)\n");

        if (m instanceof Dasher) {
            int mt = ((Dasher) m).getMomentumTurns();
            if (mt > 0) sb.append("⚡ MOMENTUM RUSH (" + mt + " turns)\n");
        }
        if (m instanceof MultiTasker) {
            int ft = ((MultiTasker) m).getNormalSpeedTurns();
            if (ft > 0) sb.append("🎯 FOCUS MODE (" + ft + " turns)\n");
        }

        return sb.length() == 0 ? "✅ OPERATIONAL" : sb.toString().trim();
    }

    private String statusStyle(Monster m) {
        if (m.isFrozen())   return "-fx-font-size: 11px; -fx-text-fill: #5DADE2;";
        if (m.isConfused()) return "-fx-font-size: 11px; -fx-text-fill: #E74C3C;";
        boolean hasPositive = m.isShielded() ||
            (m instanceof Dasher && ((Dasher) m).getMomentumTurns() > 0) ||
            (m instanceof MultiTasker && ((MultiTasker) m).getNormalSpeedTurns() > 0);
        if (hasPositive)    return "-fx-font-size: 11px; -fx-text-fill: #F1C40F;";
        return "-fx-font-size: 11px; -fx-text-fill: #2ECC71;";
    }

    private void updateTurnIndicator() {
        Monster current = game.getCurrent();
        boolean isPlayer = current == game.getPlayer();
        boolean frozen   = current.isFrozen();

        if (frozen) {
            view.turnLbl.setText("❄️ " + current.getName().toUpperCase() + " — FROZEN (turn will skip)");
            view.turnLbl.setStyle(
                "-fx-font-family: 'Segoe UI Black';" +
                "-fx-font-size: 16px; -fx-text-fill: #5DADE2; -fx-letter-spacing: 1px;"
            );
        } else {
            String accent = isPlayer ? GameBoardView.CYAN : GameBoardView.PINK;
            view.turnLbl.setText(current.getName().toUpperCase() + "'S TURN");
            view.turnLbl.setStyle(
                "-fx-font-family: 'Segoe UI Black';" +
                "-fx-font-size: 18px; -fx-text-fill: " + accent + "; -fx-letter-spacing: 1.5px;"
            );
        }

        // In bot mode: only enable buttons when it's the human player's turn
        boolean humanTurn = !botMode || isPlayer;
        view.rollBtn.setDisable(!humanTurn);
        view.powerBtn.setDisable(!humanTurn);
    }

    private void updateDeckCount() {
        int remaining = Board.getCards().size();
        view.deckCountLbl.setText("🃏 " + remaining + " remaining");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Card drawn listener (called from engine thread during onLand)
    // ─────────────────────────────────────────────────────────────────────────

    private void onCardDrawn(Card card) {
        Platform.runLater(() -> {
            view.cardNameLbl.setText(card.getName());
            view.cardDescLbl.setText(card.getDescription());
            updateDeckCount();
            addLog("  🃏 Card: " + card.getName() + " — " + card.getDescription());
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Roll Dice handler
    // ─────────────────────────────────────────────────────────────────────────

    private void handleRoll() {
        view.rollBtn.setDisable(true);
        view.powerBtn.setDisable(true);

        Monster mover         = game.getCurrent();
        boolean wasFrozen     = mover.isFrozen();
        int     positionBefore = mover.getPosition();
        int     energyBefore   = mover.getEnergy();

        try {
            game.playTurn();

            int posAfter  = mover.getPosition();
            int enAfter   = mover.getEnergy();

            if (wasFrozen) {
                view.moveResultLbl.setText("❄️ " + mover.getName() + " frozen — turn skipped");
                addLog("[" + mover.getName() + "] FROZEN → turn skipped");
            } else {
                String moveStr = positionBefore + " → " + posAfter;
                view.moveResultLbl.setText("🎲 Move: " + moveStr);
                addLog("[" + mover.getName() + "] moved: cell " + moveStr);

                int energyDelta = enAfter - energyBefore;
                if (energyDelta != 0) {
                    String sign = energyDelta > 0 ? "+" : "";
                    addLog("  Energy: " + sign + energyDelta + " → " + enAfter);
                }
            }

            finishTurn();

        } catch (InvalidMoveException ex) {
            view.moveResultLbl.setText("🚫 Blocked — destination occupied! Roll again.");
            addLog("[" + mover.getName() + "] BLOCKED — occupied cell, roll again");
            view.rollBtn.setDisable(false);
            view.powerBtn.setDisable(false);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Power-Up handler
    // ─────────────────────────────────────────────────────────────────────────

    private void handlePowerUp() {
        Monster current = game.getCurrent();
        try {
            game.usePowerup();
            addLog("[" + current.getName() + "] activated Power-Up! (−500 energy)");
            updateStats();
        } catch (OutOfEnergyException ex) {
            showDialog("Power-Up Unavailable",
                current.getName() + " needs 500 energy to activate a power-up.\n" +
                "Current energy: " + current.getEnergy());
        }
    }

    private void showDialog(String header, String body) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Door Dash");

        Label headerLbl = new Label(header);
        headerLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 14px; -fx-text-fill: white;"
        );
        Label bodyLbl = new Label(body);
        bodyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");
        bodyLbl.setWrapText(true);
        bodyLbl.setMaxWidth(340);
        Button okBtn = new Button("OK");
        okBtn.setStyle(
            "-fx-background-color: #2ECC71; -fx-text-fill: #111424;" +
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px;" +
            "-fx-background-radius: 14; -fx-cursor: hand;"
        );
        okBtn.setPrefWidth(100);
        okBtn.setOnAction(e -> dialog.close());

        VBox box = new VBox(14, headerLbl, bodyLbl, okBtn);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #111424;");
        dialog.setScene(new Scene(box, 400, 170));
        dialog.show();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Post-turn logic
    // ─────────────────────────────────────────────────────────────────────────

    private void finishTurn() {
        renderBoard();
        updateStats();

        Monster winner = game.getWinner();
        if (winner != null) {
            SceneManager.getInstance().setWinner(winner);
            addLog("🏆 " + winner.getName() + " wins!");
            // Brief pause so the log message is visible before scene change
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(e -> SceneManager.getInstance().showGameOver());
            delay.play();
            return;
        }

        updateTurnIndicator();
        scheduleBot();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bot logic
    // ─────────────────────────────────────────────────────────────────────────

    private void scheduleBot() {
        if (!botMode) return;
        if (game.getCurrent() != game.getOpponent()) return;

        // Disable buttons while bot is thinking
        view.rollBtn.setDisable(true);
        view.powerBtn.setDisable(true);

        PauseTransition thinking = new PauseTransition(Duration.seconds(1.4));
        thinking.setOnFinished(e -> playBotTurn());
        thinking.play();
    }

    private void playBotTurn() {
        Monster bot = game.getCurrent(); // opponent
        addLog("[BOT " + bot.getName() + "] thinking…");

        // Bot uses power-up if it has enough energy (random 30 % chance)
        if (bot.getEnergy() >= 500 && Math.random() < 0.3) {
            try {
                game.usePowerup();
                addLog("[BOT " + bot.getName() + "] used Power-Up!");
                updateStats();
            } catch (OutOfEnergyException ignored) {}
        }

        boolean wasFrozen      = bot.isFrozen();
        int     positionBefore = bot.getPosition();
        int     energyBefore   = bot.getEnergy();

        try {
            game.playTurn();

            if (wasFrozen) {
                view.moveResultLbl.setText("❄️ BOT frozen — turn skipped");
                addLog("[BOT " + bot.getName() + "] FROZEN → skipped");
            } else {
                int posAfter = bot.getPosition();
                view.moveResultLbl.setText("🤖 BOT: " + positionBefore + " → " + posAfter);
                addLog("[BOT " + bot.getName() + "] moved: " + positionBefore + " → " + posAfter);

                int ed = bot.getEnergy() - energyBefore;
                if (ed != 0) addLog("  Energy: " + (ed > 0 ? "+" : "") + ed);
            }

            finishTurn();

        } catch (InvalidMoveException ex) {
            addLog("[BOT " + bot.getName() + "] BLOCKED — retrying…");
            // Retry after short delay (occupied cell)
            PauseTransition retry = new PauseTransition(Duration.seconds(0.8));
            retry.setOnFinished(e -> playBotTurn());
            retry.play();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void addLog(String msg) {
        view.gameLog.appendText(msg + "\n");
    }
}

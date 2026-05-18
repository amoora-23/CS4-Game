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
import java.util.HashMap;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameBoardController {

    private final GameBoardView view;
    private final Game          game;
    private final boolean       botMode;

    private int     prevPlayerEnergy;
    private int     prevOppEnergy;
    private boolean powerUpUsedThisTurn = false;

    // Shield state tracked before each turn to detect consumption
    private boolean playerWasShielded = false;
    private boolean oppWasShielded    = false;

    // Stationed monster energies captured before each turn
    private final Map<Monster, Integer> prevStationedEnergy = new HashMap<>();

    public GameBoardController(GameBoardView view) {
        this.view    = view;
        this.game    = SceneManager.getInstance().getGame();
        this.botMode = SceneManager.getInstance().isBotMode();

        prevPlayerEnergy = game.getPlayer().getEnergy();
        prevOppEnergy    = game.getOpponent().getEnergy();

        CardCell.setCardDrawnListener(this::onCardDrawn);

        renderBoard();
        updateStats();
        updateTurnIndicator();
        updateDeckCount();

        view.rollBtn.setOnAction(e  -> handleRoll());
        view.powerBtn.setOnAction(e -> handlePowerUp());

        scheduleBot();
        attachKeyHandlers();
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
            int boardRow   = idx / 10;
            int rawCol     = idx % 10;
            int boardCol   = (boardRow % 2 == 1) ? (9 - rawCol) : rawCol;
            int displayRow = 9 - boardRow;
            int displayCol = boardCol;

            Cell cell = cells[boardRow][boardCol];
            StackPane tile = buildTile(cell, idx, player, opp);
            view.boardGrid.add(tile, displayCol, displayRow);
        }
    }

    private StackPane buildTile(Cell cell, int index, Monster player, Monster opp) {
        StackPane tile = new StackPane();
        tile.setPrefSize(60, 60);
        tile.setMinSize(60, 60);
        tile.setMaxSize(60, 60);
        tile.setStyle(
            "-fx-background-color: " + cellColor(cell) + ";" +
            "-fx-border-color: #1A2518; -fx-border-width: 0.5;"
        );

        VBox content = new VBox(1);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(3));

        Label idxLbl = new Label(String.valueOf(index));
        idxLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.45);");
        content.getChildren().add(idxLbl);

        if (cell instanceof DoorCell) {
            DoorCell door = (DoorCell) cell;
            Label eLbl = new Label(String.valueOf(door.getEnergy()));
            eLbl.setStyle("-fx-font-size: 9px; -fx-text-fill: #D4C9A8;");
            Label rLbl = new Label(door.getRole() == Role.SCARER ? "S" : "L");
            rLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: rgba(255,255,255,0.6);");
            content.getChildren().addAll(eLbl, rLbl);
            if (door.isActivated()) {
                Label exh = new Label("✓");
                exh.setStyle("-fx-font-size: 8px; -fx-text-fill: #666;");
                content.getChildren().add(exh);
            }
        }
        if (cell instanceof MonsterCell) {
            MonsterCell mc = (MonsterCell) cell;
            String name = mc.getCellMonster().getName().split(" ")[0];
            Label mLbl = new Label(name);
            mLbl.setStyle("-fx-font-size: 8px; -fx-text-fill: #A8C4A8;");
            mLbl.setWrapText(true);
            content.getChildren().add(mLbl);
        }
        if (cell instanceof ConveyorBelt) {
            Label cLbl = new Label("▲");
            cLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #A9DFBF;");
            content.getChildren().add(cLbl);
        }
        if (cell instanceof ContaminationSock) {
            Label sLbl = new Label("▼");
            sLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #E6B87A;");
            content.getChildren().add(sLbl);
        }
        if (cell instanceof CardCell) {
            Label cLbl = new Label("🃏");
            cLbl.setStyle("-fx-font-size: 11px;");
            content.getChildren().add(cLbl);
        }

        tile.getChildren().add(content);

        // Player token — large, centered
        if (player.getPosition() == index) {
            Label token = new Label("P");
            token.setPrefSize(40, 40);
            token.setMinSize(40, 40);
            token.setMaxSize(40, 40);
            token.setAlignment(Pos.CENTER);
            token.setStyle(
                "-fx-background-color: " + GameBoardView.CYAN + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 15px;" +
                "-fx-background-radius: 5;"
            );
            StackPane.setAlignment(token, Pos.CENTER);
            tile.getChildren().add(token);
        }
        // Opponent token — large, centered
        if (opp.getPosition() == index) {
            Label token = new Label("O");
            token.setPrefSize(40, 40);
            token.setMinSize(40, 40);
            token.setMaxSize(40, 40);
            token.setAlignment(Pos.CENTER);
            token.setStyle(
                "-fx-background-color: " + GameBoardView.PINK + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 15px;" +
                "-fx-background-radius: 5;"
            );
            StackPane.setAlignment(token, Pos.CENTER);
            tile.getChildren().add(token);
        }

        // Cell detail popup on click
        tile.setOnMouseClicked(e -> {
            showCellInfo(cell, index);
            e.consume();
        });
        tile.setStyle(tile.getStyle() + "-fx-cursor: hand;");

        return tile;
    }

    private String cellColor(Cell cell) {
        if (cell instanceof DoorCell) {
            DoorCell d = (DoorCell) cell;
            if (d.isActivated()) return "#2A3020";
            return d.getRole() == Role.SCARER ? "#2D5A1A" : "#5C3A10";
        }
        if (cell instanceof CardCell)          return "#8B3500";
        if (cell instanceof ConveyorBelt)      return "#1A5A20";
        if (cell instanceof ContaminationSock) return "#8B4500";
        if (cell instanceof MonsterCell)       return "#1A4A30";
        return "#1F2E18";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Stats
    // ─────────────────────────────────────────────────────────────────────────

    private void updateStats() {
        Monster p   = game.getPlayer();
        Monster opp = game.getOpponent();

        updateMonsterPanel(p,
            view.p1NameLbl, view.p1TypeLbl, view.p1OrigRoleLbl, view.p1CurrRoleLbl,
            view.p1EnergyLbl, view.p1Bar, view.p1PosLbl, view.p1StatusLbl,
            prevPlayerEnergy);

        updateMonsterPanel(opp,
            view.p2NameLbl, view.p2TypeLbl, view.p2OrigRoleLbl, view.p2CurrRoleLbl,
            view.p2EnergyLbl, view.p2Bar, view.p2PosLbl, view.p2StatusLbl,
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

        if (m.isConfused()) {
            currRoleLbl.setText("CURR ROLE: " + m.getRole().name() + " ⚠ CONFUSED");
            currRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #C0754A;");
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

        if (delta != 0) showEnergyAnimation(m, delta);
    }

    private String buildStatusText(Monster m) {
        StringBuilder sb = new StringBuilder();
        if (m.isFrozen())   sb.append("❄ FROZEN\n");
        if (m.isShielded()) sb.append("🛡 SHIELDED\n");
        if (m.isConfused()) sb.append("⚠ CONFUSED (" + m.getConfusionTurns() + " turns)\n");
        if (m instanceof Dasher) {
            int mt = ((Dasher) m).getMomentumTurns();
            if (mt > 0) sb.append("MOMENTUM RUSH (" + mt + " turns)\n");
        }
        if (m instanceof MultiTasker) {
            int ft = ((MultiTasker) m).getNormalSpeedTurns();
            if (ft > 0) sb.append("FOCUS MODE (" + ft + " turns)\n");
        }
        return sb.length() == 0 ? "✅ OPERATIONAL" : sb.toString().trim();
    }

    private String statusStyle(Monster m) {
        if (m.isFrozen())   return "-fx-font-size: 11px; -fx-text-fill: #7EC8D8;";
        if (m.isConfused()) return "-fx-font-size: 11px; -fx-text-fill: #C0754A;";
        boolean positive = m.isShielded()
            || (m instanceof Dasher && ((Dasher) m).getMomentumTurns() > 0)
            || (m instanceof MultiTasker && ((MultiTasker) m).getNormalSpeedTurns() > 0);
        if (positive) return "-fx-font-size: 11px; -fx-text-fill: " + GameBoardView.GOLD + ";";
        return "-fx-font-size: 11px; -fx-text-fill: " + GameBoardView.GREEN + ";";
    }

    private void updateTurnIndicator() {
        powerUpUsedThisTurn = false;
        Monster current  = game.getCurrent();
        boolean isPlayer = current == game.getPlayer();
        boolean frozen   = current.isFrozen();

        if (frozen) {
            view.turnLbl.setText("❄ " + current.getName().toUpperCase() + " — FROZEN");
            view.turnLbl.setStyle(
                "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px;" +
                "-fx-text-fill: #7EC8D8; -fx-letter-spacing: 1px;"
            );
        } else {
            String accent = isPlayer ? GameBoardView.CYAN : GameBoardView.PINK;
            view.turnLbl.setText(current.getName().toUpperCase() + "'S TURN");
            view.turnLbl.setStyle(
                "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 18px;" +
                "-fx-text-fill: " + accent + "; -fx-letter-spacing: 1.5px;"
            );
        }

        boolean humanTurn = !botMode || isPlayer;
        view.rollBtn.setDisable(!humanTurn);
        view.powerBtn.setDisable(!humanTurn);
    }

    private void updateDeckCount() {
        view.deckCountLbl.setText("🃏 " + Board.getCards().size() + " remaining");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Card drawn listener
    // ─────────────────────────────────────────────────────────────────────────

    private void onCardDrawn(Card card) {
        Platform.runLater(() -> {
            view.cardNameLbl.setText(card.getName());
            view.cardDescLbl.setText(card.getDescription());
            updateDeckCount();
            addLog("  🃏 " + card.getName() + " — " + card.getDescription());
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Roll handler
    // ─────────────────────────────────────────────────────────────────────────

    private void handleRoll() {
        view.rollBtn.setDisable(true);
        view.powerBtn.setDisable(true);

        Monster mover = game.getCurrent();
        captureShieldState();
        captureStationedEnergies();
        boolean wasFrozen    = mover.isFrozen();
        int     posBefore    = mover.getPosition();
        int     energyBefore = mover.getEnergy();

        try {
            game.playTurn();

            if (wasFrozen) {
                view.moveResultLbl.setText("❄ " + mover.getName() + " — frozen, turn skipped");
                addLog("[" + mover.getName() + "] FROZEN → skipped");
            } else {
                int posAfter = mover.getPosition();
                view.moveResultLbl.setText("Move: " + posBefore + " → " + posAfter);
                addLog("[" + mover.getName() + "] cell " + posBefore + " → " + posAfter);
                int ed = mover.getEnergy() - energyBefore;
                if (ed != 0) addLog("  Energy: " + (ed > 0 ? "+" : "") + ed + " → " + mover.getEnergy());
            }

            finishTurn();

        } catch (InvalidMoveException ex) {
            view.moveResultLbl.setText("Blocked — destination occupied. Roll again.");
            addLog("[" + mover.getName() + "] BLOCKED — roll again");
            view.rollBtn.setDisable(false);
            view.powerBtn.setDisable(!(!botMode || mover == game.getPlayer()) || powerUpUsedThisTurn);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Power-Up handler  (once per turn)
    // ─────────────────────────────────────────────────────────────────────────

    private void handlePowerUp() {
        if (powerUpUsedThisTurn) return;
        Monster current = game.getCurrent();
        try {
            game.usePowerup();
            powerUpUsedThisTurn = true;
            view.powerBtn.setDisable(true);
            addLog("[" + current.getName() + "] Power-Up activated  (−500 energy)");
            updateStats();
        } catch (OutOfEnergyException ex) {
            showDialog("Power-Up Unavailable",
                current.getName() + " needs 500 energy.\nCurrent: " + current.getEnergy());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Post-turn
    // ─────────────────────────────────────────────────────────────────────────

    private void finishTurn() {
        checkShieldAnimations();
        checkStationedMonsterAnimations();
        renderBoard();
        updateStats();

        Monster winner = game.getWinner();
        if (winner != null) {
            SceneManager.getInstance().setWinner(winner);
            addLog("🏆 " + winner.getName() + " wins!");
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(e -> SceneManager.getInstance().showGameOver());
            delay.play();
            return;
        }

        updateTurnIndicator();
        scheduleBot();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Bot
    // ─────────────────────────────────────────────────────────────────────────

    private void scheduleBot() {
        if (!botMode) return;
        if (game.getCurrent() != game.getOpponent()) return;
        view.rollBtn.setDisable(true);
        view.powerBtn.setDisable(true);
        PauseTransition thinking = new PauseTransition(Duration.seconds(1.4));
        thinking.setOnFinished(e -> playBotTurn());
        thinking.play();
    }

    private void playBotTurn() {
        Monster bot = game.getCurrent();
        addLog("[BOT " + bot.getName() + "] thinking…");

        if (bot.getEnergy() >= 500 && Math.random() < 0.3) {
            try {
                game.usePowerup();
                addLog("[BOT " + bot.getName() + "] used Power-Up!");
                updateStats();
            } catch (OutOfEnergyException ignored) {}
        }

        captureShieldState();
        captureStationedEnergies();
        boolean wasFrozen = bot.isFrozen();
        int posBefore     = bot.getPosition();
        int energyBefore  = bot.getEnergy();

        try {
            game.playTurn();

            if (wasFrozen) {
                view.moveResultLbl.setText("❄ BOT frozen — skipped");
                addLog("[BOT " + bot.getName() + "] FROZEN → skipped");
            } else {
                int posAfter = bot.getPosition();
                view.moveResultLbl.setText("BOT: " + posBefore + " → " + posAfter);
                addLog("[BOT " + bot.getName() + "] " + posBefore + " → " + posAfter);
                int ed = bot.getEnergy() - energyBefore;
                if (ed != 0) addLog("  Energy: " + (ed > 0 ? "+" : "") + ed);
            }

            finishTurn();

        } catch (InvalidMoveException ex) {
            addLog("[BOT " + bot.getName() + "] BLOCKED — retrying…");
            PauseTransition retry = new PauseTransition(Duration.seconds(0.8));
            retry.setOnFinished(e -> playBotTurn());
            retry.play();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Animations
    // ─────────────────────────────────────────────────────────────────────────

    private void captureShieldState() {
        playerWasShielded = game.getPlayer().isShielded();
        oppWasShielded    = game.getOpponent().isShielded();
    }

    private void checkShieldAnimations() {
        if (playerWasShielded && !game.getPlayer().isShielded())
            showShieldAnimation(game.getPlayer());
        if (oppWasShielded && !game.getOpponent().isShielded())
            showShieldAnimation(game.getOpponent());
    }

    private void captureStationedEnergies() {
        prevStationedEnergy.clear();
        Cell[][] cells = game.getBoard().getBoardCells();
        for (Cell[] row : cells)
            for (Cell c : row)
                if (c instanceof MonsterCell) {
                    Monster m = ((MonsterCell) c).getCellMonster();
                    if (m != null) prevStationedEnergy.put(m, m.getEnergy());
                }
    }

    private void checkStationedMonsterAnimations() {
        Cell[][] cells = game.getBoard().getBoardCells();
        for (Cell[] row : cells)
            for (Cell c : row)
                if (c instanceof MonsterCell) {
                    Monster m = ((MonsterCell) c).getCellMonster();
                    if (m == null) continue;
                    Integer prev = prevStationedEnergy.get(m);
                    if (prev == null) continue;
                    int delta = m.getEnergy() - prev;
                    if (delta != 0) {
                        showEnergyAnimation(m, delta);
                        String sign = delta > 0 ? "+" : "";
                        addLog("  [" + m.getName() + "] stationed " + sign + delta + " → " + m.getEnergy());
                    }
                }
    }

    private void showEnergyAnimation(Monster m, int delta) {
        double[] pos = overlayPos(m.getPosition());
        String sign  = delta > 0 ? "+" : "";
        String color = delta > 0 ? "#5DBB63" : "#D4735A";

        Label lbl = new Label(sign + delta);
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-background-color: #0A1207;" +
            "-fx-padding: 1 4; -fx-background-radius: 3;"
        );
        lbl.setLayoutX(pos[0]);
        lbl.setLayoutY(pos[1]);
        view.boardOverlay.getChildren().add(lbl);

        TranslateTransition tt = new TranslateTransition(Duration.millis(950), lbl);
        tt.setByY(-36);
        FadeTransition ft = new FadeTransition(Duration.millis(950), lbl);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> view.boardOverlay.getChildren().remove(lbl));
        pt.play();
    }

    private void showShieldAnimation(Monster m) {
        double[] pos = overlayPos(m.getPosition());
        Label lbl = new Label("🛡 BLOCKED");
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 11px;" +
            "-fx-text-fill: " + GameBoardView.GOLD + ";" +
            "-fx-background-color: #0A1207;" +
            "-fx-padding: 1 4; -fx-background-radius: 3;"
        );
        lbl.setLayoutX(pos[0] - 10);
        lbl.setLayoutY(pos[1]);
        view.boardOverlay.getChildren().add(lbl);

        TranslateTransition tt = new TranslateTransition(Duration.millis(1300), lbl);
        tt.setByY(-32);
        FadeTransition ft = new FadeTransition(Duration.millis(1300), lbl);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ParallelTransition pt = new ParallelTransition(tt, ft);
        pt.setOnFinished(e -> view.boardOverlay.getChildren().remove(lbl));
        pt.play();
    }

    /** Returns top-left pixel position of a cell on the boardOverlay Pane. */
    private double[] overlayPos(int idx) {
        int boardRow   = idx / 10;
        int rawCol     = idx % 10;
        int boardCol   = (boardRow % 2 == 1) ? (9 - rawCol) : rawCol;
        int displayRow = 9 - boardRow;
        int displayCol = boardCol;
        // 6px grid padding + 61px per cell (60 cell + 1 gap)
        return new double[]{ 6 + displayCol * 61 + 8, 6 + displayRow * 61 + 4 };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cell info popup
    // ─────────────────────────────────────────────────────────────────────────

    private void showCellInfo(Cell cell, int idx) {
        view.cellInfoTitle.setText("CELL  " + idx);
        view.cellInfoType.setText(cellTypeName(cell));
        view.cellInfoDetails.setText(buildCellDetails(cell));
        view.cellInfoPopup.setVisible(true);
        view.cellInfoPopup.toFront();
    }

    private String cellTypeName(Cell cell) {
        if (cell instanceof DoorCell)
            return ((DoorCell) cell).getRole() == Role.SCARER ? "SCARER DOOR" : "LAUGHER DOOR";
        if (cell instanceof CardCell)          return "CARD CELL";
        if (cell instanceof ConveyorBelt)      return "CONVEYOR BELT";
        if (cell instanceof ContaminationSock) return "CONTAMINATION SOCK";
        if (cell instanceof MonsterCell)       return "MONSTER CELL";
        return "NORMAL CELL";
    }

    private String buildCellDetails(Cell cell) {
        if (cell instanceof DoorCell) {
            DoorCell d = (DoorCell) cell;
            int e = d.getEnergy();
            return d.getRole().name() + " team effect\n" +
                   (e >= 0 ? "+" : "") + e + " energy to whole team\n" +
                   "Status: " + (d.isActivated() ? "USED" : "ACTIVE");
        }
        if (cell instanceof CardCell)
            return "Draw from Factory Deck\n" + Board.getCards().size() + " cards remaining";
        if (cell instanceof ConveyorBelt)
            return "Advances you forward\nalong the corridor";
        if (cell instanceof ContaminationSock)
            return "Sends you backward\nand drains 100 energy";
        if (cell instanceof MonsterCell) {
            Monster m = ((MonsterCell) cell).getCellMonster();
            if (m != null)
                return "Stationed: " + m.getName() +
                       "\nRole: " + m.getOriginalRole().name() +
                       "\nEnergy: " + m.getEnergy();
            return "Monster stationed here";
        }
        return "Standard corridor\nNo special effect";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Cheat keys  (W = goto cell 99 | E = +100 energy)
    // ─────────────────────────────────────────────────────────────────────────

    private void attachKeyHandlers() {
        view.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.W)      cheatGotoCell99();
            else if (e.getCode() == KeyCode.E) cheatAddEnergy();
        });
    }

    private void cheatGotoCell99() {
        Monster current = game.getCurrent();
        current.setPosition(99);
        addLog("[CHEAT W] " + current.getName() + " → cell 99");
        renderBoard();
        updateStats();
        Monster winner = game.getWinner();
        if (winner != null) {
            SceneManager.getInstance().setWinner(winner);
            addLog("🏆 " + winner.getName() + " wins!");
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(ev -> SceneManager.getInstance().showGameOver());
            delay.play();
        } else {
            updateTurnIndicator();
            addLog("[CHEAT W] Need >= 1000 energy (now: " + current.getEnergy() + ")");
        }
    }

    private void cheatAddEnergy() {
        Monster current = game.getCurrent();
        current.setEnergy(current.getEnergy() + 100);
        addLog("[CHEAT E] " + current.getName() + " +100 → " + current.getEnergy());
        updateStats();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Dialog helper
    // ─────────────────────────────────────────────────────────────────────────

    private void showDialog(String header, String body) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Door Dash");

        Label headerLbl = new Label(header);
        headerLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px; -fx-text-fill: #D4C9A8;"
        );
        Label bodyLbl = new Label(body);
        bodyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + GameBoardView.GRAY + ";");
        bodyLbl.setWrapText(true);
        bodyLbl.setMaxWidth(340);

        Button okBtn = new Button("OK");
        okBtn.setStyle(
            "-fx-background-color: " + GameBoardView.GREEN + "; -fx-text-fill: " + GameBoardView.BG + ";" +
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px;" +
            "-fx-background-radius: 14; -fx-cursor: hand;"
        );
        okBtn.setPrefWidth(100);
        okBtn.setOnAction(e -> dialog.close());

        VBox box = new VBox(14, headerLbl, bodyLbl, okBtn);
        box.setPadding(new Insets(24));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: " + GameBoardView.BG + ";");
        dialog.setScene(new Scene(box, 400, 170));
        dialog.show();
    }

    private void addLog(String msg) {
        view.gameLog.appendText(msg + "\n");
    }
}

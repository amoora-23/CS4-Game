package game.gui.controllers;

import java.io.IOException;

import game.engine.Game;
import game.engine.Role;
import game.gui.SceneManager;
import game.gui.views.StartView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StartController {

    private final StartView view;

    public StartController(StartView view) {
        this.view = view;
        attachHandlers();
    }

    // ── Event wiring ──────────────────────────────────────────────────────────

    private void attachHandlers() {
        view.startBtn.setOnAction(e -> handleStart());
        view.instructionsBtn.setOnAction(e -> handleInstructions());
    }

    // ── Handlers ──────────────────────────────────────────────────────────────

    private void handleStart() {
        if (view.factionGroup.getSelectedToggle() == null) {
            showDialog("No Faction Selected",
                "Please choose a faction (SCARER or LAUGHER) before starting.");
            return;
        }

        Role playerRole = view.scarerBtn.isSelected() ? Role.SCARER : Role.LAUGHER;
        boolean botMode = view.botModeBtn.isSelected();

        try {
            Game game = new Game(playerRole);
            SceneManager sm = SceneManager.getInstance();
            sm.setGame(game);
            sm.setBotMode(botMode);
            sm.showVersusScreen();
        } catch (IOException ex) {
            showDialog("Load Error",
                "Could not load game data: " + ex.getMessage() +
                "\nMake sure cards.csv, cells.csv, and monsters.csv are present.");
        }
    }

    private void handleInstructions() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Game Instructions");

        TextArea ta = new TextArea(
            "OBJECTIVE\n" +
            "  Reach cell 99 (Boo's Door) with at least 1000 energy.\n\n" +
            "TURN SEQUENCE\n" +
            "  1. Optionally activate your Power-Up (costs 500 energy).\n" +
            "  2. Roll the dice and move forward.\n" +
            "  3. If the target cell is occupied, roll again.\n\n" +
            "CELL TYPES\n" +
            "  Purple  — SCARER Door   Blue  — LAUGHER Door\n" +
            "  Red     — Card Cell     Green — Conveyor Belt (move forward)\n" +
            "  Orange  — Sock Cell (move back, −100 energy)\n" +
            "  Blue    — Monster Cell  Cream — Normal Cell\n\n" +
            "DOORS\n" +
            "  Match your role → entire team gains energy.\n" +
            "  Mismatch        → entire team loses energy.\n" +
            "  Each door activates only once.\n\n" +
            "MONSTER TYPES\n" +
            "  Dasher      — 2x speed; Power-Up: 3x speed for 3 turns.\n" +
            "  Dynamo      — 2x energy gains & losses; Power-Up: freeze opponent.\n" +
            "  MultiTasker — Half speed, +200 energy bonus; Power-Up: normal speed for 2 turns.\n" +
            "  Schemer     — +10 on all changes; Power-Up: steal 10 energy from everyone.\n\n" +
            "CARDS\n" +
            "  Position Swap  — Swap if behind.\n" +
            "  Energy Steal   — Take 50/100/150 from opponent.\n" +
            "  Start Over     — Send you or opponent back to cell 0.\n" +
            "  Super Shield   — Block the next negative effect.\n" +
            "  Confusion      — Swap roles for 2-3 turns.\n\n" +
            "WIN CONDITION\n" +
            "  First to land on cell 99 with >= 1000 energy wins!"
        );
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(500, 380);
        ta.setStyle(
            "-fx-control-inner-background: #0b0d19;" +
            "-fx-text-fill: #94A3B8;" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 12px;"
        );

        Label header = new Label("DooR DasH — How to Play");
        header.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 15px;" +
            "-fx-text-fill: #F1C40F;"
        );

        Button closeBtn = new Button("CLOSE");
        closeBtn.setStyle(
            "-fx-background-color: #2ECC71; -fx-text-fill: #111424;" +
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px;" +
            "-fx-background-radius: 14; -fx-cursor: hand;"
        );
        closeBtn.setPrefWidth(120);
        closeBtn.setOnAction(e -> dialog.close());

        VBox box = new VBox(12, header, ta, closeBtn);
        box.setPadding(new Insets(20));
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-color: #111424;");

        dialog.setScene(new Scene(box, 540, 460));
        dialog.show();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
        bodyLbl.setMaxWidth(360);

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

        dialog.setScene(new Scene(box, 420, 180));
        dialog.show();
    }
}

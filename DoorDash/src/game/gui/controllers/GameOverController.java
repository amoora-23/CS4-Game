package game.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

import game.engine.Game;
import game.engine.monsters.Dasher;
import game.engine.monsters.Dynamo;
import game.engine.monsters.Monster;
import game.engine.monsters.MultiTasker;
import game.engine.monsters.Schemer;

public class GameOverController {

    @FXML private Label winnerNameLabel;
    @FXML private Label winnerRoleLabel;
    @FXML private Label winnerEnergyLabel;

    @FXML private Label player1FinalLabel;
    @FXML private Label player2FinalLabel;

    @FXML private Label resultMessageLabel;
    @FXML private Button restartBtn;
    @FXML private Button exitBtn;

    // FIX #4: Extra labels for personalised winner display
    @FXML private Label winnerTaglineLabel;
    @FXML private Label winnerBannerLabel;
    @FXML private VBox  winnerHighlightBox;

    // Held for restart navigation
    private Game engine;

    @FXML
    public void initialize() {
        styleButton(restartBtn, "#3498db");
        styleButton(exitBtn, "#e74c3c");
    }

    /**
     * FIX #4: New overloaded entry point — takes explicit winner reference.
     * Called by GameBoardController with the actual winning Monster.
     */
    public void setEndgameState(Game engine, Monster winner) {
        this.engine = engine;
        if (engine == null) return;

        Monster player   = engine.getPlayer();
        Monster opponent = engine.getOpponent();

        // ── Personalise based on winner type ────────────────────────────────
        applyWinnerPersonalisation(winner);

        // Winner block
        winnerNameLabel.setText("🏆  " + winner.getName().toUpperCase());
        winnerRoleLabel.setText("ROLE: " + winner.getOriginalRole().name()
                + "   |   TYPE: " + getTypeName(winner));
        winnerEnergyLabel.setText("Final Energy: " + winner.getEnergy());

        // Both monsters
        player1FinalLabel.setText(player.getName() + ":  " + player.getEnergy() + " energy"
                + (player == winner ? "  🏆" : ""));
        player2FinalLabel.setText(opponent.getName() + ":  " + opponent.getEnergy() + " energy"
                + (opponent == winner ? "  🏆" : ""));

        resultMessageLabel.setText("Reached cell 99 with ≥ 1000 energy — TOUCHDOWN!");
    }

    /**
     * Legacy no-winner overload (called from old code paths or when winner is unknown).
     * Delegates to the new method, picking whichever monster met the win condition.
     */
    public void setEndgameState(Game engine) {
        if (engine == null) return;
        Monster winner = engine.getWinner();
        if (winner == null) winner = engine.getPlayer(); // fallback
        setEndgameState(engine, winner);
    }

    // ── FIX #4: Winner personalisation ──────────────────────────────────────────
    /**
     * Applies character-specific colours, taglines, and style accents to the
     * win screen based on who won. All changes are CSS-only (no FXML edits needed
     * for basic personalisation; the label content drives the difference).
     */
    private void applyWinnerPersonalisation(Monster winner) {
        String accentColor;
        String bgGlow;
        String tagline;
        String banner;
        String borderColor;

        if (winner instanceof Dasher) {
            accentColor = "#00f2fe";
            bgGlow      = "#00f2fe20";
            tagline     = "💨 Speed was the key — no one could keep up!";
            banner      = "⚡ MOMENTUM CHAMPION";
            borderColor = "#00f2fe";
        } else if (winner instanceof Dynamo) {
            accentColor = "#e74c3c";
            bgGlow      = "#e74c3c20";
            tagline     = "🔥 Raw power and dominance — the floor shook!";
            banner      = "🔥 POWERHOUSE VICTOR";
            borderColor = "#e74c3c";
        } else if (winner instanceof MultiTasker) {
            accentColor = "#2ecc71";
            bgGlow      = "#2ecc7120";
            tagline     = "🛠️ Efficiency and focus — every move counted!";
            banner      = "🎯 STRATEGIC MASTER";
            borderColor = "#2ecc71";
        } else if (winner instanceof Schemer) {
            accentColor = "#9b59b6";
            bgGlow      = "#9b59b620";
            tagline     = "🧠 Cunning triumphed — the plan worked perfectly!";
            banner      = "🧠 GRAND SCHEMER WINS";
            borderColor = "#9b59b6";
        } else {
            accentColor = "#f1c40f";
            bgGlow      = "#f1c40f20";
            tagline     = "Victory belongs to the bold!";
            banner      = "🏆 WINNER";
            borderColor = "#f1c40f";
        }

        // Apply accent colour to the winner name
        winnerNameLabel.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 32px; " +
            "-fx-text-fill: " + accentColor + ";"
        );

        // Apply to the winner highlight box border
        if (winnerHighlightBox != null) {
            winnerHighlightBox.setStyle(
                "-fx-background-color: #0b0d19; -fx-padding: 28; " +
                "-fx-background-radius: 12; -fx-border-color: " + borderColor + "; " +
                "-fx-border-width: 2; -fx-border-radius: 12; -fx-min-width: 500;"
            );
        }

        // Set personalised tagline and banner
        if (winnerTaglineLabel != null) {
            winnerTaglineLabel.setText(tagline);
            winnerTaglineLabel.setStyle(
                "-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; " +
                "-fx-text-fill: " + accentColor + "; -fx-font-style: italic;"
            );
        }

        if (winnerBannerLabel != null) {
            winnerBannerLabel.setText(banner);
            winnerBannerLabel.setStyle(
                "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; " +
                "-fx-text-fill: " + accentColor + "; -fx-letter-spacing: 2px;"
            );
        }

        // Style the restart button with the winner's accent colour
        styleButton(restartBtn, accentColor);
    }

    @FXML
    private void handleReturnToMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/gui/views/StartScreen.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            String css = getClass().getResource("/game/gui/views/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to reload StartScreen: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }

    private void styleButton(Button btn, String color) {
        if (btn == null) return;
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                     "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px; " +
                     "-fx-background-radius: 25; -fx-cursor: hand;");
        btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
        btn.setOnMouseExited(e -> btn.setOpacity(1.0));
    }

    private String getTypeName(Monster m) {
        if (m instanceof Dasher)      return "Dasher";
        if (m instanceof Dynamo)      return "Dynamo";
        if (m instanceof MultiTasker) return "MultiTasker";
        if (m instanceof Schemer)     return "Schemer";
        return "Monster";
    }

    @FXML
    private void handleCloseWindow() {
        Stage stage = (Stage) restartBtn.getScene().getWindow();
        stage.close();
    }
}

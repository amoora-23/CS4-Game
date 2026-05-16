package game.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

import game.engine.Game;
import game.engine.monsters.Monster;

public class GameOverController {

    @FXML private Label winnerNameLabel;
    @FXML private Label winnerRoleLabel;
    @FXML private Label winnerEnergyLabel;

    // Both monsters' final energies
    @FXML private Label player1FinalLabel;
    @FXML private Label player2FinalLabel;

    @FXML private Label resultMessageLabel;
    @FXML private Button restartBtn;
    @FXML private Button exitBtn;

    @FXML
    public void initialize() {
        styleButton(restartBtn, "#3498db");
        styleButton(exitBtn, "#e74c3c");
    }

    public void setEndgameState(Game engine) {
        if (engine == null) return;

        Monster winner   = engine.getWinner();
        Monster player   = engine.getPlayer();
        Monster opponent = engine.getOpponent();

        if (winner != null) {
            winnerNameLabel.setText("🏆  " + winner.getName().toUpperCase());
            winnerRoleLabel.setText("ROLE: " + winner.getOriginalRole().name()
                    + "   |   TYPE: " + getTypeName(winner));
            winnerEnergyLabel.setText("Final Energy: " + winner.getEnergy());
        }

        // Both monsters
        player1FinalLabel.setText(player.getName() + ":  " + player.getEnergy() + " energy"
                + (player == winner ? "  🏆" : ""));
        player2FinalLabel.setText(opponent.getName() + ":  " + opponent.getEnergy() + " energy"
                + (opponent == winner ? "  🏆" : ""));

        resultMessageLabel.setText("Reached cell 99 with ≥ 1000 energy — TOUCHDOWN!");
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
        if (m instanceof game.engine.monsters.Dasher)      return "Dasher";
        if (m instanceof game.engine.monsters.Dynamo)      return "Dynamo";
        if (m instanceof game.engine.monsters.MultiTasker) return "MultiTasker";
        if (m instanceof game.engine.monsters.Schemer)     return "Schemer";
        return "Monster";
    }
}

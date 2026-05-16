package game.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

import game.engine.Game;

public class GameOverController {

    // These names must match your FXML fx:id parameters perfectly!
    @FXML private Label winnerNameLabel;
    @FXML private Label finalEnergyLabel;
    @FXML private Label resultMessageLabel;
    @FXML private Button restartBtn;
    @FXML private Button exitBtn;

    @FXML
    public void initialize() {
        // Safe styling wrapper with null checks
        if (restartBtn != null) {
            restartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px; -fx-background-radius: 25; -fx-cursor: hand;");
            restartBtn.setOnMouseEntered(e -> restartBtn.setStyle("-fx-background-color: #00f2fe; -fx-text-fill: #111424; -fx-background-radius: 25;"));
            restartBtn.setOnMouseExited(e -> restartBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: #111424; -fx-background-radius: 25;"));
        }
    }

    /**
     * Handshake method to pull winner statistics out of your game engine
     */
    public void setEndgameState(Game engine) {
        if (engine != null && engine.getWinner() != null) {
            winnerNameLabel.setText(engine.getWinner().getName().toUpperCase());
            finalEnergyLabel.setText("Final Energy: " + engine.getWinner().getEnergy());
            resultMessageLabel.setText("Touchdown target achieved successfully!");
        }
    }

    /**
     * Matches onAction="#handleReturnToMenu" from your FXML
     */
    @FXML
    private void handleReturnToMenu(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/game/gui/views/StartScreen.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreen(true); 
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Failed to reload StartScreen FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Matches onAction="#handleExit" from your FXML
     */
    @FXML
    private void handleExit(ActionEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
}
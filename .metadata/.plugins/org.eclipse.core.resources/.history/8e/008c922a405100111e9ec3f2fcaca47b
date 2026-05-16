package game.gui.controllers;

	import javafx.fxml.FXML;
	import javafx.fxml.FXMLLoader;
	import javafx.scene.Node;
	import javafx.scene.Parent;
	import javafx.scene.Scene;
	import javafx.scene.control.Label;
	import javafx.stage.Stage;
	import javafx.event.ActionEvent;
	import game.engine.monsters.Monster;

	public class GameOverController {

	    // Must match fx:id in GameOver.fxml
	    @FXML private Label winnerNameLabel;
	    @FXML private Label finalEnergyLabel;
	    @FXML private Label resultMessageLabel;

	    /**
	     * Handshake method to display the winner's stats.
	     */
	    public void setWinnerData(Monster winner) {
	        winnerNameLabel.setText(winner.getName() + " Wins!");
	        finalEnergyLabel.setText("Final Energy: " + winner.getEnergy());
	        
	        // Custom message based on role [cite: 83]
	        if (winner.getOriginalRole().toString().equals("SCARER")) {
	            resultMessageLabel.setText("The Scare Floor is yours!");
	        } else {
	            resultMessageLabel.setText("The world is powered by laughter!");
	        }
	    }

	    /**
	     * Resets the game by returning to the Start Screen.
	     */
	    @FXML
	    private void handlePlayAgain(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/gui/views/StartScreen.fxml"));
	            Parent root = loader.load();
	            
	            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
	            stage.setScene(new Scene(root));
	            stage.show();
	        } catch (Exception e) {
	            System.out.println("Error returning to start: " + e.getMessage());
	        }
	    }

	    /**
	     * Closes the application.
	     */
	    @FXML
	    private void handleExit() {
	        System.exit(0);
	    }
	
	
}
	

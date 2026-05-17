package game.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.event.ActionEvent;

import java.io.IOException;

import game.engine.Game;
import game.engine.Role;
import game.engine.monsters.*;
import game.gui.controllers.*;

public class StartController {
	Game gameEngine;

    // Main Layout Panes
    @FXML private VBox mainMenuPane;
    @FXML private VBox briefingPane;

    // Form inputs & drop-downs
    @FXML private ComboBox<String> gameModeBox;
    @FXML private RadioButton scarerBtn;
    @FXML private RadioButton laugherBtn;
    @FXML private Button startBtn;
    @FXML private Button howToPlayBtn;

    // Briefing Screen Fields
    @FXML private Label lblUserName;
    @FXML private Label lblUserType;
    @FXML private Label lblUserEnergy;
    @FXML private Label lblOppName;
    @FXML private Label lblOppType;
    @FXML private Label lblOppEnergy;
    @FXML private Button commenceMatchBtn;

    @FXML
    public void initialize() {
        scarerBtn.setSelected(true);
        gameModeBox.getSelectionModel().selectFirst();

        // Style the Menu Controls
        startBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px; -fx-background-radius: 25; -fx-cursor: hand;");
        startBtn.setOnMouseEntered(e -> startBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px; -fx-background-radius: 25;"));
        startBtn.setOnMouseExited(e -> startBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px; -fx-background-radius: 25;"));

        // Style the Transition Launch Button
        commenceMatchBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px; -fx-background-radius: 25; -fx-cursor: hand;");
        commenceMatchBtn.setOnMouseEntered(e -> commenceMatchBtn.setStyle("-fx-background-color: #d4ac0d; -fx-text-fill: white;"));
        commenceMatchBtn.setOnMouseExited(e -> commenceMatchBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #111424;"));
    }

    /**
     * MODIFIED HANDLE START: Intercepts selection data, populates briefing stats,
     * and displays the transition pane full-screen without leaving the file.
     */
    @FXML
    private void handleStartGame(ActionEvent event) {
        try{
        	Role selectedRole = scarerBtn.isSelected() ? Role.SCARER : Role.LAUGHER;
            boolean isScarerSelected = scarerBtn.isSelected();
            gameEngine = new Game(selectedRole);

            // Populate dynamic values based on the choice
            	Monster m = gameEngine.getPlayer();
                String s = "";
                if(m instanceof Dasher){
                	s = "Dasher";
                }
                else if(m instanceof Dynamo){
                	s = "Dynamo";
                }
                else if(m instanceof MultiTasker){
                	s = "MultiTasker";
                }
                else{
                	s = "Schemer";
                }
                lblUserName.setText(m.getName());
                lblUserType.setText("ARCHETYPE CLASS: "+ s);
                lblUserEnergy.setText("INITIAL POWER RESERVES: "+m.getEnergy());

                m = gameEngine.getOpponent();
                s = "";
                if(m instanceof Dasher){
                	s = "Dasher";
                }
                else if(m instanceof Dynamo){
                	s = "Dynamo";
                }
                else if(m instanceof MultiTasker){
                	s = "MultiTasker";
                }
                else{
                	s = "Schemer";
                }
                lblOppName.setText(m.getName());
                lblOppType.setText("ARCHETYPE CLASS: "+ s);
                lblOppEnergy.setText("INITIAL POWER RESERVES: "+ m.getEnergy());

            // SWAP PANES AT THE WINDOW ROOT LEVEL
            mainMenuPane.setVisible(false);
            mainMenuPane.setManaged(false);

            briefingPane.setVisible(true);
            briefingPane.setManaged(true);
        }
        catch (IOException e) {
            System.err.println("Error loading game data or FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * NEW ACTION METHOD: Fires when "COMMENCE MATCH SEQUENCE" is triggered, 
     * running your exact untouched scene transition loop.
     */
    @FXML
    private void handleCommenceMatch(ActionEvent event) {
        try {
            // Your original structural compilation sequence:

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/game/gui/views/GameBoard.fxml"));
            Parent root = loader.load();

            GameBoardController boardController = loader.getController();
            boardController.setGameEngine(gameEngine);

            boolean isVsBot = gameModeBox.getValue().toUpperCase().contains("BOT");
            boardController.setBotMode(isVsBot);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            
            String css = getClass().getResource("/game/gui/views/style.css").toExternalForm();
            scene.getStylesheets().add(css);
            
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading game data or FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showInstructions() {
        // 1. Create a clean, custom frameless window stage
        Stage stagePopup = new Stage();
        stagePopup.initModality(Modality.APPLICATION_MODAL); // Locks focus to this window
        stagePopup.initStyle(StageStyle.UNDECORATED);       // Strips standard OS borders

        // 2. Window Header Title
        Label headerLabel = new Label("DOOR DASH — MISSION OPERATIONAL MANUAL");
        headerLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; -fx-text-fill: #f1c40f; -fx-letter-spacing: 1.5px;");

        // 3. Instruction Content Manual text
        TextArea instructionsText = new TextArea();
        instructionsText.setEditable(false);
        instructionsText.setWrapText(true);
        instructionsText.setPrefHeight(280);
        instructionsText.setPrefWidth(460);
        
        // Custom styled text area matching your dashboard log feed terminal colors
        instructionsText.setStyle(
            "-fx-control-inner-background: #0b0d19; " +
            "-fx-text-fill: #2ecc71; " +
            "-fx-font-family: 'Consolas', monospace; " +
            "-fx-font-size: 12px; " +
            "-fx-background-color: transparent; " +
            "-fx-border-color: #3498db; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 4;"
        );

        // Write your clear milestone rules here
        instructionsText.setText(
            "» OBJECTIVE:\n" +
            "  Race through the factory floor grid and achieve a Touchdown at cell 99\n" +
            "  with at least 1,000 Energy Units accumulated.\n\n" +
            "» SYSTEM CONTROLS:\n" +
            "  1. Click [ROLL DICE] to advance your token across the board matrix.\n" +
            "  2. Click [USE POWER-UP] to execute dynamic tactical abilities.\n\n" +
            "» FACTION RULES:\n" +
            "  - DASHER  : Highly agile, specializes in movement speed buffs.\n" +
            "  - SCHEMER : Calculation expert, specializes in resource control.\n\n" +
            "» GRID HAZARDS:\n" +
            "  - Activated Door Cells will trap or drain energy reserves upon landing.\n" +
            "  - Monitor the transmission log feed for real-time turn tracking updates."
        );

        // 4. Close Window CTA Button
        Button closeBtn = new Button("RETURN TO MAIN COMMAND");
        closeBtn.setPrefWidth(240);
        closeBtn.setPrefHeight(40);
        closeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand;");
        
        // Wire button click to close the pop up stage cleanly
        closeBtn.setOnAction(e -> stagePopup.close());

        // Premium interactive button hover styles
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #00f2fe; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: #111424; -fx-font-family: 'Segoe UI Black'; -fx-font-size: 11px; -fx-background-radius: 20; -fx-cursor: hand;"));

        // 5. Build Layout Container
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: #111424; -fx-border-color: #f1c40f; -fx-border-width: 2; -fx-background-radius: 12; -fx-border-radius: 12;");
        mainLayout.setEffect(new DropShadow(25, Color.web("#f1c40f25")));

        mainLayout.getChildren().addAll(headerLabel, instructionsText, closeBtn);

        // 6. Display to user
        Scene popupScene = new Scene(mainLayout);
        popupScene.setFill(Color.TRANSPARENT); // Clears background corners outside border radius
        stagePopup.setScene(popupScene);
        stagePopup.showAndWait();
    }
    
    @FXML
    private void handleCloseWindow() {
        // Option A: close just this window
        Stage stage = (Stage) startBtn.getScene().getWindow();
        stage.close();
    }
}
package game.gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import game.engine.Game;
import game.engine.Constants;
import game.engine.monsters.Monster;
import game.engine.cells.*;
import game.engine.cells.Cell;
import game.engine.exceptions.*;

public class GameBoardController {

    @FXML private GridPane gameBoardGrid;
    @FXML private Label turnLabel;
    @FXML private CheckBox powerupCheckBox;
    
    // Left Dashboard (Player 1)
    @FXML private Label player1NameLabel;
    @FXML private Label player1EnergyLabel;
    @FXML private ProgressBar player1ProgressBar;
    @FXML private Label player1StatusLabel; // Displays detailed statuses
    
    // Right Dashboard (Player 2)
    @FXML private Label player2NameLabel;
    @FXML private Label player2EnergyLabel;
    @FXML private ProgressBar player2ProgressBar;
    @FXML private Label player2StatusLabel; // Displays detailed statuses

    // Bottom Right Card Interface
    @FXML private Label cardNameLabel;
    @FXML private Label cardDescriptionLabel;

    private Game engine;
    private StackPane[] visualCells = new StackPane[100];

    public void setGameEngine(Game engine) {
        this.engine = engine;
        
        player1NameLabel.setText(engine.getPlayer().getName().toUpperCase());
        player2NameLabel.setText(engine.getOpponent().getName().toUpperCase());
        
        generateBoard();
        updateUI();
    }

    private void generateBoard() {
        gameBoardGrid.getChildren().clear();
        gameBoardGrid.setHgap(4); 
        gameBoardGrid.setVgap(4);
        
        for (int i = 0; i < 100; i++) {
            StackPane cell = new StackPane();
            cell.setPrefSize(62, 62);
            cell.setMinWidth(62);  
            cell.setMinHeight(62); 
            
            int row = 9 - (i / 10);
            int col = ((i / 10) % 2 == 0) ? (i % 10) : (9 - (i % 10));

            cell.setStyle("-fx-background-color: " + getCellColor(i) + "; -fx-background-radius: 6;");
            
            Label idLabel = new Label(String.valueOf(i));
            idLabel.setStyle("-fx-font-size: 10px; -fx-font-family: 'Segoe UI Semibold'; -fx-text-fill: rgba(255,255,255,0.25);");
            StackPane.setAlignment(idLabel, Pos.TOP_LEFT);
            StackPane.setMargin(idLabel, new Insets(4, 0, 0, 6));
            cell.getChildren().add(idLabel);

            visualCells[i] = cell;
            gameBoardGrid.add(cell, col, row);
        }
    }

public void updateUI() {
    Monster p1 = engine.getPlayer();
    Monster p2 = engine.getOpponent();
    Monster current = engine.getCurrent();

    // 1. Refresh Left Sidebar (Player 1)
    player1NameLabel.setText(p1.getName().toUpperCase());
    player1EnergyLabel.setText("Role: " + getMonsterType(p1) + "\nEnergy: " + p1.getEnergy() + " LP");
    player1ProgressBar.setProgress((double) p1.getEnergy() / Constants.WINNING_ENERGY);
    player1StatusLabel.setText(buildStatusString(p1));
    
    // Style switch alert for Player 1
    if (p1.isConfused()) {
        player1NameLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; -fx-text-fill: #e67e22;"); // Warning Orange
    } else {
        player1NameLabel.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 16px; -fx-text-fill: white;");
    }

    // 2. Refresh Right Sidebar (Player 2)
    player2NameLabel.setText(p2.getName().toUpperCase());
    player2EnergyLabel.setText("Role: " + getMonsterType(p2) + "\nEnergy: " + p2.getEnergy() + " LP");
    player2ProgressBar.setProgress((double) p2.getEnergy() / Constants.WINNING_ENERGY);
    player2StatusLabel.setText(buildStatusString(p2));
    
    // Style switch alert for Player 2
    if (p2.isConfused()) {
        player2NameLabel.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; -fx-text-fill: #e67e22;"); // Warning Orange
    } else {
        player2NameLabel.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 16px; -fx-text-fill: white;");
    }

    // 3. Central Turn Indicator & 4. Card Deck Code remain exactly the same...
    turnLabel.setText(current.getName().toUpperCase() + "'S TURN");
    turnLabel.setTextFill(current == p1 ? Color.web("#00f2fe") : Color.web("#f35588"));

    int pos = current.getPosition();
    Cell currentCell = engine.getBoard().getBoardCells()[pos / 10][pos % 10];
    if (currentCell instanceof CardCell) {
        cardNameLabel.setText("CARD CELL ENCOUNTERED!");
        cardDescriptionLabel.setText(current.getName() + " landed on a Red Zone! Stats and conditions have updated.");
    } else {
        cardNameLabel.setText("DECK STANDBY");
        cardDescriptionLabel.setText("Awaiting arrival on a Red Node zone.");
    }

    renderMonsters();
}
    /**
     * Checks the true structural subtype class of a given Monster object instance.
     * Updates dynamically based on whatever package pathing your engine classes use.
     */
    /**
     * Detects the real monster type, or switches it to a scrambled role if confused.
     */
    private String getMonsterType(Monster monster) {
        String realType = "STANDARD";
        
        if (monster instanceof game.engine.monsters.Dasher) realType = "DASHER";
        else if (monster instanceof game.engine.monsters.Schemer) realType = "SCHEMER";
        else if (monster instanceof game.engine.monsters.MultiTasker) realType = "MULTITASKER";
        else if (monster instanceof game.engine.monsters.Dynamo) realType = "DYNAMO";

        // IF CONFUSED: Explicitly scramble and switch their type visually!
        if (monster.isConfused()) {
            switch (realType) {
                case "DASHER":      return "🌀 SCHEMER (SCRAMBLED!)";
                case "SCHEMER":     return "🌀 DYNAMO (SCRAMBLED!)";
                case "MULTITASKER": return "🌀 DASHER (SCRAMBLED!)";
                case "DYNAMO":      return "🌀 MULTITASKER (SCRAMBLED!)";
                default:            return "🌀 CONFUSED (SCRAMBLED!)";
            }
        }

        // IF HEALTHY: Show their true role
        switch (realType) {
            case "DASHER":      return "DASHER ⚡";
            case "SCHEMER":     return "SCHEMER 🧠";
            case "MULTITASKER": return "MULTITASKER 🛠️";
            case "DYNAMO":      return "DYNAMO 🔥";
            default:            return "MONSTER";
        }
    }

    /**
     * Builds an expressive, multi-line status display reading variables directly from the engine.
     */
    private String buildStatusString(Monster monster) {
        StringBuilder status = new StringBuilder("STATUS REPORT:\n");
        boolean hasCondition = false;

        if (monster.isFrozen()) {
            status.append("• FROZEN ❄️ (Turn Skipped)\n");
            hasCondition = true;
        }
        
        // Checks your engine's shield flag method (adjust naming if your method is named differently)
        if (monster.isShielded()) {
            status.append("• SHIELDED 🛡️ (Protected)\n");
            hasCondition = true;
        }

        // Checks your engine's confusion mechanism parameters
        if (monster.isConfused()) {
            // Displays status alongside remaining turn counts directly from your engine variables
            status.append("• CONFUSED 🌀 (Turns: ").append(monster.getConfusionTurns()).append(")\n");
            hasCondition = true;
        }

        if (!hasCondition) {
            status.append("• OPERATIONAL 🟢 (Healthy)\n");
        }

        return status.toString();
    }

    private void renderMonsters() {
        for (StackPane cell : visualCells) {
            cell.getChildren().removeIf(n -> n instanceof Circle);
        }

        Monster p1 = engine.getPlayer();
        Monster p2 = engine.getOpponent();

        Circle token1 = new Circle(14, Color.web("#00f2fe"));
        token1.setEffect(new DropShadow(10, Color.web("#00f2fe")));

        Circle token2 = new Circle(14, Color.web("#f35588"));
        token2.setEffect(new DropShadow(10, Color.web("#f35588")));

        // EXPRESSIVE COMPONENT CHANGES: Change token outline based on status
        if (p1.isFrozen()) token1.setStroke(Color.ALICEBLUE);
        else if (p1.isConfused()) token1.setStroke(Color.DARKORANGE);
        else if (p1.isShielded()) token1.setStroke(Color.GOLD);

        if (p2.isFrozen()) token2.setStroke(Color.ALICEBLUE);
        else if (p2.isConfused()) token2.setStroke(Color.DARKORANGE);
        else if (p2.isShielded()) token2.setStroke(Color.GOLD);

        visualCells[p1.getPosition()].getChildren().add(token1);
        visualCells[p2.getPosition()].getChildren().add(token2);
    }

    @FXML
    private void handleRollDice() {
        try {
            // Simply plays the standard turn sequence
            engine.playTurn();
            updateUI();
        } catch (InvalidMoveException e) {
            showCustomPopup("Action Blocked", e.getMessage());
        }
    }

    @FXML
    private void handleUsePowerUp() {
        Monster current = engine.getCurrent();
        try {
            // Explicitly deploys the ability right now
            engine.usePowerup();
            updateUI();
            
            // Show an expressive success pop-up notification!
            showCustomPopup("Power-Up Activated", current.getName() + " successfully unleashed their specialized trait matrix!");
        } catch (Exception e) {
            // Catches exceptions if they don't have enough energy or if it's invalid
            showCustomPopup("Power-Up Failed", e.getMessage());
        }
    }

    private void showCustomPopup(String title, String msg) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(javafx.stage.StageStyle.UNDECORATED);

        Label titleLbl = new Label(title.toUpperCase());
        titleLbl.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 15px; -fx-text-fill: #f1c40f;");
        Label msgLbl = new Label(msg);
        msgLbl.setStyle("-fx-font-family: 'Segoe UI'; -fx-text-fill: white; -fx-text-alignment: center;");
        msgLbl.setWrapText(true);
        
        Button okBtn = new Button("OK");
        okBtn.setPrefWidth(100);
        okBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #111424; -fx-font-weight: bold; -fx-background-radius: 15;");
        okBtn.setOnAction(e -> window.close());

        VBox layout = new VBox(20, titleLbl, msgLbl, okBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: #1a1c2e; -fx-border-color: #f1c40f; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10;");

        window.setScene(new Scene(layout, 350, 180));
        window.showAndWait();
    }

    private String getCellColor(int i) {
        if (contains(Constants.SOCK_CELL_INDICES, i)) return "#d35400"; 
        if (contains(Constants.CONVEYOR_CELL_INDICES, i)) return "#27ae60"; 
        if (contains(Constants.CARD_CELL_INDICES, i)) return "#c0392b"; 
        if (contains(Constants.MONSTER_CELL_INDICES, i)) return "#2980b9"; 
        return (i % 2 != 0) ? "#6c5ce7" : "#2c3e50"; 
    }

    private boolean contains(int[] arr, int val) {
        for (int i : arr) if (i == val) return true;
        return false;
    }
}
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
    @FXML private Label player1StatusLabel;
    
    // Right Dashboard (Player 2)
    @FXML private Label player2NameLabel;
    @FXML private Label player2EnergyLabel;
    @FXML private ProgressBar player2ProgressBar;
    @FXML private Label player2StatusLabel;

    // Bottom Right Card Interface
    @FXML private Label cardNameLabel;
    @FXML private Label cardDescriptionLabel;

    private Game engine;
    private StackPane[] visualCells = new StackPane[100];

    public void setGameEngine(Game engine) {
        this.engine = engine;
        
        // Assign unchanging user baseline profile data labels at launch
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

        // 1. Refresh Left Sidebar (Player 1 Status)
        player1EnergyLabel.setText("Energy: " + p1.getEnergy() + " LP");
        player1ProgressBar.setProgress((double) p1.getEnergy() / Constants.WINNING_ENERGY);
        player1StatusLabel.setText(p1.isFrozen() ? "STATUS: FROZEN ❄️" : "STATUS: ACTIVE 🟢");
        player1StatusLabel.setTextFill(p1.isFrozen() ? Color.web("#00f2fe") : Color.web("#27ae60"));

        // 2. Refresh Right Sidebar (Player 2 Status)
        player2EnergyLabel.setText("Energy: " + p2.getEnergy() + " LP");
        player2ProgressBar.setProgress((double) p2.getEnergy() / Constants.WINNING_ENERGY);
        player2StatusLabel.setText(p2.isFrozen() ? "STATUS: FROZEN ❄️" : "STATUS: ACTIVE 🟢");
        player2StatusLabel.setTextFill(p2.isFrozen() ? Color.web("#f35588") : Color.web("#27ae60"));

        // 3. Central Turn Indicator Announcement Label
        turnLabel.setText(current.getName().toUpperCase() + "'S TURN");
        turnLabel.setTextFill(current == p1 ? Color.web("#00f2fe") : Color.web("#f35588"));

        // 4. Update Card Deck Frame Details on Bottom Right
        int pos = current.getPosition();
        Cell currentCell = engine.getBoard().getBoardCells()[pos / 10][pos % 10];
        if (currentCell instanceof CardCell) {
            CardCell cCell = (CardCell) currentCell;
            cardNameLabel.setText("DRAWN: " + cCell.getName().toUpperCase());
            cardDescriptionLabel.setText(cCell.toString());
        } else {
            cardNameLabel.setText("DECK STANDBY");
            cardDescriptionLabel.setText("Awaiting arrival on a Red Node zone.");
        }

        renderMonsters();
    }

    private void renderMonsters() {
        for (StackPane cell : visualCells) {
            cell.getChildren().removeIf(n -> n instanceof Circle);
        }

        Circle token1 = new Circle(14, Color.web("#00f2fe"));
        token1.setEffect(new DropShadow(10, Color.web("#00f2fe")));

        Circle token2 = new Circle(14, Color.web("#f35588"));
        token2.setEffect(new DropShadow(10, Color.web("#f35588")));

        visualCells[engine.getPlayer().getPosition()].getChildren().add(token1);
        visualCells[engine.getOpponent().getPosition()].getChildren().add(token2);
    }

    @FXML
    private void handleRollDice() {
        try {
            if (powerupCheckBox.isSelected()) {
                engine.usePowerup();
            }
            engine.playTurn();
            updateUI();
        } catch (InvalidMoveException | OutOfEnergyException e) {
            showCustomPopup("Action Blocked", e.getMessage());
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
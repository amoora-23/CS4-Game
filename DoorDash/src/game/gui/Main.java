package game.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the very first screen
        Parent root = FXMLLoader.load(getClass().getResource("/game/gui/views/StartScreen.fxml"));
        
        primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        Scene scene = new Scene(root);
        
        // Link the CSS
        scene.getStylesheets().add(getClass().getResource("/game/gui/views/style.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1300); 
        primaryStage.setMinHeight(1000);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package game.gui;

import game.gui.controllers.*;
import game.gui.views.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneManager.getInstance().init(primaryStage);

        StartView view = new StartView();
        new StartController(view);

        primaryStage.setScene(view.getScene());
        primaryStage.setTitle("DooR DasH: Scare vs Laugh Touchdown");
        //primaryStage.setMinHeight(1000);
        //primaryStage.setMinWidth(1300);
        //primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

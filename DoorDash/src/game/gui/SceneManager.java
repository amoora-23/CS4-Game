package game.gui;

import game.engine.Game;
import game.engine.monsters.Monster;
import game.gui.controllers.*;
import game.gui.views.*;
import javafx.stage.Stage;

public class SceneManager {

    private static final SceneManager INSTANCE = new SceneManager();

    private Stage stage;
    private Game  game;
    private boolean botMode;
    private Monster winner;

    private SceneManager() {}

    public static SceneManager getInstance() {
        return INSTANCE;
    }

    public void init(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest(e -> System.exit(0));
    }

    // ── Getters / setters ────────────────────────────────────────────────────

    public Game getGame()                   { return game; }
    public void setGame(Game game)          { this.game = game; }
    public boolean isBotMode()              { return botMode; }
    public void setBotMode(boolean botMode) { this.botMode = botMode; }
    public Monster getWinner()              { return winner; }
    public void setWinner(Monster winner)   { this.winner = winner; }

    // ── Scene transitions ────────────────────────────────────────────────────

    public void showStartScreen() {
        StartView view = new StartView();
        new StartController(view);
        stage.setScene(view.getScene());
        stage.setTitle("DooR DasH — Start");
    }

    public void showVersusScreen() {
        VersusView view = new VersusView();
        new VersusController(view);
        stage.setScene(view.getScene());
        stage.setTitle("DooR DasH — Match Briefing");
    }

    public void showGameBoard() {
        GameBoardView view = new GameBoardView();
        new GameBoardController(view);
        stage.setScene(view.getScene());
        stage.setTitle("DooR DasH — The Floor");
    }

    public void showGameOver() {
        GameOverView view = new GameOverView();
        new GameOverController(view);
        stage.setScene(view.getScene());
        stage.setTitle("DooR DasH — Game Over");
    }
}

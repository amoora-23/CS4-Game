package game.gui.controllers;

import game.engine.Game;
import game.engine.monsters.Monster;
import game.gui.SceneManager;
import game.gui.views.GameOverView;
import javafx.application.Platform;

public class GameOverController {

    private final GameOverView view;

    public GameOverController(GameOverView view) {
        this.view = view;
        populateResults();
        attachHandlers();
    }

    // ── Populate ──────────────────────────────────────────────────────────────

    private void populateResults() {
        SceneManager sm     = SceneManager.getInstance();
        Game         game   = sm.getGame();
        Monster      winner = sm.getWinner();
        Monster      player = game.getPlayer();
        Monster      opp    = game.getOpponent();

        // Winner banner accent colour
        boolean playerWon = winner == player;
        String accent = playerWon ? "#52A65A" : "#A06330";

        view.winnerBannerLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 17px;" +
            "-fx-text-fill: " + accent + ";" +
            "-fx-letter-spacing: 2px;"
        );
        view.winnerBannerLbl.setText(playerWon ? "🏆 PLAYER WINS!" : "🏆 OPPONENT WINS!");

        // Winner details
        view.winnerNameLbl.setText(winner.getName());
        view.winnerRoleTypeLbl.setText(
            "ROLE: " + winner.getOriginalRole().name() +
            "  |  TYPE: " + VersusController.monsterType(winner)
        );
        view.winnerEnergyLbl.setText("Final Energy: " + winner.getEnergy());
        view.winnerTagline.setText(winner.getDescription());

        // Final standings
        view.p1FinalLbl.setText(player.getName() + ": " + player.getEnergy() + " energy");
        view.p2FinalLbl.setText(opp.getName()    + ": " + opp.getEnergy()    + " energy");
    }

    // ── Events ────────────────────────────────────────────────────────────────

    private void attachHandlers() {
        view.playAgainBtn.setOnAction(e -> SceneManager.getInstance().showStartScreen());
        view.exitBtn.setOnAction(e -> Platform.exit());
    }
}

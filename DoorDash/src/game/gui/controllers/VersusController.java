package game.gui.controllers;

import game.engine.Game;
import game.engine.monsters.*;
import game.gui.SceneManager;
import game.gui.views.VersusView;

public class VersusController {

    private final VersusView view;

    public VersusController(VersusView view) {
        this.view = view;
        populateMonsterCards();
        attachHandlers();
    }

    // ── Populate ──────────────────────────────────────────────────────────────

    private void populateMonsterCards() {
        Game game = SceneManager.getInstance().getGame();
        Monster player = game.getPlayer();
        Monster opp    = game.getOpponent();

        fillCard(player,
                 view.playerNameLbl, view.playerTypeLbl,
                 view.playerRoleLbl, view.playerEnergyLbl, view.playerDescLbl);

        fillCard(opp,
                 view.oppNameLbl, view.oppTypeLbl,
                 view.oppRoleLbl, view.oppEnergyLbl, view.oppDescLbl);
    }

    private void fillCard(Monster m,
                           javafx.scene.control.Label nameLbl,
                           javafx.scene.control.Label typeLbl,
                           javafx.scene.control.Label roleLbl,
                           javafx.scene.control.Label energyLbl,
                           javafx.scene.control.Label descLbl) {
        nameLbl.setText(m.getName());
        typeLbl.setText("TYPE: " + monsterType(m));
        roleLbl.setText("ROLE: " + m.getRole().name());
        energyLbl.setText("⚡ Starting Energy: " + m.getEnergy());
        descLbl.setText(m.getDescription());
    }

    // ── Events ────────────────────────────────────────────────────────────────

    private void attachHandlers() {
        view.commenceBtn.setOnAction(e -> SceneManager.getInstance().showGameBoard());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    static String monsterType(Monster m) {
        if (m instanceof Dasher)      return "Dasher";
        if (m instanceof Dynamo)      return "Dynamo";
        if (m instanceof MultiTasker) return "MultiTasker";
        if (m instanceof Schemer)     return "Schemer";
        return "Unknown";
    }
}

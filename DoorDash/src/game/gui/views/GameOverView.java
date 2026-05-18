package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class GameOverView {

    // ── Forest palette ────────────────────────────────────────────────────────
    private static final String BG      = "#0D1A09";
    private static final String CARD_BG = "#0A1207";
    private static final String GOLD    = "#C8900A";
    private static final String GRAY    = "#7A8C6A";
    private static final String GREEN   = "#4CAF50";
    private static final String RED     = "#8B3500";

    // ── Exposed nodes ─────────────────────────────────────────────────────────
    public final Label  winnerBannerLbl   = new Label("🏆 WINNER");
    public final Label  winnerNameLbl     = new Label("—");
    public final Label  winnerRoleTypeLbl = new Label("ROLE: —  |  TYPE: —");
    public final Label  winnerEnergyLbl   = new Label("Final Energy: —");
    public final Label  winnerTagline     = new Label();
    public final Label  p1FinalLbl        = new Label("Player:   — energy");
    public final Label  p2FinalLbl        = new Label("Opponent: — energy");
    public final Button playAgainBtn      = new Button("PLAY AGAIN");
    public final Button exitBtn           = new Button("EXIT GAME");

    private final Scene scene;

    public GameOverView() {
        scene = new Scene(buildRoot(), 900, 600);
    }

    public Scene getScene() { return scene; }

    // ── Layout ────────────────────────────────────────────────────────────────

    private StackPane buildRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG + ";");
        VBox content = buildContent();
        StackPane.setAlignment(content, Pos.CENTER);
        root.getChildren().add(content);
        return root;
    }

    private VBox buildContent() {
        VBox box = new VBox(20);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(50));
        box.setMaxWidth(720);

        Label title = new Label("GAME OVER");
        title.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 30px;" +
            "-fx-text-fill: " + RED + ";" +
            "-fx-letter-spacing: 4px;"
        );

        winnerBannerLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 17px;" +
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-letter-spacing: 2px;"
        );

        VBox winnerBox  = buildWinnerBox();
        VBox standings  = buildStandingsBox();

        styleButton(playAgainBtn, GREEN, BG,      160, 44);
        styleButton(exitBtn,      RED,   "#D4C9A8", 160, 44);
        HBox btnRow = new HBox(24, playAgainBtn, exitBtn);
        btnRow.setAlignment(Pos.CENTER);

        box.getChildren().addAll(title, winnerBannerLbl, winnerBox, standings, btnRow);
        return box;
    }

    private VBox buildWinnerBox() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(26));
        box.setMinWidth(500);
        box.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + GOLD + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
        DropShadow glow = new DropShadow(18, Color.web(GOLD + "50"));
        box.setEffect(glow);

        winnerNameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 34px;" +
            "-fx-text-fill: " + GOLD + ";"
        );
        winnerRoleTypeLbl.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GRAY + ";"
        );
        winnerEnergyLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 20px;" +
            "-fx-text-fill: " + GREEN + ";"
        );
        winnerTagline.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-font-style: italic;"
        );
        winnerTagline.setWrapText(true);

        box.getChildren().addAll(winnerNameLbl, winnerRoleTypeLbl,
                                  winnerEnergyLbl, winnerTagline);
        return box;
    }

    private VBox buildStandingsBox() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(16));
        box.setMinWidth(500);
        box.setStyle(
            "-fx-background-color: #162012;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #3A4A2E;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );

        Label hdr = new Label("FINAL STANDINGS");
        hdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + GRAY + ";" +
            "-fx-letter-spacing: 1px;"
        );
        p1FinalLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #D4C9A8;");
        p2FinalLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #D4C9A8;");

        box.getChildren().addAll(hdr, p1FinalLbl, p2FinalLbl);
        return box;
    }

    private void styleButton(Button btn, String bg, String fg, double w, double h) {
        btn.setPrefWidth(w);
        btn.setPrefHeight(h);
        btn.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + fg + ";" +
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 22;" +
            "-fx-cursor: hand;"
        );
    }
}

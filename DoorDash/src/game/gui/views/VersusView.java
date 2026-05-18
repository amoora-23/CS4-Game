package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class VersusView {

    private static final String BG      = "#111424";
    private static final String CARD_BG = "#0b0d19";
    private static final String CYAN    = "#00F2FE";
    private static final String PINK    = "#F35588";
    private static final String GOLD    = "#F1C40F";
    private static final String GRAY    = "#94A3B8";
    private static final String GREEN   = "#2ECC71";

    // ── Exposed nodes ─────────────────────────────────────────────────────────
    // Player card labels
    public final Label playerNameLbl  = new Label("—");
    public final Label playerTypeLbl  = new Label("TYPE: —");
    public final Label playerRoleLbl  = new Label("ROLE: —");
    public final Label playerEnergyLbl= new Label("⚡ —");
    public final Label playerDescLbl  = new Label();

    // Opponent card labels
    public final Label oppNameLbl     = new Label("—");
    public final Label oppTypeLbl     = new Label("TYPE: —");
    public final Label oppRoleLbl     = new Label("ROLE: —");
    public final Label oppEnergyLbl   = new Label("⚡ —");
    public final Label oppDescLbl     = new Label();

    // Action button
    public final Button commenceBtn   = new Button("COMMENCE MATCH");

    private final Scene scene;

    public VersusView() {
        scene = new Scene(buildRoot(), 1024, 720);
    }

    public Scene getScene() { return scene; }

    // ── Layout ────────────────────────────────────────────────────────────────

    private StackPane buildRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox content = new VBox(32);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));

        // Title
        Label title = new Label("MATCH BRIEFING");
        title.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 34px;" +
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-letter-spacing: 2px;"
        );
        DropShadow glow = new DropShadow(14, Color.web(GOLD + "50"));
        title.setEffect(glow);

        Label subtitle = new Label("Your monster has been assigned. The Floor awaits.");
        subtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + GRAY + ";");

        Separator sep = new Separator();
        sep.setMaxWidth(800);
        sep.setStyle("-fx-opacity: 0.15;");

        // Monster matchup row
        HBox matchup = buildMatchupRow();

        // Commence button
        styleButton(commenceBtn, GREEN, "#111424", 320, 50);

        content.getChildren().addAll(title, subtitle, sep, matchup, commenceBtn);
        root.getChildren().add(content);
        return root;
    }

    private HBox buildMatchupRow() {
        HBox row = new HBox(40);
        row.setAlignment(Pos.CENTER);

        VBox playerCard = buildMonsterCard(
            "YOUR OPERATIVE", CYAN,
            playerNameLbl, playerTypeLbl, playerRoleLbl,
            playerEnergyLbl, playerDescLbl
        );
        playerNameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 26px;" +
            "-fx-text-fill: " + CYAN + ";"
        );

        Label vs = new Label("VS");
        vs.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 28px;" +
            "-fx-text-fill: #334155;"
        );
        VBox.setMargin(vs, new Insets(80, 0, 0, 0));

        VBox oppCard = buildMonsterCard(
            "TARGET OPPONENT", PINK,
            oppNameLbl, oppTypeLbl, oppRoleLbl,
            oppEnergyLbl, oppDescLbl
        );
        oppNameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 26px;" +
            "-fx-text-fill: " + PINK + ";"
        );

        row.getChildren().addAll(playerCard, vs, oppCard);
        return row;
    }

    private VBox buildMonsterCard(String headerText, String accent,
                                   Label nameLbl, Label typeLbl, Label roleLbl,
                                   Label energyLbl, Label descLbl) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(28));
        card.setPrefWidth(340);
        card.setMinHeight(220);
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + accent + ";" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 12;"
        );
        DropShadow shadow = new DropShadow(18, Color.web(accent + "40"));
        card.setEffect(shadow);

        Label header = new Label(headerText);
        header.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + GRAY + ";" +
            "-fx-letter-spacing: 1px;"
        );

        typeLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        roleLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: white;");
        energyLbl.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GREEN + ";"
        );
        descLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GRAY + ";");
        descLbl.setWrapText(true);
        descLbl.setMaxWidth(290);

        card.getChildren().addAll(header, nameLbl, typeLbl, roleLbl, energyLbl, descLbl);
        return card;
    }

    private void styleButton(Button btn, String bg, String fg, double w, double h) {
        btn.setPrefWidth(w);
        btn.setPrefHeight(h);
        btn.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + fg + ";" +
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 14px;" +
            "-fx-background-radius: 24;" +
            "-fx-cursor: hand;"
        );
    }
}

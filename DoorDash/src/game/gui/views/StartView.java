package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class StartView {

    // ── Colours ──────────────────────────────────────────────────────────────
    private static final String BG          = "#111424";
    private static final String CARD_BG     = "#0b0d19";
    private static final String CYAN        = "#00F2FE";
    private static final String PINK        = "#F35588";
    private static final String GOLD        = "#F1C40F";
    private static final String GRAY        = "#94A3B8";
    private static final String DIM         = "#64748B";
    private static final String GREEN       = "#2ECC71";

    // ── Exposed nodes (controller accesses these) ─────────────────────────────
    public final ToggleGroup modeGroup      = new ToggleGroup();
    public final ToggleGroup factionGroup   = new ToggleGroup();
    public final RadioButton botModeBtn     = new RadioButton("1 VS BOT");
    public final RadioButton pvpModeBtn     = new RadioButton("1 VS 1");
    public final RadioButton scarerBtn      = new RadioButton();
    public final RadioButton laugherBtn     = new RadioButton();
    public final Button      startBtn       = new Button("INITIALIZE PROTOCOL");
    public final Button      instructionsBtn= new Button("GAME INSTRUCTIONS");

    private final Scene scene;

    public StartView() {
        scene = new Scene(buildRoot(), 1024, 720);
    }

    public Scene getScene() { return scene; }

    // ── Layout ────────────────────────────────────────────────────────────────

    private StackPane buildRoot() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        VBox card = buildCard();
        StackPane.setAlignment(card, Pos.CENTER);
        root.getChildren().add(card);
        return root;
    }

    private VBox buildCard() {
        VBox card = new VBox(22);
        card.setAlignment(Pos.CENTER);
        card.setMaxWidth(560);
        card.setPadding(new Insets(38));
        card.setStyle(
            "-fx-background-color: " + CARD_BG + ";" +
            "-fx-border-color: #3498DB;" +
            "-fx-border-width: 2;" +
            "-fx-background-radius: 15;" +
            "-fx-border-radius: 15;"
        );

        DropShadow glow = new DropShadow(25, Color.web("#3498DB40"));
        card.setEffect(glow);

        card.getChildren().addAll(
            buildTitleSection(),
            buildSeparator(),
            buildModeSection(),
            buildSeparator(),
            buildFactionSection(),
            buildSeparator(),
            buildButtonSection()
        );
        return card;
    }

    private VBox buildTitleSection() {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);

        Label title = new Label("DOOR DASH");
        title.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 42px;" +
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-letter-spacing: 3px;"
        );
        DropShadow glow = new DropShadow(12, Color.web(GOLD));
        title.setEffect(glow);

        Label sub = new Label("SCARE VS LAUGH TOUCHDOWN");
        sub.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: " + GRAY + ";" +
            "-fx-letter-spacing: 2px;"
        );

        box.getChildren().addAll(title, sub);
        return box;
    }

    private VBox buildModeSection() {
        VBox section = new VBox(10);
        section.setAlignment(Pos.CENTER);

        Label lbl = sectionLabel("CHOOSE GAME MODE");

        botModeBtn.setToggleGroup(modeGroup);
        pvpModeBtn.setToggleGroup(modeGroup);
        botModeBtn.setSelected(true);
        styleRadio(botModeBtn, CYAN);
        styleRadio(pvpModeBtn, CYAN);

        HBox row = new HBox(30);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(botModeBtn, pvpModeBtn);

        section.getChildren().addAll(lbl, row);
        return section;
    }

    private HBox buildFactionSection() {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER);

        // SCARER card
        VBox scarerCard = factionCard(
            "SCARE DIVISION", CYAN,
            "Monsters, Inc. OG — terrify kids for energy",
            scarerBtn, "DEPLOY SCARER"
        );
        scarerBtn.setToggleGroup(factionGroup);

        // LAUGHER card
        VBox laugherCard = factionCard(
            "LAUGH DIVISION", PINK,
            "Wazowski's revolution — laughter is 10× stronger",
            laugherBtn, "DEPLOY LAUGHER"
        );
        laugherBtn.setToggleGroup(factionGroup);

        row.getChildren().addAll(scarerCard, laugherCard);
        return row;
    }

    private VBox factionCard(String title, String accent, String desc,
                              RadioButton rb, String rbText) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setPrefWidth(220);
        card.setStyle(
            "-fx-background-color: #1a1c2e;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + accent + "40;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-cursor: hand;"
        );

        Label titleLbl = new Label(title);
        titleLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + accent + ";"
        );

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GRAY + ";");
        descLbl.setWrapText(true);

        rb.setText(rbText);
        styleRadio(rb, accent);

        card.getChildren().addAll(titleLbl, descLbl, rb);
        card.setOnMouseClicked(e -> rb.setSelected(true));
        return card;
    }

    private VBox buildButtonSection() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);

        styleButton(startBtn, GREEN, "#111424", 320, 46);
        styleButton(instructionsBtn, "#334155", GRAY, 220, 36);

        box.getChildren().addAll(startBtn, instructionsBtn);
        return box;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Label sectionLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 11px;" +
            "-fx-text-fill: #3498DB;" +
            "-fx-letter-spacing: 1.5px;"
        );
        return lbl;
    }

    private Separator buildSeparator() {
        Separator sep = new Separator();
        sep.setMaxWidth(420);
        sep.setStyle("-fx-opacity: 0.15;");
        return sep;
    }

    private void styleRadio(RadioButton rb, String accent) {
        rb.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-cursor: hand;"
        );
    }

    private void styleButton(Button btn, String bg, String fg,
                              double w, double h) {
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

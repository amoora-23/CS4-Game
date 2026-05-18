package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;

public class GameBoardView {

    // ── Palette ───────────────────────────────────────────────────────────────
	public static final String BG       = "#111424";
    public static final String SIDE_BG  = "#0b0d19";
    public static final String CYAN     = "#00F2FE";
    public static final String PINK     = "#F35588";
    public static final String GOLD     = "#F1C40F";
    public static final String GRAY     = "#94A3B8";
    public static final String DIM      = "#64748B";
    public static final String GREEN    = "#2ECC71";
    public static final String RED_CARD = "#C0392B";

    // ── Left sidebar (player) ─────────────────────────────────────────────────
    public final Label p1NameLbl         = new Label("Player");
    public final Label p1TypeLbl         = new Label("TYPE: —");
    public final Label p1OrigRoleLbl     = new Label("ORIGINAL ROLE: —");
    public final Label p1CurrRoleLbl     = new Label("CURRENT ROLE: —");
    public final Label p1EnergyLbl       = new Label("⚡ 0");
    public final ProgressBar p1Bar       = new ProgressBar(0);
    public final Label p1PosLbl          = new Label("📍 Cell 0");
    public final Label p1StatusLbl       = new Label("✅ OPERATIONAL");
    public final TextArea gameLog        = new TextArea();

    // ── Right sidebar (opponent) ──────────────────────────────────────────────
    public final Label p2NameLbl         = new Label("Opponent");
    public final Label p2TypeLbl         = new Label("TYPE: —");
    public final Label p2OrigRoleLbl     = new Label("ORIGINAL ROLE: —");
    public final Label p2CurrRoleLbl     = new Label("CURRENT ROLE: —");
    public final Label p2EnergyLbl       = new Label("⚡ 0");
    public final ProgressBar p2Bar       = new ProgressBar(0);
    public final Label p2PosLbl          = new Label("📍 Cell 0");
    public final Label p2StatusLbl       = new Label("✅ OPERATIONAL");

    // ── Card panel ────────────────────────────────────────────────────────────
    public final Label deckCountLbl      = new Label("🃏 — remaining");
    public final Label cardNameLbl       = new Label("DECK STANDBY");
    public final Label cardDescLbl       = new Label("Awaiting card cell…");

    // ── Center area ───────────────────────────────────────────────────────────
    public final Label turnLbl           = new Label("—'S TURN");
    public final Label moveResultLbl     = new Label("Roll the dice to begin!");
    public final GridPane boardGrid      = new GridPane();
    public final Button rollBtn          = new Button("🎲  ROLL DICE");
    public final Button powerBtn         = new Button("⚡  USE POWER-UP");

    private final Scene scene;

    public GameBoardView() {
        scene = new Scene(buildRoot(), 1366, 810);
    }

    public Scene getScene() { return scene; }

    // ── Layout ────────────────────────────────────────────────────────────────

    private BorderPane buildRoot() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG + ";");

        root.setTop(buildTopBar());
        root.setLeft(buildLeftSidebar());
        root.setCenter(buildCenterArea());
        root.setRight(buildRightSidebar());

        return root;
    }

    // ── Top bar ───────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle("-fx-background-color: " + SIDE_BG + "; -fx-padding: 6 12;");

        Label title = new Label("DooR DasH  —  The Floor");
        title.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + GOLD + ";"
        );
        HBox.setHgrow(title, Priority.ALWAYS);
        bar.getChildren().add(title);
        return bar;
    }

    // ── Left sidebar (player) ─────────────────────────────────────────────────

    private VBox buildLeftSidebar() {
        VBox box = new VBox(9);
        box.setMinWidth(240);
        box.setPrefWidth(240);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + SIDE_BG + ";" +
            "-fx-border-color: " + CYAN + "20;" +
            "-fx-border-width: 0 1 0 0;"
        );

        Label header = sideHeader("YOUR MONITOR", CYAN);
        Separator sep = dimSep();

        p1NameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 16px; -fx-text-fill: white;"
        );
        styleSideLabel(p1TypeLbl);
        styleSideLabel(p1OrigRoleLbl);
        p1CurrRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + CYAN + ";");

        p1EnergyLbl.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 13px; -fx-text-fill: " + CYAN + ";"
        );
        p1Bar.setPrefWidth(200);
        p1Bar.setStyle("-fx-accent: " + CYAN + ";");
        styleSideLabel(p1PosLbl);

        Label statusHdr = new Label("STATUS");
        statusHdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 10px; -fx-text-fill: " + CYAN + "; -fx-letter-spacing: 0.5;"
        );
        p1StatusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN + ";");
        p1StatusLbl.setWrapText(true);

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Log
        Label logHdr = new Label("TRANSMISSION FEED");
        logHdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 10px; -fx-text-fill: " + CYAN + ";"
        );
        gameLog.setEditable(false);
        gameLog.setWrapText(true);
        gameLog.setPrefHeight(170);
        gameLog.setStyle(
            "-fx-control-inner-background: #111424;" +
            "-fx-text-fill: " + GREEN + ";" +
            "-fx-font-family: 'Consolas', monospace;" +
            "-fx-font-size: 10px;"
        );

        box.getChildren().addAll(
            header, sep,
            p1NameLbl, p1TypeLbl, p1OrigRoleLbl, p1CurrRoleLbl,
            p1EnergyLbl, p1Bar, p1PosLbl,
            statusHdr, p1StatusLbl,
            spacer,
            logHdr, gameLog
        );
        return box;
    }

    // ── Center area (board) ───────────────────────────────────────────────────

    private VBox buildCenterArea() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));

        turnLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 18px;" +
            "-fx-text-fill: " + GOLD + ";" +
            "-fx-letter-spacing: 1.5px;"
        );
        moveResultLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 20px;" +
            "-fx-text-fill: #ECF0F1;"
        );

        // Board grid
        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(1);
        boardGrid.setVgap(1);
        boardGrid.setStyle(
            "-fx-background-color: #1a1c2e;" +
            "-fx-padding: 6;" +
            "-fx-background-radius: 8;"
        );

        // Action buttons
        styleActionBtn(rollBtn,  GREEN,    "#111424");
        styleActionBtn(powerBtn, GOLD,     "#111424");
        HBox btns = new HBox(24, rollBtn, powerBtn);
        btns.setAlignment(Pos.CENTER);

        // Legend
        HBox legend = buildLegend();

        box.getChildren().addAll(turnLbl, moveResultLbl, boardGrid, btns, legend);
        return box;
    }

    private HBox buildLegend() {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(
            legendItem("■ SCARER Door", "#6C3483"),
            legendItem("■ LAUGHER Door", "#1A5276"),
            legendItem("■ Sock (−)", "#D35400"),
            legendItem("■ Belt (+)", "#1E8449"),
            legendItem("■ Card", "#922B21"),
            legendItem("■ Monster", "#1A56A5"),
            legendItem("□ Exhausted", "#555555")
        );
        return row;
    }

    private Label legendItem(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + color + ";");
        return lbl;
    }

    private void styleActionBtn(Button btn, String bg, String fg) {
        btn.setPrefHeight(46);
        btn.setPrefWidth(185);
        btn.setStyle(
            "-fx-background-color: " + bg + ";" +
            "-fx-text-fill: " + fg + ";" +
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 13px;" +
            "-fx-background-radius: 22;" +
            "-fx-cursor: hand;"
        );
    }

    // ── Right sidebar (opponent + card panel) ─────────────────────────────────

    private VBox buildRightSidebar() {
        VBox box = new VBox(9);
        box.setMinWidth(240);
        box.setPrefWidth(240);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + SIDE_BG + ";" +
            "-fx-border-color: " + PINK + "20;" +
            "-fx-border-width: 0 0 0 1;"
        );

        Label header = sideHeader("OPPONENT MONITOR", PINK);
        Separator sep = dimSep();

        p2NameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 16px; -fx-text-fill: white;"
        );
        styleSideLabel(p2TypeLbl);
        styleSideLabel(p2OrigRoleLbl);
        p2CurrRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PINK + ";");

        p2EnergyLbl.setStyle(
            "-fx-font-family: 'Segoe UI Semibold';" +
            "-fx-font-size: 13px; -fx-text-fill: " + PINK + ";"
        );
        p2Bar.setPrefWidth(200);
        p2Bar.setStyle("-fx-accent: " + PINK + ";");
        styleSideLabel(p2PosLbl);

        Label statusHdr = new Label("STATUS");
        statusHdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 10px; -fx-text-fill: " + PINK + "; -fx-letter-spacing: 0.5;"
        );
        p2StatusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN + ";");
        p2StatusLbl.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Card panel
        VBox cardPanel = buildCardPanel();

        box.getChildren().addAll(
            header, sep,
            p2NameLbl, p2TypeLbl, p2OrigRoleLbl, p2CurrRoleLbl,
            p2EnergyLbl, p2Bar, p2PosLbl,
            statusHdr, p2StatusLbl,
            spacer,
            cardPanel
        );
        return box;
    }

    private VBox buildCardPanel() {
        VBox panel = new VBox(8);

        // Deck count row
        HBox deckRow = new HBox(8);
        deckRow.setAlignment(Pos.CENTER_LEFT);
        deckRow.setPadding(new Insets(8));
        deckRow.setStyle(
            "-fx-background-color: #1a1c2e;" +
            "-fx-background-radius: 6;" +
            "-fx-border-color: " + RED_CARD + ";" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;"
        );
        Label deckHdr = new Label("FACTORY DECK");
        deckHdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 10px; -fx-text-fill: " + RED_CARD + ";"
        );
        deckCountLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #ECF0F1;");
        deckRow.getChildren().addAll(deckHdr, deckCountLbl);

        // Last drawn card
        VBox drawnBox = new VBox(5);
        drawnBox.setPadding(new Insets(10));
        drawnBox.setStyle(
            "-fx-background-color: #111424;" +
            "-fx-border-color: " + GOLD + ";" +
            "-fx-border-width: 1;" +
            "-fx-background-radius: 6;" +
            "-fx-border-radius: 6;"
        );
        Label drawnHdr = new Label("LAST DRAWN CARD");
        drawnHdr.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 9px; -fx-text-fill: " + GRAY + "; -fx-letter-spacing: 0.5;"
        );
        cardNameLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 12px; -fx-text-fill: " + GOLD + ";"
        );
        cardNameLbl.setWrapText(true);
        cardDescLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GRAY + ";");
        cardDescLbl.setWrapText(true);

        drawnBox.getChildren().addAll(drawnHdr, cardNameLbl, cardDescLbl);

        panel.getChildren().addAll(deckRow, drawnBox);
        return panel;
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private Label sideHeader(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black';" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-letter-spacing: 1px;"
        );
        return lbl;
    }

    private Separator dimSep() {
        Separator s = new Separator();
        s.setStyle("-fx-opacity: 0.2;");
        return s;
    }

    private void styleSideLabel(Label lbl) {
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GRAY + ";");
    }
}

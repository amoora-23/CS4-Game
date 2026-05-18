package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class GameBoardView {

    // ── Forest palette (matte) ────────────────────────────────────────────────
    public static final String BG       = "#0D1A09";
    public static final String SIDE_BG  = "#0A1207";
    public static final String CYAN     = "#52A65A";
    public static final String PINK     = "#A06330";
    public static final String GOLD     = "#C8900A";
    public static final String GRAY     = "#7A8C6A";
    public static final String DIM      = "#4A5840";
    public static final String GREEN    = "#4CAF50";
    public static final String RED_CARD = "#8B3500";

    // ── Left sidebar ──────────────────────────────────────────────────────────
    public final Label     p1NameLbl     = new Label("Player");
    public final Label     p1TypeLbl     = new Label("TYPE: —");
    public final Label     p1OrigRoleLbl = new Label("ORIGINAL ROLE: —");
    public final Label     p1CurrRoleLbl = new Label("CURRENT ROLE: —");
    public final Label     p1EnergyLbl   = new Label("⚡ 0");
    public final ProgressBar p1Bar       = new ProgressBar(0);
    public final Label     p1PosLbl      = new Label("📍 Cell 0");
    public final Label     p1StatusLbl   = new Label("✅ OPERATIONAL");
    public final TextArea  gameLog       = new TextArea();

    // ── Right sidebar ─────────────────────────────────────────────────────────
    public final Label     p2NameLbl     = new Label("Opponent");
    public final Label     p2TypeLbl     = new Label("TYPE: —");
    public final Label     p2OrigRoleLbl = new Label("ORIGINAL ROLE: —");
    public final Label     p2CurrRoleLbl = new Label("CURRENT ROLE: —");
    public final Label     p2EnergyLbl   = new Label("⚡ 0");
    public final ProgressBar p2Bar       = new ProgressBar(0);
    public final Label     p2PosLbl      = new Label("📍 Cell 0");
    public final Label     p2StatusLbl   = new Label("✅ OPERATIONAL");

    // ── Card panel ────────────────────────────────────────────────────────────
    public final Label deckCountLbl = new Label("🃏 — remaining");
    public final Label cardNameLbl  = new Label("DECK STANDBY");
    public final Label cardDescLbl  = new Label("Awaiting card cell…");

    // ── Center ────────────────────────────────────────────────────────────────
    public final Label    turnLbl       = new Label("—'S TURN");
    public final Label    moveResultLbl = new Label("Roll the dice to begin!");
    public final GridPane boardGrid     = new GridPane();
    public final Button   rollBtn       = new Button("🎲  ROLL DICE");
    public final Button   powerBtn      = new Button("⚡  USE POWER-UP");

    // ── Board overlay (floating animation labels) ─────────────────────────────
    public final Pane boardOverlay = new Pane();

    // ── Cell info popup ───────────────────────────────────────────────────────
    public final Label cellInfoTitle   = new Label("CELL 0");
    public final Label cellInfoType    = new Label("—");
    public final Label cellInfoDetails = new Label("—");
    public final VBox  cellInfoPopup   = new VBox(8);

    private final Scene scene;

    public GameBoardView() {
        scene = new Scene(buildRoot(), 1366, 810);
    }

    public Scene getScene() { return scene; }

    // ── Root ──────────────────────────────────────────────────────────────────

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
        bar.setStyle("-fx-background-color: " + SIDE_BG + "; -fx-padding: 6 12;" +
                     "-fx-border-color: " + DIM + "; -fx-border-width: 0 0 1 0;");
        Label title = new Label("DooR DasH  —  The Floor");
        title.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 14px;" +
            "-fx-text-fill: " + GOLD + ";"
        );
        Label hint = new Label("  [W] goto 99  [E] +100 energy  |  click any cell for details");
        hint.setStyle("-fx-font-size: 10px; -fx-text-fill: " + DIM + ";");
        HBox.setHgrow(title, Priority.ALWAYS);
        bar.getChildren().addAll(title, hint);
        return bar;
    }

    // ── Left sidebar ──────────────────────────────────────────────────────────

    private VBox buildLeftSidebar() {
        VBox box = new VBox(9);
        box.setMinWidth(240);
        box.setPrefWidth(240);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + SIDE_BG + ";" +
            "-fx-border-color: " + DIM + "; -fx-border-width: 0 1 0 0;"
        );

        Label header = sideHeader("YOUR MONITOR", CYAN);

        p1NameLbl.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; -fx-text-fill: white;");
        styleSideLabel(p1TypeLbl);
        styleSideLabel(p1OrigRoleLbl);
        p1CurrRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + CYAN + ";");
        p1EnergyLbl.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 13px; -fx-text-fill: " + CYAN + ";");
        p1Bar.setPrefWidth(200);
        p1Bar.setStyle("-fx-accent: " + CYAN + ";");
        styleSideLabel(p1PosLbl);

        Label statusHdr = microLabel("STATUS", CYAN);
        p1StatusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN + ";");
        p1StatusLbl.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label logHdr = microLabel("TRANSMISSION FEED", CYAN);
        gameLog.setEditable(false);
        gameLog.setWrapText(true);
        gameLog.setPrefHeight(170);
        gameLog.setStyle(
            "-fx-control-inner-background: " + BG + ";" +
            "-fx-text-fill: " + GREEN + ";" +
            "-fx-font-family: 'Consolas', monospace; -fx-font-size: 10px;"
        );

        box.getChildren().addAll(
            header, dimSep(),
            p1NameLbl, p1TypeLbl, p1OrigRoleLbl, p1CurrRoleLbl,
            p1EnergyLbl, p1Bar, p1PosLbl,
            statusHdr, p1StatusLbl,
            spacer, logHdr, gameLog
        );
        return box;
    }

    // ── Center area ───────────────────────────────────────────────────────────

    private VBox buildCenterArea() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(12));

        turnLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 18px;" +
            "-fx-text-fill: " + GOLD + "; -fx-letter-spacing: 1.5px;"
        );
        moveResultLbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 20px;" +
            "-fx-text-fill: #D4C9A8;"
        );

        boardGrid.setAlignment(Pos.CENTER);
        boardGrid.setHgap(1);
        boardGrid.setVgap(1);
        boardGrid.setStyle(
            "-fx-background-color: #162012; -fx-padding: 6; -fx-background-radius: 6;"
        );

        // Overlay for floating animation labels
        boardOverlay.setMouseTransparent(true);
        boardOverlay.setPickOnBounds(false);
        boardOverlay.setPrefSize(621, 621);

        // Cell info popup
        setupCellInfoPopup();

        StackPane boardStack = new StackPane(boardGrid, boardOverlay, cellInfoPopup);
        boardStack.setAlignment(Pos.CENTER);
        StackPane.setAlignment(cellInfoPopup, Pos.CENTER);

        styleActionBtn(rollBtn,  GREEN, BG);
        styleActionBtn(powerBtn, GOLD,  BG);
        HBox btns = new HBox(24, rollBtn, powerBtn);
        btns.setAlignment(Pos.CENTER);

        box.getChildren().addAll(turnLbl, moveResultLbl, boardStack, btns, buildLegend());
        return box;
    }

    private void setupCellInfoPopup() {
        cellInfoPopup.setPadding(new Insets(14));
        cellInfoPopup.setMaxWidth(210);
        cellInfoPopup.setMaxHeight(210);
        cellInfoPopup.setStyle(
            "-fx-background-color: #0A1207;" +
            "-fx-border-color: " + GOLD + ";" +
            "-fx-border-width: 1;" +
            "-fx-background-radius: 8; -fx-border-radius: 8;"
        );
        cellInfoPopup.setVisible(false);

        cellInfoTitle.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px; -fx-text-fill: " + GOLD + ";"
        );

        Button closeBtn = new Button("×");
        closeBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: " + GRAY + ";" +
            "-fx-font-size: 16px; -fx-padding: 0 2; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> cellInfoPopup.setVisible(false));

        HBox titleRow = new HBox(4);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(cellInfoTitle, Priority.ALWAYS);
        titleRow.getChildren().addAll(cellInfoTitle, closeBtn);

        Separator sep = new Separator();
        sep.setStyle("-fx-opacity: 0.2;");

        cellInfoType.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 10px;" +
            "-fx-text-fill: " + CYAN + "; -fx-letter-spacing: 0.5;"
        );

        cellInfoDetails.setStyle("-fx-font-size: 11px; -fx-text-fill: #D4C9A8;");
        cellInfoDetails.setWrapText(true);
        cellInfoDetails.setMaxWidth(190);

        cellInfoPopup.getChildren().addAll(titleRow, sep, cellInfoType, cellInfoDetails);
    }

    private HBox buildLegend() {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER);
        row.getChildren().addAll(
            legendItem("■ SCARER Door", "#2D5A1A"),
            legendItem("■ LAUGHER Door", "#5C3A10"),
            legendItem("■ Sock (−)", "#8B4500"),
            legendItem("■ Belt (+)", "#1A5A20"),
            legendItem("■ Card", "#8B3500"),
            legendItem("■ Monster", "#1A4A30"),
            legendItem("□ Exhausted", "#2A3020")
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
            "-fx-background-color: " + bg + "; -fx-text-fill: " + fg + ";" +
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px;" +
            "-fx-background-radius: 22; -fx-cursor: hand;"
        );
    }

    // ── Right sidebar ─────────────────────────────────────────────────────────

    private VBox buildRightSidebar() {
        VBox box = new VBox(9);
        box.setMinWidth(240);
        box.setPrefWidth(240);
        box.setPadding(new Insets(20));
        box.setStyle(
            "-fx-background-color: " + SIDE_BG + ";" +
            "-fx-border-color: " + DIM + "; -fx-border-width: 0 0 0 1;"
        );

        Label header = sideHeader("OPPONENT MONITOR", PINK);

        p2NameLbl.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 16px; -fx-text-fill: white;");
        styleSideLabel(p2TypeLbl);
        styleSideLabel(p2OrigRoleLbl);
        p2CurrRoleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + PINK + ";");
        p2EnergyLbl.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 13px; -fx-text-fill: " + PINK + ";");
        p2Bar.setPrefWidth(200);
        p2Bar.setStyle("-fx-accent: " + PINK + ";");
        styleSideLabel(p2PosLbl);

        Label statusHdr = microLabel("STATUS", PINK);
        p2StatusLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: " + GREEN + ";");
        p2StatusLbl.setWrapText(true);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        box.getChildren().addAll(
            header, dimSep(),
            p2NameLbl, p2TypeLbl, p2OrigRoleLbl, p2CurrRoleLbl,
            p2EnergyLbl, p2Bar, p2PosLbl,
            statusHdr, p2StatusLbl,
            spacer, buildCardPanel()
        );
        return box;
    }

    private VBox buildCardPanel() {
        VBox panel = new VBox(8);

        HBox deckRow = new HBox(8);
        deckRow.setAlignment(Pos.CENTER_LEFT);
        deckRow.setPadding(new Insets(8));
        deckRow.setStyle(
            "-fx-background-color: #162012;" +
            "-fx-border-color: " + RED_CARD + "; -fx-border-width: 1;" +
            "-fx-background-radius: 6; -fx-border-radius: 6;"
        );
        Label deckHdr = new Label("FACTORY DECK");
        deckHdr.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 10px; -fx-text-fill: " + RED_CARD + ";");
        deckCountLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #D4C9A8;");
        deckRow.getChildren().addAll(deckHdr, deckCountLbl);

        VBox drawnBox = new VBox(5);
        drawnBox.setPadding(new Insets(10));
        drawnBox.setStyle(
            "-fx-background-color: " + BG + ";" +
            "-fx-border-color: " + GOLD + "; -fx-border-width: 1;" +
            "-fx-background-radius: 6; -fx-border-radius: 6;"
        );
        Label drawnHdr = new Label("LAST DRAWN CARD");
        drawnHdr.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 9px; -fx-text-fill: " + GRAY + ";");
        cardNameLbl.setStyle("-fx-font-family: 'Segoe UI Black'; -fx-font-size: 12px; -fx-text-fill: " + GOLD + ";");
        cardNameLbl.setWrapText(true);
        cardDescLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: " + GRAY + ";");
        cardDescLbl.setWrapText(true);

        drawnBox.getChildren().addAll(drawnHdr, cardNameLbl, cardDescLbl);
        panel.getChildren().addAll(deckRow, drawnBox);
        return panel;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Label sideHeader(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 13px;" +
            "-fx-text-fill: " + color + "; -fx-letter-spacing: 1px;"
        );
        return lbl;
    }

    private Label microLabel(String text, String color) {
        Label lbl = new Label(text);
        lbl.setStyle(
            "-fx-font-family: 'Segoe UI Black'; -fx-font-size: 10px;" +
            "-fx-text-fill: " + color + "; -fx-letter-spacing: 0.5;"
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

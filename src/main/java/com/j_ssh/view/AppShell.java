package com.j_ssh.view;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.objects.JScene;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.Desktop;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Window chrome: sidebar (navigation, open terminals, connection summary) plus the page area. */
public class AppShell extends BorderPane {
    private static final Pattern WEB_ADDRESS = Pattern.compile("https?://\\S+");

    private final StackPane pages = new StackPane();
    private final StackPane loadingOverlay = new StackPane();
    private final StackPane messageOverlay = new StackPane();
    private final TextFlow messageFlow = new TextFlow();
    private final VBox terminalTabs = new VBox(2);
    private final VBox connectionCard = new VBox(2);
    private final Label connectionName = new Label();
    private final Label connectionDetail = new Label();

    private Button sessionsButton;
    private Button actionsButton;
    private Button triggersButton;
    private Button terminalButton;
    private Button settingsButton;
    private Button helpButton;
    private StackPane overlayHost;

    private Runnable messageClose;
    private String visibleMessage;
    private String recentMessage;
    private long recentMessageAt;

    public AppShell() {
        getStyleClass().add("app-shell");
        setLeft(buildSidebar());

        pages.getStyleClass().add("content-frame");
        buildLoadingOverlay();

        StackPane frame = new StackPane(pages, loadingOverlay);
        frame.getStyleClass().add("content-frame");
        setCenter(frame);
        buildMessageOverlay();
    }

    public void attachOverlayHost(StackPane host) {
        this.overlayHost = host;
    }

    /** Holder for one row per open terminal. The terminal page adds and removes rows here. */
    public VBox getTerminalTabs() {
        return terminalTabs;
    }

    /** Shows the "Connected" card in the sidebar. Pass null to hide it. */
    public void setConnectionSummary(String name, String detail) {
        boolean visible = name != null;
        connectionCard.setVisible(visible);
        connectionCard.setManaged(visible);
        connectionName.setText(name == null ? "" : name);
        connectionDetail.setText(detail == null ? "" : detail);
    }

    // ---------------------------------------------------------------- pages

    public void showPage(Node page) {
        if (page instanceof Region region) {
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        if (pages.getChildren().size() == 1 && pages.getChildren().get(0) == page) {
            return;
        }
        pages.getChildren().setAll(page);
    }

    public void setLoading(boolean loading) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setMouseTransparent(!loading);
    }

    public void setActive(JScene scene) {
        setActiveButton(sessionsButton, scene == JScene.SESSIONS || scene == JScene.DASHBOARD);
        setActiveButton(actionsButton, scene == JScene.ACTIONS);
        setActiveButton(triggersButton, scene == JScene.TRIGGERS);
        setActiveButton(terminalButton, scene == JScene.TERMINAL);
        setActiveButton(settingsButton, scene == JScene.SETTINGS);
        setActiveButton(helpButton, scene == JScene.HELP);
    }

    private void setActiveButton(Button button, boolean active) {
        if (active) {
            if (!button.getStyleClass().contains("nav-active")) {
                button.getStyleClass().add("nav-active");
            }
        } else {
            button.getStyleClass().remove("nav-active");
        }
    }

    // -------------------------------------------------------------- sidebar

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");

        StackPane mark = new StackPane(UiKit.icon(UiKit.PROMPT));
        mark.getStyleClass().add("brand-mark");
        Label title = new Label("J-SSH");
        title.getStyleClass().add("brand-title");
        HBox header = new HBox(10, mark, title);
        header.getStyleClass().add("sidebar-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label section = new Label("Workspace");
        section.getStyleClass().add("sidebar-label");

        sessionsButton = navButton(UiKit.MONITOR, "Sessions", null, () -> MainApp.get().changeScene(JScene.SESSIONS));
        actionsButton = navButton(UiKit.PLAY, "Actions", null, () -> MainApp.get().changeScene(JScene.ACTIONS));
        triggersButton = navButton(UiKit.BOLT, "Triggers", null, () -> MainApp.get().changeScene(JScene.TRIGGERS));

        Label terminalDot = new Label("\u25CF");
        terminalDot.getStyleClass().add("nav-dot");
        terminalDot.visibleProperty().bind(Bindings.isNotEmpty(terminalTabs.getChildren()));
        terminalButton = navButton(UiKit.PROMPT, "Terminal", terminalDot, () -> MainApp.get().changeScene(JScene.TERMINAL));

        terminalTabs.getStyleClass().add("terminal-tabs");
        terminalTabs.visibleProperty().bind(Bindings.isNotEmpty(terminalTabs.getChildren()));
        terminalTabs.managedProperty().bind(Bindings.isNotEmpty(terminalTabs.getChildren()));

        VBox nav = new VBox(2, sessionsButton, actionsButton, triggersButton, terminalButton, terminalTabs);
        nav.getStyleClass().add("sidebar-nav");

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);

        Label connected = new Label("\u25CF  Connected");
        connected.getStyleClass().add("conn-status");
        connectionName.getStyleClass().add("conn-name");
        connectionDetail.getStyleClass().add("conn-detail");
        connectionCard.getChildren().addAll(connected, connectionName, connectionDetail);
        connectionCard.getStyleClass().add("conn-card");
        setConnectionSummary(null, null);

        settingsButton = navButton("\uE713", "Settings", null, () -> MainApp.get().changeScene(JScene.SETTINGS));
        helpButton = navButton("\uE897", "Help", null, () -> MainApp.get().changeScene(JScene.HELP));
        VBox footer = new VBox(2, settingsButton, helpButton);
        footer.getStyleClass().add("sidebar-footer");

        VBox connectionHolder = new VBox(connectionCard);
        connectionHolder.setPadding(new Insets(0, 10, 8, 10));

        sidebar.getChildren().addAll(header, section, nav, grow, connectionHolder, footer);
        return sidebar;
    }

    private Button navButton(String icon, String title, Node trailing, Runnable action) {
        Button button = new Button();
        button.getStyleClass().add("nav-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Label iconLabel = UiKit.icon(icon);
        iconLabel.getStyleClass().add("nav-icon");
        iconLabel.setMinWidth(18);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("nav-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox graphic = new HBox(10, iconLabel, titleLabel, spacer);
        if (trailing != null) {
            graphic.getChildren().add(trailing);
        }
        graphic.setAlignment(Pos.CENTER_LEFT);
        button.setGraphic(graphic);
        button.setOnAction(event -> action.run());
        return button;
    }

    // ------------------------------------------------------------- overlays

    private void buildLoadingOverlay() {
        loadingOverlay.getStyleClass().add("loading-overlay");
        loadingOverlay.setVisible(false);
        loadingOverlay.setMouseTransparent(true);

        VBox card = new VBox(14);
        card.getStyleClass().add("loading-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(36, 36);
        Label message = new Label("Connecting to server");
        card.getChildren().addAll(spinner, message);
        loadingOverlay.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    public void showServerMessage(String message, Runnable onClose) {
        String text = message == null ? "" : message.trim();
        boolean sameAsOpen = text.equals(visibleMessage);
        boolean sameAsRecent = text.equals(recentMessage) && System.currentTimeMillis() - recentMessageAt < 8000;
        if (sameAsOpen || sameAsRecent) {
            if (onClose != null) {
                onClose.run();
            }
            return;
        }
        this.visibleMessage = text;
        this.messageClose = onClose;
        messageFlow.getChildren().setAll(messageNodes(text));
        if (overlayHost != null && !overlayHost.getChildren().contains(messageOverlay)) {
            overlayHost.getChildren().add(messageOverlay);
        }
        messageOverlay.setVisible(true);
        messageOverlay.setMouseTransparent(false);
        messageOverlay.requestFocus();
    }

    private void dismissServerMessage() {
        messageOverlay.setVisible(false);
        messageOverlay.setMouseTransparent(true);
        recentMessage = visibleMessage;
        recentMessageAt = System.currentTimeMillis();
        visibleMessage = null;
        Runnable done = messageClose;
        messageClose = null;
        if (done != null) {
            done.run();
        }
    }

    private void buildMessageOverlay() {
        messageOverlay.getStyleClass().add("message-overlay");
        messageOverlay.setVisible(false);
        messageOverlay.setMouseTransparent(true);
        messageOverlay.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox card = new VBox(14);
        card.getStyleClass().add("message-card");
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setPrefWidth(420);
        card.setMinWidth(320);

        Label title = new Label("Server message");
        title.getStyleClass().add("message-title");
        Label subtitle = new Label("The server sent this before the session continued.");
        subtitle.setWrapText(true);
        subtitle.getStyleClass().add("message-subtitle");

        messageFlow.getStyleClass().add("message-flow");
        messageFlow.setMaxWidth(412);

        Button ok = UiKit.button("OK", null, "btn-primary");
        ok.setDefaultButton(true);
        ok.setOnAction(event -> dismissServerMessage());
        HBox actions = new HBox(ok);
        actions.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(title, subtitle, messageFlow, actions);
        messageOverlay.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);
    }

    private java.util.List<Node> messageNodes(String message) {
        java.util.List<Node> nodes = new java.util.ArrayList<>();
        Matcher matcher = WEB_ADDRESS.matcher(message);
        int cursor = 0;
        while (matcher.find()) {
            if (matcher.start() > cursor) {
                nodes.add(messageText(message.substring(cursor, matcher.start())));
            }
            String link = trimTrailingPunctuation(matcher.group());
            Hyperlink hyperlink = new Hyperlink(link);
            hyperlink.getStyleClass().add("message-link");
            hyperlink.setOnAction(event -> openLink(link));
            nodes.add(hyperlink);
            cursor = matcher.start() + link.length();
        }
        if (cursor < message.length()) {
            nodes.add(messageText(message.substring(cursor)));
        }
        if (nodes.isEmpty()) {
            nodes.add(messageText(message));
        }
        return nodes;
    }

    private Text messageText(String value) {
        Text text = new Text(value);
        text.getStyleClass().add("message-text");
        return text;
    }

    private String trimTrailingPunctuation(String link) {
        return link.replaceAll("[.,;:)]+$", "");
    }

    private void openLink(String link) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI.create(link));
            }
        } catch (Exception ignored) {
        }
    }
}

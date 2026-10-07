package com.j_ssh.view;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.objects.JScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppShell extends BorderPane {
    private final StackPane pages = new StackPane();
    private final StackPane loadingOverlay = new StackPane();
    private Button sessionsButton;
    private Button actionsButton;
    private Button triggersButton;
    private Button terminalButton;
    private Button settingsButton;
    private Button helpButton;
    private StackPane overlayHost;
    private final StackPane messageOverlay = new StackPane();
    private final TextFlow messageFlow = new TextFlow();
    private Runnable messageClose;
    private String visibleMessage;
    private String recentMessage;
    private long recentMessageAt;
    private static final Pattern WEB_ADDRESS = Pattern.compile("https?://\\S+");

    public AppShell() {
        getStyleClass().add("app-shell");
        setLeft(buildSidebar());

        pages.getStyleClass().add("content-frame");
        loadingOverlay.getStyleClass().add("loading-overlay");
        loadingOverlay.setVisible(false);
        loadingOverlay.setMouseTransparent(true);

        VBox card = new VBox(14);
        card.getStyleClass().add("loading-card");
        card.setAlignment(Pos.CENTER);
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(42, 42);
        Label message = new Label("Connecting to server");
        card.getChildren().addAll(spinner, message);
        loadingOverlay.getChildren().add(card);
        StackPane.setAlignment(card, Pos.CENTER);

        StackPane frame = new StackPane(pages, loadingOverlay);
        frame.getStyleClass().add("content-frame");
        setCenter(frame);
        buildMessageOverlay();
    }

    public void attachOverlayHost(StackPane host) {
        this.overlayHost = host;
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

        Button ok = new Button("OK");
        ok.getStyleClass().add("settings-primary-btn");
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

    public void showPage(Node page) {
        if (page instanceof Region region) {
            region.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
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

    public void setTerminalEnabled(boolean enabled) {
        terminalButton.setDisable(!enabled);
        if (!enabled) {
            terminalButton.getStyleClass().remove("nav-active");
        }
    }

    private void setActiveButton(Button button, boolean active) {
        if (button == null) {
            return;
        }
        if (active) {
            if (!button.getStyleClass().contains("nav-active")) {
                button.getStyleClass().add("nav-active");
            }
        } else {
            button.getStyleClass().remove("nav-active");
        }
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");

        HBox header = new HBox(12);
        header.getStyleClass().add("sidebar-header");
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane mark = new StackPane();
        mark.getStyleClass().add("brand-mark");
        Label markIcon = new Label("\uE756");
        markIcon.getStyleClass().add("brand-mark-icon");
        mark.getChildren().add(markIcon);

        VBox titles = new VBox(2);
        Label title = new Label("J-SSH");
        title.getStyleClass().add("brand-title");
        Label subtitle = new Label("SSH Client");
        subtitle.getStyleClass().add("brand-subtitle");
        titles.getChildren().addAll(title, subtitle);
        header.getChildren().addAll(mark, titles);

        VBox nav = new VBox(6);
        nav.getStyleClass().add("sidebar-nav");
        VBox.setVgrow(nav, Priority.ALWAYS);

        sessionsButton = navButton("\uE7F4", "Sessions", "Manage SSH connections", () -> MainApp.get().changeScene(JScene.SESSIONS));
        actionsButton = navButton("\uE768", "Actions", "Reusable commands", () -> MainApp.get().changeScene(JScene.ACTIONS));
        triggersButton = navButton("\uE7C8", "Triggers", "Automated sequences", () -> MainApp.get().changeScene(JScene.TRIGGERS));
        terminalButton = navButton("\uE756", "Terminal", "Active session", () -> MainApp.get().changeScene(JScene.TERMINAL));
        terminalButton.setDisable(true);
        nav.getChildren().addAll(sessionsButton, actionsButton, triggersButton, terminalButton);

        VBox footer = new VBox(4);
        footer.getStyleClass().add("sidebar-footer");
        settingsButton = navButton("\uE713", "Settings", "App and saved data", () -> MainApp.get().changeScene(JScene.SETTINGS));
        helpButton = navButton("\uE897", "Help", "How to use J-SSH", () -> MainApp.get().changeScene(JScene.HELP));
        footer.getChildren().addAll(settingsButton, helpButton);

        sidebar.getChildren().addAll(header, nav, footer);
        return sidebar;
    }

    private Button navButton(String icon, String title, String description, Runnable action) {
        Button button = new Button();
        button.getStyleClass().add("nav-item");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("nav-icon");
        iconLabel.setMinWidth(20);

        VBox text = new VBox(1);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("nav-title");
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("nav-desc");
        text.getChildren().addAll(titleLabel, descLabel);

        HBox graphic = new HBox(12, iconLabel, text);
        graphic.setAlignment(Pos.CENTER_LEFT);
        graphic.setPadding(new Insets(2, 0, 2, 0));
        button.setGraphic(graphic);
        button.setOnAction(event -> action.run());
        return button;
    }
}

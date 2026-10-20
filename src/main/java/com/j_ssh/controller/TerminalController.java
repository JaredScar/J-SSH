package com.j_ssh.controller;

import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.ConnectionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.view.UiKit;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * The terminal page. Every open SSH terminal also gets a row in the sidebar (see
 * {@link com.j_ssh.view.AppShell#getTerminalTabs()}); double-click a row to rename it.
 */
public class TerminalController extends BorderPane {
    private static final String NAME_KEY = "sessionName";
    private static final String ROW_KEY = "sidebarRow";
    private static final String LABEL_KEY = "sidebarLabel";
    private static final String HOST_KEY = "sidebarNameHost";
    private static final int MAX_NAME_LENGTH = 40;

    private final StackPane terminalHost = new StackPane();
    private final List<TerminalTabComponent> openTerminals = new ArrayList<>();
    private final ActionManager actionManager;
    private final VBox emptyState;
    private final Label nameLabel = new Label("No session selected");
    private final Label addressLabel = new Label();
    private final Label statusDot = new Label("\u25CF");
    private Button clearButton;
    private Button disconnectButton;
    private VBox actionsContainer;
    private TerminalTabComponent activeTerminal;

    public TerminalController() {
        this.actionManager = ActionManager.get();
        getStyleClass().add("terminal-container");

        terminalHost.getStyleClass().add("terminal-host");
        terminalHost.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        terminalHost.setMinSize(0, 0);

        Button toSessions = UiKit.button("Go to Sessions", null, "btn-secondary");
        toSessions.setOnAction(event -> MainApp.get().changeScene(JScene.SESSIONS));
        emptyState = UiKit.emptyState(UiKit.PROMPT, "No active session",
                "Connect from Sessions to open a terminal.", toSessions);
        emptyState.getStyleClass().add("terminal-empty");

        StackPane body = new StackPane(terminalHost, emptyState);
        body.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox column = new VBox(createHeader(), body);
        VBox.setVgrow(body, Priority.ALWAYS);
        column.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setCenter(column);
        setRight(createQuickActions());
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        refreshEmptyState();
    }

    // ---------------------------------------------------------------- layout

    private HBox createHeader() {
        statusDot.getStyleClass().addAll("status-dot", "dot-on");
        nameLabel.getStyleClass().add("terminal-name");
        addressLabel.getStyleClass().add("terminal-address");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        clearButton = UiKit.button("Clear", UiKit.ERASER, "btn-ghost");
        clearButton.setOnAction(event -> {
            if (activeTerminal != null) {
                activeTerminal.clearScreen();
            }
        });
        disconnectButton = UiKit.button("Disconnect", UiKit.POWER, "btn-ghost");
        disconnectButton.setOnAction(event -> {
            if (activeTerminal != null) {
                closeTerminal(activeTerminal);
            }
        });

        HBox bar = new HBox(10, statusDot, nameLabel, addressLabel, spacer, clearButton, disconnectButton);
        bar.getStyleClass().add("terminal-chrome");
        bar.setAlignment(Pos.CENTER_LEFT);
        return bar;
    }

    private VBox createQuickActions() {
        Label title = new Label("Quick actions");
        title.getStyleClass().add("quick-title");

        actionsContainer = new VBox();
        ScrollPane scroll = new ScrollPane(actionsContainer);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox panel = new VBox(title, scroll);
        panel.getStyleClass().add("quick-panel");
        panel.setPrefWidth(260);
        panel.setMinWidth(240);
        panel.setMaxWidth(280);
        loadQuickActions();
        return panel;
    }

    public void refreshActions() {
        if (actionsContainer != null) {
            loadQuickActions();
        }
    }

    private void loadQuickActions() {
        actionsContainer.getChildren().clear();
        List<ActionData> actions = actionManager.getAllActions();
        if (actions.isEmpty()) {
            Label none = new Label("No actions yet. Create one on the Actions page.");
            none.getStyleClass().add("quick-empty");
            none.setWrapText(true);
            actionsContainer.getChildren().add(none);
            return;
        }
        for (ActionData action : actions) {
            actionsContainer.getChildren().add(createQuickRow(action));
        }
    }

    private HBox createQuickRow(ActionData action) {
        Label name = new Label(action.getName());
        name.getStyleClass().add("quick-name");
        Label detail = new Label(action.getCommands().size() == 1
                ? "1 command" : action.getCommands().size() + " commands");
        detail.getStyleClass().add("quick-desc");
        VBox text = new VBox(2, name, detail);
        HBox.setHgrow(text, Priority.ALWAYS);

        Label play = UiKit.icon(UiKit.PLAY);
        play.getStyleClass().add("quick-play");

        HBox row = new HBox(8, text, play);
        row.getStyleClass().add("quick-row");
        row.setAlignment(Pos.CENTER_LEFT);
        row.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                runAction(action);
            }
        });
        return row;
    }

    private void refreshEmptyState() {
        boolean hasTabs = !openTerminals.isEmpty();
        emptyState.setVisible(!hasTabs);
        emptyState.setManaged(!hasTabs);
        terminalHost.setVisible(hasTabs);
        terminalHost.setManaged(hasTabs);
        statusDot.setVisible(hasTabs);
        clearButton.setDisable(!hasTabs);
        disconnectButton.setDisable(!hasTabs);
        if (!hasTabs) {
            nameLabel.setText("No session selected");
            addressLabel.setText("");
        }
    }

    // ------------------------------------------------------- tabs / sessions

    public void addTerminalTab(TerminalTabComponent tab) {
        String nickname = tab.getNickname() == null || tab.getNickname().isBlank()
                ? "Terminal" : tab.getNickname();
        String sessionName = uniqueName(nickname, null);
        tab.getProperties().put(NAME_KEY, sessionName);
        tab.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        openTerminals.add(tab);
        terminalHost.getChildren().add(tab);

        HBox row = createSidebarRow(tab, sessionName);
        tab.getProperties().put(ROW_KEY, row);
        MainApp.get().getAppShell().getTerminalTabs().getChildren().add(row);

        showTerminal(tab);
        refreshEmptyState();
    }

    private String uniqueName(String wanted, TerminalTabComponent ignore) {
        String candidate = wanted;
        int count = 2;
        while (nameTaken(candidate, ignore)) {
            candidate = wanted + " (" + count + ")";
            count++;
        }
        return candidate;
    }

    private boolean nameTaken(String name, TerminalTabComponent ignore) {
        for (TerminalTabComponent open : openTerminals) {
            if (open != ignore && name.equals(open.getProperties().get(NAME_KEY))) {
                return true;
            }
        }
        return false;
    }

    private HBox createSidebarRow(TerminalTabComponent tab, String name) {
        Label dot = new Label("\u25CF");
        dot.getStyleClass().add("term-dot");

        Label label = new Label(name);
        label.getStyleClass().add("term-name");
        label.setMaxWidth(Double.MAX_VALUE);
        StackPane nameHost = new StackPane(label);
        nameHost.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameHost, Priority.ALWAYS);

        Button close = UiKit.iconButton(UiKit.CLOSE, "Close terminal");
        close.getStyleClass().add("term-tab-close");
        close.setOnAction(event -> closeTerminal(tab));

        HBox row = new HBox(8, dot, nameHost, close);
        row.getStyleClass().add("term-tab");
        row.setAlignment(Pos.CENTER_LEFT);
        Tooltip.install(row, new Tooltip("Double-click to rename"));

        row.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            if (event.getClickCount() >= 2) {
                beginRename(tab);
            } else {
                showTerminal(tab);
                MainApp.get().changeScene(JScene.TERMINAL);
            }
        });

        ContextMenu menu = new ContextMenu(
                UiKit.menuItem("Rename", () -> beginRename(tab)),
                UiKit.menuItem("Close terminal", () -> closeTerminal(tab)));
        row.setOnContextMenuRequested(event -> {
            menu.show(row, event.getScreenX(), event.getScreenY());
            event.consume();
        });

        tab.getProperties().put(LABEL_KEY, label);
        tab.getProperties().put(HOST_KEY, nameHost);
        return row;
    }

    private void beginRename(TerminalTabComponent tab) {
        Object labelObject = tab.getProperties().get(LABEL_KEY);
        Object hostObject = tab.getProperties().get(HOST_KEY);
        if (!(labelObject instanceof Label label) || !(hostObject instanceof StackPane host)) {
            return;
        }
        if (host.getChildren().size() == 1 && host.getChildren().get(0) instanceof TextField) {
            return;
        }

        TextField field = new TextField(label.getText());
        field.getStyleClass().add("term-rename-field");
        field.setMaxWidth(Double.MAX_VALUE);
        boolean[] finished = {false};

        Runnable commit = () -> {
            if (finished[0]) {
                return;
            }
            finished[0] = true;
            applyRename(tab, field.getText());
            host.getChildren().setAll(label);
        };
        Runnable cancel = () -> {
            if (finished[0]) {
                return;
            }
            finished[0] = true;
            host.getChildren().setAll(label);
        };

        field.setOnAction(event -> commit.run());
        field.focusedProperty().addListener((obs, was, focused) -> {
            if (!focused) {
                commit.run();
            }
        });
        field.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                cancel.run();
                event.consume();
            }
        });

        host.getChildren().setAll(field);
        Platform.runLater(() -> {
            field.requestFocus();
            field.selectAll();
        });
    }

    private void applyRename(TerminalTabComponent tab, String requested) {
        String trimmed = requested == null ? "" : requested.trim();
        if (trimmed.isEmpty()) {
            return;
        }
        if (trimmed.length() > MAX_NAME_LENGTH) {
            trimmed = trimmed.substring(0, MAX_NAME_LENGTH).trim();
        }
        String finalName = uniqueName(trimmed, tab);
        tab.getProperties().put(NAME_KEY, finalName);
        Object labelObject = tab.getProperties().get(LABEL_KEY);
        if (labelObject instanceof Label label) {
            label.setText(finalName);
        }
        if (tab == activeTerminal) {
            nameLabel.setText(finalName);
            MainApp.get().getAppShell().setConnectionSummary(finalName, addressLabel.getText());
        }
    }

    private void showTerminal(TerminalTabComponent tab) {
        activeTerminal = tab;
        for (TerminalTabComponent open : openTerminals) {
            boolean selected = open == tab;
            open.setVisible(selected);
            open.setManaged(selected);
            Object row = open.getProperties().get(ROW_KEY);
            if (row instanceof HBox box) {
                if (selected && !box.getStyleClass().contains("term-tab-active")) {
                    box.getStyleClass().add("term-tab-active");
                } else if (!selected) {
                    box.getStyleClass().remove("term-tab-active");
                }
            }
        }
        Object name = tab.getProperties().get(NAME_KEY);
        String shownName = name == null ? "Terminal" : name.toString();
        Connection connection = tab.getConnection();
        String address = connection == null ? "" : connection.getUsername() + "@" + connection.getHost();
        nameLabel.setText(shownName);
        addressLabel.setText(address);
        MainApp.get().getAppShell().setConnectionSummary(shownName, address);
    }

    private void closeTerminal(TerminalTabComponent tab) {
        ConnectionManager.get().removeConnection(tab);
        tab.close();
        openTerminals.remove(tab);
        terminalHost.getChildren().remove(tab);
        Object row = tab.getProperties().get(ROW_KEY);
        if (row instanceof HBox box) {
            MainApp.get().getAppShell().getTerminalTabs().getChildren().remove(box);
        }
        boolean wasShowing = getScene() != null;
        if (openTerminals.isEmpty()) {
            activeTerminal = null;
            MainApp.get().getAppShell().setConnectionSummary(null, null);
            refreshEmptyState();
            if (wasShowing) {
                MainApp.get().changeScene(JScene.SESSIONS);
            }
        } else {
            showTerminal(openTerminals.get(openTerminals.size() - 1));
            refreshEmptyState();
        }
    }

    public void closeTerminalTab(TerminalTabComponent tab) {
        tab.close();
    }

    // ------------------------------------------- helpers for the other pages

    public boolean hasActiveTerminal() {
        return activeTerminal != null;
    }

    private TerminalTabComponent findTab(Connection connection) {
        for (TerminalTabComponent open : openTerminals) {
            if (open.getConnection() == connection) {
                return open;
            }
        }
        return null;
    }

    /** Brings the terminal that belongs to this connection to the front. */
    public void focusConnection(Connection connection) {
        TerminalTabComponent tab = findTab(connection);
        if (tab != null) {
            showTerminal(tab);
        }
        MainApp.get().changeScene(JScene.TERMINAL);
    }

    /** Closes the terminal that belongs to this connection (or just drops the link if none). */
    public void closeConnection(Connection connection) {
        TerminalTabComponent tab = findTab(connection);
        if (tab != null) {
            closeTerminal(tab);
        } else if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    /** Runs an action in the terminal that is currently selected. */
    public void runAction(ActionData action) {
        if (activeTerminal == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No terminal open");
            alert.setHeaderText("No terminal open");
            alert.setContentText("Connect to a session first, then run the action.");
            MainApp.theme(alert);
            alert.showAndWait();
            return;
        }
        dispatch(action, activeTerminal);
    }

    /** Runs an action in the terminal for a specific connection. Returns false if none is open. */
    public boolean runActionOn(Connection connection, ActionData action) {
        TerminalTabComponent tab = findTab(connection);
        if (tab == null) {
            return false;
        }
        dispatch(action, tab);
        return true;
    }

    private void dispatch(ActionData action, TerminalTabComponent tab) {
        Thread runner = new Thread(() -> actionManager.executeAction(action, tab), "action-runner");
        runner.setDaemon(true);
        runner.start();
    }
}

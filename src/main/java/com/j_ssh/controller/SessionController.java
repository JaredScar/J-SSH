package com.j_ssh.controller;

import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.AsyncManager;
import com.j_ssh.model.managers.ConnectionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.view.UiKit;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionController extends BorderPane {
    private final SessionManager sessionManager;
    private final ConnectionManager connectionManager;
    private final Set<ServerData> connecting = new HashSet<>();
    private final TextField searchField = new TextField();
    private final VBox listHolder = new VBox();

    public SessionController() {
        this.sessionManager = SessionManager.get();
        this.connectionManager = ConnectionManager.get();
        getStyleClass().add("page");

        searchField.textProperty().addListener((obs, oldValue, newValue) -> render());
        Button addButton = UiKit.button("New session", UiKit.PLUS, "btn-primary");
        addButton.setOnAction(event -> showAddSessionDialog());

        setTop(UiKit.pageHeader("Sessions", "Saved SSH connections",
                UiKit.searchBox(searchField, "Search sessions"), addButton));
        setCenter(UiKit.scrollPage(listHolder));
        render();
    }

    private void render() {
        listHolder.getChildren().clear();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        List<ServerData> sessions = sessionManager.getAllSessions().stream()
                .filter(session -> query.isEmpty()
                        || session.getNickname().toLowerCase().contains(query)
                        || session.getIp().toLowerCase().contains(query)
                        || session.getUsername().toLowerCase().contains(query))
                .toList();

        if (sessions.isEmpty()) {
            Button add = UiKit.button("New session", UiKit.PLUS, "btn-primary");
            add.setOnAction(event -> showAddSessionDialog());
            boolean searching = !query.isEmpty();
            listHolder.getChildren().add(UiKit.emptyState(UiKit.MONITOR,
                    searching ? "No matching sessions" : "No sessions yet",
                    searching ? "Try a different search." : "Add your first SSH connection to get started.",
                    searching ? null : add));
            return;
        }

        VBox card = new VBox();
        card.getStyleClass().add("list-card");
        Label head = new Label("Name");
        head.getStyleClass().add("list-head");
        card.getChildren().add(head);
        for (ServerData session : sessions) {
            card.getChildren().add(createRow(session));
        }
        listHolder.getChildren().add(card);
    }

    private HBox createRow(ServerData session) {
        boolean connected = connectionManager.isServerConnected(session);
        boolean busy = connecting.contains(session);

        Label dot = new Label("\u25CF");
        dot.getStyleClass().addAll("status-dot", connected ? "dot-on" : busy ? "dot-busy" : "dot-off");

        Label name = new Label(session.getNickname());
        name.getStyleClass().add("row-title");
        HBox nameLine = new HBox(8, name);
        nameLine.setAlignment(Pos.CENTER_LEFT);
        if (connected) {
            Label badge = new Label("Active");
            badge.getStyleClass().add("badge-active");
            nameLine.getChildren().add(badge);
        }
        Label address = new Label(session.getUsername() + "@" + session.getIp());
        address.getStyleClass().add("row-sub");
        VBox info = new VBox(3, nameLine, address);
        HBox.setHgrow(info, Priority.ALWAYS);

        HBox actions = new HBox(6);
        actions.setAlignment(Pos.CENTER_RIGHT);
        if (connected) {
            Button open = UiKit.button("Open", null, "btn-secondary");
            open.setOnAction(event -> openSession(session));
            Button disconnect = UiKit.button("Disconnect", null, "btn-ghost");
            disconnect.setOnAction(event -> disconnectFromSession(session));
            actions.getChildren().addAll(open, disconnect);
        } else {
            Button connect = UiKit.button(busy ? "Connecting..." : "Connect", null, "btn-secondary");
            connect.setDisable(busy);
            connect.setOnAction(event -> connectToSession(session));
            actions.getChildren().add(connect);
        }

        Button more = UiKit.moreButton(
                UiKit.menuItem("Edit", () -> editSession(session)),
                UiKit.menuItem("Delete", () -> deleteSession(session)));

        HBox row = new HBox(14, dot, info, actions, more);
        row.getStyleClass().add("list-row");
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private void openSession(ServerData session) {
        Connection connection = connectionManager.getServerConnection(session);
        MainApp.get().getTerminalController().focusConnection(connection);
    }

    private void connectToSession(ServerData session) {
        System.out.println("Connecting to: " + session.getNickname());
        connecting.add(session);
        render();

        MainApp.get().changeScene(JScene.LOADING);

        Thread connectionThread = new Thread(() -> {
            Connection connection = new Connection(session.getUsername(), session.getIp(), session.getPassword(), 22);
            connection.addKnownHost();

            Platform.runLater(() -> {
                boolean connected = connection.connect();
                connecting.remove(session);
                if (connected && connection.start()) {
                    TerminalTabComponent termTab = new TerminalTabComponent(session.getNickname(), connection);
                    connectionManager.addConnection(termTab, connection);
                    MainApp.get().getTerminalController().addTerminalTab(termTab);
                    render();
                    MainApp.get().changeScene(JScene.TERMINAL);
                } else {
                    MainApp.get().changeScene(JScene.SESSIONS);
                    render();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection Failed");
                    alert.setHeaderText("Failed to connect to " + session.getNickname());
                    alert.setContentText(connection.error() != null ? connection.error() : "Unknown connection error");
                    MainApp.theme(alert);
                    alert.showAndWait();
                }
            });
        });

        AsyncManager.get().addThread(connectionThread);
    }

    private void disconnectFromSession(ServerData session) {
        Connection connection = connectionManager.getServerConnection(session);
        if (connection != null) {
            MainApp.get().getTerminalController().closeConnection(connection);
        }
        render();
    }

    /** Called when the Sessions page is shown again, so the status dots are current. */
    public void onViewActivated() {
        render();
    }

    public void refreshAllConnectionStatuses() {
        render();
    }

    private void editSession(ServerData session) {
        SessionDialog dialog = new SessionDialog("Edit Session", session);
        dialog.showAndWait().ifPresent(sessionData -> {
            if (sessionData != null) {
                sessionManager.updateSession(
                    session.getIndex(),
                    sessionData.getNickname(),
                    sessionData.getIconURL(),
                    sessionData.getIp(),
                    sessionData.getUsername(),
                    sessionData.getPassword(),
                    sessionData.getPrivateKeyLocation()
                );
                render();
            }
        });
    }

    private void deleteSession(ServerData session) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Session");
        alert.setHeaderText("Are you sure you want to delete this session?");
        alert.setContentText("Session: " + session.getNickname() + " (" + session.getIp() + ")");
        MainApp.theme(alert);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sessionManager.deleteSession(session.getIndex());
                render();
            }
        });
    }

    private void showAddSessionDialog() {
        SessionDialog dialog = new SessionDialog("Add New Session", null);
        dialog.showAndWait().ifPresent(sessionData -> {
            if (sessionData != null) {
                sessionManager.createSession(
                    sessionData.getNickname(),
                    sessionData.getIconURL(),
                    sessionData.getIp(),
                    sessionData.getUsername(),
                    sessionData.getPassword(),
                    sessionData.getPrivateKeyLocation()
                );
                render();
            }
        });
    }

    // Inner class for session dialog
    private static class SessionDialog extends Dialog<ServerData> {
        private TextField nicknameField;
        private TextField iconURLField;
        private TextField ipField;
        private TextField usernameField;
        private PasswordField passwordField;
        private TextField privateKeyField;

        public SessionDialog(String title, ServerData existingSession) {
            setTitle(title);
            setHeaderText(title);
            
            // Create form with modern styling
            VBox formContainer = new VBox();
            formContainer.setSpacing(16);
            formContainer.setPadding(new Insets(20));
            formContainer.getStyleClass().add("session-dialog");
            
            // Create form fields
            nicknameField = createStyledTextField("My Server");
            iconURLField = createStyledTextField("https://example.com/icon.png");
            ipField = createStyledTextField("192.168.1.100");
            usernameField = createStyledTextField("root");
            passwordField = createStyledPasswordField();
            privateKeyField = createStyledTextField("/path/to/private/key");

            // Add labeled fields
            formContainer.getChildren().addAll(
                createFieldGroup("Nickname", nicknameField),
                createFieldGroup("Icon URL (Optional)", iconURLField),
                createFieldGroup("Host/IP Address", ipField),
                createFieldGroup("Username", usernameField),
                createFieldGroup("Password", passwordField),
                createFieldGroup("Private Key Path (Optional)", privateKeyField)
            );

            // Pre-fill fields if editing
            if (existingSession != null) {
                nicknameField.setText(existingSession.getNickname());
                iconURLField.setText(existingSession.getIconURL() != null ? existingSession.getIconURL() : "");
                ipField.setText(existingSession.getIp());
                usernameField.setText(existingSession.getUsername());
                passwordField.setText(existingSession.getPassword());
                privateKeyField.setText(existingSession.getPrivateKeyLocation() != null ? existingSession.getPrivateKeyLocation() : "");
            }

            getDialogPane().setContent(formContainer);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            MainApp.theme(this);
            
            // Style the dialog buttons
            getDialogPane().getStyleClass().add("session-dialog-pane");

            setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new ServerData(
                        existingSession != null ? existingSession.getIndex() : -1,
                        nicknameField.getText(),
                        iconURLField.getText(),
                        ipField.getText(),
                        usernameField.getText(),
                        passwordField.getText(),
                        privateKeyField.getText()
                    );
                }
                return null;
            });
        }
        
        private TextField createStyledTextField(String promptText) {
            TextField field = new TextField();
            field.setPromptText(promptText);
            field.getStyleClass().add("session-dialog-field");
            return field;
        }
        
        private PasswordField createStyledPasswordField() {
            PasswordField field = new PasswordField();
            field.setPromptText("••••••••");
            field.getStyleClass().add("session-dialog-field");
            return field;
        }
        
        private VBox createFieldGroup(String labelText, Control field) {
            VBox group = new VBox();
            group.setSpacing(6);
            
            Label label = new Label(labelText);
            label.getStyleClass().add("session-dialog-label");
            
            group.getChildren().addAll(label, field);
            return group;
        }
    }
}

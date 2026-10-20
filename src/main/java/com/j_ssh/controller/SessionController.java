package com.j_ssh.controller;

import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.AsyncManager;
import com.j_ssh.model.managers.ConnectionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.model.objects.ServerData;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SessionController extends BorderPane {
    private FlowPane sessionsGrid;
    private SessionManager sessionManager;
    private ConnectionManager connectionManager;
    private TextField searchField;
    private Label sessionCountLabel;
    private Map<ServerData, Label> statusLabels = new HashMap<>();
    private Map<ServerData, Button> connectButtons = new HashMap<>();

    public SessionController() {
        this.sessionManager = SessionManager.get();
        this.connectionManager = ConnectionManager.get();
        initializeComponents();
        loadSessions();
    }

    private void initializeComponents() {
        this.getStyleClass().add("sessions-container");
        setTop(createHeaderSection());
        setCenter(createSessionsGrid());
    }

    private VBox createHeaderSection() {
        VBox headerContainer = new VBox();
        headerContainer.getStyleClass().add("sessions-header");
        headerContainer.setSpacing(20);
        headerContainer.setPadding(new Insets(24, 24, 0, 24));
        
        // Title and subtitle
        VBox titleSection = new VBox();
        titleSection.setSpacing(8);
        
        Label titleLabel = new Label("SSH Sessions");
        titleLabel.getStyleClass().add("sessions-title");
        
        Label subtitleLabel = new Label("Manage your server connections");
        subtitleLabel.getStyleClass().add("sessions-subtitle");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Search and action bar
        HBox actionBar = new HBox();
        actionBar.setSpacing(16);
        actionBar.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search sessions...");
        searchField.getStyleClass().add("sessions-search");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterSessions(newVal));
        
        // Session count label
        sessionCountLabel = new Label();
        sessionCountLabel.getStyleClass().add("sessions-count");
        
        // Add session button
        Button addButton = new Button("+ Add Session");
        addButton.getStyleClass().add("sessions-add-btn");
        addButton.setOnAction(e -> showAddSessionDialog());
        
        actionBar.getChildren().addAll(searchField, sessionCountLabel, new Region(), addButton);
        HBox.setHgrow(actionBar.getChildren().get(2), Priority.ALWAYS);
        
        headerContainer.getChildren().addAll(titleSection, actionBar);
        return headerContainer;
    }

    private ScrollPane createSessionsGrid() {
        sessionsGrid = new FlowPane();
        sessionsGrid.getStyleClass().add("sessions-grid");
        sessionsGrid.setHgap(16);
        sessionsGrid.setVgap(16);
        sessionsGrid.setPadding(new Insets(24));

        ScrollPane scroll = new ScrollPane(sessionsGrid);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.viewportBoundsProperty().addListener((obs, oldBounds, bounds) ->
                sessionsGrid.setPrefWrapLength(Math.max(280, bounds.getWidth() - 8)));
        return scroll;
    }

    private void loadSessions() {
        sessionsGrid.getChildren().clear();
        statusLabels.clear();
        connectButtons.clear();
        
        List<ServerData> sessions = sessionManager.getAllSessions();
        
        if (sessions.isEmpty()) {
            showEmptyState();
        } else {
            for (ServerData session : sessions) {
                VBox sessionCard = createSessionCard(session);
                sessionsGrid.getChildren().add(sessionCard);
            }
        }
        
        updateSessionCount(sessions.size());
    }
    
    private void filterSessions(String searchText) {
        sessionsGrid.getChildren().clear();
        List<ServerData> sessions = sessionManager.getAllSessions();
        
        if (searchText == null || searchText.trim().isEmpty()) {
            loadSessions();
            return;
        }
        
        String lowerSearchText = searchText.toLowerCase();
        List<ServerData> filteredSessions = sessions.stream()
            .filter(session -> 
                session.getNickname().toLowerCase().contains(lowerSearchText) ||
                session.getIp().toLowerCase().contains(lowerSearchText) ||
                session.getUsername().toLowerCase().contains(lowerSearchText)
            )
            .collect(java.util.stream.Collectors.toList());
        
        if (filteredSessions.isEmpty()) {
            showEmptyState();
        } else {
            for (ServerData session : filteredSessions) {
                VBox sessionCard = createSessionCard(session);
                sessionsGrid.getChildren().add(sessionCard);
            }
        }
        
        updateSessionCount(filteredSessions.size());
    }
    
    private VBox createSessionCard(ServerData session) {
        VBox card = new VBox();
        card.getStyleClass().add("session-card");
        card.setSpacing(18);
        card.setPrefWidth(300);
        card.setMinWidth(260);

        // Identity row: avatar + name/address
        StackPane avatar = createAvatar(session);

        Label nameLabel = new Label(session.getNickname());
        nameLabel.getStyleClass().add("session-name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label addressLabel = new Label(session.getUsername() + "@" + session.getIp());
        addressLabel.getStyleClass().add("session-address");
        addressLabel.setMaxWidth(Double.MAX_VALUE);

        VBox sessionInfo = new VBox(3, nameLabel, addressLabel);
        sessionInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(sessionInfo, Priority.ALWAYS);

        HBox header = new HBox(14, avatar, sessionInfo);
        header.setAlignment(Pos.CENTER_LEFT);

        // Status pill (dot + label) with icon buttons on the right
        HBox statusContainer = new HBox(7);
        statusContainer.getStyleClass().add("status-pill");
        statusContainer.setAlignment(Pos.CENTER_LEFT);

        Label statusIcon = new Label("\u25CF");
        statusIcon.getStyleClass().add("session-status-icon");

        Label statusLabel = new Label();
        statusLabel.getStyleClass().add("session-status");

        statusContainer.getChildren().addAll(statusIcon, statusLabel);
        updateConnectionStatus(session, statusIcon, statusLabel);
        statusLabels.put(session, statusLabel);

        Button editButton = new Button("\uE70F");
        editButton.getStyleClass().add("session-action-btn");
        editButton.setTooltip(new Tooltip("Edit session"));
        editButton.setOnAction(e -> editSession(session));

        Button deleteButton = new Button("\uE74D");
        deleteButton.getStyleClass().addAll("session-action-btn", "session-delete-btn");
        deleteButton.setTooltip(new Tooltip("Delete session"));
        deleteButton.setOnAction(e -> deleteSession(session));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox statusRow = new HBox(6, statusContainer, spacer, editButton, deleteButton);
        statusRow.setAlignment(Pos.CENTER_LEFT);

        // Primary action
        Button connectButton = new Button();
        connectButton.getStyleClass().add("session-connect-btn");
        connectButton.setMaxWidth(Double.MAX_VALUE);
        updateConnectButton(session, connectButton);
        connectButton.setOnAction(e -> toggleConnection(session));
        connectButtons.put(session, connectButton);

        card.getChildren().addAll(header, statusRow, connectButton);
        return card;
    }

    private StackPane createAvatar(ServerData session) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("session-icon-container");
        avatar.setPrefSize(48, 48);
        avatar.setMinSize(48, 48);
        avatar.setMaxSize(48, 48);

        boolean imageShown = false;
        try {
            String url = session.getIconURL();
            if (url != null && !url.isBlank()) {
                Image image = new Image(url, 30, 30, true, true);
                if (!image.isError()) {
                    ImageView serverIcon = new ImageView(image);
                    serverIcon.setFitWidth(30);
                    serverIcon.setFitHeight(30);
                    serverIcon.setPreserveRatio(true);
                    avatar.getChildren().add(serverIcon);
                    imageShown = true;
                }
            }
        } catch (Exception ignored) {
            // fall through to the initial
        }

        if (!imageShown) {
            String nickname = session.getNickname() == null ? "" : session.getNickname().trim();
            String initial = nickname.isEmpty() ? "?" : nickname.substring(0, 1).toUpperCase();
            Label initialLabel = new Label(initial);
            initialLabel.getStyleClass().add("session-initial");
            avatar.getChildren().add(initialLabel);
        }
        return avatar;
    }

    private void applyPill(Label statusLabel, String state) {
        if (statusLabel.getParent() == null) {
            return;
        }
        var classes = statusLabel.getParent().getStyleClass();
        classes.removeAll("pill-connected", "pill-disconnected", "pill-connecting");
        classes.add(state);
    }

    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("sessions-empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(20);
        emptyState.setPrefHeight(300);
        
        Label iconLabel = new Label("");
        iconLabel.getStyleClass().add("empty-state-icon");
        
        Label titleLabel = new Label("No sessions found");
        titleLabel.getStyleClass().add("empty-state-title");
        
        Label subtitleLabel = new Label("Get started by adding your first SSH session");
        subtitleLabel.getStyleClass().add("empty-state-subtitle");
        
        Button addButton = new Button("+ Add Session");
        addButton.getStyleClass().add("empty-state-btn");
        addButton.setOnAction(e -> showAddSessionDialog());
        
        emptyState.getChildren().addAll(iconLabel, titleLabel, subtitleLabel, addButton);
        
        sessionsGrid.getChildren().add(emptyState);
    }
    
    private void updateSessionCount(int count) {
        if (sessionCountLabel != null) {
            sessionCountLabel.setText(count + " session" + (count != 1 ? "s" : ""));
        }
    }
    
    private void updateConnectionStatus(ServerData session, Label statusIcon, Label statusLabel) {
        boolean isConnected = connectionManager.isServerConnected(session);
        
        if (isConnected) {
            statusIcon.setText("●");
            statusIcon.getStyleClass().removeAll("status-disconnected-icon", "status-connecting-icon");
            statusIcon.getStyleClass().add("status-connected-icon");
            statusLabel.setText("Connected");
            applyPill(statusLabel, "pill-connected");
            statusLabel.getStyleClass().removeAll("status-disconnected", "status-connecting");
            statusLabel.getStyleClass().add("status-connected");
        } else {
            statusIcon.setText("●");
            statusIcon.getStyleClass().removeAll("status-connected-icon", "status-connecting-icon");
            statusIcon.getStyleClass().add("status-disconnected-icon");
            statusLabel.setText("Disconnected");
            applyPill(statusLabel, "pill-disconnected");
            statusLabel.getStyleClass().removeAll("status-connected", "status-connecting");
            statusLabel.getStyleClass().add("status-disconnected");
        }
    }
    
    private void updateConnectButton(ServerData session, Button connectButton) {
        boolean isConnected = connectionManager.isServerConnected(session);
        
        if (isConnected) {
            connectButton.setText("Disconnect");
            connectButton.getStyleClass().removeAll("session-connect-btn");
            connectButton.getStyleClass().add("session-disconnect-btn");
        } else {
            connectButton.setText("▶ Connect");
            connectButton.getStyleClass().removeAll("session-disconnect-btn");
            connectButton.getStyleClass().add("session-connect-btn");
        }
    }
    
    private void toggleConnection(ServerData session) {
        boolean isConnected = connectionManager.isServerConnected(session);
        
        if (isConnected) {
            disconnectFromSession(session);
        } else {
            connectToSession(session);
        }
    }
    
    private void connectToSession(ServerData session) {
        System.out.println("Connecting to: " + session.getNickname());
        
        // Update UI to show connecting state
        Button connectButton = connectButtons.get(session);
        Label statusLabel = statusLabels.get(session);
        
        if (connectButton != null) {
            connectButton.setText("Connecting...");
            connectButton.setDisable(true);
        }
        
        if (statusLabel != null) {
            statusLabel.setText("Connecting");
            applyPill(statusLabel, "pill-connecting");
            statusLabel.getStyleClass().removeAll("status-connected", "status-disconnected");
            statusLabel.getStyleClass().add("status-connecting");
        }
        
        // Show loading screen and connect in background
        MainApp.get().changeScene(JScene.LOADING);
        
        Thread connectionThread = new Thread(() -> {
            Connection connection = new Connection(session.getUsername(), session.getIp(), session.getPassword(), 22);
            connection.addKnownHost();
            
            Platform.runLater(() -> {
                boolean connected = connection.connect();
                if (connected && connection.start()) {
                    // Connection successful
                    TerminalTabComponent termTab = new TerminalTabComponent(session.getNickname(), connection);
                    connectionManager.addConnection(termTab, connection);
                    MainApp.get().getTerminalController().addTerminalTab(termTab);
                    MainApp.get().changeScene(JScene.TERMINAL);
                } else {
                    // Connection failed - return to sessions page and update UI
                    Platform.runLater(() -> {
                        MainApp.get().changeScene(JScene.SESSIONS);
                        refreshConnectionStatus(session);
                        
                        // Show error alert
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Connection Failed");
                        alert.setHeaderText("Failed to connect to " + session.getNickname());
                        alert.setContentText(connection.error() != null ? connection.error() : "Unknown connection error");
                        MainApp.theme(alert);
                        alert.showAndWait();
                    });
                }
            });
        });
        
        AsyncManager.get().addThread(connectionThread);
    }
    
    private void disconnectFromSession(ServerData session) {
        Connection connection = connectionManager.getServerConnection(session);
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
        
        refreshConnectionStatus(session);
    }
    
    private void refreshConnectionStatus(ServerData session) {
        Button connectButton = connectButtons.get(session);
        Label statusLabel = statusLabels.get(session);
        
        if (connectButton != null) {
            connectButton.setDisable(false);
            updateConnectButton(session, connectButton);
        }
        
        if (statusLabel != null) {
            // Find the status icon (previous sibling in the status container)
            HBox statusContainer = (HBox) statusLabel.getParent();
            if (statusContainer != null && statusContainer.getChildren().size() >= 2) {
                Label statusIcon = (Label) statusContainer.getChildren().get(0);
                updateConnectionStatus(session, statusIcon, statusLabel);
            }
        }
    }
    
    public void refreshAllConnectionStatuses() {
        for (ServerData session : statusLabels.keySet()) {
            refreshConnectionStatus(session);
        }
    }
    
    // Method to be called when returning to sessions view to refresh all statuses
    public void onViewActivated() {
        refreshAllConnectionStatuses();
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
                loadSessions();
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
                loadSessions();
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
                loadSessions();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        MainApp.theme(alert);
        alert.showAndWait();
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

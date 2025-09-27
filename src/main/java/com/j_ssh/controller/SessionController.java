package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class SessionController extends com.j_ssh.view.bootstrap.BootstrapPane {
    private FlowPane sessionsGrid;
    private SessionManager sessionManager;
    private TextField searchField;
    private Label sessionCountLabel;

    public SessionController() {
        this.sessionManager = SessionManager.get();
        initializeComponents();
        loadSessions();
    }

    private void initializeComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);

        // Create header section
        BootstrapRow headerRow = createHeaderSection();

        // Create sessions grid
        BootstrapRow gridRow = createSessionsGrid();

        // Add rows to the main pane
        this.addRow(headerRow);
        this.addRow(gridRow);
        
        // Apply modern styling
        this.getStyleClass().add("sessions-container");
    }

    private BootstrapRow createHeaderSection() {
        BootstrapRow row = new BootstrapRow();
        
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
        
        row.addColumn(API.get().createColumn(headerContainer, 12));
        return row;
    }

    private BootstrapRow createSessionsGrid() {
        BootstrapRow row = new BootstrapRow();
        
        VBox gridContainer = new VBox();
        gridContainer.getStyleClass().add("sessions-grid-container");
        gridContainer.setPadding(new Insets(24));
        
        // Create sessions grid
        sessionsGrid = new FlowPane();
        sessionsGrid.getStyleClass().add("sessions-grid");
        sessionsGrid.setHgap(20);
        sessionsGrid.setVgap(20);
        sessionsGrid.setPrefWrapLength(0);
        
        gridContainer.getChildren().add(sessionsGrid);
        
        row.addColumn(API.get().createColumn(gridContainer, 12));
        return row;
    }

    private void loadSessions() {
        sessionsGrid.getChildren().clear();
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
        card.setSpacing(16);
        card.setPrefWidth(280);
        card.setMinHeight(180);
        
        // Header with icon and status
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);
        
        // Server icon
        VBox iconContainer = new VBox();
        iconContainer.getStyleClass().add("session-icon-container");
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPrefSize(40, 40);
        iconContainer.setMinSize(40, 40);
        iconContainer.setMaxSize(40, 40);
        
        ImageView serverIcon = new ImageView();
        serverIcon.setFitWidth(24);
        serverIcon.setFitHeight(24);
        serverIcon.setPreserveRatio(true);
        
        // Try to load custom icon, fallback to default
        try {
            if (session.getIconURL() != null && !session.getIconURL().isEmpty()) {
                serverIcon.setImage(new Image(session.getIconURL()));
            } else {
                // Use default server icon (you might want to add a default icon resource)
                serverIcon.getStyleClass().add("default-server-icon");
            }
        } catch (Exception e) {
            serverIcon.getStyleClass().add("default-server-icon");
        }
        
        iconContainer.getChildren().add(serverIcon);
        
        // Session info
        VBox sessionInfo = new VBox();
        sessionInfo.setSpacing(4);
        
        Label nameLabel = new Label(session.getNickname());
        nameLabel.getStyleClass().add("session-name");
        
        Label addressLabel = new Label(session.getUsername() + "@" + session.getIp());
        addressLabel.getStyleClass().add("session-address");
        
        sessionInfo.getChildren().addAll(nameLabel, addressLabel);
        
        // Status indicator (mock - you can implement real connection status)
        Label statusLabel = new Label("Disconnected");
        statusLabel.getStyleClass().add("session-status");
        statusLabel.getStyleClass().add("status-disconnected");
        
        header.getChildren().addAll(iconContainer, sessionInfo, new Region(), statusLabel);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        
        // Connection details
        VBox details = new VBox();
        details.setSpacing(8);
        details.getStyleClass().add("session-details");
        
        HBox lastUsedRow = new HBox();
        lastUsedRow.setAlignment(Pos.CENTER_LEFT);
        lastUsedRow.setSpacing(8);
        
        Label clockIcon = new Label("🕒");
        clockIcon.getStyleClass().add("session-detail-icon");
        
        Label lastUsedLabel = new Label("Last used: 2 hours ago"); // Mock data
        lastUsedLabel.getStyleClass().add("session-detail-text");
        
        lastUsedRow.getChildren().addAll(clockIcon, lastUsedLabel);
        
        details.getChildren().add(lastUsedRow);
        
        // Action buttons
        HBox actions = new HBox();
        actions.setSpacing(8);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        Button connectButton = new Button("Connect");
        connectButton.getStyleClass().add("session-connect-btn");
        connectButton.setOnAction(e -> connectToSession(session));
        
        Button editButton = new Button("✏");
        editButton.getStyleClass().add("session-action-btn");
        editButton.setOnAction(e -> editSession(session));
        
        Button deleteButton = new Button("🗑");
        deleteButton.getStyleClass().add("session-action-btn");
        deleteButton.getStyleClass().add("session-delete-btn");
        deleteButton.setOnAction(e -> deleteSession(session));
        
        actions.getChildren().addAll(connectButton, new Region(), editButton, deleteButton);
        HBox.setHgrow(actions.getChildren().get(1), Priority.ALWAYS);
        
        card.getChildren().addAll(header, details, actions);
        
        return card;
    }
    
    private void showEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("sessions-empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(20);
        emptyState.setPrefHeight(300);
        
        Label iconLabel = new Label("🖥");
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
    
    private void connectToSession(ServerData session) {
        // Implement connection logic here
        System.out.println("Connecting to: " + session.getNickname());
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

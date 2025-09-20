package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SessionController extends com.j_ssh.view.bootstrap.BootstrapPane {
    private TableView<ServerData> sessionsTable;
    private SessionManager sessionManager;

    public SessionController() {
        this.sessionManager = SessionManager.get();
        initializeComponents();
        loadSessions();
    }

    private void initializeComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);

        // Create toolbar
        BootstrapRow toolbarRow = createToolbar();

        // Create sessions table
        BootstrapRow tableRow = createSessionsTable();

        // Add rows to the main pane
        this.addRow(toolbarRow);
        this.addRow(tableRow);
    }

    private BootstrapRow createToolbar() {
        BootstrapRow row = new BootstrapRow();

        // Create buttons
        Button addButton = new Button("Add Session");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> showAddSessionDialog());

        Button editButton = new Button("Edit Session");
        editButton.getStyleClass().add("btn-secondary");
        editButton.setOnAction(e -> editSelectedSession());

        Button deleteButton = new Button("Delete Session");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> deleteSelectedSession());

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("btn-info");
        refreshButton.setOnAction(e -> loadSessions());

        // Add buttons to row
        row.addColumn(API.get().createColumn(addButton, 3));
        row.addColumn(API.get().createColumn(editButton, 3));
        row.addColumn(API.get().createColumn(deleteButton, 3));
        row.addColumn(API.get().createColumn(refreshButton, 3));

        return row;
    }

    private BootstrapRow createSessionsTable() {
        BootstrapRow row = new BootstrapRow();

        sessionsTable = new TableView<>();
        sessionsTable.setPrefHeight(400);

        // Create columns
        TableColumn<ServerData, String> nicknameColumn = new TableColumn<>("Nickname");
        nicknameColumn.setCellValueFactory(new PropertyValueFactory<>("nickname"));

        TableColumn<ServerData, String> ipColumn = new TableColumn<>("IP Address");
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        TableColumn<ServerData, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<ServerData, String> authColumn = new TableColumn<>("Authentication");
        authColumn.setCellValueFactory(cellData -> {
            ServerData session = cellData.getValue();
            String auth = session.getPassword().isEmpty() ? "Private Key" : "Password";
            return new javafx.beans.property.SimpleStringProperty(auth);
        });

        sessionsTable.getColumns().addAll(nicknameColumn, ipColumn, usernameColumn, authColumn);

        row.addColumn(API.get().createColumn(sessionsTable, 12));
        return row;
    }

    private void loadSessions() {
        sessionsTable.getItems().clear();
        List<ServerData> sessions = sessionManager.getAllSessions();
        sessionsTable.getItems().addAll(sessions);
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

    private void editSelectedSession() {
        ServerData selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession != null) {
            SessionDialog dialog = new SessionDialog("Edit Session", selectedSession);
            dialog.showAndWait().ifPresent(sessionData -> {
                if (sessionData != null) {
                    sessionManager.updateSession(
                        selectedSession.getIndex(),
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
        } else {
            showAlert("No Selection", "Please select a session to edit.");
        }
    }

    private void deleteSelectedSession() {
        ServerData selectedSession = sessionsTable.getSelectionModel().getSelectedItem();
        if (selectedSession != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Session");
            alert.setHeaderText("Are you sure you want to delete this session?");
            alert.setContentText("Session: " + selectedSession.getNickname() + " (" + selectedSession.getIp() + ")");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    sessionManager.deleteSession(selectedSession.getIndex());
                    loadSessions();
                }
            });
        } else {
            showAlert("No Selection", "Please select a session to delete.");
        }
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
            
            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            nicknameField = new TextField();
            iconURLField = new TextField();
            ipField = new TextField();
            usernameField = new TextField();
            passwordField = new PasswordField();
            privateKeyField = new TextField();

            grid.add(new Label("Nickname:"), 0, 0);
            grid.add(nicknameField, 1, 0);
            grid.add(new Label("Icon URL:"), 0, 1);
            grid.add(iconURLField, 1, 1);
            grid.add(new Label("IP Address:"), 0, 2);
            grid.add(ipField, 1, 2);
            grid.add(new Label("Username:"), 0, 3);
            grid.add(usernameField, 1, 3);
            grid.add(new Label("Password:"), 0, 4);
            grid.add(passwordField, 1, 4);
            grid.add(new Label("Private Key Path:"), 0, 5);
            grid.add(privateKeyField, 1, 5);

            // Pre-fill fields if editing
            if (existingSession != null) {
                nicknameField.setText(existingSession.getNickname());
                iconURLField.setText(existingSession.getIconURL());
                ipField.setText(existingSession.getIp());
                usernameField.setText(existingSession.getUsername());
                passwordField.setText(existingSession.getPassword());
                privateKeyField.setText(existingSession.getPrivateKeyLocation());
            }

            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

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
    }
}

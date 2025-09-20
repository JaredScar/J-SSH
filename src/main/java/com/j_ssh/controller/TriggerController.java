package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.managers.TriggerManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.model.objects.TriggerData;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;

public class TriggerController extends com.j_ssh.view.bootstrap.BootstrapPane {
    private TableView<TriggerData> triggersTable;
    private TriggerManager triggerManager;
    private SessionManager sessionManager;
    private ActionManager actionManager;

    public TriggerController() {
        this.triggerManager = TriggerManager.get();
        this.sessionManager = SessionManager.get();
        this.actionManager = ActionManager.get();
        initializeComponents();
        loadTriggers();
    }

    private void initializeComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);

        // Create toolbar
        BootstrapRow toolbarRow = createToolbar();

        // Create triggers table
        BootstrapRow tableRow = createTriggersTable();

        // Add rows to the main pane
        this.addRow(toolbarRow);
        this.addRow(tableRow);
    }

    private BootstrapRow createToolbar() {
        BootstrapRow row = new BootstrapRow();

        // Create buttons
        Button addButton = new Button("Add Trigger");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> showAddTriggerDialog());

        Button editButton = new Button("Edit Trigger");
        editButton.getStyleClass().add("btn-secondary");
        editButton.setOnAction(e -> editSelectedTrigger());

        Button deleteButton = new Button("Delete Trigger");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> deleteSelectedTrigger());

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("btn-info");
        refreshButton.setOnAction(e -> loadTriggers());

        // Add buttons to row
        row.addColumn(API.get().createColumn(addButton, 3));
        row.addColumn(API.get().createColumn(editButton, 3));
        row.addColumn(API.get().createColumn(deleteButton, 3));
        row.addColumn(API.get().createColumn(refreshButton, 3));

        return row;
    }

    private BootstrapRow createTriggersTable() {
        BootstrapRow row = new BootstrapRow();

        triggersTable = new TableView<>();
        triggersTable.setPrefHeight(400);

        // Create columns
        TableColumn<TriggerData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<TriggerData, String> descriptionColumn = new TableColumn<>("Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<TriggerData, String> triggersCountColumn = new TableColumn<>("Triggers Count");
        triggersCountColumn.setCellValueFactory(cellData -> {
            TriggerData trigger = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(trigger.getTriggers().size()));
        });

        TableColumn<TriggerData, String> triggersPreviewColumn = new TableColumn<>("Triggers Preview");
        triggersPreviewColumn.setCellValueFactory(cellData -> {
            TriggerData trigger = cellData.getValue();
            StringBuilder preview = new StringBuilder();
            int count = 0;
            for (Integer serverIndex : trigger.getTriggers().keySet()) {
                if (count >= 2) {
                    preview.append("...");
                    break;
                }
                ServerData server = sessionManager.getSession(serverIndex);
                ActionData action = actionManager.getAction(trigger.getTriggers().get(serverIndex));
                if (server != null && action != null) {
                    preview.append(server.getNickname()).append(" → ").append(action.getName());
                    if (count < trigger.getTriggers().size() - 1) {
                        preview.append(", ");
                    }
                }
                count++;
            }
            return new javafx.beans.property.SimpleStringProperty(preview.toString());
        });

        triggersTable.getColumns().addAll(nameColumn, descriptionColumn, triggersCountColumn, triggersPreviewColumn);

        row.addColumn(API.get().createColumn(triggersTable, 12));
        return row;
    }

    private void loadTriggers() {
        triggersTable.getItems().clear();
        List<TriggerData> triggers = triggerManager.getAllTriggers();
        triggersTable.getItems().addAll(triggers);
    }

    private void showAddTriggerDialog() {
        TriggerDialog dialog = new TriggerDialog("Add New Trigger", null);
        dialog.showAndWait().ifPresent(triggerData -> {
            if (triggerData != null) {
                triggerManager.createTrigger(
                    triggerData.getName(),
                    triggerData.getDescription(),
                    triggerData.getTriggers()
                );
                loadTriggers();
            }
        });
    }

    private void editSelectedTrigger() {
        TriggerData selectedTrigger = triggersTable.getSelectionModel().getSelectedItem();
        if (selectedTrigger != null) {
            TriggerDialog dialog = new TriggerDialog("Edit Trigger", selectedTrigger);
            dialog.showAndWait().ifPresent(triggerData -> {
                if (triggerData != null) {
                    triggerManager.updateTrigger(
                        selectedTrigger.getIndex(),
                        triggerData.getName(),
                        triggerData.getDescription(),
                        triggerData.getTriggers()
                    );
                    loadTriggers();
                }
            });
        } else {
            showAlert("No Selection", "Please select a trigger to edit.");
        }
    }

    private void deleteSelectedTrigger() {
        TriggerData selectedTrigger = triggersTable.getSelectionModel().getSelectedItem();
        if (selectedTrigger != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Trigger");
            alert.setHeaderText("Are you sure you want to delete this trigger?");
            alert.setContentText("Trigger: " + selectedTrigger.getName());
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    triggerManager.deleteTrigger(selectedTrigger.getIndex());
                    loadTriggers();
                }
            });
        } else {
            showAlert("No Selection", "Please select a trigger to delete.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for trigger dialog
    private class TriggerDialog extends Dialog<TriggerData> {
        private TextField nameField;
        private TextArea descriptionField;
        private ListView<String> triggersList;
        private ComboBox<String> serverCombo;
        private ComboBox<String> actionCombo;
        private Button addTriggerButton;

        public TriggerDialog(String title, TriggerData existingTrigger) {
            setTitle(title);
            setHeaderText(title);
            
            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            nameField = new TextField();
            descriptionField = new TextArea();
            descriptionField.setPrefRowCount(3);
            triggersList = new ListView<>();
            serverCombo = new ComboBox<>();
            actionCombo = new ComboBox<>();
            addTriggerButton = new Button("Add Trigger");

            // Populate combo boxes
            populateComboBoxes();

            // Add trigger button action
            addTriggerButton.setOnAction(e -> addTrigger());

            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Description:"), 0, 1);
            grid.add(descriptionField, 1, 1);
            grid.add(new Label("Server:"), 0, 2);
            grid.add(serverCombo, 1, 2);
            grid.add(new Label("Action:"), 0, 3);
            grid.add(actionCombo, 1, 3);
            grid.add(addTriggerButton, 1, 4);
            grid.add(new Label("Triggers:"), 0, 5);
            grid.add(triggersList, 1, 5);

            // Pre-fill fields if editing
            if (existingTrigger != null) {
                nameField.setText(existingTrigger.getName());
                descriptionField.setText(existingTrigger.getDescription());
                
                // Populate triggers list
                for (Integer serverIndex : existingTrigger.getTriggers().keySet()) {
                    ServerData server = sessionManager.getSession(serverIndex);
                    ActionData action = actionManager.getAction(existingTrigger.getTriggers().get(serverIndex));
                    if (server != null && action != null) {
                        triggersList.getItems().add(server.getNickname() + " → " + action.getName());
                    }
                }
            }

            getDialogPane().setContent(grid);
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    HashMap<Integer, Integer> triggers = new HashMap<>();
                    if (existingTrigger != null) {
                        triggers.putAll(existingTrigger.getTriggers());
                    }
                    
                    return new TriggerData(
                        existingTrigger != null ? existingTrigger.getIndex() : -1,
                        nameField.getText(),
                        descriptionField.getText(),
                        triggers
                    );
                }
                return null;
            });
        }

        private void populateComboBoxes() {
            // Populate server combo
            List<ServerData> servers = sessionManager.getAllSessions();
            for (ServerData server : servers) {
                serverCombo.getItems().add(server.getNickname() + " (" + server.getIp() + ")");
            }

            // Populate action combo
            List<ActionData> actions = actionManager.getAllActions();
            for (ActionData action : actions) {
                actionCombo.getItems().add(action.getName());
            }
        }

        private void addTrigger() {
            String selectedServer = serverCombo.getSelectionModel().getSelectedItem();
            String selectedAction = actionCombo.getSelectionModel().getSelectedItem();
            
            if (selectedServer != null && selectedAction != null) {
                // Find server and action indices
                List<ServerData> servers = sessionManager.getAllSessions();
                List<ActionData> actions = actionManager.getAllActions();
                
                int serverIndex = -1;
                int actionIndex = -1;
                
                for (int i = 0; i < servers.size(); i++) {
                    if ((servers.get(i).getNickname() + " (" + servers.get(i).getIp() + ")").equals(selectedServer)) {
                        serverIndex = i;
                        break;
                    }
                }
                
                for (int i = 0; i < actions.size(); i++) {
                    if (actions.get(i).getName().equals(selectedAction)) {
                        actionIndex = i;
                        break;
                    }
                }
                
                if (serverIndex != -1 && actionIndex != -1) {
                    // Add to triggers list display
                    triggersList.getItems().add(selectedServer + " → " + selectedAction);
                    
                    // Clear selections
                    serverCombo.getSelectionModel().clearSelection();
                    actionCombo.getSelectionModel().clearSelection();
                }
            }
        }
    }
}

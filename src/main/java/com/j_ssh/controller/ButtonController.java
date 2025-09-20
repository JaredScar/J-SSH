package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ButtonController extends BootstrapPane {
    private TableView<ActionData> actionsTable;
    private ObservableList<ActionData> actionsList;
    private ActionManager actionManager;
    
    public ButtonController() {
        this.actionManager = ActionManager.get();
        initializeComponents();
        loadActions();
    }
    
    private void initializeComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);
        
        // Create toolbar
        BootstrapRow toolbarRow = createToolbar();
        
        // Create actions table
        BootstrapRow tableRow = createActionsTable();
        
        // Add rows to the main pane
        this.addRow(toolbarRow);
        this.addRow(tableRow);
    }
    
    private BootstrapRow createToolbar() {
        BootstrapRow row = new BootstrapRow();
        
        // Create buttons
        Button addButton = new Button("Add Action");
        addButton.getStyleClass().add("btn-primary");
        addButton.setOnAction(e -> showAddActionDialog());
        
        Button editButton = new Button("Edit Action");
        editButton.getStyleClass().add("btn-secondary");
        editButton.setOnAction(e -> editSelectedAction());
        
        Button deleteButton = new Button("Delete Action");
        deleteButton.getStyleClass().add("btn-danger");
        deleteButton.setOnAction(e -> deleteSelectedAction());
        
        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("btn-info");
        refreshButton.setOnAction(e -> loadActions());
        
        // Add buttons to columns
        row.addColumn(API.get().createColumn(addButton, 3));
        row.addColumn(API.get().createColumn(editButton, 3));
        row.addColumn(API.get().createColumn(deleteButton, 3));
        row.addColumn(API.get().createColumn(refreshButton, 3));
        
        return row;
    }
    
    private BootstrapRow createActionsTable() {
        BootstrapRow row = new BootstrapRow();
        
        // Create table
        actionsTable = new TableView<>();
        actionsList = FXCollections.observableArrayList();
        actionsTable.setItems(actionsList);
        
        // Create columns
        TableColumn<ActionData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);
        
        TableColumn<ActionData, String> commandsCountColumn = new TableColumn<>("Commands Count");
        commandsCountColumn.setCellValueFactory(cellData -> {
            ActionData action = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(String.valueOf(action.getCommands().size()));
        });
        commandsCountColumn.setPrefWidth(150);
        
        TableColumn<ActionData, String> commandsPreviewColumn = new TableColumn<>("Commands Preview");
        commandsPreviewColumn.setCellValueFactory(cellData -> {
            ActionData action = cellData.getValue();
            List<String> commands = action.getCommands();
            String preview;
            if (commands.isEmpty()) {
                preview = "No commands";
            } else if (commands.size() == 1) {
                preview = commands.get(0);
            } else {
                preview = commands.get(0) + " (+" + (commands.size() - 1) + " more)";
            }
            return new javafx.beans.property.SimpleStringProperty(preview);
        });
        commandsPreviewColumn.setPrefWidth(300);
        
        // Add columns to table
        actionsTable.getColumns().addAll(nameColumn, commandsCountColumn, commandsPreviewColumn);
        
        // Set table properties
        actionsTable.setPrefHeight(400);
        actionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Add table to column
        BootstrapColumn tableCol = API.get().createColumn(actionsTable, 12);
        row.addColumn(tableCol);
        
        return row;
    }
    
    private void loadActions() {
        List<ActionData> actions = actionManager.getAllActions();
        actionsList.clear();
        actionsList.addAll(actions);
    }
    
    private void showAddActionDialog() {
        ActionDialog dialog = new ActionDialog("Add New Action", null);
        dialog.showAndWait().ifPresent(actionData -> {
            actionManager.createAction(actionData.getName(), actionData.getCommands());
            loadActions();
        });
    }
    
    private void editSelectedAction() {
        ActionData selected = actionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            ActionDialog dialog = new ActionDialog("Edit Action", selected);
            dialog.showAndWait().ifPresent(actionData -> {
                actionManager.updateAction(selected.getIndex(), actionData.getName(), actionData.getCommands());
                loadActions();
            });
        } else {
            showAlert("No Selection", "Please select an action to edit.");
        }
    }
    
    private void deleteSelectedAction() {
        ActionData selected = actionsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Action");
            alert.setHeaderText("Delete Action: " + selected.getName());
            alert.setContentText("Are you sure you want to delete this action? This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    actionManager.deleteAction(selected.getIndex());
                    loadActions();
                }
            });
        } else {
            showAlert("No Selection", "Please select an action to delete.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Dialog for adding/editing actions
     */
    private static class ActionDialog extends Dialog<ActionData> {
        private TextField nameField;
        private TextArea commandsArea;
        
        public ActionDialog(String title, ActionData existingAction) {
            setTitle(title);
            setHeaderText(title);
            
            // Create form
            VBox form = new VBox(10);
            
            // Name field
            Label nameLabel = new Label("Action Name:");
            nameField = new TextField();
            nameField.setPromptText("Enter action name");
            if (existingAction != null) {
                nameField.setText(existingAction.getName());
            }
            
            // Commands area
            Label commandsLabel = new Label("Commands (one per line):");
            commandsArea = new TextArea();
            commandsArea.setPromptText("Enter commands, one per line");
            commandsArea.setPrefRowCount(8);
            if (existingAction != null) {
                commandsArea.setText(String.join("\n", existingAction.getCommands()));
            }
            
            form.getChildren().addAll(nameLabel, nameField, commandsLabel, commandsArea);
            
            getDialogPane().setContent(form);
            
            // Add buttons
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            
            // Set result converter
            setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String name = nameField.getText().trim();
                    String commandsText = commandsArea.getText().trim();
                    
                    if (name.isEmpty() || commandsText.isEmpty()) {
                        return null;
                    }
                    
                    List<String> commands = new ArrayList<>();
                    for (String line : commandsText.split("\n")) {
                        String command = line.trim();
                        if (!command.isEmpty()) {
                            commands.add(command);
                        }
                    }
                    
                    if (existingAction != null) {
                        existingAction.setName(name);
                        existingAction.setCommands(new ArrayList<>(commands));
                        return existingAction;
                    } else {
                        return new ActionData(-1, name, new ArrayList<>(commands));
                    }
                }
                return null;
            });
        }
    }
}

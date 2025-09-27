package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class ButtonController extends BootstrapPane {
    private VBox actionsContainer;
    private ActionManager actionManager;
    private TextField searchField;
    private Label actionCountLabel;
    
    public ButtonController() {
        this.actionManager = ActionManager.get();
        
        // Apply modern styling to main container
        this.getStyleClass().add("actions-container");
        
        initializeModernComponents();
        loadActions();
    }
    
    private void initializeModernComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);
        
        // Create modern header section
        BootstrapRow headerRow = createModernHeader();
        
        // Create actions content area
        BootstrapRow contentRow = createActionsContent();
        
        // Add rows to the main pane
        this.addRow(headerRow);
        this.addRow(contentRow);
    }
    
    private BootstrapRow createModernHeader() {
        BootstrapRow row = new BootstrapRow();
        
        VBox headerSection = new VBox();
        headerSection.getStyleClass().add("actions-header");
        headerSection.setSpacing(16);
        headerSection.setPadding(new Insets(24));
        
        // Title and subtitle section
        VBox titleSection = new VBox();
        titleSection.setSpacing(8);
        
        Label titleLabel = new Label("Actions");
        titleLabel.getStyleClass().add("actions-title");
        
        Label subtitleLabel = new Label("Reusable command sequences");
        subtitleLabel.getStyleClass().add("actions-subtitle");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Search and action section
        HBox searchActionSection = new HBox();
        searchActionSection.setSpacing(16);
        searchActionSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        searchField = new TextField();
        searchField.getStyleClass().add("actions-search");
        searchField.setPromptText("Search actions...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldText, newText) -> filterActions(newText));
        
        // Action count
        actionCountLabel = new Label("0 actions");
        actionCountLabel.getStyleClass().add("actions-count");
        
        // Add action button
        Button addButton = new Button("+ Add Action");
        addButton.getStyleClass().add("actions-add-btn");
        addButton.setOnAction(e -> showAddActionDialog());
        
        searchActionSection.getChildren().addAll(searchField, actionCountLabel, new Region(), addButton);
        HBox.setHgrow(searchActionSection.getChildren().get(2), Priority.ALWAYS);
        
        headerSection.getChildren().addAll(titleSection, searchActionSection);
        
        BootstrapColumn headerCol = API.get().createColumn(headerSection, 12);
        row.addColumn(headerCol);
        
        return row;
    }
    
    private BootstrapRow createActionsContent() {
        BootstrapRow row = new BootstrapRow();
        
        // Create scrollable content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("actions-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Create actions container
        actionsContainer = new VBox();
        actionsContainer.getStyleClass().add("actions-content");
        actionsContainer.setSpacing(16);
        actionsContainer.setPadding(new Insets(24));
        
        scrollPane.setContent(actionsContainer);
        
        BootstrapColumn contentCol = API.get().createColumn(scrollPane, 12);
        row.addColumn(contentCol);
        
        return row;
    }
    
    private VBox createActionCard(ActionData action) {
        VBox card = new VBox();
        card.getStyleClass().add("action-card");
        card.setSpacing(16);
        card.setPadding(new Insets(20));
        
        // Card header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);
        
        // Action icon
        Label actionIcon = new Label("▶");
        actionIcon.getStyleClass().add("action-icon");
        
        // Action info
        VBox actionInfo = new VBox();
        actionInfo.setSpacing(4);
        
        Label nameLabel = new Label(action.getName());
        nameLabel.getStyleClass().add("action-name");
        
        String description = getActionDescription(action);
        if (description != null && !description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.getStyleClass().add("action-description");
            actionInfo.getChildren().addAll(nameLabel, descLabel);
        } else {
            actionInfo.getChildren().add(nameLabel);
        }
        
        // Usage badge
        Label usageBadge = new Label("Used 15 times");
        usageBadge.getStyleClass().add("action-usage-badge");
        
        header.getChildren().addAll(actionIcon, actionInfo, new Region(), usageBadge);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        
        // Commands section
        VBox commandsSection = createCommandsSection(action);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button runButton = new Button("▶ Run Action");
        runButton.getStyleClass().add("action-run-btn");
        runButton.setOnAction(e -> runAction(action));
        
        Button editButton = new Button("✎");
        editButton.getStyleClass().add("action-edit-btn");
        editButton.setOnAction(e -> editAction(action));
        
        Button deleteButton = new Button("🗑");
        deleteButton.getStyleClass().add("action-delete-btn");
        deleteButton.setOnAction(e -> deleteAction(action));
        
        actionButtons.getChildren().addAll(runButton, editButton, deleteButton);
        
        card.getChildren().addAll(header, commandsSection, actionButtons);
        
        return card;
    }
    
    private VBox createCommandsSection(ActionData action) {
        VBox commandsSection = new VBox();
        commandsSection.getStyleClass().add("action-commands-section");
        commandsSection.setSpacing(8);
        commandsSection.setPadding(new Insets(16));
        
        HBox commandsHeader = new HBox();
        commandsHeader.setAlignment(Pos.CENTER_LEFT);
        commandsHeader.setSpacing(8);
        
        Label commandsTitle = new Label("Commands:");
        commandsTitle.getStyleClass().add("action-commands-title");
        
        Button copyButton = new Button("📋");
        copyButton.getStyleClass().add("action-copy-btn");
        copyButton.setOnAction(e -> copyCommands(action));
        
        commandsHeader.getChildren().addAll(commandsTitle, new Region(), copyButton);
        HBox.setHgrow(commandsHeader.getChildren().get(1), Priority.ALWAYS);
        
        VBox commandsList = new VBox();
        commandsList.setSpacing(4);
        
        for (String command : action.getCommands()) {
            HBox commandRow = new HBox();
            commandRow.setAlignment(Pos.CENTER_LEFT);
            commandRow.setSpacing(8);
            
            Label promptLabel = new Label("$");
            promptLabel.getStyleClass().add("action-command-prompt");
            
            Label commandLabel = new Label(command);
            commandLabel.getStyleClass().add("action-command-text");
            
            commandRow.getChildren().addAll(promptLabel, commandLabel);
            commandsList.getChildren().add(commandRow);
        }
        
        commandsSection.getChildren().addAll(commandsHeader, commandsList);
        
        return commandsSection;
    }
    
    private void loadActions() {
        actionsContainer.getChildren().clear();
        List<ActionData> actions = actionManager.getAllActions();
        
        // Update action count
        actionCountLabel.setText(actions.size() + " action" + (actions.size() != 1 ? "s" : ""));
        
        if (actions.isEmpty()) {
            // Show empty state
            VBox emptyState = createEmptyState();
            actionsContainer.getChildren().add(emptyState);
        } else {
            for (ActionData action : actions) {
                VBox actionCard = createActionCard(action);
                actionsContainer.getChildren().add(actionCard);
            }
        }
    }
    
    private VBox createEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("actions-empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(64));
        
        Label emptyIcon = new Label("▶");
        emptyIcon.getStyleClass().add("actions-empty-icon");
        
        Label emptyTitle = new Label("No actions found");
        emptyTitle.getStyleClass().add("actions-empty-title");
        
        Label emptyText = new Label("Create your first reusable command action");
        emptyText.getStyleClass().add("actions-empty-text");
        
        Button createButton = new Button("+ Add Action");
        createButton.getStyleClass().add("actions-add-btn");
        createButton.setOnAction(e -> showAddActionDialog());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyText, createButton);
        
        return emptyState;
    }
    
    private String getActionDescription(ActionData action) {
        // For now, return a generic description based on action name
        // In the future, this could be stored as part of ActionData
        if (action.getName().toLowerCase().contains("git pull")) {
            return "Pull latest changes from repository";
        } else if (action.getName().toLowerCase().contains("restart")) {
            return "Restart system service";
        } else if (action.getName().toLowerCase().contains("deploy")) {
            return "Deploy application to server";
        }
        return null;
    }
    
    private void filterActions(String searchText) {
        // Implementation for filtering actions based on search text
        // This would filter the actions and reload the display
        loadActions(); // For now, just reload all actions
    }
    
    private void runAction(ActionData action) {
        // Implementation to run the action
        System.out.println("Running action: " + action.getName());
        // Here you would integrate with the actual action execution system
    }
    
    private void editAction(ActionData action) {
        showEditActionDialog(action);
    }
    
    private void deleteAction(ActionData action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Action");
        alert.setHeaderText("Are you sure you want to delete this action?");
        alert.setContentText("Action: " + action.getName());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                actionManager.deleteAction(action.getIndex());
                loadActions();
            }
        });
    }
    
    private void copyCommands(ActionData action) {
        // Implementation to copy commands to clipboard
        StringBuilder commands = new StringBuilder();
        for (String command : action.getCommands()) {
            commands.append(command).append("\n");
        }
        
        // Copy to clipboard (simplified implementation)
        System.out.println("Copied commands to clipboard:\n" + commands.toString());
    }
    
    private void showAddActionDialog() {
        ActionDialog dialog = new ActionDialog("Add New Action", null);
        dialog.showAndWait().ifPresent(actionData -> {
            actionManager.createAction(actionData.getName(), actionData.getCommands());
            loadActions();
        });
    }
    
    private void showEditActionDialog(ActionData action) {
        ActionDialog dialog = new ActionDialog("Edit Action", action);
        dialog.showAndWait().ifPresent(actionData -> {
            actionManager.updateAction(action.getIndex(), actionData.getName(), actionData.getCommands());
            loadActions();
        });
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

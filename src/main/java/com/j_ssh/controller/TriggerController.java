package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.managers.TriggerManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.model.objects.TriggerData;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.HashMap;
import java.util.List;

public class TriggerController extends com.j_ssh.view.bootstrap.BootstrapPane {
    private VBox triggersContainer;
    private TriggerManager triggerManager;
    private SessionManager sessionManager;
    private ActionManager actionManager;
    private TextField searchField;
    private Label triggerCountLabel;

    public TriggerController() {
        this.triggerManager = TriggerManager.get();
        this.sessionManager = SessionManager.get();
        this.actionManager = ActionManager.get();
        
        // Apply modern styling to main container
        this.getStyleClass().add("triggers-container");
        
        initializeModernComponents();
        loadTriggers();
    }

    private void initializeModernComponents() {
        // Add menu bar for navigation
        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);

        // Create modern header section
        BootstrapRow headerRow = createModernHeader();
        
        // Create triggers content area
        BootstrapRow contentRow = createTriggersContent();

        // Add rows to the main pane
        this.addRow(headerRow);
        this.addRow(contentRow);
    }
    
    private BootstrapRow createModernHeader() {
        BootstrapRow row = new BootstrapRow();
        
        VBox headerSection = new VBox();
        headerSection.getStyleClass().add("triggers-header");
        headerSection.setSpacing(16);
        headerSection.setPadding(new Insets(24));
        
        // Title and subtitle section
        VBox titleSection = new VBox();
        titleSection.setSpacing(8);
        
        Label titleLabel = new Label("Triggers");
        titleLabel.getStyleClass().add("triggers-title");
        
        Label subtitleLabel = new Label("Automated action sequences");
        subtitleLabel.getStyleClass().add("triggers-subtitle");
        
        titleSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Search and action section
        HBox searchActionSection = new HBox();
        searchActionSection.setSpacing(16);
        searchActionSection.setAlignment(Pos.CENTER_LEFT);
        
        // Search field
        searchField = new TextField();
        searchField.getStyleClass().add("triggers-search");
        searchField.setPromptText("Search triggers...");
        searchField.setPrefWidth(300);
        searchField.textProperty().addListener((obs, oldText, newText) -> filterTriggers(newText));
        
        // Trigger count
        triggerCountLabel = new Label("0 triggers");
        triggerCountLabel.getStyleClass().add("triggers-count");
        
        // Add trigger button
        Button addButton = new Button("+ Add Trigger");
        addButton.getStyleClass().add("triggers-add-btn");
        addButton.setOnAction(e -> showAddTriggerDialog());
        
        searchActionSection.getChildren().addAll(searchField, triggerCountLabel, new Region(), addButton);
        HBox.setHgrow(searchActionSection.getChildren().get(2), Priority.ALWAYS);
        
        headerSection.getChildren().addAll(titleSection, searchActionSection);
        
        BootstrapColumn headerCol = API.get().createColumn(headerSection, 12);
        row.addColumn(headerCol);
        
        return row;
    }

    private BootstrapRow createTriggersContent() {
        BootstrapRow row = new BootstrapRow();
        
        // Create scrollable content area
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("triggers-scroll");
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Create triggers container
        triggersContainer = new VBox();
        triggersContainer.getStyleClass().add("triggers-content");
        triggersContainer.setSpacing(16);
        triggersContainer.setPadding(new Insets(24));
        
        scrollPane.setContent(triggersContainer);
        
        BootstrapColumn contentCol = API.get().createColumn(scrollPane, 12);
        row.addColumn(contentCol);
        
        return row;
    }

    private VBox createTriggerCard(TriggerData trigger) {
        VBox card = new VBox();
        card.getStyleClass().add("trigger-card");
        card.setSpacing(16);
        card.setPadding(new Insets(20));
        
        // Card header with expand/collapse functionality
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(12);
        
        // Trigger icon
        Label triggerIcon = new Label("⚡");
        triggerIcon.getStyleClass().add("trigger-icon");
        
        // Trigger info
        VBox triggerInfo = new VBox();
        triggerInfo.setSpacing(4);
        
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.setSpacing(12);
        
        Label nameLabel = new Label(trigger.getName());
        nameLabel.getStyleClass().add("trigger-name");
        
        Label actionsCount = new Label(trigger.getTriggers().size() + " actions");
        actionsCount.getStyleClass().add("trigger-actions-count");
        
        titleRow.getChildren().addAll(nameLabel, actionsCount);
        
        Label descLabel = new Label(trigger.getDescription());
        descLabel.getStyleClass().add("trigger-description");
        
        // Server info
        Label serverInfo = new Label("Server: Jared Test Server • Last run: 2 hours ago • Runs: 5");
        serverInfo.getStyleClass().add("trigger-server-info");
        
        triggerInfo.getChildren().addAll(titleRow, descLabel, serverInfo);
        
        // Expand/collapse button
        Button expandButton = new Button("▼");
        expandButton.getStyleClass().add("trigger-expand-btn");
        
        header.getChildren().addAll(triggerIcon, triggerInfo, new Region(), expandButton);
        HBox.setHgrow(header.getChildren().get(2), Priority.ALWAYS);
        
        // Action sequence (initially hidden)
        VBox actionSequence = createActionSequence(trigger);
        actionSequence.setVisible(false);
        actionSequence.setManaged(false);
        
        // Action buttons
        HBox actionButtons = new HBox();
        actionButtons.setSpacing(8);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button runButton = new Button("▶ Run Trigger");
        runButton.getStyleClass().add("trigger-run-btn");
        runButton.setOnAction(e -> runTrigger(trigger));
        
        Button editButton = new Button("✎");
        editButton.getStyleClass().add("trigger-edit-btn");
        editButton.setOnAction(e -> editTrigger(trigger));
        
        Button deleteButton = new Button("🗑");
        deleteButton.getStyleClass().add("trigger-delete-btn");
        deleteButton.setOnAction(e -> deleteTrigger(trigger));
        
        actionButtons.getChildren().addAll(runButton, editButton, deleteButton);
        
        // Expand/collapse functionality
        expandButton.setOnAction(e -> {
            boolean isExpanded = actionSequence.isVisible();
            actionSequence.setVisible(!isExpanded);
            actionSequence.setManaged(!isExpanded);
            expandButton.setText(isExpanded ? "▼" : "▲");
        });
        
        card.getChildren().addAll(header, actionSequence, actionButtons);
        
        return card;
    }
    
    private VBox createActionSequence(TriggerData trigger) {
        VBox sequence = new VBox();
        sequence.getStyleClass().add("trigger-action-sequence");
        sequence.setSpacing(8);
        sequence.setPadding(new Insets(16));
        
        Label sequenceTitle = new Label("Action Sequence:");
        sequenceTitle.getStyleClass().add("trigger-sequence-title");
        
        VBox sequenceItems = new VBox();
        sequenceItems.setSpacing(8);
        
        int count = 1;
        for (Integer serverIndex : trigger.getTriggers().keySet()) {
            HBox sequenceItem = new HBox();
            sequenceItem.setAlignment(Pos.CENTER_LEFT);
            sequenceItem.setSpacing(12);
            
            Label stepNumber = new Label(String.valueOf(count));
            stepNumber.getStyleClass().add("trigger-step-number");
            
            ActionData action = actionManager.getAction(trigger.getTriggers().get(serverIndex));
            String actionName = action != null ? action.getName() : "Unknown Action";
            
            Label actionLabel = new Label(actionName);
            actionLabel.getStyleClass().add("trigger-sequence-action");
            
            if (count < trigger.getTriggers().size()) {
                Label arrow = new Label("→");
                arrow.getStyleClass().add("trigger-sequence-arrow");
                sequenceItem.getChildren().addAll(stepNumber, actionLabel, arrow);
            } else {
                sequenceItem.getChildren().addAll(stepNumber, actionLabel);
            }
            
            sequenceItems.getChildren().add(sequenceItem);
            count++;
        }
        
        sequence.getChildren().addAll(sequenceTitle, sequenceItems);
        
        return sequence;
    }

    private void loadTriggers() {
        triggersContainer.getChildren().clear();
        List<TriggerData> triggers = triggerManager.getAllTriggers();
        
        // Update trigger count
        triggerCountLabel.setText(triggers.size() + " trigger" + (triggers.size() != 1 ? "s" : ""));
        
        if (triggers.isEmpty()) {
            // Show empty state
            VBox emptyState = createEmptyState();
            triggersContainer.getChildren().add(emptyState);
        } else {
            for (TriggerData trigger : triggers) {
                VBox triggerCard = createTriggerCard(trigger);
                triggersContainer.getChildren().add(triggerCard);
            }
        }
    }
    
    private VBox createEmptyState() {
        VBox emptyState = new VBox();
        emptyState.getStyleClass().add("triggers-empty-state");
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setSpacing(16);
        emptyState.setPadding(new Insets(64));
        
        Label emptyIcon = new Label("⚡");
        emptyIcon.getStyleClass().add("triggers-empty-icon");
        
        Label emptyTitle = new Label("No triggers found");
        emptyTitle.getStyleClass().add("triggers-empty-title");
        
        Label emptyText = new Label("Create your first automated trigger sequence");
        emptyText.getStyleClass().add("triggers-empty-text");
        
        Button createButton = new Button("+ Add Trigger");
        createButton.getStyleClass().add("triggers-add-btn");
        createButton.setOnAction(e -> showAddTriggerDialog());
        
        emptyState.getChildren().addAll(emptyIcon, emptyTitle, emptyText, createButton);
        
        return emptyState;
    }
    
    private void filterTriggers(String searchText) {
        // Implementation for filtering triggers based on search text
        // This would filter the triggers and reload the display
        loadTriggers(); // For now, just reload all triggers
    }
    
    private void runTrigger(TriggerData trigger) {
        // Implementation to run the trigger
        System.out.println("Running trigger: " + trigger.getName());
        // Here you would integrate with the actual trigger execution system
    }
    
    private void editTrigger(TriggerData trigger) {
        showEditTriggerDialog(trigger);
    }
    
    private void deleteTrigger(TriggerData trigger) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Trigger");
        alert.setHeaderText("Are you sure you want to delete this trigger?");
        alert.setContentText("Trigger: " + trigger.getName());
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                triggerManager.deleteTrigger(trigger.getIndex());
                loadTriggers();
            }
        });
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

    private void showEditTriggerDialog(TriggerData trigger) {
        TriggerDialog dialog = new TriggerDialog("Edit Trigger", trigger);
        dialog.showAndWait().ifPresent(triggerData -> {
            if (triggerData != null) {
                triggerManager.updateTrigger(
                    trigger.getIndex(),
                    triggerData.getName(),
                    triggerData.getDescription(),
                    triggerData.getTriggers()
                );
                loadTriggers();
            }
        });
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

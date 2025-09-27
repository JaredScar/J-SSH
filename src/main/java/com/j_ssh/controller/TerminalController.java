package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class TerminalController extends BootstrapPane {
    private final TabPane tabs = new TabPane();
    private VBox actionButtonsPanel;
    private final ActionManager actionManager;
    private Label sessionInfoLabel;
    private Button focusTerminalButton;
    
    public TerminalController() {
        this.actionManager = ActionManager.get();
        this.tabs.setId("terminal_tabs");
        // Set preferred height without binding to avoid issues
        this.tabs.setPrefHeight(600);
        
        // Apply modern styling to main container
        this.getStyleClass().add("terminal-container");
        
        // Create action buttons panel
        createModernActionButtonsPanel();
        
        BootstrapRow mainRow = new BootstrapRow();
        
        // Terminal tabs (takes most of the space)
        BootstrapColumn terminalCol = API.get().createColumn(this.tabs, 12, 12, 9, 9, 8, 8);
        mainRow.addColumn(terminalCol);
        
        // Action buttons panel (sidebar)
        BootstrapColumn actionsCol = API.get().createColumn(actionButtonsPanel, 0, 0, 3, 3, 4, 4);
        mainRow.addColumn(actionsCol);

        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);
        this.addRow(mainRow);
    }
    
    private void createModernActionButtonsPanel() {
        actionButtonsPanel = new VBox();
        actionButtonsPanel.getStyleClass().add("terminal-sidebar");
        actionButtonsPanel.setSpacing(16);
        actionButtonsPanel.setPadding(new Insets(24));
        actionButtonsPanel.setPrefWidth(320);
        actionButtonsPanel.setMinWidth(300);
        
        // Header section
        VBox headerSection = new VBox();
        headerSection.setSpacing(8);
        
        Label titleLabel = new Label("Quick Actions");
        titleLabel.getStyleClass().add("terminal-sidebar-title");
        
        Label subtitleLabel = new Label("Run predefined commands");
        subtitleLabel.getStyleClass().add("terminal-sidebar-subtitle");
        
        headerSection.getChildren().addAll(titleLabel, subtitleLabel);
        
        // Session info section
        VBox sessionSection = new VBox();
        sessionSection.setSpacing(8);
        sessionSection.getStyleClass().add("terminal-session-info");
        
        sessionInfoLabel = new Label("Connected to session 2");
        sessionInfoLabel.getStyleClass().add("terminal-session-label");
        
        sessionSection.getChildren().add(sessionInfoLabel);
        
        // Actions container
        VBox actionsContainer = new VBox();
        actionsContainer.setSpacing(12);
        actionsContainer.getStyleClass().add("terminal-actions-container");
        
        // Load and add action buttons
        loadModernActionButtons(actionsContainer);
        
        // Focus terminal button
        focusTerminalButton = new Button("Focus Terminal");
        focusTerminalButton.getStyleClass().add("terminal-focus-btn");
        focusTerminalButton.setMaxWidth(Double.MAX_VALUE);
        focusTerminalButton.setOnAction(e -> focusCurrentTerminal());
        
        // Add all sections to main panel
        actionButtonsPanel.getChildren().addAll(
            headerSection,
            sessionSection,
            actionsContainer,
            new Region(), // Spacer
            focusTerminalButton
        );
        
        // Make the spacer grow to push focus button to bottom
        VBox.setVgrow(actionButtonsPanel.getChildren().get(3), Priority.ALWAYS);
    }
    
    private void loadModernActionButtons(VBox actionsContainer) {
        // Clear existing buttons
        actionsContainer.getChildren().clear();
        
        // Load actions and create modern action cards
        List<ActionData> actions = actionManager.getAllActions();
        if (actions.isEmpty()) {
            // Show empty state
            VBox emptyState = new VBox();
            emptyState.setAlignment(Pos.CENTER);
            emptyState.setSpacing(12);
            emptyState.getStyleClass().add("terminal-empty-actions");
            
            Label emptyIcon = new Label("⚡");
            emptyIcon.getStyleClass().add("terminal-empty-icon");
            
            Label emptyText = new Label("No actions available");
            emptyText.getStyleClass().add("terminal-empty-text");
            
            emptyState.getChildren().addAll(emptyIcon, emptyText);
            actionsContainer.getChildren().add(emptyState);
        } else {
            for (ActionData action : actions) {
                VBox actionCard = createModernActionCard(action);
                actionsContainer.getChildren().add(actionCard);
            }
        }
    }
    
    private VBox createModernActionCard(ActionData action) {
        VBox card = new VBox();
        card.getStyleClass().add("terminal-action-card");
        card.setSpacing(8);
        
        // Card header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(8);
        
        Label nameLabel = new Label(action.getName());
        nameLabel.getStyleClass().add("terminal-action-name");
        
        Label descLabel = new Label("Action: " + action.getCommands().size() + " command(s)");
        descLabel.getStyleClass().add("terminal-action-desc");
        
        // Run button
        Button runButton = new Button("▶");
        runButton.getStyleClass().add("terminal-action-run-btn");
        runButton.setOnAction(e -> executeActionOnCurrentTerminal(action));
        
        header.getChildren().addAll(nameLabel, new Region(), runButton);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        
        card.getChildren().addAll(header, descLabel);
        
        return card;
    }
    
    private void focusCurrentTerminal() {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getContent() instanceof TerminalTabComponent) {
            selectedTab.getContent().requestFocus();
        }
    }
    
    private void executeActionOnCurrentTerminal(ActionData action) {
        Tab selectedTab = tabs.getSelectionModel().getSelectedItem();
        if (selectedTab != null && selectedTab.getContent() instanceof TerminalTabComponent) {
            TerminalTabComponent terminal = (TerminalTabComponent) selectedTab.getContent();
            actionManager.executeAction(action, terminal);
        } else {
            // Show alert if no terminal is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Terminal Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a terminal tab to execute the action.");
            alert.showAndWait();
        }
    }
    public void addTerminalTab(TerminalTabComponent tab) {
        String nickname = "Terminal Session";
        int count = 1;
        for (Tab existingTab : this.tabs.getTabs()) {
            if (existingTab.getText().equals(nickname)) {
                nickname = "Terminal Session (" + count + ")";
                count++;
            }
        }
        Tab newTab = new Tab(nickname, tab);
        newTab.getStyleClass().add("terminal_tab");
        this.tabs.getTabs().add(newTab);
        newTab.setOnClosed((event -> tab.close()));
    }
    public void closeTerminalTab(TerminalTabComponent tab) {
        tab.close();
    }
}

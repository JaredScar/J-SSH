package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.components.ActionableButtonComponent;
import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class TerminalController extends BootstrapPane {
    private TabPane tabs = new TabPane();
    private VBox actionButtonsPanel;
    private ActionManager actionManager;
    
    public TerminalController() {
        this.actionManager = ActionManager.get();
        this.tabs.setId("terminal_tabs");
        this.tabs.prefHeightProperty().bind(MainApp.get().getPrimaryStage().heightProperty());
        
        // Create action buttons panel
        createActionButtonsPanel();
        
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
    
    private void createActionButtonsPanel() {
        actionButtonsPanel = new VBox(5);
        actionButtonsPanel.getStyleClass().add("action-buttons-panel");
        actionButtonsPanel.setPrefWidth(200);
        actionButtonsPanel.setMinWidth(150);
        
        // Add title
        Label titleLabel = new Label("Actions");
        titleLabel.getStyleClass().add("action-panel-title");
        actionButtonsPanel.getChildren().add(titleLabel);
        
        // Add separator
        Separator separator = new Separator();
        actionButtonsPanel.getChildren().add(separator);
        
        // Load and add action buttons
        loadActionButtons();
    }
    
    private void loadActionButtons() {
        // Clear existing buttons and refresh button (keep title and separator)
        actionButtonsPanel.getChildren().removeIf(node -> 
            node instanceof ActionableButtonComponent || 
            (node instanceof Button && ((Button) node).getText().equals("Refresh Actions")));
        
        // Load actions and create buttons
        List<ActionData> actions = actionManager.getAllActions();
        for (ActionData action : actions) {
            ActionableButtonComponent button = new ActionableButtonComponent(action);
            
            // Add event handler for button clicks
            button.addEventHandler(ActionableButtonComponent.ActionButtonClickEvent.ACTION_BUTTON_CLICKED, event -> {
                executeActionOnCurrentTerminal(action);
            });
            
            actionButtonsPanel.getChildren().add(button);
        }
        
        // Add refresh button
        Button refreshButton = new Button("Refresh Actions");
        refreshButton.getStyleClass().add("btn-secondary");
        refreshButton.setOnAction(e -> loadActionButtons());
        actionButtonsPanel.getChildren().add(refreshButton);
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
        String nickname = tab.getNickname();
        int count = 1;
        for (Tab existingTab : this.tabs.getTabs()) {
            if (existingTab.getText().equals(nickname)) {
                nickname = tab.getNickname() + " (" + count + ")";
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

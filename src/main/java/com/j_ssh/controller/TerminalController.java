package com.j_ssh.controller;

import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.ConnectionManager;
import com.j_ssh.model.objects.ActionData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class TerminalController extends BorderPane {
    private final StackPane terminalHost = new StackPane();
    private final HBox sessionTabs = new HBox(8);
    private final List<TerminalTabComponent> openTerminals = new ArrayList<>();
    private TerminalTabComponent activeTerminal;
    private VBox actionButtonsPanel;
    private VBox actionsContainer;
    private final ActionManager actionManager;
    private Label sessionInfoLabel;
    private Label statusBadge;
    private Button closeSessionButton;
    private final VBox emptyState = new VBox();
    
    public TerminalController() {
        this.actionManager = ActionManager.get();
        this.getStyleClass().add("terminal-container");
        this.terminalHost.setStyle("-fx-background-color: #000000;");
        this.terminalHost.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.terminalHost.setMinSize(0, 0);
        this.sessionTabs.getStyleClass().add("terminal-session-tabs");
        this.sessionTabs.setPadding(new Insets(8, 12, 0, 12));

        emptyState.getStyleClass().add("terminal-empty");
        emptyState.setAlignment(Pos.CENTER);
        Label emptyTitle = new Label("No active session");
        emptyTitle.getStyleClass().add("terminal-empty-title");
        Label emptyText = new Label("Connect from Sessions to open a terminal");
        emptyText.getStyleClass().add("terminal-empty-text");
        emptyState.getChildren().addAll(emptyTitle, emptyText);

        StackPane terminalBody = new StackPane(terminalHost, emptyState);
        terminalBody.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(terminalBody, Priority.ALWAYS);
        VBox terminalColumn = new VBox(createChrome(), sessionTabs, terminalBody);
        terminalColumn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(terminalBody, Priority.ALWAYS);
        setCenter(terminalColumn);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        createModernActionButtonsPanel();
        setRight(actionButtonsPanel);
        refreshEmptyState();
    }

    private HBox createChrome() {
        HBox bar = new HBox(10);
        bar.getStyleClass().add("terminal-chrome");
        bar.setAlignment(Pos.CENTER_LEFT);

        closeSessionButton = new Button();
        closeSessionButton.getStyleClass().add("terminal-close-dot");
        closeSessionButton.setTooltip(new Tooltip("Close this terminal"));
        closeSessionButton.setDisable(true);
        closeSessionButton.setOnAction(event -> {
            if (activeTerminal != null) {
                closeTerminal(activeTerminal);
            }
        });

        Label title = new Label("Terminal");
        title.getStyleClass().add("terminal-chrome-title");
        sessionInfoLabel = new Label("No session selected");
        sessionInfoLabel.getStyleClass().add("terminal-session-label");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        statusBadge = new Label("Disconnected");
        statusBadge.getStyleClass().add("terminal-badge");
        bar.getChildren().addAll(closeSessionButton, title, sessionInfoLabel, spacer, statusBadge);
        return bar;
    }

    private void refreshEmptyState() {
        boolean hasTabs = !openTerminals.isEmpty();
        emptyState.setVisible(!hasTabs);
        emptyState.setManaged(!hasTabs);
        terminalHost.setVisible(hasTabs);
        terminalHost.setManaged(hasTabs);
        sessionTabs.setVisible(hasTabs);
        sessionTabs.setManaged(hasTabs);
        if (closeSessionButton != null) {
            closeSessionButton.setDisable(!hasTabs);
        }
        if (!hasTabs) {
            sessionInfoLabel.setText("No session selected");
            statusBadge.setText("Disconnected");
            statusBadge.getStyleClass().remove("badge-live");
        }
    }

    public void refreshActions() {
        if (actionsContainer != null) {
            loadModernActionButtons(actionsContainer);
        }
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
        
        // Actions container
        actionsContainer = new VBox();
        actionsContainer.setSpacing(12);
        actionsContainer.getStyleClass().add("terminal-actions-container");
        
        // Load and add action buttons
        loadModernActionButtons(actionsContainer);
        
        Button focusTerminalButton = new Button("Focus Terminal");
        focusTerminalButton.getStyleClass().add("terminal-focus-btn");
        focusTerminalButton.setMaxWidth(Double.MAX_VALUE);
        focusTerminalButton.setOnAction(e -> focusCurrentTerminal());
        
        ScrollPane actionScroll = new ScrollPane(actionsContainer);
        actionScroll.setFitToWidth(true);
        actionScroll.getStyleClass().add("page-scroll");
        actionScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(actionScroll, Priority.ALWAYS);

        actionButtonsPanel.getChildren().addAll(headerSection, actionScroll, focusTerminalButton);
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
        if (activeTerminal != null) {
            activeTerminal.requestFocus();
        }
    }
    
    private void executeActionOnCurrentTerminal(ActionData action) {
        if (activeTerminal != null) {
            actionManager.executeAction(action, activeTerminal);
        } else {
            // Show alert if no terminal is selected
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Terminal Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a terminal tab to execute the action.");
            MainApp.theme(alert);
            alert.showAndWait();
        }
    }
    public void addTerminalTab(TerminalTabComponent tab) {
        String nickname = tab.getNickname() == null || tab.getNickname().isBlank()
                ? "Terminal"
                : tab.getNickname();
        String uniqueName = nickname;
        int count = 2;
        for (TerminalTabComponent existing : openTerminals) {
            Object existingName = existing.getProperties().get("sessionName");
            if (uniqueName.equals(existingName)) {
                uniqueName = nickname + " (" + count + ")";
                count++;
            }
        }
        final String sessionName = uniqueName;
        tab.getProperties().put("sessionName", sessionName);
        tab.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        openTerminals.add(tab);
        terminalHost.getChildren().add(tab);

        Button chip = new Button(sessionName);
        chip.getStyleClass().add("session-chip");
        chip.setOnAction(event -> showTerminal(tab));
        Button close = new Button("x");
        close.getStyleClass().add("session-chip-close");
        close.setOnAction(event -> closeTerminal(tab));
        HBox chipRow = new HBox(4, chip, close);
        chipRow.setAlignment(Pos.CENTER_LEFT);
        chipRow.getStyleClass().add("session-chip-row");
        tab.getProperties().put("chip", chipRow);
        sessionTabs.getChildren().add(chipRow);

        showTerminal(tab);
        statusBadge.setText("Connected");
        if (!statusBadge.getStyleClass().contains("badge-live")) {
            statusBadge.getStyleClass().add("badge-live");
        }
        refreshEmptyState();
        MainApp.get().setTerminalEnabled(true);
    }

    private void showTerminal(TerminalTabComponent tab) {
        activeTerminal = tab;
        for (TerminalTabComponent open : openTerminals) {
            boolean selected = open == tab;
            open.setVisible(selected);
            open.setManaged(selected);
            Object chip = open.getProperties().get("chip");
            if (chip instanceof HBox row) {
                if (selected && !row.getStyleClass().contains("session-chip-active")) {
                    row.getStyleClass().add("session-chip-active");
                } else if (!selected) {
                    row.getStyleClass().remove("session-chip-active");
                }
            }
        }
        Object name = tab.getProperties().get("sessionName");
        sessionInfoLabel.setText(name == null ? "Terminal" : name.toString());
    }

    private void closeTerminal(TerminalTabComponent tab) {
        ConnectionManager.get().removeConnection(tab);
        tab.close();
        openTerminals.remove(tab);
        terminalHost.getChildren().remove(tab);
        Object chip = tab.getProperties().get("chip");
        if (chip instanceof HBox row) {
            sessionTabs.getChildren().remove(row);
        }
        if (openTerminals.isEmpty()) {
            activeTerminal = null;
            MainApp.get().setTerminalEnabled(false);
            refreshEmptyState();
            MainApp.get().changeScene(com.j_ssh.model.objects.JScene.SESSIONS);
        } else {
            showTerminal(openTerminals.get(openTerminals.size() - 1));
            refreshEmptyState();
        }
    }
    public void closeTerminalTab(TerminalTabComponent tab) {
        tab.close();
    }
}

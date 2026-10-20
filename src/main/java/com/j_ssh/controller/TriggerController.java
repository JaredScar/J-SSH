package com.j_ssh.controller;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.ConnectionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.managers.TriggerManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.model.objects.TriggerData;
import com.j_ssh.view.UiKit;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TriggerController extends BorderPane {
    private final TriggerManager triggerManager;
    private final SessionManager sessionManager;
    private final ActionManager actionManager;
    private final TextField searchField = new TextField();
    private final VBox body = new VBox(14);

    public TriggerController() {
        this.triggerManager = TriggerManager.get();
        this.sessionManager = SessionManager.get();
        this.actionManager = ActionManager.get();
        getStyleClass().add("page");

        searchField.textProperty().addListener((obs, oldText, newText) -> loadTriggers());
        Button addButton = UiKit.button("New trigger", UiKit.PLUS, "btn-primary");
        addButton.setOnAction(event -> showAddTriggerDialog());

        setTop(UiKit.pageHeader("Triggers", "Run a sequence of actions on a server",
                UiKit.searchBox(searchField, "Search triggers"), addButton));
        setCenter(UiKit.scrollPage(body));
        loadTriggers();
    }

    private void loadTriggers() {
        body.getChildren().clear();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        List<TriggerData> triggers = triggerManager.getAllTriggers().stream()
                .filter(trigger -> query.isEmpty()
                        || trigger.getName().toLowerCase().contains(query)
                        || (trigger.getDescription() != null && trigger.getDescription().toLowerCase().contains(query))
                        || describeServers(trigger).toLowerCase().contains(query))
                .toList();

        if (triggers.isEmpty()) {
            Button create = UiKit.button("New trigger", UiKit.PLUS, "btn-primary");
            create.setOnAction(event -> showAddTriggerDialog());
            boolean searching = !query.isEmpty();
            body.getChildren().add(UiKit.emptyState(UiKit.BOLT,
                    searching ? "No matching triggers" : "No triggers yet",
                    searching ? "Try a different search." : "Create a trigger to run actions on your servers.",
                    searching ? null : create));
            return;
        }
        for (TriggerData trigger : triggers) {
            body.getChildren().add(createTriggerCard(trigger));
        }
    }

    private VBox createTriggerCard(TriggerData trigger) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label name = new Label(trigger.getName());
        name.getStyleClass().add("card-title");
        VBox titles = new VBox(3, name);
        if (trigger.getDescription() != null && !trigger.getDescription().isBlank()) {
            Label desc = new Label(trigger.getDescription());
            desc.getStyleClass().add("card-sub");
            desc.setWrapText(true);
            titles.getChildren().add(desc);
        }
        HBox.setHgrow(titles, Priority.ALWAYS);

        Button run = UiKit.button("Run", UiKit.PLAY, "btn-secondary");
        run.setOnAction(event -> runTrigger(trigger));
        Button more = UiKit.moreButton(
                UiKit.menuItem("Edit", () -> showEditTriggerDialog(trigger)),
                UiKit.menuItem("Delete", () -> deleteTrigger(trigger)));
        HBox top = new HBox(8, titles, run, more);
        top.setAlignment(Pos.TOP_LEFT);

        FlowPane steps = new FlowPane(8, 8);
        steps.setAlignment(Pos.CENTER_LEFT);
        int count = 1;
        for (Integer serverIndex : trigger.getTriggers().keySet()) {
            ActionData action = actionManager.getAction(trigger.getTriggers().get(serverIndex));
            String actionName = action != null ? action.getName() : "Unknown action";
            Label number = new Label(String.valueOf(count));
            number.getStyleClass().add("step-number");
            Label label = new Label(actionName);
            label.getStyleClass().add("step-name");
            HBox chip = new HBox(8, number, label);
            chip.getStyleClass().add("step-chip");
            chip.setAlignment(Pos.CENTER_LEFT);
            if (count > 1) {
                Label separator = UiKit.icon(UiKit.CHEVRON);
                separator.getStyleClass().add("step-sep");
                steps.getChildren().add(separator);
            }
            steps.getChildren().add(chip);
            count++;
        }

        Label serversIcon = UiKit.icon(UiKit.MONITOR);
        serversIcon.getStyleClass().add("card-meta");
        Label servers = new Label(describeServers(trigger));
        servers.getStyleClass().add("card-meta");
        HBox footer = new HBox(7, serversIcon, servers);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(top, steps, footer);
        return card;
    }

    private String describeServers(TriggerData trigger) {
        return trigger.getTriggers().keySet().stream()
                .map(sessionManager::getSession)
                .filter(server -> server != null)
                .map(ServerData::getNickname)
                .distinct()
                .reduce((left, right) -> left + ", " + right)
                .orElse("No server");
    }

    /** Runs each step on its server, for servers that already have an open terminal. */
    private void runTrigger(TriggerData trigger) {
        TerminalController terminal = MainApp.get().getTerminalController();
        int ran = 0;
        List<String> notConnected = new ArrayList<>();
        for (Map.Entry<Integer, Integer> step : trigger.getTriggers().entrySet()) {
            ServerData server = sessionManager.getSession(step.getKey());
            ActionData action = actionManager.getAction(step.getValue());
            if (server == null || action == null) {
                continue;
            }
            Connection connection = ConnectionManager.get().getServerConnection(server);
            if (connection != null && connection.isConnected() && terminal.runActionOn(connection, action)) {
                ran++;
            } else {
                notConnected.add(server.getNickname());
            }
        }
        if (ran > 0) {
            MainApp.get().changeScene(JScene.TERMINAL);
        }
        if (!notConnected.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Trigger");
            alert.setHeaderText(ran == 0 ? "Nothing was run" : "Some steps were skipped");
            alert.setContentText("No open terminal for: " + String.join(", ", notConnected)
                    + ".\nConnect to those sessions, then run the trigger again.");
            MainApp.theme(alert);
            alert.showAndWait();
        }
    }

    private void deleteTrigger(TriggerData trigger) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Trigger");
        alert.setHeaderText("Are you sure you want to delete this trigger?");
        alert.setContentText("Trigger: " + trigger.getName());
        MainApp.theme(alert);

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
            MainApp.theme(this);

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

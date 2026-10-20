package com.j_ssh.controller;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.view.UiKit;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class ButtonController extends BorderPane {
    private final ActionManager actionManager;
    private final TextField searchField = new TextField();
    private final VBox body = new VBox();

    public ButtonController() {
        this.actionManager = ActionManager.get();
        getStyleClass().add("page");

        searchField.textProperty().addListener((obs, oldText, newText) -> loadActions());
        Button addButton = UiKit.button("New action", UiKit.PLUS, "btn-primary");
        addButton.setOnAction(event -> showAddActionDialog());

        setTop(UiKit.pageHeader("Actions", "Reusable command sequences",
                UiKit.searchBox(searchField, "Search actions"), addButton));
        setCenter(UiKit.scrollPage(body));
        loadActions();
    }

    private void loadActions() {
        body.getChildren().clear();
        String query = searchField.getText() == null ? "" : searchField.getText().trim().toLowerCase();
        List<ActionData> actions = actionManager.getAllActions().stream()
                .filter(action -> query.isEmpty()
                        || action.getName().toLowerCase().contains(query)
                        || action.getCommands().stream().anyMatch(command -> command.toLowerCase().contains(query)))
                .toList();

        if (actions.isEmpty()) {
            Button create = UiKit.button("New action", UiKit.PLUS, "btn-primary");
            create.setOnAction(event -> showAddActionDialog());
            boolean searching = !query.isEmpty();
            body.getChildren().add(UiKit.emptyState(UiKit.PLAY,
                    searching ? "No matching actions" : "No actions yet",
                    searching ? "Try a different search." : "Create your first reusable command sequence.",
                    searching ? null : create));
            return;
        }

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        for (int i = 0; i < 2; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(50);
            column.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(column);
        }
        int index = 0;
        for (ActionData action : actions) {
            VBox card = createActionCard(action);
            card.setMaxWidth(Double.MAX_VALUE);
            grid.add(card, index % 2, index / 2);
            index++;
        }
        body.getChildren().add(grid);
    }

    private VBox createActionCard(ActionData action) {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label name = new Label(action.getName());
        name.getStyleClass().add("card-title");
        name.setMaxWidth(Double.MAX_VALUE);
        VBox titles = new VBox(3, name);
        String description = getActionDescription(action);
        if (description != null && !description.isEmpty()) {
            Label desc = new Label(description);
            desc.getStyleClass().add("card-sub");
            desc.setMaxWidth(Double.MAX_VALUE);
            titles.getChildren().add(desc);
        }
        HBox.setHgrow(titles, Priority.ALWAYS);

        Button copy = UiKit.iconButton(UiKit.COPY, "Copy commands");
        copy.setOnAction(event -> copyCommands(action));
        Button more = UiKit.moreButton(
                UiKit.menuItem("Edit", () -> showEditActionDialog(action)),
                UiKit.menuItem("Delete", () -> deleteAction(action)));
        HBox top = new HBox(4, titles, copy, more);
        top.setAlignment(Pos.TOP_LEFT);

        VBox code = new VBox(4);
        code.getStyleClass().add("code-block");
        for (String command : action.getCommands()) {
            Label prompt = new Label("$");
            prompt.getStyleClass().add("code-prompt");
            Label text = new Label(command);
            text.getStyleClass().add("code-text");
            text.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(text, Priority.ALWAYS);
            HBox line = new HBox(8, prompt, text);
            line.setAlignment(Pos.CENTER_LEFT);
            code.getChildren().add(line);
        }

        int count = action.getCommands().size();
        Label counter = new Label(count + (count == 1 ? " command" : " commands"));
        counter.getStyleClass().add("card-meta");
        Button run = UiKit.button("Run", UiKit.PLAY, "btn-secondary");
        run.setOnAction(event -> runAction(action));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(8, counter, spacer, run);
        footer.setAlignment(Pos.CENTER_LEFT);

        Region grow = new Region();
        VBox.setVgrow(grow, Priority.ALWAYS);
        card.getChildren().addAll(top, code, grow, footer);
        return card;
    }

    private String getActionDescription(ActionData action) {
        String lower = action.getName().toLowerCase();
        if (lower.contains("git pull")) {
            return "Pull latest changes from repository";
        } else if (lower.contains("restart")) {
            return "Restart system service";
        } else if (lower.contains("deploy")) {
            return "Deploy application to server";
        }
        return null;
    }

    private void runAction(ActionData action) {
        TerminalController terminal = MainApp.get().getTerminalController();
        boolean hadTerminal = terminal.hasActiveTerminal();
        terminal.runAction(action);
        if (hadTerminal) {
            MainApp.get().changeScene(JScene.TERMINAL);
        }
    }

    private void deleteAction(ActionData action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Action");
        alert.setHeaderText("Are you sure you want to delete this action?");
        alert.setContentText("Action: " + action.getName());
        MainApp.theme(alert);

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                actionManager.deleteAction(action.getIndex());
                loadActions();
            }
        });
    }

    private void copyCommands(ActionData action) {
        StringBuilder commands = new StringBuilder();
        for (String command : action.getCommands()) {
            commands.append(command).append("\n");
        }
        ClipboardContent content = new ClipboardContent();
        content.putString(commands.toString().trim());
        Clipboard.getSystemClipboard().setContent(content);
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

            VBox form = new VBox(10);

            Label nameLabel = new Label("Action Name:");
            nameField = new TextField();
            nameField.setPromptText("Enter action name");
            if (existingAction != null) {
                nameField.setText(existingAction.getName());
            }

            Label commandsLabel = new Label("Commands (one per line):");
            commandsArea = new TextArea();
            commandsArea.setPromptText("Enter commands, one per line");
            commandsArea.setPrefRowCount(8);
            if (existingAction != null) {
                commandsArea.setText(String.join("\n", existingAction.getCommands()));
            }

            form.getChildren().addAll(nameLabel, nameField, commandsLabel, commandsArea);

            getDialogPane().setContent(form);

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
            MainApp.theme(this);

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

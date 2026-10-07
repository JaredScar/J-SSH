package com.j_ssh.controller;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.ActionManager;
import com.j_ssh.model.managers.SessionManager;
import com.j_ssh.model.managers.TriggerManager;
import com.j_ssh.model.objects.JScene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.Desktop;
import java.io.File;

public class SettingsController extends BorderPane {
    private final Label sessionStat = new Label("0");
    private final Label actionStat = new Label("0");
    private final Label triggerStat = new Label("0");
    private final Label pathLabel = new Label();
    private final Label pathStatus = new Label();

    public SettingsController() {
        getStyleClass().add("settings-container");

        VBox page = new VBox(18);
        page.setPadding(new Insets(28, 32, 40, 32));
        page.setMaxWidth(860);
        page.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Settings");
        title.getStyleClass().add("page-title");
        Label subtitle = new Label("What this app stores, and where to read the guide.");
        subtitle.setWrapText(true);
        subtitle.getStyleClass().add("page-intro");

        page.getChildren().addAll(title, subtitle, statsRow(), dataCard(), aboutCard());

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setCenter(scroll);
        refresh();
    }

    public void refresh() {
        sessionStat.setText(String.valueOf(SessionManager.get().getAllSessions().size()));
        actionStat.setText(String.valueOf(ActionManager.get().getAllActions().size()));
        triggerStat.setText(String.valueOf(TriggerManager.get().getAllTriggers().size()));
        pathLabel.setText(new File("data.json").getAbsolutePath());
    }

    private HBox statsRow() {
        HBox row = new HBox(12);
        row.getChildren().addAll(
                statCard(sessionStat, "Sessions"),
                statCard(actionStat, "Actions"),
                statCard(triggerStat, "Triggers")
        );
        return row;
    }

    private VBox statCard(Label value, String caption) {
        VBox card = new VBox(4);
        card.getStyleClass().add("stat-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        value.getStyleClass().add("stat-value");
        Label label = new Label(caption);
        label.getStyleClass().add("stat-caption");
        card.getChildren().addAll(value, label);
        return card;
    }

    private VBox dataCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("guide-section");

        Label heading = new Label("Saved data");
        heading.getStyleClass().add("guide-heading");
        Label body = new Label("Sessions, actions, and triggers are written to this file whenever you save a change. Host keys are kept in knownHosts.txt beside it.");
        body.setWrapText(true);
        body.getStyleClass().add("guide-lead");

        pathLabel.setWrapText(true);
        pathLabel.getStyleClass().add("settings-path");

        Button copy = new Button("Copy path");
        copy.getStyleClass().add("settings-secondary-btn");
        copy.setOnAction(event -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(pathLabel.getText());
            Clipboard.getSystemClipboard().setContent(content);
            pathStatus.setText("Path copied.");
        });

        Button open = new Button("Open folder");
        open.getStyleClass().add("settings-primary-btn");
        open.setOnAction(event -> openDataFolder());

        pathStatus.getStyleClass().add("stat-caption");
        card.getChildren().addAll(heading, body, pathLabel, new HBox(10, open, copy), pathStatus);
        return card;
    }

    private void openDataFolder() {
        File folder = new File("data.json").getAbsoluteFile().getParentFile();
        try {
            if (folder != null && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(folder);
                pathStatus.setText("");
                return;
            }
        } catch (Exception ignored) {
        }
        pathStatus.setText("Could not open the folder. The path is shown above.");
    }

    private VBox aboutCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("guide-section");

        Label heading = new Label("About J-SSH");
        heading.getStyleClass().add("guide-heading");
        Label body = new Label("J-SSH is a desktop SSH client. Save a server, write reusable commands, and run them from the terminal without retyping them.");
        body.setWrapText(true);
        body.getStyleClass().add("guide-lead");
        Label version = new Label("Version 1.0");
        version.getStyleClass().add("stat-caption");

        Button help = new Button("Open the guide");
        help.getStyleClass().add("settings-primary-btn");
        help.setOnAction(event -> MainApp.get().changeScene(JScene.HELP));

        card.getChildren().addAll(heading, body, version, help);
        return card;
    }
}

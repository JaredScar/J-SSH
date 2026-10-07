package com.j_ssh.controller;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class HelpController extends BorderPane {
    public HelpController() {
        getStyleClass().add("settings-container");

        VBox page = new VBox(16);
        page.setPadding(new Insets(28, 32, 40, 32));
        page.setMaxWidth(860);
        page.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Help");
        title.getStyleClass().add("page-title");
        Label intro = new Label("J-SSH keeps your servers, command sequences, and live terminals in one window. Use the sidebar to move between each part.");
        intro.setWrapText(true);
        intro.getStyleClass().add("page-intro");

        page.getChildren().addAll(
                title,
                intro,
                section("Sessions",
                        "A session is a saved SSH connection: nickname, host, username, and password.",
                        "Add Session creates a new connection.",
                        "Connect opens a terminal for that server. The button changes to Disconnect while it is open.",
                        "The pencil edits the saved details. The trash icon deletes the session."),
                section("Actions",
                        "An action is a list of commands you can run again later.",
                        "Add Action, then enter one command per line.",
                        "Run Action sends those commands to the terminal that is currently open.",
                        "The copy button puts the commands on the clipboard."),
                section("Triggers",
                        "A trigger runs one or more actions on chosen servers.",
                        "Add Trigger, pick the servers and actions, and save.",
                        "Open a trigger to see the sequence, then use Run Trigger.",
                        "Each step is tied to a server and an action you already created."),
                section("Terminal",
                        "The terminal is the live shell for a connected session.",
                        "The red circle in the header closes that terminal and disconnects.",
                        "If more than one session is open, use the chips under the header to switch.",
                        "Quick Actions on the right run a saved action in the terminal you are viewing.",
                        "Type in the black area. Each key is sent once, and the server echo is what you see."),
                section("Your data",
                        "Sessions, actions, and triggers are written to data.json in the folder you start J-SSH from.",
                        "Known host keys are stored in knownHosts.txt in that same folder.",
                        "The Settings page shows the exact path and how many items are saved.")
        );

        ScrollPane scroll = new ScrollPane(page);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        setCenter(scroll);
    }

    private VBox section(String heading, String... lines) {
        VBox box = new VBox(8);
        box.getStyleClass().add("guide-section");
        Label title = new Label(heading);
        title.getStyleClass().add("guide-heading");
        box.getChildren().add(title);
        boolean first = true;
        for (String line : lines) {
            Label body = new Label(first ? line : "•  " + line);
            body.setWrapText(true);
            body.getStyleClass().add(first ? "guide-lead" : "guide-step");
            box.getChildren().add(body);
            first = false;
        }
        return box;
    }
}

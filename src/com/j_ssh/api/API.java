package com.j_ssh.api;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class API {
    public static API api = new API();
    public static API get() {
        return api;
    }
    public void createToolbox(VBox vbox) {
        MenuBar mbar = new MenuBar();

        // Main Menu
        Menu mainMenu = new Menu("J-SSH");
        MenuItem[] mainItems = {new MenuItem("About"), new MenuItem("Help")};
        //mainItems[0].addEventHandler(); // TODO Add event handler for About
        //mainItems[1].addEventHandler(); // TODO Add event handler for Help
        mainMenu.getItems().addAll(mainItems);

        // Session Menu
        Menu sessionMenu = new Menu("Session");
        MenuItem[] sessionItems = {new MenuItem("Create New Session"), new MenuItem("Edit Sessions"), new MenuItem("Open Session")};
        //sessionItems[0].addEventHandler(); // TODO Add event handler for Create New Session
        //sessionItems[1].addEventHandler(); // TODO Add event handler for Edit Sessions
        //sessionItems[2].addEventHandler(); // TODO Add event handler for Open Session
        sessionMenu.getItems().addAll(sessionItems);

        // Action Menu
        Menu actionMenu = new Menu("Actions");
        MenuItem[] actionItems = {new MenuItem("Create New Action"), new MenuItem("Edit Actions"), new MenuItem("Trigger Action")};
        //actionItems[0].addEventHandler(); // TODO Add event handler for Create New Action
        //actionItems[1].addEventHandler(); // TODO Add event handler for Edit Actions
        //actionItems[2].addEventHandler(); // TODO Add event handler for Trigger Action
        actionMenu.getItems().addAll(actionItems);

        mbar.getMenus().addAll(mainMenu, sessionMenu, actionMenu);
        vbox.getChildren().add(mbar);
    }
}

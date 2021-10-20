package com.j_ssh.api;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
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
        MenuItem[] mainItems = {new MenuItem("About")};
        //mainItems[0].addEventHandler(); // TODO Add event handler for About
        mainMenu.getItems().addAll(mainItems);

        // Session Menu
        Menu sessionMenu = new Menu("Session");
        MenuItem[] sessionItems = {new MenuItem("Create New Session"), new MenuItem("Open Session")};
        //sessionItems[0].addEventHandler(); // TODO Add event handler for Create New Session
        //sessionItems[1].addEventHandler(); // TODO Add event handler for Open Session
        sessionMenu.getItems().addAll(sessionItems);

        // Action Menu
        Menu actionMenu = new Menu("Actions");
        MenuItem[] actionItems = {new MenuItem("Create New Action"), new MenuItem("Edit Action"), new MenuItem("Delete Action")};
        actionMenu.getItems().addAll(actionItems);

        mbar.getMenus().addAll(mainMenu, sessionMenu, actionMenu);
        vbox.getChildren().add(mbar);
    }
}

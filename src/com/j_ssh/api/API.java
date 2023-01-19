package com.j_ssh.api;

import com.j_ssh.main.MainApp;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapRow;
import com.j_ssh.view.bootstrap.Breakpoint;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.concurrent.Callable;

public class API {
    public static API api = new API();
    public static API get() {
        return api;
    }
    public BootstrapRow createToolbox() {
        VBox vBox = new VBox();
        MenuBar mbar = new MenuBar();

        // Main Menu
        Menu mainMenu = new Menu("J-SSH");
        MenuItem[] mainItems = {new MenuItem("About"), new MenuItem("Help")};
        mainItems[0].addEventHandler(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                PopupHandler.triggerAboutPopup();
            }
        }); // TODO Add event handler for About
        //mainItems[1].addEventHandler(); // TODO Add event handler for Help
        mainMenu.getItems().addAll(mainItems);

        // Session Menu
        Menu sessionMenu = new Menu("Session");
        MenuItem[] sessionItems = {new MenuItem("Create New Session"), new MenuItem("Edit Sessions"), new MenuItem("Open Session")};
        sessionItems[0].addEventHandler(EventType.ROOT, event -> {
            PopupHandler.createSessionPopup();
        });
        sessionItems[1].addEventHandler(EventType.ROOT, event -> {
            PopupHandler.editSessionPopup();
        });
        sessionItems[2].addEventHandler(EventType.ROOT, event -> {
            // We want to change to the dashboard screen for them to open a new session
            MainApp.get().changeScene(JScene.DASHBOARD);
        });
        sessionMenu.getItems().addAll(sessionItems);

        // Action Menu
        Menu actionMenu = new Menu("Actions");
        MenuItem[] actionItems = {new MenuItem("Create New Action"), new MenuItem("Edit Actions"), new MenuItem("Trigger Action")};
        actionItems[0].addEventHandler(EventType.ROOT, event -> {
            PopupHandler.createActionPopup();
        });
        actionItems[1].addEventHandler(EventType.ROOT, event -> {
            PopupHandler.editSessionPopup();
        });
        actionItems[2].addEventHandler(EventType.ROOT, event -> {
            // TODO Add event handler for Trigger Action (opens popup for triggering an action)
        });
        actionMenu.getItems().addAll(actionItems);

        mbar.getMenus().addAll(mainMenu, sessionMenu, actionMenu);
        mbar.getStyleClass().add("global-menu-bar");
        vBox.getChildren().add(mbar);
        BootstrapRow bootstrapRow = new BootstrapRow();
        BootstrapColumn col = createColumn(vBox, 12);
        bootstrapRow.addColumn(col);
        return bootstrapRow;
    }

    public BootstrapRow createFooter() {
        return null;
    }

    public BootstrapColumn createColumn(Node widget, int xxSmall) {
        return createColumn(widget, xxSmall, xxSmall, xxSmall, xxSmall, xxSmall, xxSmall, xxSmall);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall) {
        return createColumn(widget, xxSmall, xSmall, xSmall, xSmall, xSmall, xSmall, xSmall);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall, int small) {
        return createColumn(widget, xxSmall, xSmall, small, small, small, small, small);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall, int small, int medium) {
        return createColumn(widget, xxSmall, xSmall, small, medium, medium, medium, medium);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall, int small, int medium, int large) {
        return createColumn(widget, xxSmall, xSmall, small, medium, large, large, large);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall, int small, int medium, int large, int xLarge) {
        return createColumn(widget, xxSmall, xSmall, small, medium, large, xLarge, xLarge);
    }
    public BootstrapColumn createColumn(Node widget, int xxSmall, int xSmall, int small, int medium, int large, int xLarge, int xxLarge) {
        BootstrapColumn col = new BootstrapColumn(widget);
        col.setBreakpointColumnWidth(Breakpoint.XXSMALL, xxSmall);
        col.setBreakpointColumnWidth(Breakpoint.XSMALL, xSmall);
        col.setBreakpointColumnWidth(Breakpoint.SMALL, small);
        col.setBreakpointColumnWidth(Breakpoint.MEDIUM, medium);
        col.setBreakpointColumnWidth(Breakpoint.LARGE, large);
        col.setBreakpointColumnWidth(Breakpoint.XLARGE, xLarge);
        col.setBreakpointColumnWidth(Breakpoint.XXLARGE, xxLarge);
        return col;
    }
}

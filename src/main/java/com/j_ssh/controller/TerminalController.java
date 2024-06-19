package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.main.MainApp;
import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TerminalController extends BootstrapPane {
    private TabPane tabs = new TabPane();
    public TerminalController() {
        this.tabs.setId("terminal_tabs");
        this.tabs.prefHeightProperty().bind(MainApp.get().getPrimaryStage().heightProperty());
        BootstrapRow row = new BootstrapRow();
        row.addColumn(API.get().createColumn(this.tabs, 12, 12, 9, 10, 10));

        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);
        this.addRow(row);
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

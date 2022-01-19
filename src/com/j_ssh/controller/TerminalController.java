package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.TerminalTab;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TerminalController extends BootstrapPane {
    private TabPane tabs = new TabPane();
    public TerminalController() {
        this.tabs.setId("terminal_tabs");
        BootstrapRow row = new BootstrapRow();
        row.addColumn(API.get().createColumn(this.tabs, 12, 12, 9, 10, 10));
        this.addRow(row);
    }
    public void addTerminalTab(TerminalTab tab) {
        String nickname = tab.getNickname();
        int count = 1;
        for (Tab existingTab : this.tabs.getTabs()) {
            if (existingTab.getText().equals(nickname)) {
                nickname = tab.getNickname() + " (" + count + ")";
                count++;
            }
        }
        Tab newTab = new Tab(nickname, tab);
        newTab.setId("terminal_tab");
        this.tabs.getTabs().add(newTab);
    }
    public void closeTerminalTab(TerminalTab tab) {}
}

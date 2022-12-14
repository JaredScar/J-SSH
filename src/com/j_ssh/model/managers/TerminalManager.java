package com.j_ssh.model.managers;

import com.j_ssh.model.TerminalTab;

import java.util.ArrayList;
import java.util.List;

public class TerminalManager {
    private List<TerminalTab> tabs = new ArrayList<>();
    public TerminalManager() {}

    public void add(TerminalTab tab) {
        this.tabs.add(tab);
    }
    public void remove(TerminalTab tab) {
        this.tabs.remove(tab);
    }
    public List<TerminalTab> getTabs() {
        return this.tabs;
    }
}

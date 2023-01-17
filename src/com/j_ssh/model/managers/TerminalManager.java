package com.j_ssh.model.managers;

import com.j_ssh.components.TerminalTabComponent;

import java.util.ArrayList;
import java.util.List;

public class TerminalManager {
    private List<TerminalTabComponent> tabs = new ArrayList<>();
    public TerminalManager() {}

    public void add(TerminalTabComponent tab) {
        this.tabs.add(tab);
    }
    public void remove(TerminalTabComponent tab) {
        this.tabs.remove(tab);
    }
    public List<TerminalTabComponent> getTabs() {
        return this.tabs;
    }
}

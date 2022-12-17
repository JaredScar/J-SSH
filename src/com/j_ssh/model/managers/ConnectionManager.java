package com.j_ssh.model.managers;

import com.j_ssh.model.objects.Connection;
import com.j_ssh.components.TerminalTab;

import java.util.Collection;
import java.util.HashMap;

public class ConnectionManager {
    private HashMap<TerminalTab, Connection> connections = new HashMap<>();
    public ConnectionManager() {}


    public Collection<Connection> getConnections() {
        return connections.values();
    }
    public void closeConnections() {}
}

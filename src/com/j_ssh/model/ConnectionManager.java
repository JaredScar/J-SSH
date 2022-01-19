package com.j_ssh.model;

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

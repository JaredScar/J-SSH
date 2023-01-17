package com.j_ssh.model.managers;

import com.j_ssh.model.objects.Connection;
import com.j_ssh.components.TerminalTabComponent;

import java.util.Collection;
import java.util.HashMap;

public class ConnectionManager {
    private HashMap<TerminalTabComponent, Connection> connections = new HashMap<>();
    public ConnectionManager() {}


    public Collection<Connection> getConnections() {
        return connections.values();
    }
    public void closeConnections() {}
}

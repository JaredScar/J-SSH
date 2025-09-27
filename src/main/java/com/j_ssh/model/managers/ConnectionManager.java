package com.j_ssh.model.managers;

import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.components.TerminalTabComponent;

import java.util.Collection;
import java.util.HashMap;

public class ConnectionManager {
    private static ConnectionManager instance = new ConnectionManager();
    private HashMap<TerminalTabComponent, Connection> connections = new HashMap<>();
    private HashMap<String, Connection> serverConnections = new HashMap<>(); // Track by server key
    
    private ConnectionManager() {}
    
    public static ConnectionManager get() {
        return instance;
    }

    public Collection<Connection> getConnections() {
        return connections.values();
    }
    
    public void addConnection(TerminalTabComponent tab, Connection connection) {
        connections.put(tab, connection);
        // Create unique key for server
        String serverKey = connection.getUsername() + "@" + connection.getHost() + ":" + connection.getPort();
        serverConnections.put(serverKey, connection);
    }
    
    public void removeConnection(TerminalTabComponent tab) {
        Connection connection = connections.remove(tab);
        if (connection != null) {
            String serverKey = connection.getUsername() + "@" + connection.getHost() + ":" + connection.getPort();
            serverConnections.remove(serverKey);
        }
    }
    
    public boolean isServerConnected(ServerData serverData) {
        String serverKey = serverData.getUsername() + "@" + serverData.getIp() + ":22";
        Connection connection = serverConnections.get(serverKey);
        return connection != null && connection.isConnected();
    }
    
    public Connection getServerConnection(ServerData serverData) {
        String serverKey = serverData.getUsername() + "@" + serverData.getIp() + ":22";
        return serverConnections.get(serverKey);
    }
    
    public void closeConnections() {
        for (Connection connection : connections.values()) {
            if (connection.isConnected()) {
                connection.disconnect();
            }
        }
        connections.clear();
        serverConnections.clear();
    }
}

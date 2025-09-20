package com.j_ssh.model.managers;

import com.j_ssh.model.objects.ServerData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance = new SessionManager();
    private DataManager dataManager;

    private SessionManager() {
        this.dataManager = DataManager.get();
    }

    public static SessionManager get() {
        return instance;
    }

    public List<ServerData> getAllSessions() {
        JSONArray sessionsArray = dataManager.getServersData();
        List<ServerData> sessions = new ArrayList<>();
        if (sessionsArray != null) {
            for (int i = 0; i < sessionsArray.length(); i++) {
                JSONObject sessionJson = sessionsArray.getJSONObject(i);
                sessions.add(ServerData.fromJSON(i, sessionJson));
            }
        }
        return sessions;
    }

    public ServerData getSession(int index) {
        JSONArray sessionsArray = dataManager.getServersData();
        if (sessionsArray != null && index >= 0 && index < sessionsArray.length()) {
            JSONObject sessionJson = sessionsArray.getJSONObject(index);
            return ServerData.fromJSON(index, sessionJson);
        }
        return null;
    }

    public void createSession(String nickname, String iconURL, String ip, String username, String password, String privateKeyLocation) {
        ServerData newSession = new ServerData(-1, nickname, iconURL, ip, username, password, privateKeyLocation);
        newSession.saveData();
    }

    public void updateSession(int index, String nickname, String iconURL, String ip, String username, String password, String privateKeyLocation) {
        ServerData existingSession = getSession(index);
        if (existingSession != null) {
            existingSession.setNickname(nickname);
            existingSession.setIconURL(iconURL);
            existingSession.setIp(ip);
            existingSession.setUsername(username);
            existingSession.setPassword(password);
            existingSession.setPrivateKeyLocation(privateKeyLocation);
            existingSession.saveData();
        }
    }

    public void deleteSession(int index) {
        ServerData sessionToDelete = getSession(index);
        if (sessionToDelete != null) {
            sessionToDelete.purgeData();
        }
    }
}

package com.j_ssh.model.objects;

import com.j_ssh.model.managers.DataManager;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ServerData {
    @Getter
    @Setter
    private int index = -1;
    @Getter
    @Setter
    private String nickname = "";
    @Getter
    @Setter
    private String iconURL = "";
    @Getter
    @Setter
    private String ip = "";
    @Getter
    @Setter
    private String username = "";
    @Getter
    @Setter
    private String password = "";
    @Getter
    @Setter
    private String privateKeyLocation = "";

    public static ServerData fromJSON(int index, JSONObject json) {
        ServerData serverData = fromJSON(json);
        serverData.setIndex(index);
        return serverData;
    }

    public static ServerData fromJSON(JSONObject json) {
        String nickname = json.getString("Nickname");
        String iconURL = json.getString("IconURL");
        String ip = json.getString("IP");
        String username = json.getString("Username");
        String password = json.getString("Password");
        String privateKeyLoc = json.getString("PrivateKey-Location");
        return new ServerData(-1, nickname, iconURL, ip, username, password, privateKeyLoc);
    }

    public ServerData(int index, String nickname, String iconURL, String ip, String username, String password, String privateKeyLocation) {
        this.index = index;
        this.nickname = nickname;
        this.iconURL = iconURL;
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.privateKeyLocation = privateKeyLocation;
    }

    public JSONObject serializeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("Nickname", nickname);
        obj.put("IconURL", iconURL);
        obj.put("IP", ip);
        obj.put("Username", username);
        obj.put("Password", password);
        obj.put("PrivateKey-Location", privateKeyLocation);
        return obj;
    }

    public void saveData() {
        JSONArray serversData = DataManager.get().getServersData();
        if (this.index == -1) {
            // New entry
            List<Object> servers = serversData.toList();
            servers.add(this.serializeJSON());
        } else {
            // Place it in this slot
            serversData.put(this.index, this.serializeJSON());
        }
        DataManager.get().saveData();
    }
    public void purgeData() {
        if (this.index == -1) return;
        JSONArray serversData = DataManager.get().getServersData();
        serversData.remove(this.index);
        DataManager.get().saveData();
    }
}

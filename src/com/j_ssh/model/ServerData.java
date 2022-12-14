package com.j_ssh.model;

import com.j_ssh.model.managers.DataManager;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

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
            // TODO Add to serversData
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

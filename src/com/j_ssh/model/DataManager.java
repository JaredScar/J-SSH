package com.j_ssh.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataManager {
    private static DataManager dm = new DataManager();
    private JSONObject data;
    public DataManager() {
        try {
            this.data = new JSONObject(new String(Files.readAllBytes(Paths.get("data.json"))));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static DataManager get() {
        return dm;
    }

    public JSONObject getData() {
        return this.data;
    }

    public JSONArray getServersData() {
        return this.data.optJSONArray("Servers");
    }
    public JSONArray getActionsData() {
        return this.data.optJSONArray("Actions");
    }
}

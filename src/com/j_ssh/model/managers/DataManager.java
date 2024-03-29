package com.j_ssh.model.managers;

import com.j_ssh.api.AlertHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
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
            AlertHandler.triggerExceptionAlert("DataManager Error", "Error Encountered", e);
            e.printStackTrace();
        }
    }
    public static DataManager get() {
        return dm;
    }

    private void reloadData() {
        try {
            this.data = new JSONObject(new String(Files.readAllBytes(Paths.get("data.json"))));
        } catch (IOException e) {
            AlertHandler.triggerExceptionAlert("DataManager Error", "Error Encountered", e);
            e.printStackTrace();
        }
    }

    public void saveData() {
        try {
            FileWriter writer = new FileWriter("data.json");
            writer.write(this.data.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            AlertHandler.triggerExceptionAlert("DataManager Error", "Error Encountered", e);
            e.printStackTrace();
        }
        reloadData();
    }

    public JSONObject getData() {
        return this.data;
    }

    public JSONArray getServersData() {
        return this.data.optJSONArray("Servers");
    }
    public JSONArray getTriggersData() {
        return this.data.optJSONArray("Triggers");
    }
    public JSONArray getActionsData() {
        return this.data.optJSONArray("Actions");
    }
}

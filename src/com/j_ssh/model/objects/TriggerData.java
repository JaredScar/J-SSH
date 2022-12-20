package com.j_ssh.model.objects;

import com.j_ssh.model.managers.DataManager;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TriggerData {
    @Getter
    @Setter
    private int index = -1;
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    // Server Index, Action Index
    private HashMap<Integer, Integer> triggers;

    public static TriggerData fromJSON(JSONObject json) {}

    public TriggerData(int index, String name, String description) {
        this.index = index;
        this.name = name;
        this.description = description;
    }

    public JSONObject serializeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("Name", this.name);
        obj.put("Description", this.description);
        ArrayList<JSONObject> trigs = new ArrayList<>();
        for (int serverIndex : this.triggers.keySet()) {
            JSONObject sub = new JSONObject();
            sub.put("Server", serverIndex);
            sub.put("Action", this.triggers.get(serverIndex));
            trigs.add(sub);
        }
        obj.put("Triggers", trigs);
        return obj;
    }

    public void saveData() {
        JSONArray triggersData = DataManager.get().getTriggersData();
        if (this.index == -1) {
            // New entry
            List<Object> trigs = triggersData.toList();
            trigs.add(this.serializeJSON());
        } else {
            // Place it in this slot
            triggersData.put(this.index, this.serializeJSON());
        }
        DataManager.get().saveData();
    }

    public void purgeData() {
        if (this.index == -1) return;
        JSONArray triggersData = DataManager.get().getTriggersData();
        triggersData.remove(this.index);
        DataManager.get().saveData();
    }
}

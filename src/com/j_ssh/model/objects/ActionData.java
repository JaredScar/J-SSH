package com.j_ssh.model.objects;

import com.j_ssh.model.managers.DataManager;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionData {
    @Getter
    @Setter
    private int index;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private ArrayList<String> commands;

    public void addCommand(String command) {
        this.commands.add(command);
    }

    public void removeCommand(int index) {
        this.commands.remove(index);
    }

    public JSONObject serializeJSON() {
        JSONObject obj = new JSONObject();
        obj.put("Nickname", name);
        obj.put("Commands", this.commands);
        return obj;
    }

    public void saveData() {
        JSONArray actionsData = DataManager.get().getActionsData();
        if (this.index == -1) {
            // New entry
            List<Object> actions = actionsData.toList();
            actions.add(this.serializeJSON());
        } else {
            // Place it in this slot
            actionsData.put(this.index, this.serializeJSON());
        }
        DataManager.get().saveData();
    }
    public void purgeData() {
        if (this.index == -1) return;
        JSONArray actionsData = DataManager.get().getActionsData();
        actionsData.remove(this.index);
        DataManager.get().saveData();
    }
}

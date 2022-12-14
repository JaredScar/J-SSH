package com.j_ssh.model;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;

public class TriggerData {
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

    public void saveData() {}
    public void purgeData() {}
}

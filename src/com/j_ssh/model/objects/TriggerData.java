package com.j_ssh.model.objects;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.HashMap;

public class TriggerData {
    @Getter
    @Setter
    private int index;
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private HashMap<ServerData, ActionData> triggers;

    public JSONObject serializeJSON() {}

    public void saveData() {}

    public void purgeData() {}
}

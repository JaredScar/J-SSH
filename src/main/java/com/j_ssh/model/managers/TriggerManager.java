package com.j_ssh.model.managers;

import com.j_ssh.model.objects.TriggerData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TriggerManager {
    private static TriggerManager instance = new TriggerManager();
    private DataManager dataManager;

    private TriggerManager() {
        this.dataManager = DataManager.get();
    }

    public static TriggerManager get() {
        return instance;
    }

    public List<TriggerData> getAllTriggers() {
        JSONArray triggersArray = dataManager.getTriggersData();
        List<TriggerData> triggers = new ArrayList<>();
        if (triggersArray != null) {
            for (int i = 0; i < triggersArray.length(); i++) {
                JSONObject triggerJson = triggersArray.getJSONObject(i);
                triggers.add(TriggerData.fromJSON(i, triggerJson));
            }
        }
        return triggers;
    }

    public TriggerData getTrigger(int index) {
        JSONArray triggersArray = dataManager.getTriggersData();
        if (triggersArray != null && index >= 0 && index < triggersArray.length()) {
            JSONObject triggerJson = triggersArray.getJSONObject(index);
            return TriggerData.fromJSON(index, triggerJson);
        }
        return null;
    }

    public void createTrigger(String name, String description, HashMap<Integer, Integer> triggers) {
        TriggerData newTrigger = new TriggerData(-1, name, description, triggers);
        newTrigger.saveData();
    }

    public void updateTrigger(int index, String name, String description, HashMap<Integer, Integer> triggers) {
        TriggerData existingTrigger = getTrigger(index);
        if (existingTrigger != null) {
            existingTrigger.setName(name);
            existingTrigger.setDescription(description);
            existingTrigger.setTriggers(triggers);
            existingTrigger.saveData();
        }
    }

    public void deleteTrigger(int index) {
        TriggerData triggerToDelete = getTrigger(index);
        if (triggerToDelete != null) {
            triggerToDelete.purgeData();
        }
    }
}

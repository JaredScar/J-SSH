package com.j_ssh.model.managers;

import com.j_ssh.model.objects.ActionData;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionManager {
    private static ActionManager instance = new ActionManager();
    
    public static ActionManager get() {
        return instance;
    }
    
    private ActionManager() {}
    
    /**
     * Get all actions from the data store
     */
    public List<ActionData> getAllActions() {
        List<ActionData> actions = new ArrayList<>();
        JSONArray actionsData = DataManager.get().getActionsData();
        
        if (actionsData != null) {
            for (int i = 0; i < actionsData.length(); i++) {
                JSONObject actionJson = actionsData.getJSONObject(i);
                ActionData action = ActionData.fromJSON(i, actionJson);
                actions.add(action);
            }
        }
        
        return actions;
    }
    
    /**
     * Get a specific action by index
     */
    public ActionData getAction(int index) {
        JSONArray actionsData = DataManager.get().getActionsData();
        if (actionsData != null && index >= 0 && index < actionsData.length()) {
            JSONObject actionJson = actionsData.getJSONObject(index);
            return ActionData.fromJSON(index, actionJson);
        }
        return null;
    }
    
    /**
     * Create a new action
     */
    public ActionData createAction(String name, List<String> commands) {
        ActionData action = new ActionData(-1, name, new ArrayList<>(commands));
        action.saveData();
        return action;
    }
    
    /**
     * Update an existing action
     */
    public void updateAction(int index, String name, List<String> commands) {
        ActionData action = getAction(index);
        if (action != null) {
            action.setName(name);
            action.setCommands(new ArrayList<>(commands));
            action.saveData();
        }
    }
    
    /**
     * Delete an action
     */
    public void deleteAction(int index) {
        ActionData action = getAction(index);
        if (action != null) {
            action.purgeData();
        }
    }
    
    /**
     * Execute an action on a terminal component
     */
    public void executeAction(ActionData action, com.j_ssh.components.TerminalTabComponent terminal) {
        if (action != null && terminal != null) {
            for (String command : action.getCommands()) {
                terminal.sendCommand(command + "\n");
                // Add a small delay between commands to ensure proper execution
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}

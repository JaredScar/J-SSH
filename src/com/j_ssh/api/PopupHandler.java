package com.j_ssh.api;

import com.j_ssh.model.managers.DataManager;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.TriggerData;
import javafx.scene.control.Alert;

import java.util.function.Consumer;

public class PopupHandler {
    private static DataManager dataManager = DataManager.get();
    public static void createSessionPopup() {}
    public static void editSessionPopup() {}
    public static void saveSessionData(ServerData serverData) {}
    public static void createTriggerPopup() {}
    public static void editTriggerPopup() {}
    public static void saveTriggerData(TriggerData triggerData) {}
    public static void createActionPopup() {}
    public static void editActionPopup() {}
    public static void saveActionData(ActionData actionData) {}
    public static void confirmOrCancelDialog(Consumer<Boolean> resultCallback) {}
    public static void triggerAboutPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About J-SSH");
        alert.setHeaderText("About J-SSH");
        alert.setContentText("A simple SSH application that supports running commands via actions. With this SSH application, you can save SSH sessions, credentials, and create actions (multiple commands) to perform on SSH servers. Actions can be nicknamed then added to the right sidebar of the application window. Clicking on the action will perform said action. Perform multiple commands on multiple servers with 1 single click of an action button.");

        alert.showAndWait();
    }
    public static void triggerHelpPopup() {}
}

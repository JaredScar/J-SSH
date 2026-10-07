package com.j_ssh.api;

import com.j_ssh.model.managers.DataManager;
import com.j_ssh.model.objects.ServerData;
import com.j_ssh.model.objects.ActionData;
import com.j_ssh.model.objects.TriggerData;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;
import java.util.function.Consumer;

public class PopupHandler {
    private static DataManager dataManager = DataManager.get();
    public static void createSessionPopup() {
        // Navigate to sessions management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.SESSIONS);
    }
    public static void editSessionPopup() {
        // Navigate to sessions management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.SESSIONS);
    }
    public static void saveSessionData(ServerData serverData) {
        serverData.saveData();
    }
    public static void triggerActionPopup() {
        // Navigate to triggers management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.TRIGGERS);
    }
    public static void createTriggerPopup() {
        // Navigate to triggers management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.TRIGGERS);
    }
    public static void editTriggerPopup() {
        // Navigate to triggers management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.TRIGGERS);
    }
    public static void saveTriggerData(TriggerData triggerData) {
        triggerData.saveData();
    }
    public static void createActionPopup() {
        // Navigate to actions management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.ACTIONS);
    }
    public static void editActionPopup() {
        // Navigate to actions management page
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.ACTIONS);
    }
    public static void saveActionData(ActionData actionData) {
        actionData.saveData();
    }
    public static void confirmOrCancelDialog(String title, String header, String content, Consumer<Boolean> resultCallback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        result.ifPresent(buttonType -> resultCallback.accept(buttonType == ButtonType.OK));
    }
    public static void triggerAboutPopup() {
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.SETTINGS);
    }
    public static void triggerHelpPopup() {
        com.j_ssh.main.MainApp.get().changeScene(com.j_ssh.model.objects.JScene.HELP);
    }
}

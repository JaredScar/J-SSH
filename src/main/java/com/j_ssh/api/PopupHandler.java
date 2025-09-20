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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About J-SSH");
        alert.setHeaderText("About J-SSH");
        alert.setContentText("A simple SSH application that supports running commands via actions. With this SSH application, you can save SSH sessions, credentials, and create actions (multiple commands) to perform on SSH servers. Actions can be nicknamed then added to the right sidebar of the application window. Clicking on the action will perform said action. Perform multiple commands on multiple servers with 1 single click of an action button.");

        alert.showAndWait();
    }
    public static void triggerHelpPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("J-SSH Help");
        alert.setHeaderText("J-SSH User Guide");
        
        StringBuilder helpText = new StringBuilder();
        helpText.append("Welcome to J-SSH! Here's how to use the application:\n\n");
        
        helpText.append("📋 SESSIONS:\n");
        helpText.append("• Create New Session: Add SSH server connections with credentials\n");
        helpText.append("• Edit Sessions: Modify existing server configurations\n");
        helpText.append("• Open Session: Connect to a server and open terminal\n\n");
        
        helpText.append("⚡ ACTIONS:\n");
        helpText.append("• Create New Action: Define command sequences to execute\n");
        helpText.append("• Edit Actions: Modify existing command sequences\n");
        helpText.append("• Trigger Action: Execute actions on connected terminals\n\n");
        
        helpText.append("🔗 TRIGGERS:\n");
        helpText.append("• Create New Trigger: Set up automated actions across multiple servers\n");
        helpText.append("• Edit Triggers: Modify trigger configurations\n");
        helpText.append("• Trigger Action: Execute triggers manually\n\n");
        
        helpText.append("💡 TIPS:\n");
        helpText.append("• Use the action buttons in the terminal sidebar for quick command execution\n");
        helpText.append("• Actions can contain multiple commands that execute sequentially\n");
        helpText.append("• Triggers allow you to run the same action on multiple servers simultaneously\n");
        helpText.append("• All data is automatically saved to data.json\n\n");
        
        helpText.append("🆘 SUPPORT:\n");
        helpText.append("For additional help, check the About section or contact support.");
        
        alert.setContentText(helpText.toString());
        alert.getDialogPane().setPrefWidth(600);
        alert.showAndWait();
    }
}

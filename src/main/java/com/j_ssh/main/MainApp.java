package com.j_ssh.main;

import com.j_ssh.controller.ButtonController;
import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.HelpController;
import com.j_ssh.controller.LoadingController;
import com.j_ssh.controller.SessionController;
import com.j_ssh.controller.SettingsController;
import com.j_ssh.controller.TerminalController;
import com.j_ssh.controller.TriggerController;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.view.AppShell;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class MainApp extends Application {
    private static MainApp main;

    @Getter
    private Stage primaryStage;

    private AppShell appShell;
    private Scene mainScene;

    @Getter
    @Setter
    private DashboardController dashboardController;

    @Getter
    @Setter
    private TerminalController terminalController;

    @Getter
    @Setter
    private SettingsController settingsController;

    @Getter
    @Setter
    private HelpController helpController;

    @Getter
    @Setter
    private LoadingController loadingController;

    @Getter
    @Setter
    private ButtonController buttonController;

    @Getter
    @Setter
    private SessionController sessionController;

    @Getter
    @Setter
    private TriggerController triggerController;

    public static MainApp get() {
        return main;
    }

    @Override
    public void start(Stage primaryStage) {
        // This is the primary stage for the app starting
        this.primaryStage = primaryStage;
        main = this;
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        this.appShell = new AppShell();
        this.terminalController = new TerminalController();
        this.loadingController = new LoadingController();
        this.buttonController = new ButtonController();
        this.sessionController = new SessionController();
        this.triggerController = new TriggerController();
        this.settingsController = new SettingsController();
        this.helpController = new HelpController();

        StackPane windowRoot = new StackPane(appShell);
        appShell.attachOverlayHost(windowRoot);
        this.mainScene = new Scene(windowRoot);
        addStylesheet("/shell.css");
        addStylesheet("/sessions.css");
        addStylesheet("/actions.css");
        addStylesheet("/triggers.css");
        addStylesheet("/terminal.css");
        addStylesheet("/settings.css");
        addStylesheet("/loading.css");
        addStylesheet("/dashboard.css");

        primaryStage.setScene(mainScene);
        primaryStage.setMinWidth(1020);
        primaryStage.setMinHeight(680);
        primaryStage.show();
        primaryStage.centerOnScreen();
        changeScene(JScene.SESSIONS);
    }

    private void addStylesheet(String resource) {
        var url = getClass().getResource(resource);
        if (url != null) {
            mainScene.getStylesheets().add(url.toExternalForm());
        }
    }

    public static void theme(Dialog<?> dialog) {
        if (main == null || main.primaryStage == null || main.primaryStage.getScene() == null) {
            return;
        }
        dialog.getDialogPane().getStylesheets().setAll(main.primaryStage.getScene().getStylesheets());
        if (!dialog.getDialogPane().getStyleClass().contains("jssh-dialog")) {
            dialog.getDialogPane().getStyleClass().add("jssh-dialog");
        }
    }

    public void setTerminalEnabled(boolean enabled) {
        if (appShell != null) {
            appShell.setTerminalEnabled(enabled);
        }
    }

    public void showServerMessage(String message, Runnable onClose) {
        if (appShell == null) {
            if (onClose != null) {
                onClose.run();
            }
            return;
        }
        appShell.showServerMessage(message, onClose);
    }

    public double getScreenWidth() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getScreenSize().getWidth();
    }
    public double getScreenHeight() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getScreenSize().getHeight();
    }
    
    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public TerminalController getTerminalController() {
        return this.terminalController;
    }

    public void changeScene(JScene scene) {
        if (appShell == null) {
            return;
        }
        switch (scene) {
            case LOADING:
                appShell.setLoading(true);
                break;
            case TERMINAL:
                appShell.setLoading(false);
                terminalController.refreshActions();
                appShell.showPage(terminalController);
                appShell.setActive(JScene.TERMINAL);
                appShell.setTerminalEnabled(true);
                break;
            case SETTINGS:
                appShell.setLoading(false);
                settingsController.refresh();
                appShell.showPage(settingsController);
                appShell.setActive(JScene.SETTINGS);
                break;
            case HELP:
                appShell.setLoading(false);
                appShell.showPage(helpController);
                appShell.setActive(JScene.HELP);
                break;
            case ACTIONS:
                appShell.setLoading(false);
                appShell.showPage(buttonController);
                appShell.setActive(JScene.ACTIONS);
                break;
            case TRIGGERS:
                appShell.setLoading(false);
                appShell.showPage(triggerController);
                appShell.setActive(JScene.TRIGGERS);
                break;
            case DASHBOARD:
            case SESSIONS:
            default:
                appShell.setLoading(false);
                appShell.showPage(sessionController);
                appShell.setActive(JScene.SESSIONS);
                if (sessionController != null) {
                    sessionController.onViewActivated();
                }
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

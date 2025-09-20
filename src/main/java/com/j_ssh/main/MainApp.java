package com.j_ssh.main;

import com.j_ssh.controller.ButtonController;
import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.LoadingController;
import com.j_ssh.controller.SessionController;
import com.j_ssh.controller.SettingsController;
import com.j_ssh.controller.TerminalController;
import com.j_ssh.controller.TriggerController;
import com.j_ssh.model.objects.JScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.File;

public class MainApp extends Application {
    private static MainApp main;

    @Getter
    private Stage primaryStage;

    @Getter
    @Setter
    private DashboardController dashboardController;
    @Getter
    @Setter
    private Scene dashboardScene;

    @Getter
    @Setter
    private TerminalController terminalController;
    @Getter
    @Setter
    private Scene terminalScene;

    @Getter
    @Setter
    private SettingsController settingsController;
    @Getter
    @Setter
    private Scene settingsScene;

    @Getter
    @Setter
    private LoadingController loadingController;
    @Getter
    @Setter
    private Scene loadingScene;

    @Getter
    @Setter
    private ButtonController buttonController;
    @Getter
    @Setter
    private Scene actionsScene;

    @Getter
    @Setter
    private SessionController sessionController;
    @Getter
    @Setter
    private Scene sessionsScene;

    @Getter
    @Setter
    private TriggerController triggerController;
    @Getter
    @Setter
    private Scene triggersScene;

    public static MainApp get() {
        return main;
    }

    @Override
    public void start(Stage primaryStage) {
        // This is the primary stage for the app starting
        this.primaryStage = primaryStage;
        main = this;
        this.terminalController = new TerminalController();
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        File cssFile = new File("global.css");
        String globalCssPath = null;
        if (cssFile.exists()) {
            globalCssPath = cssFile.toURI().toString();
        } else {
            System.out.println("CSS file not found: " + cssFile.getAbsolutePath());
        }
        this.dashboardController = new DashboardController();
        if (globalCssPath != null)
            this.dashboardController.getStylesheets().add(globalCssPath);
        // Add dashboard CSS
        this.dashboardController.getStylesheets().add(getClass().getResource("/dashboard.css").toString());
        Scene scene = new Scene(dashboardController);
        this.dashboardScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
        this.terminalController = new TerminalController();
        if (globalCssPath != null)
            this.terminalController.getStylesheets().add(globalCssPath);
        // Add action buttons CSS for terminal
        this.terminalController.getStylesheets().add(getClass().getResource("/action-buttons.css").toString());
        scene = new Scene(this.terminalController);
        this.terminalScene = scene;
        this.loadingController = new LoadingController();
        if (globalCssPath != null)
            this.loadingController.getStylesheets().add(globalCssPath);
        // Add loading CSS
        this.loadingController.getStylesheets().add(getClass().getResource("/loading.css").toString());
        scene = new Scene(this.loadingController);
        this.loadingScene = scene;
        
        this.buttonController = new ButtonController();
        if (globalCssPath != null)
            this.buttonController.getStylesheets().add(globalCssPath);
        // Add action buttons CSS
        this.buttonController.getStylesheets().add(getClass().getResource("/action-buttons.css").toString());
        scene = new Scene(this.buttonController);
        this.actionsScene = scene;
        
        this.sessionController = new SessionController();
        if (globalCssPath != null)
            this.sessionController.getStylesheets().add(globalCssPath);
        // Add action buttons CSS for sessions
        this.sessionController.getStylesheets().add(getClass().getResource("/action-buttons.css").toString());
        scene = new Scene(this.sessionController);
        this.sessionsScene = scene;
        
        this.triggerController = new TriggerController();
        if (globalCssPath != null)
            this.triggerController.getStylesheets().add(globalCssPath);
        // Add action buttons CSS for triggers
        this.triggerController.getStylesheets().add(getClass().getResource("/action-buttons.css").toString());
        scene = new Scene(this.triggerController);
        this.triggersScene = scene;
        
        this.settingsController = new SettingsController();
        if (globalCssPath != null)
            this.settingsController.getStylesheets().add(globalCssPath);
        // Add settings CSS
        this.settingsController.getStylesheets().add(getClass().getResource("/settings.css").toString());
        scene = new Scene(this.settingsController);
        this.settingsScene = scene;
    }

    public double getScreenWidth() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getScreenSize().getWidth();
    }
    public double getScreenHeight() {
        Toolkit tk = Toolkit.getDefaultToolkit();
        return tk.getScreenSize().getHeight();
    }

    public void changeScene(JScene scene) {
        Scene fxScene = null;
        switch (scene) {
            case TERMINAL:
                fxScene = this.getTerminalScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case DASHBOARD:
                fxScene = this.getDashboardScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case SETTINGS:
                fxScene = this.getSettingsScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case LOADING:
                fxScene = this.getLoadingScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case ACTIONS:
                fxScene = this.getActionsScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case SESSIONS:
                fxScene = this.getSessionsScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
            case TRIGGERS:
                fxScene = this.getTriggersScene();
                this.getPrimaryStage().setScene(fxScene);
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

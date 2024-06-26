package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.LoadingController;
import com.j_ssh.controller.SettingsController;
import com.j_ssh.controller.TerminalController;
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
        Scene scene = new Scene(dashboardController);
        this.dashboardScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
        this.terminalController = new TerminalController();
        if (globalCssPath != null)
            this.terminalController.getStylesheets().add(globalCssPath);
        scene = new Scene(this.terminalController);
        this.terminalScene = scene;
        this.loadingController = new LoadingController();
        if (globalCssPath != null)
            this.loadingController.getStylesheets().add(globalCssPath);
        scene = new Scene(this.loadingController);
        this.loadingScene = scene;
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
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

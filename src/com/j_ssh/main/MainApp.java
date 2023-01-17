package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.SettingsController;
import com.j_ssh.controller.TerminalController;
import com.j_ssh.model.objects.JScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.io.IOException;

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
        this.dashboardController = new DashboardController();
        this.dashboardController.getStylesheets().add("global.css");
        Scene scene = new Scene(dashboardController);
        this.dashboardScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
        this.terminalController = new TerminalController();
        this.terminalController.getStylesheets().add("global.css");
        scene = new Scene(this.terminalController);
        this.terminalScene = scene;
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
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

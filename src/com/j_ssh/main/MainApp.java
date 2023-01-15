package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.TerminalController;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.components.TerminalTab;
import com.j_ssh.model.objects.JScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

    public static MainApp get() {
        return main;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // This is the primary stage for the app starting
        this.primaryStage = primaryStage;
        main = this;
        this.terminalController = new TerminalController();
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        /** /
        Connection conn = null;
        JSONObject configJSON = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        conn = new Connection(configJSON.optString("Username", ""), configJSON.optString("Host", ""), configJSON.optString("Password", ""), configJSON.optInt("Port", 22));
        conn.addKnownHost();
        conn.connect();
        terminalTroller.addTerminalTab(new TerminalTab("Jared Test Server", conn));
        terminalTroller.getStylesheets().add("global.css");
        /**/
        this.dashboardController = new DashboardController();
        Scene scene = new Scene(dashboardController);
        this.dashboardScene = scene;
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
        this.terminalController = new TerminalController();
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
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

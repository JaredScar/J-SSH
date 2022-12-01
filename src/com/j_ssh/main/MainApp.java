package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.TerminalController;
import com.j_ssh.model.Connection;
import com.j_ssh.model.TerminalTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MainApp extends Application {
    private static MainApp main;
    private Stage primaryStage;
    private TerminalController terminalTroller;

    public static MainApp get() {
        return main;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        // This is the primary stage for the app starting
        this.primaryStage = primaryStage;
        main = this;
        this.terminalTroller = new TerminalController();
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        Connection conn = null;
        JSONObject configJSON = new JSONObject(new String(Files.readAllBytes(Paths.get("config.json"))));
        conn = new Connection(configJSON.optString("Username", ""), configJSON.optString("Host", ""), configJSON.optString("Password", ""), configJSON.optInt("Port", 22));
        conn.addKnownHost();
        conn.connect();
        terminalTroller.addTerminalTab(new TerminalTab("Jared Test Server", conn));
        terminalTroller.getStylesheets().add("style.css");
        /**/
        Scene scene = new Scene(new DashboardController());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
        this.terminalTroller = new TerminalController();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

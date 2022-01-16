package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.SshController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    private DashboardController dashController = new DashboardController();
    private SshController sshController = new SshController();

    @Override
    public void start(Stage primaryStage) {
        //this.dashController.changeStage(primaryStage);
        this.sshController.changeStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.j_ssh.main;

import com.j_ssh.controller.DashboardController;
import com.j_ssh.controller.SshController;
import com.jcraft.jsch.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Properties;

public class MainApp extends Application {
    private DashboardController dashController = new DashboardController();
    private SshController sshController = new SshController();
    @Override
    public void start(Stage primaryStage) {
        //this.dashController.changeStage(primaryStage);
        this.sshController.changeStage(primaryStage);
    }

    public static void main(String[] args) {
        JSch jsch = new JSch();
        try {
            Session sess = jsch.getSession("", "", 22);
            sess.setPassword("");
            sess.connect();
            // TODO Need to add host to known hosts
            Channel chan = sess.openChannel("shell");
            chan.setInputStream(System.in);
            chan.setOutputStream(System.out);
            chan.connect();
        } catch (JSchException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}

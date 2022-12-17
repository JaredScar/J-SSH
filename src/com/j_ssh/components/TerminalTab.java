package com.j_ssh.components;

import com.j_ssh.model.objects.Connection;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TerminalTab extends TextArea {
    private String nickname;
    private Connection connection;
    private String commandStr = "";
    public TerminalTab(String nickname, Connection connection) {
        this.nickname = nickname;
        this.connection = connection;
        this.getStyleClass().add("terminal");
        this.setWrapText(true);
        new Thread(() -> {
            while (true) {
                boolean appended = false;
                String str = "";
                try {
                    if (connection.getOutputStream() == null) {
                        break;
                    }
                    str = connection.getOutputStream().toString("UTF-8");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if (str.length() > 0)
                    appended = true;
                final boolean appendedFinal = appended;
                final String strFinal = str;
                Platform.runLater(() -> {
                    try {
                        if (strFinal.length() > 0) {
                            this.appendText(strFinal);
                            connection.getOutputStream().reset();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    if (appendedFinal)
                        this.setScrollTop(Double.MAX_VALUE);
                });
            }
        }).start();
    }
    public void sendCommand(String cmd) {
        connection.sendCommand(cmd);
    }
    public void close() {}

    public String getNickname() {
        return this.nickname;
    }
    public Connection getConnection() {
        return this.connection;
    }
}

package com.j_ssh.model;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TerminalTab extends TextArea {
    private String nickname;
    private Connection connection;
    public TerminalTab(String nickname, Connection connection) {
        this.nickname = nickname;
        this.connection = connection;
        this.setId("terminal");
        this.setWrapText(true);
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String str = "";
                try {
                    str = connection.getOutputStream().toString("UTF-8");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
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
                Platform.runLater(() -> {
                    this.setScrollTop(Double.MAX_VALUE);
                });
            }
        }).start();
    }
    public void sendCommand(String cmd) {}
    public void close() {}

    public String getNickname() {
        return this.nickname;
    }
    public Connection getConnection() {
        return this.connection;
    }
}

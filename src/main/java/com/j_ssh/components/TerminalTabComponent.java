package com.j_ssh.components;

import com.j_ssh.model.objects.Connection;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import netscape.javascript.JSObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class TerminalTabComponent extends BorderPane {
    @Getter
    private String nickname;
    @Getter
    private Connection connection;

    private WebView webView;
    private boolean terminalReady = false;

    public TerminalTabComponent(String nickname, Connection connection) {
        this.nickname = nickname;
        this.connection = connection;
        this.getStyleClass().add("terminal");
        this.webView = new WebView();
        WebEngine webEngine = this.webView.getEngine();
        connection.start();

        webEngine.setJavaScriptEnabled(true);
        TerminalBridge bridge = new TerminalBridge();
        /**/
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.terminalReady && newValue == Worker.State.SUCCEEDED) {
                System.out.println("[SUCCESS] WebEngine is ready...");
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", bridge);
                startOutputThread(bridge);
                this.terminalReady = true;
            }
        });
        webEngine.setOnError(errorEvent -> {
            System.err.println("WebView error: " + errorEvent.getMessage());
            // Handle error event
        });
        /**/
        File htmlFile = new File("terminal.html");
        try {
            String htmlFilePath = htmlFile.toURI().toString();
            webEngine.load(htmlFilePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.setCenter(this.webView);
    }

    private void startOutputThread(TerminalBridge bridge) {
        Thread outputThread = new Thread(() -> {
            try {
                while (true) {
                    byte[] buffer;
                    if (this.terminalReady) {
                        synchronized (connection.getOutputStream()) {
                            buffer = connection.getOutputStream().toByteArray();
                            connection.getOutputStream().reset(); // Clear the output stream after reading
                        }
                        String output = new String(buffer, "UTF-8");
                        if (!output.isEmpty()) {
                            bridge.sendOutputToTerminal(output);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        outputThread.start();
    }

    public void sendCommand(String cmd) {
        System.out.println("[DEBUG] command sent => " + cmd);
        connection.sendCommand(cmd);
    }

    public void close() {
        // Implement the close logic if needed
    }

    public class TerminalBridge {
        public void receiveInput(String input) {
            // Handle input here and send responses back to the terminal
            sendOutputToTerminal(input);
            if (input.contains("\n"))
                sendOutputToTerminal(input);
        }

        public void sendOutputToTerminal(String output) {
            Platform.runLater(() -> {
                System.out.println("[DEBUG] Output=> " + output);
                JSObject terminal = (JSObject) webView.getEngine().executeScript("term");
                terminal.call("write", output);
            });
        }
    }
}

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
        
        // Make WebView take up full height
        this.webView.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.webView.setMinSize(0, 0);
        
        WebEngine webEngine = this.webView.getEngine();
        connection.start();

        webEngine.setJavaScriptEnabled(true);
        TerminalBridge bridge = new TerminalBridge();
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
        try {
            // Load the HTML file from resources
            URL htmlUrl = getClass().getResource("/terminal.html");
            if (htmlUrl != null) {
                webEngine.load(htmlUrl.toString());
            } else {
                // Fallback to loading from current directory
                File htmlFile = new File("terminal.html");
                String htmlFilePath = htmlFile.toURI().toString();
                webEngine.load(htmlFilePath);
            }
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
                    // Small delay to prevent excessive CPU usage
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        outputThread.setDaemon(true); // Make it a daemon thread
        outputThread.start();
    }

    public void sendCommand(String cmd) {
        System.out.println("[DEBUG] command sent => " + cmd);
        connection.sendCommand(cmd);
    }

    public void close() {
        try {
            if (connection != null && connection.isConnected()) {
                connection.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public class TerminalBridge {
        @Getter
        private String currentStr = "";

        public void receiveInput(String input) {
            // Handle input here and send to SSH connection
            switch (input) {
                case "\u0008": // Backspace
                case "\u007f": // Delete
                    if (!this.currentStr.isEmpty()) {
                        this.currentStr = this.currentStr.substring(0, this.currentStr.length() - 1);
                    }
                    sendCommand(input); // Send backspace to SSH server
                    break;
                case "\t": // Tab
                    sendCommand(this.currentStr + input); // Send current string + tab to SSH server
                    this.currentStr = "";
                    break;
                case "\n": // Newline
                case "\r":
                case "\r\n":
                    sendCommand(this.currentStr + input); // Send current string + newline to SSH server
                    this.currentStr = "";
                    break;
                default:
                    System.out.println("[DEBUG] input sent to SSH => " + input);
                    this.currentStr += input;
                    sendCommand(input); // Send character to SSH server
                    break;
            }
        }

        public void sendOutputToTerminal(String output) {
            Platform.runLater(() -> {
                try {
                    JSObject terminal = (JSObject) webView.getEngine().executeScript("term");
                    if (terminal != null) {
                        terminal.call("write", output);
                    }
                } catch (Exception e) {
                    System.err.println("Error sending output to terminal: " + e.getMessage());
                }
            });
        }
    }
}
package com.j_ssh.components;

import com.j_ssh.model.objects.Connection;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import lombok.Getter;
import netscape.javascript.JSObject;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TerminalTabComponent extends BorderPane {
    @Getter
    private String nickname;
    @Getter
    private Connection connection;

    private WebView webView;
    private boolean terminalReady = false;
    private boolean pageRequested = false;
    private volatile boolean outputRunning = true;
    private Thread outputThread;
    private JSObject termObject;
    private final StringBuilder pendingOutput = new StringBuilder();
    private boolean flushScheduled = false;
    private double fittedWidth = -1;
    private double fittedHeight = -1;

    public TerminalTabComponent(String nickname, Connection connection) {
        this.nickname = nickname;
        this.connection = connection;
        this.getStyleClass().add("terminal");
        this.webView = new WebView();
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setMinSize(0, 0);
        this.setStyle("-fx-background-color: #000000;");

        this.webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.webView.setMinSize(0, 0);
        this.webView.setStyle("-fx-background-color: #000000;");
        this.setCenter(this.webView);

        WebEngine webEngine = this.webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        TerminalBridge bridge = new TerminalBridge();
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.terminalReady && newValue == Worker.State.SUCCEEDED) {
                System.out.println("[SUCCESS] WebEngine is ready...");
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", bridge);
                startOutputThread(bridge);
                this.terminalReady = true;
                Platform.runLater(this::fitTerminal);
            }
        });
        webEngine.setOnError(errorEvent -> System.err.println("WebView error: " + errorEvent.getMessage()));
        webEngine.getLoadWorker().exceptionProperty().addListener((obs, prev, error) -> {
            if (error != null) {
                System.err.println("WebView load failed: " + error.getMessage());
            }
        });

        // WebView stays blank if the page loads before this node is on screen.
        sceneProperty().addListener((obs, previous, scene) -> {
            if (scene != null) {
                loadPage(webEngine);
            }
        });
        webView.layoutBoundsProperty().addListener((obs, previous, bounds) -> {
            double width = bounds.getWidth();
            double height = bounds.getHeight();
            if (height < 40 || width < 40) {
                return;
            }
            if (Math.abs(width - fittedWidth) < 8 && Math.abs(height - fittedHeight) < 8) {
                return;
            }
            fittedWidth = width;
            fittedHeight = height;
            fitTerminal();
        });
    }

    private void loadPage(WebEngine webEngine) {
        if (pageRequested) {
            return;
        }
        pageRequested = true;
        try {
            URL htmlUrl = getClass().getResource("/terminal.html");
            if (htmlUrl != null) {
                webEngine.load(htmlUrl.toString());
            } else {
                webEngine.load(new File("terminal.html").toURI().toString());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void fitTerminal() {
        if (!terminalReady) {
            return;
        }
        try {
            webView.getEngine().executeScript("if (window.fitTerm) window.fitTerm();");
        } catch (Exception ignored) {
        }
    }

    private void startOutputThread(TerminalBridge bridge) {
        outputThread = new Thread(() -> {
            try {
                while (outputRunning) {
                    byte[] buffer = connection.readIncoming();
                    if (buffer.length == 0) {
                        continue;
                    }
                    bridge.sendOutputToTerminal(new String(buffer, StandardCharsets.UTF_8));
                }
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                if (outputRunning) {
                    e.printStackTrace();
                }
            }
        }, "ssh-output");
        outputThread.setDaemon(true);
        outputThread.start();
    }

    public void sendCommand(String cmd) {
        connection.sendCommand(cmd);
    }

    public void close() {
        outputRunning = false;
        if (outputThread != null) {
            outputThread.interrupt();
        }
        try {
            if (connection != null && connection.isConnected()) {
                connection.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public class TerminalBridge {
        public void receiveInput(String input) {
            if (input == null || input.isEmpty()) {
                return;
            }
            // xterm already sends each key, including Enter. Send that payload once.
            sendCommand(input);
        }

        public void sendOutputToTerminal(String output) {
            synchronized (pendingOutput) {
                pendingOutput.append(output);
                if (!flushScheduled) {
                    flushScheduled = true;
                    Platform.runLater(this::flushOutput);
                }
            }
        }

        private void flushOutput() {
            String text;
            synchronized (pendingOutput) {
                text = pendingOutput.toString();
                pendingOutput.setLength(0);
                flushScheduled = false;
            }
            if (text.isEmpty()) {
                return;
            }
            try {
                if (termObject == null) {
                    termObject = (JSObject) webView.getEngine().executeScript("term");
                }
                if (termObject != null) {
                    termObject.call("write", text);
                }
            } catch (Exception e) {
                termObject = null;
                System.err.println("Error sending output to terminal: " + e.getMessage());
            }
        }
    }
}
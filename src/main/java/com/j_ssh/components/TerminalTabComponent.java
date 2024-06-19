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

public class TerminalTabComponent extends BorderPane {
    @Getter
    private String nickname;
    @Getter
    private Connection connection;

    private WebView webView;

    public TerminalTabComponent(String nickname, Connection connection) {
        this.nickname = nickname;
        this.connection = connection;
        this.getStyleClass().add("terminal");
        WebView webView = new WebView();
        this.webView = webView;
        WebEngine webEngine = webView.getEngine();

        webEngine.setJavaScriptEnabled(true);
        /**/
        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("[SUCCESS] WebEngine is ready...");
            JSObject window = (JSObject) webEngine.executeScript("window");
            window.setMember("java", new TerminalBridge());
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

        this.setCenter(webView);
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
            System.out.println("[DEBUG] Received input: " + input);
            // Handle input here and send responses back to the terminal
            sendOutputToTerminal(input);
        }

        public void sendOutputToTerminal(String output) {
            Platform.runLater(() -> {
                webView.getEngine().executeScript("term.write('" + output.replace("\n", "\\n").replace("\r", "\\r") + "')");
            });
        }
    }

}

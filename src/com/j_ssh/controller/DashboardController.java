package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.components.TerminalTabComponent;
import com.j_ssh.main.MainApp;
import com.j_ssh.model.managers.AsyncManager;
import com.j_ssh.model.managers.DataManager;
import com.j_ssh.model.objects.Connection;
import com.j_ssh.model.objects.JScene;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardController extends BootstrapPane {
    public DashboardController() {
        BootstrapRow searchRow = new BootstrapRow();
        TextField searchBox = new TextField();
        searchBox.setPromptText("Search or Enter Address");
        searchBox.getStyleClass().add("search-box");
        BootstrapColumn searchCol = API.get().createColumn(searchBox, 10);
        Button newSessBtn = new Button("Create new session");
        newSessBtn.getStyleClass().add("new-sess-btn");
        BootstrapColumn newSessCol = API.get().createColumn(newSessBtn, 2);
        BootstrapRow iconRow = new BootstrapRow();
        JSONArray servers = DataManager.get().getServersData();
        for (int i = 0; i < servers.length(); i++) {
            JSONObject serverObj = servers.getJSONObject(i);
            String nickname = serverObj.optString("Nickname", "Server (" + (i + 1) + ")");
            String iconURL = serverObj.optString("IconURL", "");
            String ip = serverObj.optString("IP", "");
            String username = serverObj.optString("Username", "");
            String password = serverObj.optString("Password", "");
            String privateKeyLocation = serverObj.optString("PrivateKey-Location", "");
            BootstrapColumn serverCol;
            BootstrapPane serverContainer = new BootstrapPane();
            serverContainer.getStyleClass().add("server-container");
            BootstrapRow row = new BootstrapRow();
            BootstrapColumn leftCol;
            BootstrapColumn rightCol;
            // Image icon
            Image image = new Image(iconURL);
            ImageView imgView = new ImageView();
            imgView.getStyleClass().add("server-icon");
            imgView.setFitHeight(100);
            imgView.setFitWidth(100);
            imgView.setImage(image);
            leftCol = API.get().createColumn(imgView, 6);
            row.addColumn(leftCol);

            // Server nickname
            BootstrapPane rightPane = new BootstrapPane();
            Label serverNickname = new Label(nickname);
            serverNickname.getStyleClass().add("server-nickname");
            BootstrapRow dataRow = new BootstrapRow();
            dataRow.addColumn(API.get().createColumn(serverNickname, 12));

            // Server IP
            Label serverIP = new Label(ip);
            serverIP.getStyleClass().add("server-ip");
            dataRow.addColumn(API.get().createColumn(serverIP, 12));

            rightPane.addRow(dataRow);

            rightCol = API.get().createColumn(rightPane, 6);
            row.addColumn(rightCol);
            serverContainer.addRow(row);



            serverCol = API.get().createColumn(serverContainer, 12, 6, 4, 3, 2, 2);
            serverCol.getContent().setOnMouseClicked(event -> {
                // We want to set them in a loading screen until we connect or not
                MainApp.get().changeScene(JScene.LOADING);
                Thread thread = new Thread(() -> {
                    Connection servConn = new Connection(username, ip, password, 22);
                    servConn.addKnownHost();
                    Platform.runLater(() -> {
                        boolean connected = servConn.connect();
                        if (connected) {
                            TerminalTabComponent termTab = new TerminalTabComponent(nickname, servConn);
                            MainApp.get().getTerminalController().addTerminalTab(termTab);
                            MainApp.get().changeScene(JScene.TERMINAL);
                        } else {
                            MainApp.get().changeScene(JScene.DASHBOARD);
                        }
                    });
                });
                AsyncManager.get().addThread(thread);
            });
            iconRow.addColumn(serverCol);
        }
        searchRow.addColumn(searchCol);
        searchRow.addColumn(newSessCol);

        BootstrapRow menuRow = API.get().createToolbox();
        this.addRow(menuRow);
        this.addRow(searchRow);
        this.addRow(iconRow);
    }
}

package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.DataManager;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardController extends BootstrapPane {
    public DashboardController() {
        BootstrapRow searchRow = new BootstrapRow();
        BootstrapColumn searchCol = API.get().createColumn(new BootstrapPane(), 10);
        BootstrapColumn newSessCol = API.get().createColumn(new BootstrapPane(), 2);
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
            BootstrapRow row = new BootstrapRow();
            BootstrapColumn leftCol;
            BootstrapColumn rightCol;
            // Image icon
            Image image = new Image(iconURL);
            ImageView imgView = new ImageView();
            imgView.setFitHeight(100);
            imgView.setFitWidth(100);
            imgView.setImage(image);
            leftCol = API.get().createColumn(imgView, 6);
            row.addColumn(leftCol);

            // Server nickname
            BootstrapPane rightPane = new BootstrapPane();
            Label serverNickname = new Label(nickname);
            BootstrapRow dataRow = new BootstrapRow();
            dataRow.addColumn(API.get().createColumn(serverNickname, 12));

            // Server IP
            Label serverIP = new Label(ip);
            dataRow.addColumn(API.get().createColumn(serverIP, 12));

            rightPane.addRow(dataRow);

            rightCol = API.get().createColumn(rightPane, 6);
            row.addColumn(rightCol);
            serverContainer.addRow(row);



            serverCol = API.get().createColumn(serverContainer, 3);
            iconRow.addColumn(serverCol);
        }
        searchRow.addColumn(searchCol);
        searchRow.addColumn(newSessCol);
        this.addRow(searchRow);
        this.addRow(iconRow);
    }
}

package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.model.DataManager;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import org.json.JSONArray;
import org.json.JSONObject;

public class DashboardController extends BootstrapPane {
    public DashboardController() {
        BootstrapRow searchRow = new BootstrapRow();
        BootstrapColumn searchCol = API.get().createColumn(null, 10);
        BootstrapColumn newSessCol = API.get().createColumn(null, 2);
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
        }
        // TODO Add the servers to the icon row
        searchRow.addColumn(searchCol);
        searchRow.addColumn(newSessCol);
        this.addRow(searchRow);
        this.addRow(iconRow);
    }
}

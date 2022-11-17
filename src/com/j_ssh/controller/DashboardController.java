package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;

public class DashboardController extends BootstrapPane {
    public DashboardController() {
        BootstrapRow searchRow = new BootstrapRow();
        BootstrapColumn searchCol = API.get().createColumn(null, 10);
        BootstrapColumn newSessCol = API.get().createColumn(null, 2);
        BootstrapRow iconRow = new BootstrapRow();
        this.addRow(searchRow);
        this.addRow(iconRow);
    }
}

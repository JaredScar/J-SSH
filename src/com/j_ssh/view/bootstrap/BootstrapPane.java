package com.j_ssh.view.bootstrap;

import javafx.geometry.Pos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class BootstrapPane extends GridPane {

    private Breakpoint currentWindowSize = Breakpoint.XSMALL;

    public BootstrapPane() {
        super();
        setAlignment(Pos.TOP_CENTER);
        setColumnConstraints();
        setWidthEventHandlers();
    }

    private void setWidthEventHandlers() {
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            Breakpoint newBreakpoint = Breakpoint.XSMALL;
            if (newValue.doubleValue() > 576) newBreakpoint = Breakpoint.SMALL;
            if (newValue.doubleValue() > 768) newBreakpoint = Breakpoint.MEDIUM;
            if (newValue.doubleValue() > 992) newBreakpoint = Breakpoint.LARGE;
            if (newValue.doubleValue() > 1200) newBreakpoint = Breakpoint.XLARGE;

            if (newBreakpoint != currentWindowSize) {
                currentWindowSize = newBreakpoint;
                calculateNodePositions();
            }
        });
    }

    private void setColumnConstraints() {
        //Remove all current columns.
        getColumnConstraints().clear();

        //Create 12 equally sized columns for layout
        double width = 100.0 / 12.0;
        for (int i = 0; i < 12; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(width);
            getColumnConstraints().add(columnConstraints);
        }
    }

    private void calculateNodePositions() {
        int currentGridPaneRow = 0;
        for (BootstrapRow row : rows) {
            currentGridPaneRow += row.calculateRowPositions(currentGridPaneRow, currentWindowSize);
        }
    }

    private final List<BootstrapRow> rows = new ArrayList<>();

    public void addRow(BootstrapRow row) {
        if (rows.contains(row)) return; //prevent duplicate children error

        rows.add(row);
        calculateNodePositions();

        for (BootstrapColumn column : row.getColumns()) {
            getChildren().add(column.getContent());
            GridPane.setFillWidth(column.getContent(), true);
            GridPane.setFillHeight(column.getContent(), true);
        }
    }

    public void removeRow(BootstrapRow row) {
        rows.remove(row);
        calculateNodePositions();

        for (BootstrapColumn column : row.getColumns()) {
            getChildren().remove(column.getContent());
        }
    }
}

package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.awt.*;

public class SshController {
    public void changeStage(Stage primaryStage) {
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        API.get().createToolbox(vBox);
        Scene scene = new Scene(vBox);
        GridPane masterGrid = new GridPane();
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 10, 10,10));
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setHgrow(Priority.SOMETIMES);
        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.SOMETIMES);
        masterGrid.getRowConstraints().add(rowConstraints);
        masterGrid.getColumnConstraints().add(colConstraints);

        BootstrapPane bPane = new BootstrapPane();
        bPane.setPadding(new Insets(10, 10, 10, 10));
        BootstrapRow bRow = new BootstrapRow();
        BootstrapRow inputRow = new BootstrapRow();

        // Terminal Window
        TextArea terminalWindow = new TextArea();
        terminalWindow.setId("terminal");
        terminalWindow.setEditable(false);
        terminalWindow.setWrapText(true);
        terminalWindow.prefWidthProperty().bind(primaryStage.widthProperty());
        terminalWindow.prefHeightProperty().bind(primaryStage.heightProperty());
        BootstrapColumn colTerminal = API.get().createColumn(terminalWindow, 6, 6, 8, 10, 10);
        bRow.addColumn(colTerminal);
        bPane.addRow(bRow);

        // Input
        TextField tf = new TextField();
        tf.setId("terminal-input");
        tf.setEditable(true);
        terminalWindow.prefWidthProperty().bind(primaryStage.widthProperty());
        BootstrapColumn colInput = API.get().createColumn(tf, 6, 6, 8, 10, 10);
        inputRow.addColumn(colInput);
        bPane.addRow(inputRow);

        // GRID DATA
        masterGrid.add(grid, 0, 0);
        masterGrid.add(bPane, 0, 1);
        vBox.getChildren().add(masterGrid);

        // ENDING SET
        scene.getStylesheets().add("com/j_ssh/view/style.css");
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }
}

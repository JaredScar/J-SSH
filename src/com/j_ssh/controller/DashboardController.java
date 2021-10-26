package com.j_ssh.controller;

import com.j_ssh.api.API;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class DashboardController {

    public void changeStage(Stage primaryStage) {
        primaryStage.setTitle("J-SSH");
        Toolkit tk = Toolkit.getDefaultToolkit();
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        API.get().createToolbox(vBox);
        Scene scene = new Scene(vBox);
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(5, 10, 10,10));
        ColumnConstraints colConstraints = new ColumnConstraints();
        colConstraints.setHgrow(Priority.ALWAYS);

        // SEARCHBOX
        TextField searchbox = new TextField();
        searchbox.getStyleClass().add("dash-searchBox");
        searchbox.setPromptText("Search or Enter Address");
        searchbox.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
        //searchbox.addEventHandler(); // TODO

        // SEPARATOR
        Separator sep = new Separator();
        sep.getStyleClass().add("dash-sep");
        sep.setPrefWidth(30);
        sep.setOrientation(Orientation.VERTICAL);

        // BUTTON [Create new session]
        Button createNewSession = new Button("Create new session");
        createNewSession.getStyleClass().add("dash-createNewSession");
        //createNewSession.addEventFilter(); // TODO

        // USER DATA [parse data.json and put saved sessions here]
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(new File("data.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (obj != null) {
            JSONObject jo = (JSONObject) obj;
            JSONArray servers = (JSONArray) jo.get("Servers");
            servers.forEach(server -> {
                JSONObject serverJSON = (JSONObject) server;
                String nickname = (String) serverJSON.get("Nickname");
                String IP = (String) serverJSON.get("IP");
                String username = (String) serverJSON.get("Username");
                String password = (String) serverJSON.get("Password");
                String privateKeyLocation = (String) serverJSON.get("PrivateKey-Location");
            });
        }

        // GRID DATA
        grid.getColumnConstraints().add(colConstraints);
        grid.add(searchbox, 0, 0);
        grid.add(sep, 1, 0, 1, 1);
        grid.add(createNewSession, 2, 0);
        vBox.getChildren().add(grid);

        // ENDING SET
        primaryStage.setWidth(tk.getScreenSize().getWidth() - (tk.getScreenSize().getWidth() / 3));
        primaryStage.setHeight((tk.getScreenSize().getHeight()) - (tk.getScreenSize().getHeight() / 3));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

}

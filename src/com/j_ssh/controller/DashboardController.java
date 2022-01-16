package com.j_ssh.controller;

import com.j_ssh.api.API;
import com.j_ssh.view.bootstrap.BootstrapColumn;
import com.j_ssh.view.bootstrap.BootstrapPane;
import com.j_ssh.view.bootstrap.BootstrapRow;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

public class DashboardController {

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

        // GRID DATA
        grid.getColumnConstraints().add(colConstraints);
        grid.add(searchbox, 0, 0);
        grid.add(sep, 1, 0, 1, 1);
        grid.add(createNewSession, 2, 0);

        // USER DATA [parse data.json and put saved sessions here]
        Object obj = null;
        try {
            obj = new JSONParser().parse(new FileReader(new File("data.json")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        BootstrapPane serverPane = new BootstrapPane();
        serverPane.setPadding(new Insets(10, 10, 10, 10));
        serverPane.setHgap(10);
        if (obj != null) {
            JSONObject jo = (JSONObject) obj;
            JSONArray servers = (JSONArray) jo.get("Servers");
            int colIndex = 0;
            Iterator<Object> iterator = servers.iterator();
            BootstrapRow bRow = new BootstrapRow();
            while (iterator.hasNext()) {
                Object server = iterator.next();
                GridPane pane = new GridPane();
                pane.setHgap(20);
                pane.getStyleClass().add("dash-serverObject");
                pane.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,
                        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
                JSONObject serverJSON = (JSONObject) server;
                String nickname = (String) serverJSON.get("Nickname");
                VBox serverText = new VBox();
                Label labelNickname = new Label(nickname);
                labelNickname.setFont(Font.font("Roboto", FontWeight.EXTRA_BOLD, 20));
                labelNickname.setWrapText(true);
                serverText.getChildren().add(labelNickname);
                String logo = (String) serverJSON.get("IconURL");
                Image img = new Image(logo);
                ImageView logoView = new ImageView(img);
                logoView.setFitWidth(100);
                logoView.setFitHeight(100);
                logoView.setSmooth(true);
                logoView.getStyleClass().add("serverObject-logo");
                pane.add(logoView, 0, 0, 1, 2);
                String IP = (String) serverJSON.get("IP");
                Label labelIP = new Label(IP);
                labelIP.setWrapText(true);
                //serverText.setPadding(new Insets(20, 20, 20, 20));
                serverText.getChildren().add(labelIP);
                pane.add(serverText, 1, 0);
                String username = (String) serverJSON.get("Username");
                String password = (String) serverJSON.get("Password");
                String privateKeyLocation = (String) serverJSON.get("PrivateKey-Location");
                BootstrapColumn col = API.get().createColumn(pane, 12, 6, 4, 3, 3);
                bRow.addColumn(col);
                colIndex++;
            }
            serverPane.addRow(bRow);
        }

        // GRID DATA
        masterGrid.add(grid, 0, 0);
        masterGrid.add(serverPane, 0, 1);
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

package com.j_ssh.api;

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.concurrent.CountDownLatch;

public class MyUserInfo implements UserInfo, UIKeyboardInteractive {
    private String passwd;

    @Override
    public String getPassword() {
        return passwd;
    }

    public void setPassword(String passwd) {
        this.passwd = passwd;
    }

    @Override
    public boolean promptYesNo(String str) {
        return showYesNoDialog("Warning", str);
    }

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return true;
    }

    @Override
    public boolean promptPassword(String message) {
        return showPasswordDialog(message);
    }

    @Override
    public void showMessage(String message) {
        showMessageDialog(message);
    }

    @Override
    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        return showKeyboardInteractiveDialog(destination, name, instruction, prompt, echo);
    }

    private boolean showYesNoDialog(String title, String message) {
        final boolean[] result = new boolean[1];
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
            alert.setTitle(title);
            alert.setHeaderText(null);
            result[0] = alert.showAndWait().orElse(ButtonType.NO) == ButtonType.YES;
            latch.countDown();
        });
        try {
            latch.await();  // Wait for the dialog to be closed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }

    private boolean showPasswordDialog(String message) {
        if (passwd == null) {
            final boolean[] result = new boolean[1];
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setHeaderText(message);
                dialog.setContentText("Password:");

                dialog.getEditor().setPromptText("Enter password");
                result[0] = dialog.showAndWait().isPresent();
                if (result[0]) {
                    passwd = dialog.getEditor().getText();
                }
                latch.countDown();
            });
            try {
                latch.await();  // Wait for the dialog to be closed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result[0];
        } else {
            return true;
        }
    }

    private void showMessageDialog(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Message");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    private String[] showKeyboardInteractiveDialog(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        final String[][] response = new String[1][];
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(destination + ": " + name);

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10));
            grid.setHgap(10);
            grid.setVgap(10);

            Label instructionLabel = new Label(instruction);
            grid.add(instructionLabel, 0, 0, 2, 1);

            TextField[] fields = new TextField[prompt.length];
            for (int i = 0; i < prompt.length; i++) {
                Label label = new Label(prompt[i]);
                grid.add(label, 0, i + 1);

                if (echo[i]) {
                    fields[i] = new TextField();
                } else {
                    fields[i] = new PasswordField();
                }
                grid.add(fields[i], 1, i + 1);
            }

            Button okButton = new Button("OK");
            okButton.setOnAction(e -> {
                response[0] = new String[prompt.length];
                for (int i = 0; i < prompt.length; i++) {
                    response[0][i] = fields[i].getText();
                }
                latch.countDown();
                stage.close();
            });

            grid.add(okButton, 1, prompt.length + 1);

            Scene scene = new Scene(grid);
            stage.setScene(scene);
            stage.showAndWait();
        });
        try {
            latch.await();  // Wait for the dialog to be closed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response[0];
    }
}

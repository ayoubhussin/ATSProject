package main.LoginPage;

import animatefx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.fxml.Initializable;


import javafx.event.ActionEvent;

import animatefx.animation.AnimationFX;
import main.DatabaseConection;
import main.SceneController;
import main.UserCache;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController {
    private UserCache user = new UserCache();

    @FXML
    private Label loginErrorLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private ProgressBar barProgressBar;

    private SceneController sceneController = new SceneController();

    @FXML
    public void onEnter(ActionEvent e) throws IOException {
        LoginErrorLabelOnAction(e);
    }

    public void LoginErrorLabelOnAction(ActionEvent e) throws IOException {
        barProgressBar.setVisible(true);

        new Wobble(loginErrorLabel).play();


        if (usernameField.getText().isBlank() == false && passwordPasswordField.getText().isBlank() == false) {

            DatabaseConection db = new DatabaseConection();
            db.UserConnection();

            switch (db.LoginCheck(usernameField.getText(), passwordPasswordField.getText(), user)) {
                case "None":
                    barProgressBar.setVisible(false);
                    loginErrorLabel.setText("Incorrect Login Credentials");
                    break;
                case "TravelAdvisor":
                    barProgressBar.setVisible(true);
                    sceneController.switchToTravelAgentHome(e, user);
                    db.CloseConnection();
                    break;
                case "Admin":
                    barProgressBar.setVisible(true);
                    sceneController.switchToAdminHomePage(e);
                    db.CloseConnection();
                    break;
                case "OfficeManager":
                    barProgressBar.setVisible(true);
                    sceneController.switchToOfficeManagerHomePage(e);
                    db.CloseConnection();
                    break;

            }

        } else {
            barProgressBar.setVisible(false);
            loginErrorLabel.setText("Please enter username and password");

        }


    }

}

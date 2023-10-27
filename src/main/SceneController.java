package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
     private Stage stage;
     private Scene scene;
     private Parent root;

     private UserCache user;

     public void switchToTravelAgentHome(ActionEvent event, UserCache user) throws IOException {
         this.user = user;
         Parent root = FXMLLoader.load(getClass().getResource("TravelAgent/TravelAgentHome.fxml"));
         stage = (Stage)((Node)event.getSource()).getScene().getWindow();
         scene = new Scene(root);
         scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
         stage.setScene(scene);
         stage.show();
     }

    public void switchToLoginPage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LoginPage/login.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToAdminHomePage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("AdminPage/AdminHomePage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public void switchToOfficeManagerHomePage(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("OfficeManagerPage/OfficeManagerHomePage.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public UserCache getUser(){
         return user;
    }
}

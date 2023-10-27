package main.AdminPage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.ATS;
import main.BackUpDatabase;
import main.DatabaseConection;
import main.SceneController;
import main.AdminPage.Blank;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class AdminHomePage implements Initializable {
    private SceneController sceneController = new SceneController();
    private BackUpDatabase backUpDatabase = new BackUpDatabase();
    private Alert a = new Alert(Alert.AlertType.NONE);
    ActionEvent e;

    // Blanks Page
        // Blanks Table
        @FXML private TableView blanks_BlankTableView;
        @FXML private TableColumn blanks_BlankNumberColumn;
        @FXML private TableColumn blanks_AdvisorIDColumn;

        // Filter Blanks
        @FXML private ComboBox blank_AdvisorIDFilterComboBox;
        @FXML private ComboBox blank_TypeFilterComboBox;
        @FXML private CheckBox blank_UnsoldCheckBox;
        @FXML private Button blank_SearchBtn;
        @FXML private Button blank_ResetBtn;

        // New Blanks
        @FXML private ComboBox blank_CreateTypeComboBox;
        @FXML private TextField blank_CreateAmount;
        @FXML private Button blank_CreateBtn;

        // Remove Blanks
        @FXML private ComboBox blank_RemoveBlankComboBox;
        @FXML private ComboBox blank_RemoveBlankTypeComboBox;
        @FXML private Button blank_RemoveBtn;

    // My Account
    @FXML private Label myAccount_MyIDLabel;
    @FXML private PasswordField myAccount_CurrentPassTextField;
    @FXML private PasswordField myAccount_NewPassTextField;

    // Backups
    @FXML private ComboBox backup_tableComboBox;
    @FXML private Button backup_CreateBackupBtn;
    @FXML private ComboBox backup_RecoverTableComboBox;
    @FXML private ComboBox backup_RecoverBackupComboBox;
    @FXML private Button backup_RecoverBackupBtn;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();
        // BLANKS TAB
            // Filter
            blank_AdvisorIDFilterComboBox.getItems().add("Unassigned");
            blank_AdvisorIDFilterComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));
            blank_TypeFilterComboBox.getItems().addAll("101", "201", "420", "444");
            BlankTableRefresh(e);



            // Add New Blanks
            blank_CreateTypeComboBox.getItems().addAll("101", "201", "420", "444");

            // Remove Blanks
            blank_RemoveBlankTypeComboBox.getItems().addAll("101", "201", "420", "444");
            blank_RemoveBlankComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank"));

        // My Account
        myAccount_MyIDLabel.setText("My ID: "+ATS.getUser());

        // Backups
        backup_tableComboBox.getItems().addAll("Sales", "Reports", "TravelAdvisor", "Refunds", "Payment", "OfficeManager", "Flights",
                "Customer", "Blank", "Backups", "Administrator");
        backup_RecoverTableComboBox.getItems().addAll("Sales", "Reports", "TravelAdvisor", "Refunds", "Payment", "OfficeManager", "Flights",
                "Customer", "Blank", "Backups", "Administrator");
        backup_RecoverBackupComboBox.getItems().addAll(db.stringStatement("SELECT backupName FROM Backups"));
        RefreshBackupList(e);

        db.CloseConnection();
    }

    public void RefreshBackupList(ActionEvent e) {
        DatabaseConection db = new DatabaseConection();
        db.AdminConnection();

        if (true) {
            if (backup_RecoverTableComboBox.getValue() == null) {
                backup_RecoverBackupComboBox.getItems().clear();
                backup_RecoverBackupComboBox.getItems().addAll(db.stringStatement("SELECT backupName FROM Backups"));
            } else {
                backup_RecoverBackupComboBox.getItems().clear();
                backup_RecoverBackupComboBox.getItems().addAll(db.stringStatement("SELECT backupName FROM Backups WHERE tableBackUp = '" + backup_RecoverTableComboBox.getValue() + "'"));
            }
        }
        db.CloseConnection();

    }

    public void GenerateBackup(ActionEvent e) throws SQLException {
        if (backup_tableComboBox.getValue() != null) {
            backUpDatabase.CreateBackup(""+backup_tableComboBox.getValue());
            backup_tableComboBox.setValue(null);
        }
    }

    public void UseBackup() throws SQLException {
        if (backup_RecoverTableComboBox.getValue() != null && backup_RecoverBackupComboBox.getValue() != null) {
            backUpDatabase.LoadBackup(""+backup_RecoverTableComboBox.getValue(), ""+backup_RecoverBackupComboBox.getValue());
            backup_RecoverTableComboBox.setValue(null);
            backup_RecoverBackupComboBox.setValue(null);
        }
    }


    public void ChangePassword(ActionEvent e) throws SQLException {
        if (myAccount_CurrentPassTextField.getText() != "" && myAccount_NewPassTextField.getText() != "") {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();
            String cPass = (db.stringStatement("SELECT password FROM Administrator WHERE AdminID = '"+ ATS.getUser()+"'")).get(0);
            if (myAccount_CurrentPassTextField.getText().matches(cPass)) {
                Alert b = new Alert(Alert.AlertType.CONFIRMATION, "");
                b.setHeaderText("Change Password?");
                b.showAndWait();

                if (b.getResult() == ButtonType.OK) {
                    db.executeStatement("UPDATE Administrator SET password = '" + myAccount_NewPassTextField.getText() + "' WHERE managerID = '" + ATS.getUser() + "'");
                    Alert c = new Alert(Alert.AlertType.INFORMATION, "");
                    c.setHeaderText("Password Changed!");
                    c.showAndWait();
                }


            } else {
                Alert b = new Alert(Alert.AlertType.ERROR, "");
                b.setHeaderText("Incorrect Password");
                b.showAndWait();
            }
            db.CloseConnection();
        } else {
            Alert b = new Alert(Alert.AlertType.ERROR, "");
            b.setHeaderText("Fill both text fields");
            b.showAndWait();
        }

        myAccount_CurrentPassTextField.setText("");
        myAccount_NewPassTextField.setText("");
    }

    public void CreateBlanks(ActionEvent e) throws SQLException {
        Alert check = new Alert(Alert.AlertType.CONFIRMATION);
        check.setHeaderText("Confirm");
        check.setContentText("Are you sure you want to create new blanks?");
        check.showAndWait();
        if (check.getResult() == ButtonType.OK) {
            if (blank_CreateTypeComboBox.getValue() != null && blank_CreateAmount.getText() != "") {
                boolean amIValid = false;
                try {
                    Integer.parseInt(blank_CreateAmount.getText());
                    amIValid = true;
                } catch (NumberFormatException nfe) {

                }

                if (amIValid) {
                    DatabaseConection db = new DatabaseConection();
                    db.UserConnection();

                    List<String> preSort = db.stringStatement("SELECT blankNumber FROM Blank WHERE blankType = " + blank_CreateTypeComboBox.getValue());
                    List<Long> postSort = new ArrayList<>();

                    for (String a : preSort) {
                        postSort.add(Long.parseLong(a));
                    }
                    Collections.sort(postSort);
                    long largestBlankNum = postSort.get(postSort.size() - 1);
                    LocalDate date = LocalDate.now();

                    for (int i = 0; i < Integer.parseInt(blank_CreateAmount.getText()); i++) {
                        largestBlankNum++;
                        db.executeStatement(String.format("INSERT INTO Blank (blankNumber, blankType, AssignmentDate, TravelAdvisorID) VALUES ('%s', %s, '%s', NULL)",
                                largestBlankNum, blank_CreateTypeComboBox.getValue(), date));



                    }

                    a.setAlertType(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Success");
                    a.setContentText(String.format("Blanks Range Added: %s to %s", largestBlankNum - Integer.parseInt(blank_CreateAmount.getText()), largestBlankNum));
                    a.showAndWait();

                    blank_CreateTypeComboBox.setValue(null);
                    blank_CreateAmount.setText("");

                    BlankTableRefresh(e);
                    UpdateBlankRemoveTypes(e);

                    db.CloseConnection();
                } else {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setHeaderText("Error");
                    a.setContentText("Invalid amount");
                    a.showAndWait();
                }

            } else {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setHeaderText("Error");
                a.setContentText("Fill Both Fields!");
                a.showAndWait();
            }
        }


    }

    public void UpdateBlankRemoveTypes(ActionEvent e) {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        if (blank_RemoveBlankTypeComboBox.getValue() != null) {
            blank_RemoveBlankComboBox.getItems().clear();
            blank_RemoveBlankComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank WHERE blankType = " + blank_RemoveBlankTypeComboBox.getValue()));
        } else {
            blank_RemoveBlankComboBox.getItems().clear();
            blank_RemoveBlankComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank"));
        }

        db.CloseConnection();
    }

    public void BlankRemove(ActionEvent e) throws SQLException {
        Alert check = new Alert(Alert.AlertType.CONFIRMATION);
        check.setHeaderText("Confirm");
        check.setContentText("Are you sure you want to delete a blank?");
        if (blank_RemoveBlankComboBox.getValue() != null) {
            check.showAndWait();
            if (check.getResult() == ButtonType.OK) {
                DatabaseConection db = new DatabaseConection();
                db.AdminConnection();
                db.executeStatement("DELETE FROM Blank WHERE blankNumber = '" + blank_RemoveBlankComboBox.getValue() + "'");
                db.CloseConnection();

                check = new Alert(Alert.AlertType.INFORMATION);
                check.setHeaderText("Complete");
                check.setContentText("Blank ["+blank_RemoveBlankComboBox.getValue()+"] Deleted");
                check.showAndWait();

                blank_RemoveBlankComboBox.setValue(null);
                blank_RemoveBlankTypeComboBox.setValue(null);
                UpdateBlankRemoveTypes(e);
            }
        } else {
            check = new Alert(Alert.AlertType.ERROR);
            check.setHeaderText("Error");
            check.setContentText("Select a blank to delete");
            check.showAndWait();
        }
    }


    public void BlankTableRefresh(ActionEvent e) {

        BlankList blankList = new BlankList(blank_AdvisorIDFilterComboBox, blank_TypeFilterComboBox, blank_UnsoldCheckBox);
        ObservableList<Blank> list = FXCollections.observableArrayList();

        for (int i = 0; i < blankList.getBlankNumber().size(); i++) {
            list.add(new Blank((blankList.getBlankNumber()).get(i),
                    (blankList.getAdvisorID()).get(i)
            ));
        }
        blanks_BlankNumberColumn.setCellValueFactory(new PropertyValueFactory<>("blankNumber"));
        blanks_AdvisorIDColumn.setCellValueFactory(new PropertyValueFactory<>("advisorID"));

        blanks_BlankTableView.setItems(list);

    }


    public void LogOutOnAction(ActionEvent e) throws IOException {
        a.setAlertType(Alert.AlertType.CONFIRMATION);
        a.setHeaderText("Are you sure you want to log out?");
        a.setContentText("Press OK to log out...");
        a.showAndWait();

        if (a.getResult() == ButtonType.OK) {
            sceneController.switchToLoginPage(e);
        }

    }

}

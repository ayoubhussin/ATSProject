package main.OfficeManagerPage;

import animatefx.animation.Wobble;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import main.ATS;
import main.DatabaseConection;
import main.SceneController;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class OfficeManagerHomePage implements Initializable {
    private SceneController sceneController = new SceneController();
    private DatabaseConection db = new DatabaseConection();
    private Alert a = new Alert(Alert.AlertType.NONE);
    private ActionEvent e;

    // Logged in person
    String fName;
    String sName;

    // Sidebar
    @FXML private Label loggedInLabel;

    // Blanks Tab
        // Blanks -> Assign Blanks Box
        @FXML private ComboBox blank_BlankNumberComboBox;
        @FXML private ComboBox blank_AdvisorIDAssignComboBox;
        @FXML private ComboBox blank_TypeAssignComboBox;
        @FXML private Button blank_AssignBtn;
        @FXML private CheckBox blank_showUnassignedCheckBox;

        // Blanks -> Filter Blanks Box
        @FXML private ComboBox blank_AdvisorIDFilterComboBox;
        @FXML private ComboBox blank_TypeFilterComboBox;
        @FXML private Button blank_SearchBtn;
        @FXML private Button blank_ResetBtn;

        // Blanks -> Blank Table
        @FXML private TableView blanks_BlankTableView;
        @FXML private TableColumn blanks_BlankNumberColumn;
        @FXML private TableColumn blanks_AdvisorIDColumn;


    // Advisors Tab
        // Advisors Table View
        @FXML private TableView advisors_TableView;
        @FXML private TableColumn advisors_IDColumn;
        @FXML private TableColumn advisors_firstNameColumn;
        @FXML private TableColumn advisors_SecondNameColumn;
        @FXML private TableColumn advisors_PhoneNumberColumn;
        @FXML private TableColumn advisors_EmailColumn;

        // Register Advisor Box
        @FXML private TextField advisors_FirstNameTextField;
        @FXML private TextField advisors_SecondNameTextField;
        @FXML private TextField advisors_PhoneNumberField;
        @FXML private TextField advisors_EmailTextField;
        @FXML private Button advisors_RegisterAdvisorBtn;

        // Update Advisor
        @FXML private ComboBox advisors_FieldToChangeComboBox;
        @FXML private TextField advisors_NewInformationTextField;
        @FXML private Button advisors_UpdateAdvisorBtn;
        @FXML private ComboBox advisors_AdvisorIDComboBox;


    // Customer Tab
        // Search Bar
        @FXML ComboBox<String> customer_customerSearchComboBox;
        @FXML Hyperlink customer_refreshHyperLink;

        // Customer Information
        private String customerID= "";
        @FXML private Label customer_IDLabel;
        @FXML private Label customer_firstNameLabel;
        @FXML private Label customer_secondNameLabel;
        @FXML private Label customer_advisorIDLabel;
        @FXML private Hyperlink customer_emailLabel;
        @FXML private Hyperlink customer_pNumberLabel;
        @FXML private Label customer_valuedLabel;
        @FXML private Label customer_fixedDiscountLabel;
        @FXML private Label customer_flexibleDiscountLabel;
        @FXML private Label customer_aliasLabel;

        // Customer Edit Box
        private String[] changeableFields = {"First Name", "Second Name", "Alias", "Email", "Number", "Fixed Discount", "Flexible Discount"};
        @FXML private ComboBox customer_fieldToChangeComboBox;
        @FXML private TextField customer_changeTextField;
        @FXML private Button customer_markValuedBtn;
        @FXML private ComboBox customer_changeAdvisorComboBox;
        @FXML private Button customer_submitBtn;

    // My Account
    @FXML private Label myAccount_MyIDLabel;
    @FXML private PasswordField myAccount_CurrentPassTextField;
    @FXML private PasswordField myAccount_NewPassTextField;


    @Override
    public void initialize(URL url, ResourceBundle rb){
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        // Sidebar
        fName = (db.stringStatement("SELECT firstName FROM OfficeManager WHERE managerID = "+ ATS.getUser())).get(0);
        sName = (db.stringStatement("SELECT secondName FROM OfficeManager WHERE managerID = "+ ATS.getUser())).get(0);
        loggedInLabel.setText("Logged in:\n"+fName+" "+sName);


        // BLANKS TAB
            // Filter
            blank_AdvisorIDFilterComboBox.getItems().add("Unassigned");
            blank_AdvisorIDFilterComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));
            blank_TypeFilterComboBox.getItems().addAll("101", "201", "420", "444");
            BlankTableRefresh(e);

            // Assign Blank
            blank_TypeAssignComboBox.getItems().addAll("Any", "101", "201", "420", "444");
            blank_AdvisorIDAssignComboBox.getItems().add("UNASSIGN");
            blank_AdvisorIDAssignComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));

            AssignBlankUpdate(e);


        // ADVISOR TAB
            // Table View
            LoadAdvisorTableView(e);

            // Edit Advisor
            advisors_FieldToChangeComboBox.getItems().addAll("First Name", "Second Name",
                    "Phone", "Email");
            advisors_AdvisorIDComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));


        // CUSTOMER TAB
            // Search Bar
            customerListLoad(db);

            // Edit Customer
            customer_fieldToChangeComboBox.getItems().addAll(changeableFields);
            customer_fieldToChangeComboBox.getItems().add("No Change");
            customer_fieldToChangeComboBox.setValue("No Change");
            customer_changeAdvisorComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));
            customer_changeAdvisorComboBox.getItems().add("No Change");
            customer_changeAdvisorComboBox.setValue("No Change");

        // My Account
        myAccount_MyIDLabel.setText("My ID: "+ATS.getUser());

        db.CloseConnection();
    }

    public void ChangePassword(ActionEvent e) throws SQLException {
        if (myAccount_CurrentPassTextField.getText() != "" && myAccount_NewPassTextField.getText() != "") {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();
            String cPass = (db.stringStatement("SELECT password FROM OfficeManager WHERE managerID = '"+ATS.getUser()+"'")).get(0);
            if (myAccount_CurrentPassTextField.getText().matches(cPass)) {
                Alert b = new Alert(Alert.AlertType.CONFIRMATION, "");
                b.setHeaderText("Change Password?");
                b.showAndWait();

                if (b.getResult() == ButtonType.OK) {
                    db.executeStatement("UPDATE OfficeManager SET password = '" + myAccount_NewPassTextField.getText() + "' WHERE managerID = '" + ATS.getUser() + "'");
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


    public void ChangeAdvisorDetails(ActionEvent e) throws SQLException {
        if (advisors_AdvisorIDComboBox.getValue() != null && advisors_FieldToChangeComboBox.getValue() != null && advisors_NewInformationTextField.getText() != ""){
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();

            boolean numeric = false;

            switch (""+advisors_FieldToChangeComboBox.getValue()) {
                case "First Name":
                    db.executeStatement("UPDATE TravelAdvisor SET firstName = '" + advisors_NewInformationTextField.getText() + "' WHERE advisorID = " + advisors_AdvisorIDComboBox.getValue());
                    break;
                case "Second Name":
                    db.executeStatement("UPDATE TravelAdvisor SET secondName = '" + advisors_NewInformationTextField.getText() + "' WHERE advisorID = " + advisors_AdvisorIDComboBox.getValue());
                    break;
                case "Phone":
                    try {
                        long intValue = Long.parseLong(advisors_NewInformationTextField.getText());
                        db.executeStatement("UPDATE TravelAdvisor SET phone = '" + advisors_NewInformationTextField.getText() + "' WHERE advisorID = " + advisors_AdvisorIDComboBox.getValue());
                        numeric = true;
                    } catch (NumberFormatException d) {
                        Alert b = new Alert(Alert.AlertType.ERROR, "Phone number must be a numeric value!");
                        b.setTitle("Error");
                        b.show();
                    }

                    break;
                case "Email":
                    db.executeStatement("UPDATE TravelAdvisor SET email = '" + advisors_NewInformationTextField.getText() + "' WHERE advisorID = " + advisors_AdvisorIDComboBox.getValue());
                    break;

            }
            Alert c = new Alert(Alert.AlertType.INFORMATION, "Details updated");
            c.setTitle("Success");
            c.show();

            advisors_AdvisorIDComboBox.setValue(null);
            advisors_FieldToChangeComboBox.setValue(null);
            advisors_NewInformationTextField.setText("");
            LoadAdvisorTableView(e);
            db.CloseConnection();
        } else {
            Alert b = new Alert(Alert.AlertType.ERROR, "One or more fields are empty");
            b.setTitle("Error");
            b.showAndWait();
        }


    }

    public void RegisterAdvisor(ActionEvent e) throws SQLException {
        if (advisors_FirstNameTextField.getText() != "" && advisors_SecondNameTextField.getText() != "" && advisors_PhoneNumberField.getText() != "" && advisors_EmailTextField.getText() != "") {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();

            boolean numeric = false;

            byte[] array = new byte[7]; // length is bounded by 7
            new Random().nextBytes(array);
            String generatedPassword = String.format("%s.%s", advisors_FirstNameTextField.getText(), advisors_SecondNameTextField.getText());

            try {
                Long intValue = Long.parseLong(advisors_PhoneNumberField.getText());
                numeric = true;
            } catch (NumberFormatException d) {

            }

            if (numeric) {

                db.executeStatement(String.format("INSERT INTO TravelAdvisor (firstName, secondName, phone, email, password, managerID) VALUES ('%s', '%s', '%s', '%s', '%s', '%s')",
                        advisors_FirstNameTextField.getText(), advisors_SecondNameTextField.getText(), advisors_PhoneNumberField.getText(), advisors_EmailTextField.getText(), generatedPassword, ATS.getUser()
                ));

                Alert b = new Alert(Alert.AlertType.INFORMATION, String.format("%s %s has been registered.\nTheir temporary password is: '%s'",
                        advisors_FirstNameTextField.getText(), advisors_SecondNameTextField.getText(), generatedPassword), ButtonType.OK);
                b.setTitle("Success");
                b.show();

                advisors_AdvisorIDComboBox.getItems().clear();
                advisors_AdvisorIDComboBox.getItems().addAll(db.stringStatement("SELECT advisorID FROM TravelAdvisor"));

                db.CloseConnection();
                advisors_FirstNameTextField.setText("");
                advisors_SecondNameTextField.setText("");
                advisors_PhoneNumberField.setText("");
                advisors_EmailTextField.setText("");

            } else {
                Alert b = new Alert(Alert.AlertType.ERROR, "Phone number must be a numeric value!");
                b.show();
            }


            LoadAdvisorTableView(e);
        } else {
            Alert c = new Alert(Alert.AlertType.ERROR, "Fill all fields please!");
            c.show();

        }
    }

    public void LoadAdvisorTableView(ActionEvent e){
        AdvisorList advisorList = new AdvisorList();
        ObservableList<Advisor> list = FXCollections.observableArrayList();

        for (int i = 0; i < advisorList.getAdvisorID().size(); i++) {
            list.add(new Advisor((advisorList.getAdvisorID().get(i)),
                    (advisorList.getFirstName().get(i)),
                    (advisorList.getSecondName().get(i)),
                    (advisorList.getPhoneNumber().get(i)),
                    (advisorList.getEmail().get(i))
            ));
        }

        advisors_IDColumn.setCellValueFactory(new PropertyValueFactory<>("advisorID"));
        advisors_firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        advisors_SecondNameColumn.setCellValueFactory(new PropertyValueFactory<>("secondName"));
        advisors_PhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        advisors_EmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));


        advisors_TableView.setItems(list);
    }

    public void resetCustomerEditBox() {
        customer_fieldToChangeComboBox.setValue("No Change");
        customer_changeAdvisorComboBox.setValue("No Change");
        customer_changeTextField.setText("");
    }

    public void ReloadCustomerList(ActionEvent e) {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();
        new Wobble(customer_refreshHyperLink).play();
        customerListLoad(db);
        db.CloseConnection();

    }

    public void customerListLoad(DatabaseConection db) {

        List<String> customerIDList = db.stringStatement("SELECT customerID FROM Customer");

        List<String> customerList = new ArrayList<>();
        String check = "";
        String checkpass = "";

        for (int i=0; i < customerIDList.size(); i++) {
            check = String.format("%s - %s %s", customerIDList.get(i), db.stringStatement("SELECT firstName FROM Customer WHERE customerID = "+customerIDList.get(i)).get(0), db.stringStatement("SELECT secondName FROM Customer WHERE customerID = "+customerIDList.get(i)).get(0));
            if (check.substring(0,4).matches(this.customerID)) {checkpass = check;}
            customerList.add(check);
        }

        customer_customerSearchComboBox.getItems().clear();
        customer_customerSearchComboBox.getItems().addAll(customerList);

        if (checkpass != ""){
            customer_customerSearchComboBox.setValue(checkpass);
        }

    }

    public void customerSelected(ActionEvent e) throws java.lang.reflect.InvocationTargetException{

        if (customer_customerSearchComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();
            this.customerID = customer_customerSearchComboBox.getValue().substring(0, 4);
            customer_IDLabel.setText("ID: " + customerID);
            customer_firstNameLabel.setText("First Name: " + db.stringStatement("SELECT firstName FROM Customer WHERE customerID = " + customerID).get(0));
            customer_secondNameLabel.setText("Second Name: " + db.stringStatement("SELECT secondName FROM Customer WHERE customerID = " + customerID).get(0));
            customer_aliasLabel.setText("Alias: " + db.stringStatement("SELECT customerAlias FROM Customer WHERE customerID = " + customerID).get(0));
            customer_advisorIDLabel.setText("Advisor ID: " + db.stringStatement("SELECT travelAdvisorID FROM Customer WHERE customerID = " + customerID).get(0));
            customer_emailLabel.setText("Email: " + db.stringStatement("SELECT email FROM Customer WHERE customerID = " + customerID).get(0));
            customer_pNumberLabel.setText("Number: " + db.stringStatement("SELECT phoneNo FROM Customer WHERE customerID = " + customerID).get(0));

            if (db.stringStatement("SELECT valuedCustomer FROM Customer WHERE customerID = " + customerID).get(0).matches("1")) {
                customer_valuedLabel.setText("Valued: Yes");
            } else {
                customer_valuedLabel.setText("Valued: No");
            }

            customer_fixedDiscountLabel.setText("Fixed Discount: " + db.stringStatement("SELECT fixedDiscount FROM Customer WHERE customerID = " + customerID).get(0) + "%");
            customer_flexibleDiscountLabel.setText("Flexible Discount: " + db.stringStatement("SELECT flexibleDiscount FROM Customer WHERE customerID = " + customerID).get(0) + "%");

            db.CloseConnection();
        }




    }

    public void CopyToClipBoard(ActionEvent e){
        Hyperlink objectTect = ((Hyperlink) e.getSource());
        String text = objectTect.getText();
        String testText;
        String textFinal = "";
        int a = text.length();



        testText = text.substring(0, 6);

        if ("Email:".matches(testText)) {textFinal = text.substring(7,a);}

        testText = text.substring(0, 7);
        if ("Number:".matches(testText)) {textFinal = text.substring(8,a);}


        StringSelection stringSelection = new StringSelection(textFinal);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void UpdateCustomerDetails(ActionEvent e) throws SQLException {
        if (customer_customerSearchComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();

            Alert b = new Alert(Alert.AlertType.NONE);
            a.setAlertType(Alert.AlertType.INFORMATION);
            a.setHeaderText("Success");
            a.setContentText("Information Updated");


            if (customer_changeTextField.getText() != "" && customer_fieldToChangeComboBox.getValue() != "No Change") {
                String stm = "";
                String update = customer_changeTextField.getText();
                switch ("" + customer_fieldToChangeComboBox.getValue()) {
                    case "First Name":
                        db.executeStatement("UPDATE Customer SET firstName = '" + update + "' WHERE customerID = " + customerID);
                        resetCustomerEditBox();
                        break;
                    case "Second Name":
                        db.executeStatement("UPDATE Customer SET secondName = '" + update + "' WHERE customerID = " + customerID);
                        resetCustomerEditBox();
                        break;
                    case "Alias":
                        db.executeStatement("UPDATE Customer SET customerAlias = '" + update + "' WHERE customerID = " + customerID);
                        resetCustomerEditBox();
                        break;
                    case "Email":
                        db.executeStatement("UPDATE Customer SET email = '" + update + "' WHERE customerID = " + customerID);
                        resetCustomerEditBox();
                        break;
                    case "Number":
                        try {
                            db.executeStatement("UPDATE Customer SET phoneNo = " + update + " WHERE customerID = " + customerID);
                            resetCustomerEditBox();
                        } catch (SQLException s) {
                            a.setAlertType(Alert.AlertType.ERROR);
                            a.setHeaderText("Information Not Updated");
                            a.setContentText("Numerical Values only or Number too long");
                            a.showAndWait();
                        }
                        break;
                    case "Fixed Discount":
                        try {
                            db.executeStatement("UPDATE Customer SET fixedDiscount = " + update + " WHERE customerID = " + customerID);
                            resetCustomerEditBox();
                        } catch (SQLException s) {
                            a.setAlertType(Alert.AlertType.ERROR);
                            a.setHeaderText("Information Not Updated");
                            a.setContentText("Numerical Values only or Number too long");
                            a.showAndWait();
                        }
                        break;
                    case "Flexible Discount":
                        try {
                            db.executeStatement("UPDATE Customer SET flexibleDiscount = " + update + " WHERE customerID = " + customerID);
                            resetCustomerEditBox();
                        } catch (SQLException s) {
                            a.setAlertType(Alert.AlertType.ERROR);
                            a.setHeaderText("Information Not Updated");
                            a.setContentText("Numerical Values only or Number too long");
                            a.showAndWait();
                        }
                        break;

                }

            }


            if (customer_changeAdvisorComboBox.getValue() != null && customer_changeAdvisorComboBox.getValue() != "No Change") {
                db.executeStatement("UPDATE Customer SET travelAdvisorID = '" + customer_changeAdvisorComboBox.getValue() + "' WHERE customerID = " + customerID);
                resetCustomerEditBox();
            }


            try {
                customerSelected(e);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
            db.CloseConnection();

        }
    }

    public void ToggleValuedCustomer() throws SQLException {
        if (customer_customerSearchComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();

            String customerID = customer_customerSearchComboBox.getValue().substring(0, 4);

            if (customer_valuedLabel.getText().substring((customer_valuedLabel.getText().length()-2)).matches("No")) {
                db.executeStatement("UPDATE Customer SET valuedCustomer = 1 WHERE customerID = "+customerID);
            } else {
                db.executeStatement("UPDATE Customer SET valuedCustomer = 0 WHERE customerID = "+customerID);
            }
            try {
            customerSelected(e);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void AssignBlankUpdate(ActionEvent e){
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();


        blank_BlankNumberComboBox.getItems().clear();
        blank_BlankNumberComboBox.setValue(null);


        if (blank_showUnassignedCheckBox.isSelected()) {
            if (blank_TypeAssignComboBox.getValue() == null || blank_TypeAssignComboBox.getValue() == "Any") {
                blank_BlankNumberComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID IS NULL"));
            } else {
                blank_BlankNumberComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID IS NULL AND blankType = "+blank_TypeAssignComboBox.getValue()));
            }
        } else {
            if (blank_TypeAssignComboBox.getValue() == null || blank_TypeAssignComboBox.getValue() == "Any") {
            blank_BlankNumberComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank"));
            } else {
                blank_BlankNumberComboBox.getItems().addAll(db.stringStatement("SELECT blankNumber FROM Blank WHERE blankType = "+blank_TypeAssignComboBox.getValue()));
            }
        }

        db.CloseConnection();
    }

    public void AssignBlank(ActionEvent e) throws SQLException {
        if (blank_TypeAssignComboBox.getValue() == null || blank_BlankNumberComboBox.getValue() == null || blank_AdvisorIDAssignComboBox.getValue() == null) {
            Alert b = new Alert(Alert.AlertType.NONE);
            b.setAlertType(Alert.AlertType.ERROR);
            b.setHeaderText("BLANK NOT ASSIGNED");
            b.setContentText("Please make sure Type, Blank Number and AdvisorID are all selected.");
            b.showAndWait();
        } else {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();
            String stm = "";

            if (blank_AdvisorIDAssignComboBox.getValue() == "UNASSIGN") {
                stm = String.format("UPDATE Blank SET TravelAdvisorID = NULL WHERE blankNumber = %s",
                        blank_BlankNumberComboBox.getValue());
            } else {
                stm = String.format("UPDATE Blank SET TravelAdvisorID = %s WHERE blankNumber = %s",
                        blank_AdvisorIDAssignComboBox.getValue(), blank_BlankNumberComboBox.getValue());
            }




            Alert b = new Alert(Alert.AlertType.NONE);
            b.setAlertType(Alert.AlertType.CONFIRMATION);
            b.setHeaderText("Confirm Change...");
            b.setContentText(String.format("You want to assign %s to advisor %s?", blank_BlankNumberComboBox.getValue(), blank_AdvisorIDAssignComboBox.getValue()));
            b.showAndWait();

            if (b.getResult() == ButtonType.OK) {
                db.executeStatement(stm);
                Alert c = new Alert(Alert.AlertType.NONE);
                c.setAlertType(Alert.AlertType.INFORMATION);
                c.setHeaderText("Success");
                c.setContentText(String.format("%s Assigned to %s", blank_BlankNumberComboBox.getValue(), blank_AdvisorIDAssignComboBox.getValue()));
                c.showAndWait();
            }

            db.CloseConnection();
        }
    }

    public void ResetFilter(ActionEvent e){
        blank_AdvisorIDFilterComboBox.setValue(null);
        blank_TypeFilterComboBox.setValue(null);
        BlankTableRefresh(e);
    }


    public void BlankTableRefresh(ActionEvent e) {

        BlankList blankList = new BlankList(blank_AdvisorIDFilterComboBox, blank_TypeFilterComboBox);
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

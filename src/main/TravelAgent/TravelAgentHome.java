package main.TravelAgent;

import animatefx.animation.Shake;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.*;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TravelAgentHome implements Initializable {

    // General use classes
    private SceneController sceneController = new SceneController();
    private UserCache user = new UserCache();
    private UsefulFunctions functions = new UsefulFunctions();
    private Alert a = new Alert(Alert.AlertType.NONE);
    private static final DecimalFormat decfor = new DecimalFormat("#,###.00");
    private static final DecimalFormat decfor2 = new DecimalFormat("0.00");

    // SALE TAB
    @FXML private ComboBox<String> payTypeComboBox;
    @FXML private ComboBox<String> currencyComboBox;
    @FXML private TextField forenameTextField;
    @FXML private TextField surenameTextField;
    @FXML private TextField phoneNumTextField;
    @FXML private TextField emailTextField;
    @FXML private ComboBox<String> blankComboBox;
    @FXML private Label welcomeLabel;
    @FXML private CheckBox saveCustomerCheckBox;
    private List<String> currentBlanks;
    private List<String> newblanks;
    private CurrencyConverter currencyConverter = new CurrencyConverter();
    private List<String> customersList = new ArrayList<String>();
    private List<String> customerCheckList = new ArrayList<String>();

        // SALE TAB -> Payment Box
        @FXML private Label totalLabel;
        @FXML private Label discountLabel;
        @FXML private Label taxLabel;
        @FXML private CheckBox taxCheckBox;
        @FXML private Label currencyLabel;
        @FXML private Button submitSaleBtn;
        @FXML private CheckBox payLaterCheckBox;
        private String discount = "0";
        private double discountPercentage = 0.0;
        private String payments[] = {"Card", "Cash"};
        private double priceUSD = 0.00;
        private double priceUSD_postTax = 0.00;
        private double priceUSDDiscount = 0.00;
        private String exchangeCode = null;
        private double exchangeRate = 1.0;
        private double localTax = 0.2;
        private double priceFinal = 0.0;
        private String customerAlias = "not changing";

    // REFUND TAB
    @FXML private ComboBox<String> ticketNumberComboBox;
    @FXML private Button refundButton;
    @FXML private Button refreshListBtn;
    private List<String> ticketNumbers = new ArrayList<String>();


        // REFUND TAB -> Ticket
        @FXML private Label refund_datePurchaseLabel;
        @FXML private Label refund_nameOnTicketLabel;
        @FXML private Label refund_blankNumberLabel;
        @FXML private Label refund_originLabel;
        @FXML private Label refund_destinationLabel;
        @FXML private Label refund_soldByLabel;
        @FXML private Label refund_priceLabel;
        @FXML private ComboBox originFlightComboBox;
        @FXML private ComboBox destinationFlightComboBox;



    // MY BLANKS TAB
    @FXML private TableView<Blank> blanksTableView;
    @FXML private TableColumn<Blank, String> blankColumn;
    @FXML private TableColumn<Blank, String> assignDateColumn;
    @FXML private ComboBox<String> customerSelectComboBox;

        // MY BLANKS TAB -> Filter Box
        @FXML private Label refreshMsgLabel;
        @FXML private ComboBox blankTypeFilterComboBox;

    // My Account
    @FXML private Label myAccount_MyIDLabel;
    @FXML private PasswordField myAccount_CurrentPassTextField;
    @FXML private PasswordField myAccount_NewPassTextField;
    @FXML private ComboBox myAccount_GenReportComboBox;



    @Override
    public void initialize(URL url, ResourceBundle rb){

        try {
            currencyConverter.Convert("GBP");
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        welcomeLabel.setText("Logged In:\n"+user.getTravelAdvisorName());
        ATS.generateCurrencies();

        currencyComboBox.getItems().addAll(functions.removeDuplicates((ATS.getCurrencies())));

        payTypeComboBox.getItems().addAll(payments);

        blankTypeFilterComboBox.getItems().add("Interline");
        blankTypeFilterComboBox.getItems().add("Domestic");

        myAccount_MyIDLabel.setText("My ID: "+ATS.getUser());

        myAccount_GenReportComboBox.getItems().addAll("Interline", "Domestic", "Both");


        loadCustomers();
        refundTicketOptionsLoad();
        try {
            updateFlights(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


        try {
            refreshMyBlanks(e);
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public void GenerateReport() throws Exception {
        if (myAccount_GenReportComboBox.getValue() != null) {
            GenerateReport generateReport = new GenerateReport("");
            generateReport.AdvisorReport(ATS.getUser(), "" + myAccount_GenReportComboBox.getValue());

            myAccount_GenReportComboBox.setValue(null);
        }
    }

    public void ChangePassword(ActionEvent e) throws SQLException {
        if (myAccount_CurrentPassTextField.getText() != "" && myAccount_NewPassTextField.getText() != "") {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();
            String cPass = (db.stringStatement("SELECT password FROM TravelAdvisor WHERE advisorID = '"+ATS.getUser()+"'")).get(0);
            if (myAccount_CurrentPassTextField.getText().matches(cPass)) {
                Alert b = new Alert(Alert.AlertType.CONFIRMATION, "");
                b.setHeaderText("Change Password?");
                b.showAndWait();

                if (b.getResult() == ButtonType.OK) {
                    db.executeStatement("UPDATE TravelAdvisor SET password = '" + myAccount_NewPassTextField.getText() + "' WHERE advisorID = '" + ATS.getUser() + "'");
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

    public void updateMoneyBox(ActionEvent e) throws IOException {

        if (currencyComboBox.getValue() != null) {
            this.exchangeCode = currencyComboBox.getValue();
            this.exchangeRate = currencyConverter.Convert(exchangeCode);
        }
        if (exchangeCode == null){currencyLabel.setText(String.format("(%s)", "USD")); exchangeCode = "USD";} else {currencyLabel.setText(String.format("(%s)", exchangeCode));}

        if (originFlightComboBox.getValue() != null && destinationFlightComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();
            currentBlanks = blankComboBox.getItems();
            List<String> blankCheck1 = (db.stringStatement("SELECT blankNumber FROM Payment"));
            List<String> blankCheck2 = (db.stringStatement("SELECT blankNumber FROM Sales"));
            String tempType = (db.stringStatement("SELECT blankType FROM Flights WHERE origin = '"+originFlightComboBox.getValue()+"' AND destination = '"+destinationFlightComboBox.getValue()+"';")).get(0);
            newblanks = db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID = '"+ ATS.getUser() +"' AND blankType = '" + tempType+"'");

            for (int i = 0; i < newblanks.size(); i++) {
                    if (blankCheck1.contains(newblanks.get(i)) || blankCheck2.contains(newblanks.get(i))) {
                        newblanks.remove(i);
                    }
            }

            for (int i = 0; i < currentBlanks.size(); i++) {
                if (blankCheck1.contains(currentBlanks.get(i)) || blankCheck2.contains(currentBlanks.get(i))) {
                    currentBlanks.remove(i);
                    blankComboBox.getItems().remove(i);
                }
            }


            blankComboBox.getItems().clear();
            blankComboBox.getItems().addAll(newblanks);

            for (int i = 0; i < blankComboBox.getItems().size(); i++) {
                if (blankCheck1.contains(blankComboBox.getItems().get(i)) || blankCheck2.contains(blankComboBox.getItems().get(i))) {
                    blankComboBox.getItems().remove(i);

                }
            }


            this.priceUSD = Double.valueOf((db.stringStatement("SELECT priceUSD FROM Flights WHERE origin = '"+ originFlightComboBox.getValue() +"' AND destination = '" + destinationFlightComboBox.getValue() + "'")).get(0));


            db.CloseConnection();



            if (!taxCheckBox.isSelected()) {
                localTax = 0;
                if (discount == "0") {this.priceUSD_postTax = priceUSD; discountLabel.setText(String.format("Discount: -%s (%s%s)", "0.00", 0, "%"));}
                else{
                    this.priceUSDDiscount = priceUSD * (1 - (Double.valueOf(discount)/100));
                    this.priceUSD_postTax = this.priceUSDDiscount;
                    discountLabel.setText(String.format("Discount: -%s (%s%s)", decfor.format(priceUSD*(Double.valueOf(discount)/100)), discount, "%"));
                }

                taxLabel.setText("VAT: 0.00 (0%)");

                if (currencyComboBox.getValue() == null) {
                    totalLabel.setText(String.format("Total: %s     ($%s)", decfor.format(priceUSDDiscount), decfor.format(priceUSDDiscount)));
                } else {
                    totalLabel.setText(String.format("Total: %s     ($%s)", decfor.format(priceUSD_postTax * exchangeRate), decfor.format(priceUSDDiscount)));
                }
                priceFinal = priceUSDDiscount;

            } else {
                localTax = 0.2;
                if (discount == "0") {this.priceUSD_postTax = priceUSD*(1+localTax); discountLabel.setText(String.format("Discount: -%s (%s%s)", "0.00", 0, "%"));}
                else{
                    this.priceUSDDiscount = priceUSD * (1 - (Double.valueOf(discount)/100));
                    this.priceUSD_postTax = this.priceUSDDiscount*(1+localTax);
                    discountLabel.setText(String.format("Discount: -%s (%s%s)", decfor.format(priceUSD*(Double.valueOf(discount)/100)), discount, "%"));
                }
                taxLabel.setText(String.format("VAT: %s (%s%s)", decfor.format(this.priceUSDDiscount*(localTax)), localTax*100, "%"));

                if (currencyComboBox.getValue() == null) {
                    totalLabel.setText(String.format("Total: %s     ($%s)", decfor.format(priceUSD_postTax), decfor.format(priceUSD_postTax)));
                } else {
                    totalLabel.setText(String.format("Total: %s     ($%s)", decfor.format(priceUSD_postTax * exchangeRate), decfor.format(priceUSD_postTax)));
                }

                priceFinal = priceUSD_postTax;
            }


        }
    }


    public void updateFlights(ActionEvent e) throws IOException {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        List<String> origins = db.stringStatement("SELECT origin FROM Flights");
        List<String> destinations = db.stringStatement("SELECT destination FROM Flights");
        List<String> type = db.stringStatement("SELECT blankType FROM Flights");

        List<String> flights = new ArrayList<String>();

        originFlightComboBox.valueProperty().set(null);
        originFlightComboBox.getItems().addAll(functions.removeDuplicates((ArrayList)origins));
        destinationFlightComboBox.getItems().addAll(functions.removeDuplicates((ArrayList)destinations));

        db.CloseConnection();
    }

    public void updateDestinations(ActionEvent e) throws IOException {
        if (originFlightComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();
            List<String> destinations = db.stringStatement("SELECT destination FROM Flights WHERE origin = '" + originFlightComboBox.getValue() + "'");
            destinationFlightComboBox.getItems().clear();
            destinationFlightComboBox.getItems().addAll(functions.removeDuplicates((ArrayList)destinations));
            destinationFlightComboBox.setPromptText("Destination");
            db.CloseConnection();
        }
    }


    public void loadCustomers() {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();
        this.customersList = db.stringStatement("SELECT customerAlias FROM Customer");
        this.customerCheckList = db.stringStatement("SELECT customerAlias FROM Customer");;
        this.customersList.add(0, "New Customer");
        customerSelectComboBox.getItems().addAll(this.customersList);
        customerSelectComboBox.setValue("New Customer");
        db.CloseConnection();
    }



    public void CustomerSelected(ActionEvent e) throws IOException {
        if (customerSelectComboBox.getValue() != "New Customer") {
            DatabaseConection db = new DatabaseConection();
            db.UserConnection();

            this.discount = db.stringStatement("SELECT fixedDiscount FROM Customer Where customerAlias = '"+customerSelectComboBox.getValue()+"'").get(0);
            this.discountPercentage = Double.valueOf(discount)/100;



            forenameTextField.setText((db.stringStatement("SELECT firstName FROM Customer Where customerAlias = '"+customerSelectComboBox.getValue()+"'").get(0)));
            surenameTextField.setText((db.stringStatement("SELECT secondName FROM Customer Where customerAlias = '"+customerSelectComboBox.getValue()+"'").get(0)));
            emailTextField.setText((db.stringStatement("SELECT email FROM Customer Where customerAlias = '"+customerSelectComboBox.getValue()+"'").get(0)));
            phoneNumTextField.setText((db.stringStatement("SELECT phoneNo FROM Customer Where customerAlias = '"+customerSelectComboBox.getValue()+"'").get(0)));
            discountLabel.setText(String.format("Discount: -0.00 (%s%s)", discount, "%"));
            updateMoneyBox(e);

            db.CloseConnection();
        } else {
            forenameTextField.setText("");

            surenameTextField.setText("");
            emailTextField.setText("");
            phoneNumTextField.setText("");
            discount = "0";
            discountPercentage = 0.0;
            priceUSD = 0.00;
            priceUSDDiscount = 0.00;
            exchangeRate = 1.0;
            updateFlights(e);
            discountLabel.setText("Discount: -0.00 (0%)");
            updateMoneyBox(e);
        }

        updateMoneyBox(e);
    }

    public void submitSale(ActionEvent e) throws SQLException {
        boolean paymentCorrect = false;
        if (forenameTextField.getText() != "" && surenameTextField.getText() != "" && phoneNumTextField.getText() != "" && emailTextField.getText() != "" && payTypeComboBox.getValue() != null && currencyComboBox.getValue() != null && blankComboBox.getValue() != null) {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();

            String errorMsg = "Payment not taken!";
            Alert b = new Alert(Alert.AlertType.CONFIRMATION);
            b.setHeaderText("Take Payment");
            b.setContentText(String.format("Confirm payment has been taken..."));

            b.showAndWait();
            ButtonType res = b.getResult();
            if (res != ButtonType.OK && !payLaterCheckBox.isSelected()) {paymentCorrect = false;}

            paymentCorrect = false;
            String cardDetails = "";
            String dueDate = "";
            String payDate = "";
            switch (payTypeComboBox.getValue()) {
                case "Card":
                    if (!payLaterCheckBox.isSelected()) {
                        cardDetails = "VISA 1234 1234 1234 1234";
                        payDate = "" + java.time.LocalDate.now();
                        dueDate = payDate;
                        paymentCorrect = true;
                        b.showAndWait();
                    } else {
                        if (saveCustomerCheckBox.isSelected()) {
                            cardDetails = "";
                            dueDate = "" + (java.time.LocalDate.now().plusDays(30));
                            payDate = null;
                            paymentCorrect = true;
                        } else {
                            paymentCorrect = false;


                        }
                    }

                    break;
                default:
                    cardDetails = "NULL";
                    payDate = "" + java.time.LocalDate.now();
                    dueDate = payDate;
                    paymentCorrect = true;
            }


            String type;
            switch ((blankComboBox.getValue()).substring(0, 4)) {
                case "444", "440", "420":
                    type = "Interline";
                    break;
                case "201", "101":
                    type = "Domestic";
            }

            String customerAlias;
            if (customerCheckList.contains(customerSelectComboBox.getValue())) {
                this.customerAlias = customerSelectComboBox.getValue();
            } else {
                this.customerAlias = "";
            }

            String managerID = db.stringStatement("SELECT managerID FROM TravelAdvisor WHERE advisorID =" + ATS.getUser()).get(0);
            String travelAgentID = db.stringStatement("SELECT travelagentID FROM OfficeManager WHERE managerID =" + managerID).get(0);
            String commision = db.stringStatement("SELECT commission FROM TravelAgent WHERE agentID =" + travelAgentID).get(0);


            System.out.println(paymentCorrect);

            this.priceUSD = Double.valueOf((db.stringStatement("SELECT priceUSD FROM Flights WHERE origin = '"+ originFlightComboBox.getValue() +"' AND destination = '" + destinationFlightComboBox.getValue() + "'")).get(0));
            this.priceUSD = this.priceUSD * (1 - (Double.valueOf(discount)/100));
            this.priceUSD = this.priceUSD*(localTax+1);

            System.out.println(this.customerAlias);
            if (paymentCorrect) {
                String stm_payment;
                if (payDate == null) {
                    stm_payment = String.format("INSERT INTO Payment (blankNumber, saleType, paymentMethod, dueDate, paidDate, status, cardDetails, localCurrencyAmount, amountinUSD, exchangeRate) VALUES ('%s','%s','%s','%s',NULL,'%s','%s','%s','%s','%s')",
                            blankComboBox.getValue(), "Interline", payTypeComboBox.getValue(), dueDate, "", cardDetails, decfor2.format(this.priceUSD * exchangeRate), decfor2.format(this.priceUSD), exchangeRate);
                } else {
                    stm_payment = String.format("INSERT INTO Payment (blankNumber, saleType, paymentMethod, dueDate, paidDate, status, cardDetails, localCurrencyAmount, amountinUSD, exchangeRate) VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
                            blankComboBox.getValue(), "Interline", payTypeComboBox.getValue(), dueDate, payDate, "", cardDetails, decfor2.format(this.priceUSD * exchangeRate), decfor2.format(this.priceUSD), exchangeRate);
                }
                db.executeStatement(stm_payment);

                long paymentID = (db.intStatement("SELECT paymentID FROM Payment WHERE blankNumber  ='" + blankComboBox.getValue() + "'")).get(0);

                String stm_sale = String.format("INSERT INTO Sales (paymentID, advisorID, Date, blankNumber, origin, destination, destLegs, localTax, OtherTax, currencyCode, commissionRate, CustomerAlias) VALUES ('%s', '%s', '%s','%s', '%s', '%s', '%s', '%s', '%s','%s', '%s', '%s')",
                        (int) paymentID, ATS.getUser(), dueDate, blankComboBox.getValue(), originFlightComboBox.getValue(), destinationFlightComboBox.getValue(), 0, localTax*100, 0, currencyComboBox.getValue(), commision, this.customerAlias);

                db.executeStatement(stm_sale);

                Alert x = new Alert(Alert.AlertType.INFORMATION);
                x.setHeaderText("Sale Completed");
                x.setContentText(String.format("Cleared. No Errors."));
                x.showAndWait();

                if (customerSelectComboBox.getValue() == "New Customer" && saveCustomerCheckBox.isSelected()) {
                    db.executeStatement(String.format("INSERT INTO Customer (firstName, secondName, email, phoneNo, travelAdvisorID, customerAlias, valuedCustomer, flexibleDiscount, fixedDiscount) VALUES ('%s','%s','%s','%s','%s', '%s','%s','%s','%s')",
                            forenameTextField.getText(), surenameTextField.getText(), emailTextField.getText(), phoneNumTextField.getText(), ATS.getUser(), (String.format("%s%s", forenameTextField.getText(), surenameTextField.getText())), 0, 0, 0));
                }

                refundTicketOptionsLoad();


            } else {
                Alert c = new Alert(Alert.AlertType.ERROR);
                c.setHeaderText("Sale Cancelled");

                    if (!saveCustomerCheckBox.isSelected()) {
                        c.setContentText(String.format("Missing: Pay Later is only available if customer details are saved"));
                    } else if (paymentCorrect == false) {
                        c.setContentText(String.format(String.format("Payment Error: %s", errorMsg)));
                    }
                    c.showAndWait();

            }



            db.CloseConnection();


        } else {
            Alert b = new Alert(Alert.AlertType.ERROR);
            b.setHeaderText("Missing Field(s)");
            b.setContentText(String.format("Missing: One or more entries"));
            b.showAndWait();
        }

    }






    public void refundTicketOptionsLoad() {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        ticketNumberComboBox.getItems().clear();
        ticketNumberComboBox.setPromptText("Ticket Number");
        String payID;
        List<String> refunds = db.stringStatement("SELECT paymentID FROM Refunds");
        ticketNumbers = db.stringStatement("SELECT ticketNo FROM Sales");
        List<String> ticketNumbers_postCheck = new ArrayList<>();
        for (int i = 0; i < ticketNumbers.size(); i++) {
            payID = db.stringStatement("SELECT paymentID FROM Sales WHERE ticketNo = "+ticketNumbers.get(i)).get(0);
            if (!refunds.contains(payID)) {
                ticketNumbers_postCheck.add(ticketNumbers.get(i));
            }
        }
        ticketNumberComboBox.getItems().addAll(ticketNumbers_postCheck);

        db.CloseConnection();
    }

    public void refundTicketLabelFill(ActionEvent e) throws IOException{
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();
        try {
            Long ID = db.intStatement("SELECT advisorID FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0);
            refund_datePurchaseLabel.setText("Date of Purchase: " + db.dateStringStatement("SELECT Date FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0));
            refund_blankNumberLabel.setText("Blank Number\n" + db.intStatement("SELECT blankNumber FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0));
            refund_originLabel.setText("Origin\n" + db.stringStatement("SELECT origin FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0));
            refund_destinationLabel.setText("Destination\n" + db.stringStatement("SELECT destination FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0));
            refund_soldByLabel.setText("Sold by: " + db.stringStatement("SELECT firstName FROM TravelAdvisor WHERE advisorID = '" + ID + "'").get(0) + " " + db.stringStatement("SELECT secondName FROM TravelAdvisor WHERE advisorID = '" + ID + "'").get(0));
            String payID = db.stringStatement("SELECT paymentID FROM Sales WHERE ticketNo = " + ticketNumberComboBox.getValue()).get(0);
            String price = db.stringStatement("SELECT amountinUSD FROM Payment WHERE paymentID = " + payID).get(0);
            refund_priceLabel.setText(String.format("(USD) Price: $%s", decfor.format(Double.valueOf(price))));
            System.out.println(db.stringStatement("SELECT CustomerAlias FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0));
            if (db.stringStatement("SELECT CustomerAlias FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0) == null) {
                refund_nameOnTicketLabel.setText("Customer: Casual Customer");
            } else {
                String alias = db.stringStatement("SELECT CustomerAlias FROM Sales WHERE ticketNo = '" + ticketNumberComboBox.getValue() + "'").get(0);
                System.out.println(db.stringStatement("SELECT firstName FROM Customer WHERE customerAlias = '" + alias + "'"));
                String fName = db.stringStatement("SELECT firstName FROM Customer WHERE customerAlias = '" + alias + "'").get(0);
                String sName = db.stringStatement("SELECT secondName FROM Customer WHERE customerAlias = '" + alias + "'").get(0);
                refund_nameOnTicketLabel.setText(String.format("Customer: %s %s", fName, sName));
            }
        } catch (IndexOutOfBoundsException t) {

        }
        db.CloseConnection();
    }

    public void refundSubmit(ActionEvent e) throws SQLException {
        if (ticketNumberComboBox.getValue() != null || ticketNumberComboBox.getValue() != "Ticket Number") {
            DatabaseConection db = new DatabaseConection();
            db.AdminConnection();

            String payID = db.stringStatement("SELECT paymentID FROM Sales WHERE ticketNo = "+ticketNumberComboBox.getValue()).get(0);
            String blankNumber = db.stringStatement("SELECT blankNumber FROM Sales WHERE ticketNo = "+ticketNumberComboBox.getValue()).get(0);
            String date = "" + java.time.LocalDate.now();

            db.executeStatement(String.format("INSERT INTO Refunds (paymentID, blankNumber, date, fullRefundGiven) VALUES ('%s', '%s', '%s', 0)", payID, blankNumber, date));


            Alert b = new Alert(Alert.AlertType.INFORMATION);
            b.setHeaderText("Refund Successful");
            b.setContentText(String.format("Ticket [%s] has been set as refunded", ticketNumberComboBox.getValue()));
            b.showAndWait();
            refundTicketOptionsLoad();

            db.CloseConnection();
        }
    }

    public void refreshMyBlanks(ActionEvent e) throws IOException{

        MyBlank myblanks = new MyBlank(""+blankTypeFilterComboBox.getValue());

        ObservableList<Blank> list = FXCollections.observableArrayList();




        for (int i = 0; i < myblanks.getBlankNumber().size(); i++) {

                list.add(new Blank((myblanks.getDateAssigned()).get(i),
                        (myblanks.getBlankNumber()).get(i)
                ));

        }

        blankColumn.setCellValueFactory(new PropertyValueFactory<>("blankNumber"));
        assignDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAssigned"));

        //}

        blanksTableView.setItems(list);

        new Shake(refreshMsgLabel).play();

    }

    ActionEvent e;


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

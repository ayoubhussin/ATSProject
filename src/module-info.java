module ATS {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires AnimateFX;
    //requires org.json.simple.parser.ParseException;
    requires json.simple;
    requires java.mail;
    requires com.google.gson;
    requires aspose.cells;
    requires itextpdf;


    opens main to javafx.fxml;
    opens main.LoginPage to javafx.fxml;
    opens main.AdminPage to javafx.fxml;
    opens main.OfficeManagerPage to javafx.fxml;
    opens main.TravelAgent to javafx.fxml;

    exports main;
    exports main.TravelAgent;
    exports main.AdminPage;
    exports main.OfficeManagerPage;
}
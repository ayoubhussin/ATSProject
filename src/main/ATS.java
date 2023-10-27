package main;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ATS extends Application{

    private static int user;

    private static ArrayList<String > currencies = new ArrayList<String>();

    public static void generateCurrencies() {
        currencies.addAll(Arrays.asList("AFN", "ALL", "DZD", "USD", "EUR", "AOA", "XCD", "XCD", "ARS", "AMD", "AWG", "AUD", "EUR", "AZN", "BSD", "BHD", "BDT", "BBD", "BYN", "EUR", "BZD", "XOF", "BMD", "BTN", "INR", "BOB", "USD", "BAM", "BWP", "NOK", "BRL", "USD", "BND", "BGN", "XOF", "BIF", "CVE", "KHR", "XAF", "CAD", "KYD", "XAF", "XAF", "CLF", "CLP", "CNY", "AUD", "AUD", "COP", "KMF", "CDF", "XAF", "NZD", "CRC", "HRK", "CUP", "ANG", "EUR", "CZK", "XOF", "DKK", "DJF", "XCD", "DOP", "USD", "EGP", "SVC", "USD", "XAF", "ERN", "EUR", "ETB", "EUR", "FKP", "DKK", "FJD", "EUR", "EUR", "EUR", "XPF", "EUR", "XAF", "GMD", "GEL", "EUR", "GHS", "GIP", "EUR", "DKK", "XCD", "EUR", "USD", "GTQ", "GBP", "GNF", "XOF", "GYD", "HTG", "USD", "AUD", "EUR", "HNL", "HKD", "HUF", "ISK", "INR", "IDR", "XDR", "IRR", "IQD", "EUR", "GBP", "ILS", "EUR", "JMD", "JPY", "GBP", "JOD", "KZT", "KES", "AUD", "KPW", "KRW", "KWD", "KGS", "LAK", "EUR", "LBP", "LSL", "ZAR", "LRD", "LYD", "CHF", "EUR", "EUR", "MOP", "MGA", "MWK", "MYR", "MVR", "XOF", "EUR", "USD", "EUR", "MRU", "MUR", "EUR", "XUA", "MXN", "MXV", "USD", "MDL", "EUR", "MNT", "EUR", "XCD", "MAD", "MZN", "MMK", "NAD", "ZAR", "AUD", "NPR", "EUR", "XPF", "NZD", "NIO", "XOF", "NGN", "NZD", "AUD", "USD", "NOK", "OMR", "PKR", "USD", "PAB", "USD", "PGK", "PYG", "PEN", "PHP", "NZD", "PLN", "EUR", "USD", "QAR", "MKD", "RON", "RUB", "RWF", "EUR", "EUR", "SHP", "XCD", "XCD", "EUR", "EUR", "XCD", "WST", "EUR", "STN", "SAR", "XOF", "RSD", "SCR", "SLE", "SGD", "ANG", "EUR", "EUR", "SBD", "SOS", "ZAR", "SSP", "EUR", "LKR", "SDG", "SRD", "NOK", "SZL", "SEK", "CHE", "CHF", "CHW", "SYP", "TWD", "TJS", "TZS", "THB", "USD", "XOF", "NZD", "TOP", "TTD", "TND", "TRY", "TMT", "USD", "AUD", "UGX", "UAH", "AED", "GBP", "USD", "USD", "UYU", "UZS", "VUV", "VEF", "VED", "VND", "USD", "USD", "XPF", "MAD", "YER", "ZMW", "ZWL", "EUR"));
    }

    public static ArrayList<String> getCurrencies(){
        return currencies;
    }



    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(ATS.class.getResource("LoginPage/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("AirVia");
        Image icon = new Image(getClass().getResource("icons/logo_highres.png").openStream());
        stage.getIcons().add(icon);
        stage.setResizable(false);
        scene.getStylesheets().add(getClass().getResource("styles/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void setUser(int a) {
        user = a;
    }

    public static int getUser() {
        return user;
    }

    public static void main(String args[]) throws Exception {

        GenerateReport test = new GenerateReport("");
        test.AdvisorReport(250, "Domestic");



        launch();

    }
}

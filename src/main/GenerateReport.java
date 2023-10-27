package main;

import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.scene.control.Alert;
import main.DatabaseConection;

import org.json.simple.JSONObject;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.gson.*;



public class GenerateReport {

    public GenerateReport(String reportSize) {

        switch (reportSize) {
            case "Single Advisor":
                break;

            case "Whole Agency":
                break;

            case "Stock Turnover":
                break;
        }

    }

    public HashMap AdvisorReport(int advisorID, String type) throws Exception {
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        // Main Hash Map
        HashMap<String, HashMap> report = new HashMap<String, HashMap>();
            // Branch 1
            HashMap<String, String> details = new HashMap<String, String>();
            HashMap<String, HashMap> interline = new HashMap<String, HashMap>();
            HashMap<String, HashMap> domestic = new HashMap<String, HashMap>();

                // Branch 2 | Inside types
                HashMap<String, HashMap> interline_blanks = new HashMap<String, HashMap>();
                HashMap<String, HashMap> domestic_blanks = new HashMap<String, HashMap>();

                    // Branch 3 | Inside types > blank
                    HashMap<String, String> interline_blankinfo = new HashMap<String, String>();
                    HashMap<String, String> domestic_blankinfo = new HashMap<String, String>();

        // Advisor Details
        details.put("Name",String.format("%s %s", db.stringStatement("SELECT firstName FROM TravelAdvisor WHERE advisorID ="+advisorID).get(0),
                db.stringStatement("SELECT secondName FROM TravelAdvisor WHERE advisorID ="+advisorID).get(0)));
        details.put("ID", ""+advisorID);



        // Types (Interline, Domestic or both)
        List<String> refundedBlanks = db.stringStatement("SELECT blankNumber FROM Refunds");

        if (type.matches("Interline") || type.matches("Both")) {
            List<String> interlineBlanks = db.stringStatement("SELECT blankNumber FROM Sales WHERE advisorID = "+advisorID);

            // Remove blanks which have been refunded
            for (int i = 0; i < interlineBlanks.size(); i++) {
                if (refundedBlanks.contains(interlineBlanks.get(i))) {
                    interlineBlanks.remove(i);
                }
            }

            for (int i = 0; i < interlineBlanks.size(); i++) {
                if (interlineBlanks.get(i).substring(0,4).contains("201")) {
                    interlineBlanks.remove(i);
                }
            }

            for (int i = 0; i < interlineBlanks.size(); i++) {
                if (interlineBlanks.get(i).substring(0,4).contains("101")) {
                    interlineBlanks.remove(i);
                }
            }




            for (int i = 0; i < interlineBlanks.size(); i++) {
                try {
                    interline_blankinfo = new HashMap<String, String>();

                    interline_blankinfo.clear();
                    interline_blankinfo.put("Ticket Number", db.stringStatement("SELECT ticketNo FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Origin", db.stringStatement("SELECT origin FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Destination", db.stringStatement("SELECT destination FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Local Tax", db.stringStatement("SELECT localTax FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Other Tax", db.stringStatement("SELECT OtherTax FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Form of payment", db.stringStatement("SELECT paymentMethod FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Due Date", db.stringStatement("SELECT dueDate FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Paid Date", db.stringStatement("SELECT paidDate FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("USD Paid", db.stringStatement("SELECT amountinUSD FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Local Currency Paid", db.stringStatement("SELECT localCurrencyAmount FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Currency Code", db.stringStatement("SELECT currencyCode FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0));
                    interline_blankinfo.put("Exchange Rate", db.stringStatement("SELECT exchangeRate FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0));

                    if (db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0) != null &&
                            db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0) != "") {
                        String customerAlias = db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + interlineBlanks.get(i)).get(0);
                        String customerName = String.format("%s %s", db.stringStatement("SELECT firstName FROM Customer WHERE CustomerAlias = '" + customerAlias + "'").get(0),
                                db.stringStatement("SELECT secondName FROM Customer WHERE CustomerAlias = '" + customerAlias + "'").get(0));
                        interline_blankinfo.put("Customer", customerName);
                        String valued = db.stringStatement("SELECT exchangeRate FROM Payment WHERE blankNumber = " + interlineBlanks.get(i)).get(0);
                        if (valued == "1") {
                            valued = "Yes";
                        } else {
                            valued = "No";
                        }
                        interline_blankinfo.put("Valued", valued);
                        interline_blankinfo.put("Discount Used", db.stringStatement("SELECT fixedDiscount FROM Customer WHERE customerAlias = '" + customerAlias + "'").get(0));
                    } else {
                        interline_blankinfo.put("Customer", "Casual Customer");
                        interline_blankinfo.put("Valued", "N/A");
                        interline_blankinfo.put("Discount Used", "0");
                    }
                    String managerID = db.stringStatement("SELECT managerID FROM TravelAdvisor WHERE advisorID = " + advisorID).get(0);
                    String agentID = db.stringStatement("SELECT travelagentID FROM OfficeManager WHERE managerID = " + managerID).get(0);
                    interline_blankinfo.put("Commission", db.stringStatement("SELECT commission FROM TravelAgent WHERE agentID = " + agentID).get(0));
                    interline_blankinfo.put("test", "" + i);

                    interline_blanks.put(interlineBlanks.get(i), interline_blankinfo);

                } catch (Exception e) {

                }

            }



            interline.put("Interline", interline_blanks);



        }

        if (type.matches("Domestic") || type.matches("Both")) {
            List<String> domesticBlanks = db.stringStatement("SELECT blankNumber FROM Sales WHERE advisorID = "+advisorID);

            // Remove blanks which have been refunded
            for (int i = 0; i < domesticBlanks.size(); i++) {
                if (refundedBlanks.contains(domesticBlanks.get(i))) {
                    domesticBlanks.remove(i);
                }
            }

            for (int i = 0; i < domesticBlanks.size(); i++) {
                if (domesticBlanks.get(i).substring(0,4).contains("444")) {
                    domesticBlanks.remove(i);
                }
            }

            for (int i = 0; i < domesticBlanks.size(); i++) {
                if (domesticBlanks.get(i).substring(0,4).contains("440")) {
                    domesticBlanks.remove(i);
                }
            }

            for (int i = 0; i < domesticBlanks.size(); i++) {
                if (domesticBlanks.get(i).substring(0,4).contains("420")) {
                    domesticBlanks.remove(i);
                }
            }




            for (int i = 0; i < domesticBlanks.size(); i++) {
                try {
                    domestic_blankinfo = new HashMap<String, String>();
                    domestic_blankinfo.clear();
                    domestic_blankinfo.put("Ticket Number", db.stringStatement("SELECT ticketNo FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Origin", db.stringStatement("SELECT origin FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Destination", db.stringStatement("SELECT destination FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Local Tax", db.stringStatement("SELECT localTax FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Other Tax", db.stringStatement("SELECT OtherTax FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Form of payment", db.stringStatement("SELECT paymentMethod FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Due Date", db.stringStatement("SELECT dueDate FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Paid Date", db.stringStatement("SELECT paidDate FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("USD Paid", db.stringStatement("SELECT amountinUSD FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Local Currency Paid", db.stringStatement("SELECT localCurrencyAmount FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Currency Code", db.stringStatement("SELECT currencyCode FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0));
                    domestic_blankinfo.put("Exchange Rate", db.stringStatement("SELECT exchangeRate FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0));

                    if (db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0) != null &&
                            db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0) != "") {
                        String customerAlias = db.stringStatement("SELECT CustomerAlias FROM Sales WHERE blankNumber = " + domesticBlanks.get(i)).get(0);
                        String customerName = String.format("%s %s", db.stringStatement("SELECT firstName FROM Customer WHERE CustomerAlias = '" + customerAlias + "'").get(0),
                                db.stringStatement("SELECT secondName FROM Customer WHERE CustomerAlias = '" + customerAlias + "'").get(0));
                        domestic_blankinfo.put("Customer", customerName);
                        String valued = db.stringStatement("SELECT exchangeRate FROM Payment WHERE blankNumber = " + domesticBlanks.get(i)).get(0);
                        if (valued == "1") {
                            valued = "Yes";
                        } else {
                            valued = "No";
                        }
                        domestic_blankinfo.put("Valued", valued);
                        domestic_blankinfo.put("Discount Used", db.stringStatement("SELECT fixedDiscount FROM Customer WHERE customerAlias = '" + customerAlias + "'").get(0));
                    } else {
                        domestic_blankinfo.put("Customer", "Casual Customer");
                        domestic_blankinfo.put("Valued", "N/A");
                        domestic_blankinfo.put("Discount Used", "0");
                    }
                    String managerID = db.stringStatement("SELECT managerID FROM TravelAdvisor WHERE advisorID = " + advisorID).get(0);
                    String agentID = db.stringStatement("SELECT travelagentID FROM OfficeManager WHERE managerID = " + managerID).get(0);
                    domestic_blankinfo.put("Commission", db.stringStatement("SELECT commission FROM TravelAgent WHERE agentID = " + agentID).get(0));
                    domestic_blankinfo.put("test", "" + i);

                    domestic_blanks.put(domesticBlanks.get(i), domestic_blankinfo);

                } catch (Exception e) {

                }

            }



            domestic.put("Domestic", domestic_blanks);

        }



        report.put("Interline", interline);
        report.put("Domestic", domestic);

        report.put("Advisor Details", details);


        JSONObject t = new JSONObject(report);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonElement je = JsonParser.parseString(String.valueOf(t));
        String prettyJsonString = gson.toJson(je);

        //FileWriter file = new FileWriter("output.json");
        //file.write(prettyJsonString);
        //file.close();

        FileWriter file2 = new FileWriter("output.txt");
        file2.write(prettyJsonString);
        file2.close();

        TxtToPDF();


        db.CloseConnection();
        return report;
    }

    public void TxtToPDF() throws IOException {
        FileInputStream fis = null;
        DataInputStream in = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        File sourceFile = new File("output.txt");
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        LocalDate date = LocalDate.now();
        String date2 = DATE_FORMATTER.format(date);
        File destFile = new File(String.format("personalReport_%s.pdf", date2));
        try {
            com.itextpdf.text.Document pdfDoc = new com.itextpdf.text.Document();
            PdfWriter write = PdfWriter.getInstance(pdfDoc, new FileOutputStream(destFile));
            pdfDoc.open();
            pdfDoc.setMarginMirroring(true);
            pdfDoc.setMargins(36, 72, 108, 180);
            pdfDoc.topMargin();

            BaseFont courier = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.EMBEDDED);
            Font myfont = new Font(courier);
            Font bold_font = new Font();

            bold_font.setStyle(Font.BOLD);
            bold_font.setSize(10);

            myfont.setStyle(Font.NORMAL);
            myfont.setSize(12);

            pdfDoc.add(new com.itextpdf.text.Paragraph("\n"));

            if(sourceFile.exists()) {
                fis = new FileInputStream(sourceFile);
                in = new DataInputStream(fis);
                isr = new InputStreamReader(in);
                br = new BufferedReader(isr);
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    com.itextpdf.text.Paragraph para = new com.itextpdf.text.Paragraph(strLine + "\n", myfont);
                    para.setAlignment(Element.ALIGN_JUSTIFIED);
                    pdfDoc.add(para);
                }

                pdfDoc.close();

            }

            Alert good = new Alert(Alert.AlertType.INFORMATION);
            good.setHeaderText("Created Report");
            good.setContentText("Success");
            good.show();

        } catch (Exception ex) {

        } finally {
            if (br != null) {
                br.close();
            } if (fis != null) {
                fis.close();
            } if (in != null) {
                in.close();
            } if (isr != null) {
                isr.close();
            }
        }
    }

}

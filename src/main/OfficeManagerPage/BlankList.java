package main.OfficeManagerPage;

import javafx.scene.control.ComboBox;
import main.*;
import main.DatabaseConection;
import java.util.ArrayList;
import java.util.List;

public class BlankList {

    private DatabaseConection db = new DatabaseConection();
    private List<String> blankNumber  = new ArrayList<String>();
    private List<String> advisorID = new ArrayList<String>();
    private String blankStm = "SELECT blankNumber FROM Blank";
    private String idStm= "SELECT TravelAdvisorID FROM Blank";



    public BlankList(ComboBox idFilter, ComboBox typeFilter) {
        db.UserConnection();

        if ((idFilter.getValue() == null || idFilter.getValue() == "") &&
                (typeFilter.getValue() == null || typeFilter.getValue() == "")) {
            this.blankStm = "SELECT blankNumber FROM Blank";
            this.idStm = "SELECT TravelAdvisorID FROM Blank";
        } else if ((typeFilter.getValue() == null || typeFilter.getValue() == "") && (idFilter.getValue() != null || idFilter.getValue() != "")) {
            if (idFilter.getValue() == "Unassigned") {
                this.blankStm = "SELECT blankNumber FROM Blank WHERE TravelAdvisorID IS NULL";
                this.idStm = "SELECT TravelAdvisorID FROM Blank WHERE TravelAdvisorID IS NULL";
            } else{
                this.blankStm = "SELECT blankNumber FROM Blank WHERE TravelAdvisorID = " + idFilter.getValue();
                this.idStm = "SELECT TravelAdvisorID FROM Blank WHERE TravelAdvisorID = " + idFilter.getValue();
            }
        } else if ((typeFilter.getValue() != null || typeFilter.getValue() != "") && (idFilter.getValue() == null || idFilter.getValue() == "")) {
            this.blankStm = "SELECT blankNumber FROM Blank WHERE blankType = "+typeFilter.getValue();
            this.idStm = "SELECT TravelAdvisorID FROM Blank WHERE blankType = "+typeFilter.getValue();
        }  else {
            if (idFilter.getValue() == "Unassigned") {
                this.blankStm = "SELECT blankNumber FROM Blank WHERE blankType = " + typeFilter.getValue() + " AND TravelAdvisorID IS NULL";
                this.idStm = "SELECT TravelAdvisorID FROM Blank WHERE blankType = " + typeFilter.getValue() + " AND TravelAdvisorID IS NULL";
            } else {
                this.blankStm = "SELECT blankNumber FROM Blank WHERE blankType = " + typeFilter.getValue() + " AND TravelAdvisorID = " + idFilter.getValue();
                this.idStm = "SELECT TravelAdvisorID FROM Blank WHERE blankType = " + typeFilter.getValue() + " AND TravelAdvisorID = " + idFilter.getValue();
            }
        }

        this.blankNumber = db.stringStatement(this.blankStm);
        this.advisorID = db.stringStatement(this.idStm);

        db.CloseConnection();
    }


    public List<String> getBlankNumber() {
        return blankNumber;
    }

    public List<String> getAdvisorID() {
        return advisorID;
    }


}

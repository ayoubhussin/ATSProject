package main;
import main.DatabaseConection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MyBlank {
    DatabaseConection db = new DatabaseConection();


    private List<String> blankNumber  = new ArrayList<String>();
    private List<String> dateAssigned = new ArrayList<String>();

    public MyBlank(String type) {
        this.db.UserConnection();

        List<String> blankCheck1 = (db.stringStatement("SELECT blankNumber FROM Payment"));
        List<String> blankCheck2 = (db.stringStatement("SELECT blankNumber FROM Sales"));

        switch(type) {
            case "Interline":
                this.blankNumber = db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"' AND (blankType = 444 OR blankType = 440 OR blankType = 420)");
                this.dateAssigned = db.dateStringStatement("SELECT AssignmentDate FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"' AND (blankType = 444 OR blankType = 440 OR blankType = 420)");
                break;
            case "Domestic":
                this.blankNumber = db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"' AND (blankType = 201 OR blankType = 101)");
                this.dateAssigned = db.dateStringStatement("SELECT AssignmentDate FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"' AND (blankType = 201 OR blankType = 101)");
                break;
            default:
                this.blankNumber = db.stringStatement("SELECT blankNumber FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"'");
                this.dateAssigned = db.dateStringStatement("SELECT AssignmentDate FROM Blank WHERE TravelAdvisorID = '"+ATS.getUser()+"'");
        }

        for (int i = 0; i < blankNumber.size(); i++) {
            if (blankCheck1.contains(blankNumber.get(i)) || blankCheck2.contains(blankNumber.get(i))) {
                blankNumber.remove(i);
                dateAssigned.remove(i);

            }
        }


    }

    public List<String> getBlankNumber() {
        return blankNumber;
    }

    public List<String> getDateAssigned() {
        return dateAssigned;
    }

}

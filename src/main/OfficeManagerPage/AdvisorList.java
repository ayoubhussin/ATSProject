package main.OfficeManagerPage;

import javafx.scene.control.ComboBox;
import main.ATS;
import main.DatabaseConection;

import java.util.ArrayList;
import java.util.List;

public class AdvisorList {

    private DatabaseConection db = new DatabaseConection();

    private List<String> advisorID = new ArrayList<String>();
    private List<String> firstName  = new ArrayList<String>();
    private List<String> secondName  = new ArrayList<String>();
    private List<String> phoneNumber  = new ArrayList<String>();
    private List<String> email  = new ArrayList<String>();

    public AdvisorList() {
        db.UserConnection();

        this.advisorID = db.stringStatement("SELECT advisorID FROM TravelAdvisor WHERE managerID ="+ ATS.getUser());
        this.firstName = db.stringStatement("SELECT firstName FROM TravelAdvisor WHERE managerID ="+ ATS.getUser());
        this.secondName = db.stringStatement("SELECT secondName FROM TravelAdvisor WHERE managerID ="+ ATS.getUser());
        this.phoneNumber = db.stringStatement("SELECT phone FROM TravelAdvisor WHERE managerID ="+ ATS.getUser());
        this.email = db.stringStatement("SELECT email FROM TravelAdvisor WHERE managerID ="+ ATS.getUser());

        db.CloseConnection();
    }

    public List<String> getAdvisorID() {
        return advisorID;
    }

    public List<String> getFirstName() {
        return firstName;
    }

    public List<String> getSecondName() {
        return secondName;
    }

    public List<String> getPhoneNumber() {
        return phoneNumber;
    }

    public List<String> getEmail() {
        return email;
    }


}

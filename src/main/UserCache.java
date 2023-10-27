package main;


import java.util.HashMap;
import java.util.List;

public class UserCache {

    private String role;

    private int ID = ATS.getUser();

    public UserCache() {

    }

    public void TravelAgent(int ID) {
        this.ID = ID;
    }

    public String getTravelAdvisorName(){
        DatabaseConection db = new DatabaseConection();
        db.UserConnection();

        String temp = String.format("%s %s", (db.stringStatement("SELECT firstName FROM TravelAdvisor WHERE advisorID = "+ATS.getUser())).get(0), (db.stringStatement("SELECT secondName FROM TravelAdvisor WHERE advisorID = "+ATS.getUser())).get(0));

        db.CloseConnection();
        return temp;
    }




}



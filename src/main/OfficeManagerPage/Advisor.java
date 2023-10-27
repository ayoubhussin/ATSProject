package main.OfficeManagerPage;

import java.util.ArrayList;
import java.util.List;

public class Advisor {

    private String advisorID;
    private String firstName;
    private String secondName;
    private String phoneNumber;
    private String email;

    public Advisor(String a, String b, String c, String d , String e) {
        advisorID = a;
        firstName = b;
        secondName = c;
        phoneNumber = d;
        email = e;


    }

    public String getAdvisorID() {
        return advisorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }



}

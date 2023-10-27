/**
 * Class used for connecting to the database, using MySql.
 * Contains the main private connection method and 3 public methods for controlling it.
 *
 */

package main;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConection {

    private String adminUsername = "in2018g06_a";
    private String adminPassword = "beh4Yv0f";

    private String userUsername = "in2018g06_a";
    private String userPassword = "beh4Yv0f";

    private Connection con;
    private String check = "None";

    public DatabaseConection() {

    }

    private Connection getConnection(String p, String u) {

        try {

            String url = "jdbc:mysql://smcse-stuproj00.city.ac.uk:3306/in2018g06";

            this.con = DriverManager.getConnection(url, p, u);

        }
        catch(Exception e) {
            System.out.print(e);
        }
        finally{
            return con;
        }
    }

    public void executeStatement(String a) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.execute(a);
    }

    public void executeDelete(String a) throws SQLException {
        Statement stmt = con.createStatement();
        stmt.executeUpdate(a);
    }



    public String LoginCheck(String u, String p, UserCache user){
        check = "None";

        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT count(1) FROM TravelAdvisor WHERE email = '"+u+"' AND password = '"+p+"'");

            while (rs2.next()) {
                if (rs2.getInt(1) == 1) {
                    Statement stmt3 = con.createStatement();
                    ResultSet rs3 = stmt3.executeQuery("SELECT advisorID FROM TravelAdvisor WHERE email = '"+u+"' AND password = '"+p+"'");
                    while (rs3.next()) {
                        user.TravelAgent(rs3.getInt(1));
                        ATS.setUser(rs3.getInt(1));
                    }
                    check = "TravelAdvisor";
                }
            }

        } catch(Exception e){
                System.out.print(e);

            }

        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT count(1) FROM Administrator WHERE secondName = '"+u+"' AND password = '"+p+"'");

            while (rs2.next()) {
                if (rs2.getInt(1) == 1) {
                    Statement stmt3 = con.createStatement();
                    ResultSet rs3 = stmt3.executeQuery("SELECT AdminID FROM Administrator WHERE secondName = '"+u+"' AND password = '"+p+"'");
                    while (rs3.next()) {
                        user.TravelAgent(rs3.getInt(1));
                        ATS.setUser(rs3.getInt(1));
                    }
                    check = "Admin";
                }
            }

        } catch(Exception e){
            System.out.print(e);

        }

        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery("SELECT count(1) FROM OfficeManager WHERE secondName = '"+u+"' AND password = '"+p+"'");

            while (rs2.next()) {
                if (rs2.getInt(1) == 1) {
                    Statement stmt3 = con.createStatement();
                    ResultSet rs3 = stmt3.executeQuery("SELECT managerID FROM OfficeManager WHERE secondName = '"+u+"' AND password = '"+p+"'");
                    while (rs3.next()) {
                        user.TravelAgent(rs3.getInt(1));
                        ATS.setUser(rs3.getInt(1));
                    }
                    check = "OfficeManager";
                }
            }

        } catch(Exception e){
            System.out.print(e);

        }

        return check;
    }

    public void AdminConnection() {
        getConnection(adminUsername, adminPassword);
    }

    public void UserConnection() {
        getConnection(userUsername, userPassword);
    }

    public List<Long> intStatement(String statement){
        List<Long> temp = new ArrayList<Long>();

        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(statement);

            while (rs2.next()) {
                if (true) {
                    temp.add(rs2.getLong(1));
                }
            }

            return temp;

        } catch(Exception e){
            System.out.print(e);

        }

        return temp;
    }

    public List<String> stringStatement(String statement){
        List<String> temp = new ArrayList<String>();
        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(statement);

            while (rs2.next()) {
                if (true) {

                    temp.add(rs2.getString(1));
                }
            }
            return temp;

        } catch(Exception e){
            System.out.print(e);

        }

        return temp;
    }

    public List<String> dateStringStatement(String statement) {
        List<String> temp = new ArrayList<String>();
        try {
            Statement stmt2 = con.createStatement();
            ResultSet rs2 = stmt2.executeQuery(statement);

            while (rs2.next()) {
                if (true) {
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
                    temp.add(df.format(rs2.getDate(1)));

                }
            }
            return temp;

        } catch(Exception e){
            System.out.print(e);

        }

        return temp;
    }

    public void CloseConnection() {
        try{
            con.close();
        }

        catch(SQLException e){
            System.out.print(e);
        }
    }

}

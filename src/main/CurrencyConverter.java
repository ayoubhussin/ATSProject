package main;



import java.io.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CurrencyConverter {

    public CurrencyConverter(){

    }

    public static String getJSONFromFIle(String filename) {
        String jsonText = "";

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

            String line;
            while ((line = bufferedReader.readLine()) != null){
                jsonText += line + "\n";
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonText;
    }

    public double Convert(String other) throws IOException {
        String strJson = getJSONFromFIle("src/main/data/currencies.json");

        try {
            JSONParser parser = new JSONParser();
            Object object = parser.parse(strJson);
            JSONObject mainJsonObject = (JSONObject) object;

            String test = (String) mainJsonObject.get(other);
            System.out.println("Exchange Rate: " + test);
            return Double.valueOf(test);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return 1;
    }
}

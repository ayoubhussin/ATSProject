package main;

import java.util.ArrayList;
import java.util.List;

public class UsefulFunctions {



    public UsefulFunctions() {

    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> a){
        ArrayList<T> b = new ArrayList<T>();


        for (T element : a) {
            if (!b.contains(element)) {
                b.add(element);
            }
        }

        return b;
    }
}

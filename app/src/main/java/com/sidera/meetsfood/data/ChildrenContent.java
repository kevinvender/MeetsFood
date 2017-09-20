package com.sidera.meetsfood.data;

import android.util.Log;

import com.sidera.meetsfood.data.Child;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ChildrenContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<Child> CHILDREN = new ArrayList<Child>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, Child> ITEM_MAP = new HashMap<String, Child>();

    static {
        // Add 3 sample items.
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            ArrayList<String> intolerances = new ArrayList<String>();
            intolerances.add("Vegano");
            intolerances.add("Diabetico");
            ArrayList<Tariffa> tariffe = new ArrayList<Tariffa>();
            tariffe.add(new Tariffa("Mensa",5.00, format.parse("11/09/2015"), format.parse("11/09/2015")));
            tariffe.add(new Tariffa("Trasporto",3.00, format.parse("11/09/2015"), format.parse("11/09/2015")));
            tariffe.add(new Tariffa("Salagiochi",15.00, format.parse("11/09/2015"), format.parse("11/09/2015")));
            addItem(new Child(1, "Sara", "Rossi", "STNCST84L02L378E", 50.00, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances, tariffe));
            addItem(new Child(2, "Paolo", "Rossi", "STNCST84L02L378E", 25.43, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(3, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(4, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(5, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(6, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(7, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(8, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(9, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
            addItem(new Child(10, "Mario", "Rossi", "STNCST84L02L378E", 33.22, "Liceo Galileo Galilei", 2, "2015/2016", format.parse("11/09/2015"), intolerances,tariffe));
        }catch(Exception e){
            Log.e("",e.getMessage());
        }
    }

    private static void addItem(Child child) {
        CHILDREN.add(child);
        ITEM_MAP.put(String.valueOf(child.getID()), child);
    }
}

package com.example.hjd.aquasift.Misc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Locale;


public class DbHelper extends SQLiteOpenHelper{


    public static final String TABLE_NAME = "AquaSiftTable";

    public static final String COL_ENTRY_ID = "entryId";
    public static final String COL_DATE = "date";
    public static final String COL_LAT = "lat";
    public static final String COL_LONG = "long";
    public static final String COL_TEST_TYPE = "testType";
    public static final String COL_PEAK_VALUES = "peakValues";
    public static final String COL_CONCENTRATION = "concentration";
    //public static final String COL_GRAPH_DATA = "graphData";

    public static final String DATABASE_NAME = "AquaSiftDatabase";
    public static final int DATABASE_VERSION = 16;



    private static final String CMD_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_DATE  + " TEXT, " +
            COL_LAT + " TEXT, " +
            COL_LONG + " TEXT, " +
            COL_TEST_TYPE + " TEXT, " +
            COL_PEAK_VALUES + " TEXT, " +
            COL_CONCENTRATION + " TEXT" +
                    ");";

    private static final String CMD_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;



    public DbHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CMD_CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL(CMD_DROP_TABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int old_version, int new_version) {
        db.execSQL(CMD_DROP_TABLE);
        onCreate(db);
    }

    public static String packGraphData(ArrayList<ArrayList<DataPoint>> graphedData) {
        //method to convert data list into a string for storage in database
        String stringedGraphData = "[";
        for (int i=0; i < graphedData.size(); i++) {
            stringedGraphData += "[";
            ArrayList<DataPoint> activeList = graphedData.get(i);
            for (int j=0; j < activeList.size(); j++) {
                DataPoint currentDataPoint = activeList.get(j);
                double x = currentDataPoint.getX();
                double y = currentDataPoint.getY();
                stringedGraphData += "(";
                stringedGraphData += String.format(Locale.US, "%4.2f", x);
                stringedGraphData += ",";
                stringedGraphData += String.format(Locale.US, "%4.2f", x);
                stringedGraphData += ")";
            }
            stringedGraphData += "]";
        }
        stringedGraphData += "]";

        return stringedGraphData;
    }

    public static ArrayList<ArrayList<DataPoint>> unpackGraphData(String stringedGraphData) {
        ArrayList<ArrayList<DataPoint>> toReturn = new ArrayList<>();

        if (stringedGraphData.charAt(0) != '[') {
            Log.d("DEBUGGING", "Not a valid Array String");
            return null;
        }

        int i=1;
        ArrayList<DataPoint> currentList = null;
        while (i < stringedGraphData.length()) {
            if (stringedGraphData.charAt(i) == '[') {
                currentList = new ArrayList<>();
                i++;
                continue;
            }
            if (stringedGraphData.charAt(i) == ']') {
                toReturn.add(currentList);
                i++;
                continue;
            }
            if (stringedGraphData.charAt(i) == '(') {
                //begin pair
                double x = Double.parseDouble(stringedGraphData.substring(i+1, i+7));
                double y = Double.parseDouble(stringedGraphData.substring(i+9, i+15));
                DataPoint dataPoint = new DataPoint(x, y);
                currentList.add(dataPoint);
                i += 16;
                if (stringedGraphData.charAt(i) != ')') {
                    //TODO Throw Exception, can't load graph
                    Log.d("DEBUGGING", "DataPoint not terminated properly");
                } else {
                    i += 1;
                }
            }
            if (stringedGraphData.charAt(i) == ',') {
                i += 1;
            }
        }

        return toReturn;
    }

}

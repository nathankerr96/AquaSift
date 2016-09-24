package com.example.hjd.aquasift.Misc;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DbHelper extends SQLiteOpenHelper{


    public static final String TABLE_NAME = "AquaSiftTable";

    public static final String COL_ENTRY_ID = "entry";
    public static final String COL_DATE = "date";
    public static final String COL_USER_ID = "user_id";
    public static final String COL_RAW_DATA = "raw_data";
    public static final String COL_TEST_TYPE = "test_type";

    public static final String DATABASE_NAME = "AquaSiftDatabase";
    public static final int DATABASE_VERSION = 11;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String CMD_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
            COL_DATE + TEXT_TYPE + COMMA_SEP +
            COL_USER_ID + TEXT_TYPE + COMMA_SEP +
            COL_RAW_DATA + TEXT_TYPE + COMMA_SEP +
            COL_TEST_TYPE + TEXT_TYPE + ");";

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

}

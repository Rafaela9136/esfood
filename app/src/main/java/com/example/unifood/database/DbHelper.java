package com.example.unifood.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.unifood.database.UserContract.UserEntry;

/**
 * Created by root on 19/01/17.
 */

public class DbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "unifood.db";
        private static final int DATABASE_VERSION = 3;

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " +
              UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                UserEntry.COLUMN_FIRST_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_LAST_NAME + " TEXT NOT NULL, " +
                UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL, "+
                UserEntry.COLUMN_UNIVERSITY + " INTEGER NOT NULL, "+
                UserEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                +"); " ;
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);


    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}

package com.example.coyotefree;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

public class DBHandler2 extends SQLiteOpenHelper {
    public static Context context;
    private static final String DB_NAME = "coursedb2";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "restoreHistory";

    private static final String ID_COL = "id";
    private static final String FILE_NAME = "filename";
    private static final String DATE_COL = "created_at";

    public DBHandler2(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query3 = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + FILE_NAME + " TEXT, "
                + DATE_COL + " TEXT DEFAULT (datetime('now', 'localtime'))"
                + ")";
        db.execSQL(query3);
    }

    public void resetTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.execSQL("VACUUM"); // Resets auto-increment counter
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertMultipleRecords(ArrayList<String> fileNames, ArrayList<String> dateTimes) {
        // Get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        // Begin transaction for efficiency
        db.beginTransaction();

        try {
            // SQL insert statement
            String sql = "INSERT INTO " + TABLE_NAME + " (" + FILE_NAME + ", " + DATE_COL + ") VALUES (?, ?)";
            SQLiteStatement stmt = db.compileStatement(sql);

            // Loop through the lists and insert each row
            for (int i = 0; i < fileNames.size(); i++) {
                stmt.clearBindings(); // Clear previous bindings

                stmt.bindString(1, fileNames.get(i));  // Bind filename

                if (i < dateTimes.size()) {
                    stmt.bindString(2, dateTimes.get(i));  // Bind date-time
                } else {
                    stmt.bindNull(2); // If no date-time provided, insert NULL
                }

                stmt.executeInsert();
            }

            // Mark transaction as successful
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // End transaction and close database
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<String> getFileNamesWithDates() {
        // Initialize an ArrayList to store results
        ArrayList<String> resultList = new ArrayList<>();

        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to select DATE_COL and FILE_NAME
        String query = "SELECT " + FILE_NAME + ", " + DATE_COL + " FROM " + TABLE_NAME;

        // Execute query
        Cursor cursor = db.rawQuery(query, null);

        // Check if there are rows
        if (cursor.moveToFirst()) {
            do {
                // Get FILE_NAME and DATE_COL
                String fileName = cursor.getString(0);
                String dateTime = cursor.getString(1);

                // Combine them into a single string
                resultList.add(dateTime + " | " + fileName); // Format: "filename | date-time"
            } while (cursor.moveToNext());
        }

        // Close cursor and database
        cursor.close();
        db.close();

        // Return the list
        return resultList;
    }



}

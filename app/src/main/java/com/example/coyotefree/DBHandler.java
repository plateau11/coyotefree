package com.example.coyotefree;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.io.File;
import java.util.ArrayList;
/**
 * DBHandler class is used for all database operations in coyote project
 */
public class DBHandler extends SQLiteOpenHelper {
    public static Context context;
    // creating a constant variables for our database.
    // below variable is for our database name.
    public static String firstValue;
    private static final String DB_NAME = "coursedb";
    // below int is our database version
    private static final int DB_VERSION = 1;
    // below variable is for our table name.
    private static final String TABLE_NAME = "mycourses2";

    // below variable is for our id column.
    private static final String ID_COL = "id";

    // below variable is for our course name column
    private static final String NAME_COL = "name";

    // below variable id for our course duration column.
    private static final String DURATION_COL = "duration";

    // below variable for our course description column.
    private static final String DESCRIPTION_COL = "description";

    // below variable is for our course tracks column.
    private static final String TRACKS_COL = "tracks";

    private static final String FILE_NAME = "filename";

    private static final String ORG_FILE_NAME = "org_filename";

    // creating a constructor for our database handler.
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // below method is for creating a database by running a sqlite query
    @Override
    public void onCreate(SQLiteDatabase db) {
        //context = context.getApplicationContext();
        // on below line we are creating
        // an sqlite query and we are
        // setting our column names
        // along with their data types.
        String query2 = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY, "
                + FILE_NAME + " TEXT, "  // Add a comma here
                + ORG_FILE_NAME + " TEXT"  // End without a comma
                + ")";
        // at last we are calling a exec sql
        // method to execute above sql query
        db.execSQL(query2);
    }
    // this method is use to add new course to our sqlite database.
    public void addNewCourse(String filename, String orgFileName) {
        // on below line we are creating a variable for
        // our sqlite database and calling writable method
        // as we are writing data in our database.
        SQLiteDatabase db = this.getWritableDatabase();
        // on below line we are creating a
        // variable for content values.
        ContentValues values = new ContentValues();
        // Fetch the maximum ID currently in the table
        String query = "SELECT MAX(" + ID_COL + ") FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        long newId = 1; // Default to 1 if no rows exist
        if (cursor != null && cursor.moveToFirst()) {
            // Get the maximum ID and increment by 1
            newId = cursor.getLong(0) + 1;
        }
        // Close the cursor to avoid memory leaks
        if (cursor != null) {
            cursor.close();
        }
        // Now, manually set the ID value
        values.put(ID_COL, newId);  // Insert the manually managed ID
        values.put(FILE_NAME, filename);
        values.put(ORG_FILE_NAME, orgFileName);
        // After adding all values, we are passing content values to our table.
        db.insert(TABLE_NAME, null, values);
        // at last, we are closing our database after adding data.
        db.close();
    }
    public ArrayList<String> readCourses() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();
        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // on below line we are creating a new array list.
        ArrayList<String> courseModalArrayList = new ArrayList<>();
        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new String(cursorCourses.getString(1)));

            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        db.close();
        return courseModalArrayList;
    }
    public ArrayList<String> readOriginalFileLocation() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();
        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // on below line we are creating a new array list.
        ArrayList<String> courseModalArrayList = new ArrayList<>();
        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new String(cursorCourses.getString(2)));

            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        db.close();
        return courseModalArrayList;
    }

    public ArrayList<Integer> readId() {
        SQLiteDatabase db = null;
        Cursor cursorCourses = null;
        ArrayList<Integer> courseModalArrayList = new ArrayList<>();
        try {
            // Open the database for reading
            db = this.getReadableDatabase();
            // Query to select all IDs from the table
            String query = "SELECT * FROM " + TABLE_NAME; // Replace 'id_column' with your actual column name
            cursorCourses = db.rawQuery(query, null);
            // Check if cursor is not empty and move to first position
            if (cursorCourses != null && cursorCourses.moveToFirst()) {
                do {
                    // Add the ID to the list (assuming ID is in the first column)
                    courseModalArrayList.add(cursorCourses.getInt(0));
                } while (cursorCourses.moveToNext());
            }
        } catch (Exception e) {
            // Handle any errors (e.g., database issues)
            e.printStackTrace();
        } finally {
            // Ensure cursor and database are closed in finally block to prevent leaks
            if (cursorCourses != null) {
                cursorCourses.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return courseModalArrayList;
    }
    public ArrayList<String> readOrgFilename() {
        // on below line we are creating a
        // database for reading our database.
        SQLiteDatabase db = this.getReadableDatabase();
        // on below line we are creating a cursor with query to read data from database.
        Cursor cursorCourses = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        // on below line we are creating a new array list.
        ArrayList<String> courseModalArrayList = new ArrayList<>();
        // moving our cursor to first position.
        if (cursorCourses.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                courseModalArrayList.add(new String(cursorCourses.getString(2)));

            } while (cursorCourses.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursorCourses.close();
        db.close();
        return courseModalArrayList;
    }
    public boolean deleteRowById(int id) throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + ID_COL + " = ?";
        cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            String firstColumnValue = cursor.getString(1);
            //firstValue = firstColumnValue;
            //Toast.makeText(context, "value: "+ firstColumnValue, Toast.LENGTH_SHORT).show();
            File file = new File(firstColumnValue);
            file.delete();  // Get the value of the first column
        }
        // Specify the "WHERE" condition to match the row by ID_COL
        String whereClause = ID_COL + " = ?";
        String[] whereArgs = { String.valueOf(id) };  // The ID value to delete
        // Delete the row from the table
        int rowsDeleted = db.delete(TABLE_NAME, whereClause, whereArgs);
        db.close();
        if (rowsDeleted>0)
            return true;
        else
            return false;
    }

    /**
     * Data wiping code for forgot pin in main activity
     * @return
     * @throws Exception
     */
    public boolean deleteAllRowsAndProcessFirstColumn() throws Exception {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        try {
            // Query all rows
            cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

            // Check if there are rows to delete
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Get the value from the first column
                    String firstColumnValue = cursor.getString(1); // Assuming the first column is at index 0
                    // Process the value (your additional purpose here)
                    //deletes each files in app space based on string value
                    File file = new File(firstColumnValue);
                    file.delete();
                    // Get the ID of the current row (assuming ID_COL is the second column)
                    int id = cursor.getInt(cursor.getColumnIndex(ID_COL));

                    // Delete the row by ID
                    String whereClause = ID_COL + " = ?";
                    String[] whereArgs = { String.valueOf(id) };
                    db.delete(TABLE_NAME, whereClause, whereArgs);

                } while (cursor.moveToNext()); // Move to the next row
            }

            // Return true since the process completed
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        } finally {
            // Close the cursor and database
            if (cursor != null) cursor.close();
            db.close();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public int getRowCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        // SQL query to count the number of rows in the table
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME;
        // Execute the query and get the result
        Cursor cursor = db.rawQuery(query, null);
        int rowCount = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // The first column (0) contains the count result
                rowCount = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close(); // Always close the database
        return rowCount; // Return the number of rows
    }
    @SuppressLint("Range")
    public int getFirstId() {
        SQLiteDatabase db = this.getReadableDatabase();
        // SQL query to get the smallest ID (first inserted row)
        String query = "SELECT " + ID_COL + " FROM " + TABLE_NAME + " ORDER BY " + ID_COL + " ASC LIMIT 1";
        // Execute the query
        Cursor cursor = db.rawQuery(query, null);
        int firstId = -1; // Default value if no rows are found
        // If there is a row, retrieve the ID
        if (cursor != null && cursor.moveToFirst()) {
            firstId = cursor.getInt(cursor.getColumnIndex(ID_COL));
            cursor.close();
        }
        db.close();
        return firstId; // Return the first ID or -1 if the table is empty
    }

    public String readOrgFilename2(int rowId) {
        // Get readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Query to select original filename where ID matches
        Cursor cursor = db.rawQuery("SELECT " + ORG_FILE_NAME + " FROM " + TABLE_NAME + " WHERE " + ID_COL + " = ?",
                new String[]{String.valueOf(rowId)});

        // Variable to store the result
        String originalFileName = null;

        // Check if data exists
        if (cursor.moveToFirst()) {
            originalFileName = cursor.getString(0); // First column in result set
        }

        // Close cursor and database
        cursor.close();
        db.close();

        return originalFileName; // Return the original filename (or null if not found)
    }
}

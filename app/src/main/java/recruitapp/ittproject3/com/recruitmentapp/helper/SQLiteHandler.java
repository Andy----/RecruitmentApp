package recruitapp.ittproject3.com.recruitmentapp.helper;

/**
 * Created by Andrew on 07/04/2015.
 */

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login table name
    private static final String TABLE_LOGIN = "login";

    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CITY = "city";
    private static final String KEY_APP_ID = "app_id";
    private static final String KEY_CV_PATH = "cvFilePath";
    private static final String KEY_PROFILE_IMAGE_PATH = "profileImage";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FIRST_NAME + " TEXT,"
                + KEY_LAST_NAME + " TEXT," + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_CITY + " TEXT," + KEY_APP_ID + " INTEGER,"
                + KEY_CV_PATH + " TEXT" + KEY_PROFILE_IMAGE_PATH + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(Long app_id, String firstName, String lastName, String email, String city, String cvFilePath, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_APP_ID, app_id);
        values.put(KEY_FIRST_NAME, firstName);
        values.put(KEY_LAST_NAME, lastName);
        values.put(KEY_EMAIL, email);
        values.put(KEY_CITY, city);
        values.put(KEY_CV_PATH, cvFilePath);
        values.put(KEY_PROFILE_IMAGE_PATH, profileImage);

        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("app_id" ,cursor.getString(1));
            user.put("firstName", cursor.getString(2));
            user.put("lastName",cursor.getString(3));
            user.put("email" , cursor.getString(4));
            user.put("city" ,cursor.getString(5));
            user.put("cvFilePath" ,cursor.getString(6));
            user.put("profileImage" ,cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
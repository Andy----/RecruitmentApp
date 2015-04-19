package recruitapp.ittproject3.com.recruitmentapp.helper;

/**
 * Created by Andrew on 07/04/2015.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import recruitapp.ittproject3.com.recruitmentapp.JobApplication;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "jobswift_db";



    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE user(app_id LONG PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, city VARCHAR, cv_filePath VARCHAR, profile_image_path VARCHAR);";
        String CREATE_JOB_APPLICATION_TABLE = "CREATE TABLE jobapplication (app_id LONG PRIMARY KEY, job_id LONG, job_title VARCHAR, job_description TEXT, job_location VARCHAR, status VARCHAR);";
        String CREATE_QUESTION_TABLE = "CREATE TABLE questiontable (question_id LONG PRIMARY KEY, question VARCHAR, job_id LONG, FOREIGN KEY(job_id) REFERENCES joblisting(job_id));";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_JOB_APPLICATION_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS login");

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(Long app_id, String firstName, String lastName, String email, String city, String cvFilePath, String profileImage) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("app_id", app_id);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("city", city);
        values.put("cv_filePath", cvFilePath);
        values.put("profile_image_path", profileImage);

        // Inserting Row
        db.insert("user", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + email );
    }


    public void addJobApplciation (Long app_id, Long job_id, String job_title, String job_description, String job_location, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("app_id", app_id);
        values.put("job_id", job_id);
        values.put("job_title", job_title);
        values.put("job_description", job_description);
        values.put("job_location", job_location);
        values.put("status", status);

        // Inserting Row
        db.insert("jobapplication", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New job application inserted into sqlite: " + app_id );
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM user";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("first_name", cursor.getString(1));
            user.put("last_name",cursor.getString(2));
            user.put("email" , cursor.getString(3));
            user.put("city" ,cursor.getString(4));
            user.put("cv_filePath" ,cursor.getString(5));
            user.put("profile_image_path" ,cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public void updateUserDetails(Map<String, String> user) {

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

        String email="";
        for (Map.Entry<String, String> entry : user.entrySet()){
            if(entry.getKey().equals("city")){
                values.put("city", entry.getValue());
            }
            if(entry.getKey().equals("email")){
                values.put("email", entry.getValue());
                email =entry.getValue();
            }
            if(entry.getKey().equals("first_name")){
                values.put("first_name", entry.getValue());
            }
            if(entry.getKey().equals("last_name")){
                values.put("last_name", entry.getValue());
            }
            if(entry.getKey().equals("profile_image_path")){
                values.put("profile_image_path", entry.getValue());
            }
        }

        db.update("user",values,"email" + "='" + email + "'", null);
    }

    /*
    * Retrieve all users job applications from SQLite database
     */
    public List<JobApplication> getJobApplicationDetails() {
        String selectQuery = "SELECT  * FROM jobapplication";
        List<JobApplication> jobApplicationList = new ArrayList<JobApplication>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                JobApplication ja = new JobApplication(cursor.getLong(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
                jobApplicationList.add(ja);
                Log.d(TAG, "Fetched 1 row from job application table");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return jobApplicationList;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getUserRowCount() {
        String countQuery = "SELECT  * FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Getting user login status return true if rows are there in table
     * */
    public int getJobApplicationRowCount() {
        String countQuery = "SELECT  * FROM jobapplication";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Delete all tables
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete("user", null, null);
        db.delete("jobapplication", null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

}
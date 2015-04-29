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

import recruitapp.ittproject3.com.recruitmentapp.Models.*;

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
        String CREATE_LOGIN_TABLE = "CREATE TABLE user(app_id LONG PRIMARY KEY, first_name VARCHAR, last_name VARCHAR, email VARCHAR, city VARCHAR, cv_filePath VARCHAR, profile_image_path VARCHAR, cv_fileName VARCHAR);";
        String CREATE_JOB_APPLICATION_TABLE = "CREATE TABLE jobapplication (app_id LONG PRIMARY KEY, job_id LONG, job_title VARCHAR, job_description TEXT, job_location VARCHAR, status VARCHAR);";
        String CREATE_QUESTION_TABLE = "CREATE TABLE questiontable (question_id LONG PRIMARY KEY, question VARCHAR, job_id LONG, FOREIGN KEY(job_id) REFERENCES joblisting(job_id));";
        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_JOB_APPLICATION_TABLE);
        db.execSQL(CREATE_QUESTION_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS user");
        db.execSQL("DROP TABLE IF EXISTS jobapplication");
        db.execSQL("DROP TABLE IF EXISTS questiontable");

        // Create tables again
        onCreate(db);
    }

    /**
     * Storing user details in database
     * */
    public void addUser(Long app_id, String firstName, String lastName, String email, String city, String cvFilePath, String profileImage, String cvFileName) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("app_id", app_id);
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("city", city);
        values.put("cv_filePath", cvFilePath);
        values.put("profile_image_path", profileImage);
        values.put("cv_fileName", cvFileName);

        // Inserting Row
        db.insert("user", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + email );
    }

    /**
     * Storing user's job applications in database
     * */
    public void addJobApplication (Long app_id, Long job_id, String job_title, String job_description, String job_location, String status) {
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
     * Storing interview questions in database
     * */
    public void addInterviewQuestions (Long question_id, String question, Long job_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("question_id", question_id);
        values.put("question", question);
        values.put("job_id", job_id);

        // Inserting Row
        db.insert("questiontable", null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New interview question inserted into sqlite: " + question_id );
    }

    /**
     * Retrieve email of current user
     * */
    public String getCurrentUserEmail() {
        String selectQuery = "SELECT email FROM user";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getString(0);
        }
        else return null;
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
            user.put("cv_fileName" ,cursor.getString(7));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * update user data in the datbase
     * */
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
            if(entry.getKey().equals("cv_fileName")){
                values.put("cv_fileName", entry.getValue());
            }
        }

        db.update("user",values,"email" + "='" + email + "'", null);
    }

    /*
    * Retrieve all users job applications from SQLite database
     */
    public List<JobApplication> getJobApplicationDetails() {
        String selectQuery = "SELECT * FROM jobapplication";
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

    /*
    * Retrieve all interview questions for a given job
    * from the SQLite database
     */
    public List<InterviewQuestion> getInterviewQuestionList() {
        String selectQuery = "SELECT * FROM questiontable";
        List<InterviewQuestion> interviewQuestionList = new ArrayList<InterviewQuestion>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            do {
                InterviewQuestion iq = new InterviewQuestion(cursor.getLong(0), cursor.getString(1), cursor.getLong(2));
                interviewQuestionList.add(iq);
                Log.d(TAG, "Fetched 1 row from question table");
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return interviewQuestionList;
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
    public int getInterviewQuestionCount() {
        String countQuery = "SELECT  * FROM questiontable";
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
    public void deleteTable(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(tableName, null, null);
        db.close();

        Log.d(TAG, "Deleted table from sqlite");
    }

}
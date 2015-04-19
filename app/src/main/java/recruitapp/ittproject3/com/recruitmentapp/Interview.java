package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.*;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;


public class Interview extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private List<InterviewQuestion> interviewQuestions;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        db = new SQLiteHandler(getApplicationContext());

        interviewQuestions = db.getInterviewQuestionList();

        // Starts the videoInterview Task


        for(int i=0; i<interviewQuestions.size(); i++) {
            InterviewTaskParams itParams = new InterviewTaskParams((i+1), interviewQuestions.get(i).getQuestion());
            InterviewQuestionSyncTask iqTask = new InterviewQuestionSyncTask();
            iqTask.execute(itParams);
        }
    }


    private class InterviewQuestionSyncTask extends AsyncTask<InterviewTaskParams, String, Void> {

        // A callback method executed on UI thread on starting the task
        @Override
        protected void onPreExecute() {
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            previewCounter.setText("Starting");
        }

        // A callback method executed on non UI thread, invoked after
        // onPreExecute method if exists
        @Override
        protected Void doInBackground(InterviewTaskParams... params) {

            for (int j = 5; j >= 0; j--) {
                try {
                    Thread.sleep(1000);
                    publishProgress(Integer.toString(params[0].questionNumber), params[0].question, Integer.toString(j)); // Invokes onProgressUpdate()
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        // A callback method executed on UI thread, invoked by the publishProgress()
        // from doInBackground() method
        @Override
        protected void onProgressUpdate(String... values) {
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView currentQuestion = (TextView) findViewById(R.id.currentQuestionView);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            questionNumber.setText("Question Number: " + values[0]);
            currentQuestion.setText(values[1]);
            previewCounter.setText("You have\n" + values[2] + " seconds\nto prepare your answer.");

        }

        // A callback method executed on UI thread, invoked after the completion of the task
        @Override
        protected void onPostExecute(Void result) {
//            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);
//            previewCounter.setText("BEGIN");

            new CountDownTimer(2000, 2000) {
                Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

                public void onTick(long millisUntilFinished) {
//                    mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);

                    if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
                    }
                }

                public void onFinish() {
//                    mTextField.setText("done!");

                }
            }.start();

//            dispatchTakeVideoIntent();
        }
    }

    private static class InterviewTaskParams {
//        List<InterviewQuestion> iqList = new ArrayList<>();
//        InterviewTaskParams(List<InterviewQuestion> iqList) {
//            this.iqList = iqList;
//        }

        int questionNumber;
        String question;
        InterviewTaskParams(int questionNumber, String question){
            this.questionNumber = questionNumber;
            this.question = question;
        }
    }

    private void dispatchTakeVideoIntent() {

    }

}

package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.*;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;


public class Interview extends Activity {

    private List<InterviewQuestion> interviewQuestions;
    private SQLiteHandler db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        db = new SQLiteHandler(getApplicationContext());

        interviewQuestions = db.getInterviewQuestionList();

        // Starts the videoInterview Task

        InterviewTaskParams itParams = new InterviewTaskParams(interviewQuestions);

        InterviewQuestionSyncTask iqTask = new InterviewQuestionSyncTask();
        iqTask.execute(itParams);
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

            for(int i=0; i< params[0].iqList.size(); i++) {

                for (int j = 5; j >= 0; j--) {
                    try {
                        Thread.sleep(1000);
                        publishProgress(Integer.toString(i+1), params[0].iqList.get(i).getQuestion(), Integer.toString(j)); // Invokes onProgressUpdate()
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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



        }
    }

    private static class InterviewTaskParams {
        List<InterviewQuestion> iqList = new ArrayList<>();
        InterviewTaskParams(List<InterviewQuestion> iqList) {
            this.iqList = iqList;
        }
    }

}

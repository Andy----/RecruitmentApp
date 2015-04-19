package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.*;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;


public class Interview extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private List<InterviewQuestion> interviewQuestions;
    private List<InterviewTaskParams> interviewTaskParamsList = new ArrayList<>();
    private SQLiteHandler db;
    private Button nextQuestion;
    private int questionCount = 0, pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        db = new SQLiteHandler(getApplicationContext());
        interviewQuestions = db.getInterviewQuestionList();
        nextQuestion = (Button) findViewById(R.id.nextQuestionButton);

        // Create a List of task parameter objects based on the list of interview questions
        for(int i=0; i<interviewQuestions.size(); i++) {
            interviewTaskParamsList.add(new InterviewTaskParams((i+1), interviewQuestions.get(i).getQuestionId(), interviewQuestions.get(i).getQuestion()));
            questionCount++;
        }


            nextQuestion.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (questionCount > 0) {
                        InterviewQuestionSyncTask iqTask = new InterviewQuestionSyncTask();
                        iqTask.execute(interviewTaskParamsList.get(pos));
                        pos++;
                        questionCount--;
                        nextQuestion.setText("Next Question");
                    } else {

                    }
                }
            });
    }


    private class InterviewQuestionSyncTask extends AsyncTask<InterviewTaskParams, String, Long> {

        @Override
        protected void onPreExecute() {
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            previewCounter.setText("Starting");
        }

        // A callback method executed on non UI thread, invoked after
        // onPreExecute method if exists
        @Override
        protected Long doInBackground(InterviewTaskParams... params) {

            for (int j = 10; j >= 0; j--) {
                try {
                    Thread.sleep(1000);
                    publishProgress(Integer.toString(params[0].questionNumber), params[0].question, Integer.toString(j));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return params[0].questionId;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView currentQuestion = (TextView) findViewById(R.id.currentQuestionView);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            questionNumber.setText("Question Number: " + values[0]);
            currentQuestion.setText(values[1]);
            previewCounter.setText("You have\n" + values[2] + " seconds\nto prepare your answer.");

        }

        @Override
        protected void onPostExecute(Long result) {
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView currentQuestion = (TextView) findViewById(R.id.currentQuestionView);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            questionNumber.setText("");
            currentQuestion.setText("");
            previewCounter.setText("");

            dispatchVideo(result);
            if(questionCount == 0) {
                nextQuestion.setText("Submit Interview");
            }
        }
    }

    private static class InterviewTaskParams {

        int questionNumber;
        Long questionId;
        String question;
        InterviewTaskParams(int questionNumber, Long questionId, String question){
            this.questionNumber = questionNumber;
            this.questionId = questionId;
            this.question = question;
        }
    }

    private void dispatchVideo(Long questionId) {
        File videoFile = null;
        File newDir = new File(getExternalCacheDir(), "RecruitSwift/InterviewVideos");
        if(!newDir.isDirectory())
            newDir.mkdirs();

        if(newDir.canWrite())
            videoFile = new File(newDir, "VideoResponse"+questionId+".mp4");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            Uri videoUri = Uri.fromFile(videoFile);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }
}

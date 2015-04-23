package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.*;
import recruitapp.ittproject3.com.recruitmentapp.helper.MediaRecorderActivity;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;


public class Interview extends Activity {

    static final int REQUEST_VIDEO_CAPTURE = 1;
    private List<InterviewQuestion> interviewQuestions;
    private List<InterviewTaskParams> interviewTaskParamsList = new ArrayList<>();
    private SQLiteHandler db;
    MediaRecorderActivity mRecorder;
    private Button nextQuestion;
    private String currentQuestion;
    private int questionCount = 0, pos = 0;
    private Long applicationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        db = new SQLiteHandler(getApplicationContext());
        mRecorder = new MediaRecorderActivity();

        applicationId = getIntent().getExtras().getLong("applicationId");
        interviewQuestions = db.getInterviewQuestionList();
        nextQuestion = (Button) findViewById(R.id.nextQuestionButton);

        TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
        questionNumber.setText("When you are ready, click on the button below to display the first question." +
                "\n\nOnce the interview starts you will have 30 seconds to prepare your answer at which point the camera will start recording your response automatically." +
                "\n\nEach recording can last up to a maximum of 3 minutes or you can finish the recording at any time by pressing the button on screen." +
                "\n\nGood Luck!");

        // Create a List of task parameter objects based on the list of interview questions
        for(int i=0; i<interviewQuestions.size(); i++) {
            interviewTaskParamsList.add(new InterviewTaskParams((i+1), applicationId, interviewQuestions.get(i).getQuestionId(), interviewQuestions.get(i).getQuestion()));
            questionCount++;
        }


            nextQuestion.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (questionCount > 0) {
                        InterviewQuestionSyncTask iqTask = new InterviewQuestionSyncTask();
                        iqTask.execute(interviewTaskParamsList.get(pos));
                        currentQuestion = interviewTaskParamsList.get(pos).question;
                        pos++;
                        questionCount--;
                        nextQuestion.setText("Next Question");
                    }
                }
            });
    }


    private class InterviewQuestionSyncTask extends AsyncTask<InterviewTaskParams, String, InterviewTaskParams> {

        @Override
        protected void onPreExecute() {

            nextQuestion.setVisibility(View.INVISIBLE);
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);
            questionNumber.setText(null);
            previewCounter.setText("Starting");
        }

        // A callback method executed on non UI thread, invoked after
        // onPreExecute method if exists
        @Override
        protected InterviewTaskParams doInBackground(InterviewTaskParams... params) {

            for (int j = 10; j >= 0; j--) {
                try {
                    Thread.sleep(1000);
                    publishProgress(Integer.toString(params[0].questionNumber), params[0].question, Integer.toString(j));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return params[0];
        }

        @Override
        protected void onProgressUpdate(String... values) {
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView currentQuestion = (TextView) findViewById(R.id.currentQuestionView);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            questionNumber.setText("Question Number: " + values[0]);
            currentQuestion.setText("\""+values[1]+"\"");
            previewCounter.setText("You have\n" + values[2] + "\nseconds to prepare your answer.");
        }

        @Override
        protected void onPostExecute(InterviewTaskParams params) {
            TextView questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
            TextView currentQuestion = (TextView) findViewById(R.id.currentQuestionView);
            TextView previewCounter = (TextView) findViewById(R.id.previewCounter);

            questionNumber.setText("");
            currentQuestion.setText("");
            previewCounter.setText("");

            dispatchVideo(params.questionId, params.jobAppId);

            nextQuestion.setVisibility(View.VISIBLE);
            if(questionCount == 0) {
                nextQuestion.setText("Submit Interview");
            }
        }
    }

    private static class InterviewTaskParams {

        int questionNumber;
        Long jobAppId;
        Long questionId;
        String question;
        InterviewTaskParams(int questionNumber,Long jobAppId, Long questionId, String question){
            this.questionNumber = questionNumber;
            this.jobAppId = jobAppId;
            this.questionId = questionId;
            this.question = question;
        }
    }

    private void dispatchVideo(Long questionId, Long applicationId) {

        File newDir = new File(getExternalCacheDir(), "RecruitSwift/InterviewVideos/JobApplication" + applicationId);
        if(!newDir.isDirectory())
            newDir.mkdirs();

        String fileName = "/InterviewVideos/JobApplication" + Long.toString(applicationId) + "/interviewVideo" + questionId + ".mp4";
        Intent intent = new Intent(Interview.this, mRecorder.getClass());
        intent.putExtra("fileName", fileName);
        intent.putExtra("currentQuestion", currentQuestion);
        startActivity(intent);
    }
}

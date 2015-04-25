package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.*;
import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.MediaRecorderActivity;
import recruitapp.ittproject3.com.recruitmentapp.helper.MultipartRequest;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleyApplication;


public class Interview extends Activity {

    // LogCat tag
    private static final String TAG = Interview.class.getSimpleName();

    TextView questionNumber;
    private List<InterviewQuestion> interviewQuestions;
    private List<InterviewTaskParams> interviewTaskParamsList = new ArrayList<>();
    private SQLiteHandler db;
    MediaRecorderActivity mRecorder;
    private Button nextQuestion;
    private String currentQuestion;
    private int questionCount = 0, pos = 0;
    private Long applicationId;
    private HashMap<String, String> requestMap;
    private ProgressDialog progress;
    private List<String> interviewVideoFileList;

    private InterviewQuestionSyncTask iqTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        db = new SQLiteHandler(getApplicationContext());
        mRecorder = new MediaRecorderActivity();

        requestMap = new HashMap<>();
        interviewVideoFileList = new ArrayList<>();
        progress = new ProgressDialog(this);

        applicationId = getIntent().getExtras().getLong("applicationId");
        interviewQuestions = db.getInterviewQuestionList();
        nextQuestion = (Button) findViewById(R.id.nextQuestionButton);

        questionNumber = (TextView) findViewById(R.id.currentQuestionNumber);
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
                        iqTask = new InterviewQuestionSyncTask();
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

            dispatchVideo(params.questionId, params.jobAppId);

            questionNumber.setText("");
            currentQuestion.setText("");
            previewCounter.setText("");

            // Delays displaying the next set of buttons & text until the video recording has been opened
            DelayTask delayTask = new DelayTask();
            delayTask.execute();
        }
    }

    /*
    * DelayTask delays displaying the "next question" or "submit interview" button and text by 5 seconds
    * to give the video recording activity screen time to load
     */
    private class DelayTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            if(questionCount > 0) {
                questionNumber.setText("When you are ready, click on the button below to proceed to the next question.");
            }

            if(questionCount == 0) {
                questionNumber.setText("Make sure to press the button below to upload your interview to our servers before exiting the app.");
                nextQuestion.setText("Submit Interview");
                nextQuestion.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        UploadInterviewVideos uploadIntVids = new UploadInterviewVideos();
                        uploadIntVids.execute();
                    }
                });
            }

            nextQuestion.setVisibility(View.VISIBLE);
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

        // Add video path to List for retrieving video and uploading to server once interview is complete
        interviewVideoFileList.add(getExternalCacheDir() + "/RecruitSwift" + fileName);

        Intent intent = new Intent(Interview.this, mRecorder.getClass());
        intent.putExtra("fileName", fileName);
        intent.putExtra("currentQuestion", currentQuestion);
        startActivity(intent);
    }

    /**
     * Method is called when interview is finished with "submit interview" button
     * Loop retrieves each interview question's filepath and creates a
     * multipart entity and adds each one to the volley request queue
     */
    private class UploadInterviewVideos extends AsyncTask<Void, Integer, Void> {
        Double progressUpdate = 0.0;
        int progressCounter = 0;
        @Override
        protected void onPreExecute() {
            progress.setTitle("Wait");
            progress.setMessage("Uploading Interview...");
            progress.setProgressNumberFormat(null);
            progress.setCancelable(false);
            progress.setProgress(0);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String email = db.getCurrentUserEmail();
            requestMap.put("email", email);
            requestMap.put("applicationId", Long.toString(applicationId));

            for(int i=0; i < interviewVideoFileList.size(); i++) {
                File interviewVideoFile = new File(interviewVideoFileList.get(i));

                MultipartRequest requestVideo = new MultipartRequest(AppConfig.INT_VIDEOS_URL, interviewVideoFile, requestMap,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                );
                VolleyApplication.getInstance().getRequestQueue().add(requestVideo);

                if(requestVideo != null) {
                    progressUpdate += (100/interviewVideoFileList.size());
                    while (!requestVideo.hasHadResponseDelivered()) {
                        if(progressCounter < progressUpdate) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            publishProgress(progressCounter);
                            progressCounter++;
                        }
                    }
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void param) {
            if (progress.isShowing()) {
                progress.dismiss();
            }

            questionNumber.setText("Your interview is complete." +
                    "\nClick done to return to your profile.");

            nextQuestion.setText("Done");
            nextQuestion.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    finish();
                }
            });

        }
    }

    /**
     * Kills the background thread and close the activity if
     * the back button is pressed
     */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(iqTask != null) {
            if (iqTask.getStatus() == AsyncTask.Status.RUNNING) {
                iqTask.cancel(true);
            }
        }
        finish();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(iqTask != null) {
            if (iqTask.getStatus() == AsyncTask.Status.RUNNING) {
                iqTask.cancel(true);
            }
        }
    }

}

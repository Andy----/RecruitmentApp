package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.Models.InterviewQuestion;
import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleyApplication;


public class InterviewStartScreenActivity extends Activity {

    private static final String TAG = InterviewStartScreenActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private Long jobId, applicationId;
    private TextView interviewInfo;
    private Button begin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_start_screen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        begin = (Button) findViewById(R.id.startInterviewButton);

        jobId = getIntent().getExtras().getLong("jobId");
        applicationId = getIntent().getExtras().getLong("applicationId");
        db = new SQLiteHandler(getApplicationContext());
        db.deleteTable("questiontable");

        if(jobId != null) {
            getInterviewQuestions(jobId);
        }

        begin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(InterviewStartScreenActivity.this, Interview.class);
                intent.putExtra("applicationId", applicationId);
                startActivity(intent);
                finish();
            }

        });
        begin.setEnabled(false);
    }

    /**
     * function to retrieve all interview questions for job interview
     * */
    private void getInterviewQuestions(final Long id) {

        pDialog.setMessage("Preparing Your Interview...");
        showDialog();

        Map<String, Long> postParams = new HashMap<String, Long>();
        postParams.put("job_id", id);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(Request.Method.POST, AppConfig.URL_GET_QUESTIONS, new JSONObject(postParams),
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.setMessage(response.toString());
                        hideDialog();

                        try {
                            // Check for error node in json
                            if (response != null) {

                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    db.addInterviewQuestions(jsonObject.getLong("question_id"), jsonObject.getString("question"), jsonObject.getLong("job_id"));
                                }
                                setInterviewDetails();
                            }
                        } catch (Exception e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }

        };

        // Adding request to request queue
        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void setInterviewDetails() {

        interviewInfo = (TextView) findViewById(R.id.intNumQuestions);
        interviewInfo.setText("Your interview will consist of " + db.getInterviewQuestionCount() + " questions.");

        interviewInfo = (TextView) findViewById(R.id.approxTime);
        interviewInfo.setText("It will last for approximately " + (db.getInterviewQuestionCount()*3) + " minutes");

        if(db.getInterviewQuestionCount() > 0) {
            begin.setEnabled(true);
        }
    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

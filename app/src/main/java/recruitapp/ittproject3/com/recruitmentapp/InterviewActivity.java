package recruitapp.ittproject3.com.recruitmentapp;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleyApplication;


public class InterviewActivity extends ActionBarActivity {

    private static final String TAG = InterviewActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Long jobId = getIntent().getExtras().getLong("jobId");
        db = new SQLiteHandler(getApplicationContext());
        db.deleteTable("questiontable");

        if(jobId != null) {
            getInterviewQuestions(jobId);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_interview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * function to retrieve all interview questions for job interview
     * */
    private void getInterviewQuestions(final Long id) {

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



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}

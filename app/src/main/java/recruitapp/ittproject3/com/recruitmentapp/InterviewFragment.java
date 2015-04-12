package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.*;

/**
 * INTERVIEW SCREEN FRAGMENT.
 */
public class InterviewFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final String TAG = UserProfileInterviewScreenActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private SessionManager session;

    private JSONObject userDetailsObject;
    private SQLiteHandler db;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static InterviewFragment newInstance(int sectionNumber) {
        InterviewFragment fragment = new InterviewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public InterviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interview_screen, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));

        db = new SQLiteHandler(getActivity().getApplicationContext());
        userDetailsObject = new JSONObject(db.getUserDetails());

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        try {
            getInterviews(userDetailsObject.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * function to verify login details in mysql db
     * */
    private void getInterviews(final String email) {

        pDialog.setMessage("Retrieving Job Interviews ...");
        showDialog();

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("email", email);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_GET_INTERVIEWS, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.setMessage(response.toString());
                        hideDialog();

                        try {
                            boolean error = response.getBoolean("error");

                            // Check for error node in json
                            if (!error) {

                            } else {
                                // Get the error message
                                String errorMsg = response.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
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
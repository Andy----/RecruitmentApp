package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.UserDetails;

/**
 * USER PROFILE FRAGMENT.
 */
public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String JSON_STRING = "JsonString";
    private JSONObject jsonObject =null;
    private JSONObject userDetailsObject;
    private TextView mTextView = null;
    private View rootView;
    private UserDetails setDetails;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance(int sectionNumber) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        try {
            setUserDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            String userProfileString = getArguments().getString(JSON_STRING);
            ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
            try {
                jsonObject = new JSONObject(userProfileString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public void setUserDetails() throws JSONException {

        setDetails = new UserDetails(jsonObject);
        mTextView = (TextView) rootView.findViewById(R.id.cityView);
        mTextView.setText(setDetails.getCity());
        mTextView = (TextView) rootView.findViewById(R.id.emailView);
        mTextView.setText(setDetails.getEmail());
        mTextView = (TextView) rootView.findViewById(R.id.nameView);
        mTextView.setText(setDetails.getFirstName() + " " + setDetails.getSurname());
    }

    private void updateUser(final String email) {
        String tag_string_req = "req_login";
        Map<String, String> postParams = new HashMap<>();
        postParams.put("email", email);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_REFRESH, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        Toast.makeText(getActivity().getApplicationContext(),
                                response.toString(), Toast.LENGTH_LONG).show();

                        try {
                            boolean error = response.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                userDetailsObject = new JSONObject(response.toString());

                            } else {
                                // Error in login. Get the error message
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

            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }

        };

        // Adding request to request queue
        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }

    @Override
    public void onResume(){
        super.onResume();
        mTextView = (TextView) rootView.findViewById(R.id.emailView);
        System.out.println();
        updateUser(mTextView.getText().toString());
    }
}

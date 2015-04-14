package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.MultipartRequest;
import recruitapp.ittproject3.com.recruitmentapp.helper.UserDetails;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleyApplication;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleySingleton;

/**
 * USER PROFILE FRAGMENT.
 */
public class EditProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String JSON_STRING = "JsonString";
    private Map<String, String> myMap;
    private String userProfileString;
    private UserDetails setDetails;
    private JSONObject jsonObject;
    private TextView mEditText;
    private View rootView;
    private File myVideoFile;
    private File myImageFile;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EditProfileFragment newInstance(int sectionNumber) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        myVideoFile  = new File(getActivity().getExternalCacheDir() + "/RecruitSwift/intro.mp4");
        myImageFile  = new File(getActivity().getExternalCacheDir() + "/RecruitSwift/profile.jpg");
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        myMap = new HashMap<>();

        try {
            jsonObject = new JSONObject(userProfileString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            updateUser(jsonObject.getString("email"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            setUserDetails(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        Button mButton = (Button) rootView.findViewById(R.id.saveBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

//        mEditText = (TextView) rootView.findViewById(R.id.emailText);
//        updateUser(mEditText.getText().toString());
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
             userProfileString = getArguments().getString(JSON_STRING);
        }
    }


    public void saveDetails(){
        updateUser();
        MultipartRequest requestVideo = new MultipartRequest(AppConfig.URL_UPDATE, myVideoFile, myMap,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getActivity().getApplicationContext(),
                                response, Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity().getApplicationContext(),
                                error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyApplication.getInstance().getRequestQueue().add(requestVideo);

        MultipartRequest requestImage = new MultipartRequest(AppConfig.URL_UPDATE_IMAGE, myImageFile, myMap,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getActivity().getApplicationContext(),
                                response, Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity().getApplicationContext(),
                                error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        VolleyApplication.getInstance().getRequestQueue().add(requestImage);
    }


    public void setUserDetails(JSONObject userDetailsObject) throws JSONException {

        setDetails = new UserDetails(userDetailsObject);
        mEditText = (EditText) rootView.findViewById(R.id.cityText);
        mEditText.setText(setDetails.getCity());
        mEditText = (TextView) rootView.findViewById(R.id.emailText);
        mEditText.setText(setDetails.getEmail());
        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        mEditText.setText(setDetails.getFirstName() + " " + setDetails.getSurname());
        NetworkImageView avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
        avatar.setImageUrl("http://192.168.1.2:9000/assets/globalUploadFolder/k@gmail.com/profile.jpg",mImageLoader);
    }

    public void updateUser() {
//        mEditText = (EditText) rootView.findViewById(R.id.nameText);
//        String name = mEditText.getText().toString();
//        String[] names = name.split(" ");
//        String firstName = names[0];
//        String sureName = names[1];
//
//        request.addStringBody("first_name", firstName);
//        request.addStringBody("last_name", sureName);
//        mEditText = (TextView) rootView.findViewById(R.id.emailText);
//        request.addStringBody("email", mEditText.getText().toString());
//        mEditText = (TextView) rootView.findViewById(R.id.cityText);
//        request.addStringBody("city", mEditText.getText().toString());
                mEditText = (EditText) rootView.findViewById(R.id.nameText);
                String name = mEditText.getText().toString();
                String[] names = name.split(" ");
                String firstName = names[0];
                String sureName = names[1];
                myMap.put("first_name",firstName);
                myMap.put("last_name", sureName);
                mEditText = (TextView) rootView.findViewById(R.id.emailText);
                myMap.put("email", mEditText.getText().toString());
                mEditText = (TextView) rootView.findViewById(R.id.cityText);
                myMap.put("city", mEditText.getText().toString());
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
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                response.toString(), Toast.LENGTH_LONG).show();

                        try {
                            boolean error = response.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                jsonObject = new JSONObject(response.toString());
                                try {
                                    setUserDetails(jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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

    }
}

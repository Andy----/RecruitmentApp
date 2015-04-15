package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;

import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleySingleton;

/**
 * USER PROFILE FRAGMENT.
 */
public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mTextView;
    private View rootView;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private  NetworkImageView avatar;
    private SQLiteHandler db;
    private Map<String, String> userDeatilsMap;

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
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        db = new SQLiteHandler(getActivity().getApplicationContext());

        try {
            setUserDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        try {
//            jsonObject = new JSONObject(userProfileString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            updateUser(jsonObject.getString("email"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));

        }
    }

    public void setUserDetails() throws JSONException {

        String first_name ="";
        String last_name ="";
        userDeatilsMap = db.getUserDetails();
        for (Map.Entry<String, String> entry : userDeatilsMap.entrySet()){
            if(entry.getKey().equals("city")){
                mTextView = (TextView) rootView.findViewById(R.id.cityView);
                mTextView.setText(entry.getValue());
            }
            if(entry.getKey().equals("email")){
                mTextView = (TextView) rootView.findViewById(R.id.emailView);
                mTextView.setText(entry.getValue());
            }
            if(entry.getKey().equals("first_name")){
                first_name = entry.getValue();
            }
            if(entry.getKey().equals("last_name")){
                last_name =  entry.getValue();
            }
        }
        mTextView = (TextView) rootView.findViewById(R.id.nameView);
        mTextView.setText(first_name + " " + last_name);

//        setDetails = new UserDetails(userDetailsObject);
//        mTextView = (TextView) rootView.findViewById(R.id.cityView);
//        mTextView.setText(setDetails.getCity());
//        mTextView = (TextView) rootView.findViewById(R.id.emailView);
//        mTextView.setText(setDetails.getEmail());
//        mTextView = (TextView) rootView.findViewById(R.id.nameView);
//        mTextView.setText(setDetails.getFirstName() + " " + setDetails.getSurname());
//        avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
//        avatar.setImageUrl("http://192.168.1.2:9000/assets/globalUploadFolder/k@gmail.com/profile.jpg", mImageLoader);
    }

//    private void updateUser(final String email) {
//        String tag_string_req = "req_login";
//        Map<String, String> postParams = new HashMap<String, String>();
//        postParams.put("email", email);
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_REFRESH, new JSONObject(postParams),
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString());
////                        Toast.makeText(getActivity().getApplicationContext(),
////                                response.toString(), Toast.LENGTH_LONG).show();
//
//                        try {
//                            boolean error = response.getBoolean("error");
//
//                            // Check for error node in json
//                            if (!error) {
//                                jsonObject = new JSONObject(response.toString());
////                                try {
//////                                    setUserDetails(jsonObject);
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
//                            } else {
//                                // Error in login. Get the error message
//                                String errorMsg = response.getString("error_msg");
//                                Toast.makeText(getActivity().getApplicationContext(),
//                                        errorMsg, Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            // JSON error
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put( "charset", "utf-8");
//                return headers;
//            }
//        };
//
//        // Adding request to request queue
//        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
//    }

    @Override
    public void onResume(){
        super.onResume();
        avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
        avatar.setImageUrl("http://192.168.1.2:9000/assets/globalUploadFolder/k@gmail.com/profile.jpg", mImageLoader);
    }
}

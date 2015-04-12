package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.UserDetails;

/**
 * USER PROFILE FRAGMENT.
 */
public class EditProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    String userProfileString;
    private static final String JSON_STRING = "JsonString";
    private JSONObject jsonObject;
    private TextView mEditText = null;
    private View rootView;
    private Map<String, String> myMap;
    private File myFile;
    private UserDetails setDetails;
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
        myFile  = new File(getActivity().getExternalCacheDir() + "/RecruitSwift/myvideo.mp4");
        myMap = new HashMap<>();
        try {
            jsonObject = new JSONObject(userProfileString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            setUserDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button mButton = (Button) rootView.findViewById(R.id.saveBtn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (getArguments() != null) {
            ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
             userProfileString= getArguments().getString(JSON_STRING);
        }
    }


    public void saveDetails(){
        MultipartRequest request = new MultipartRequest(AppConfig.URL_UPDATE, myFile, myMap,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

//                                mEditText.setText(response.toString());

                        Toast.makeText(getActivity().getApplicationContext(),
                                response, Toast.LENGTH_LONG).show();
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                                mEditText.setText(error.toString());
                        Toast.makeText(getActivity().getApplicationContext(),
                                error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );

//                mEditText = (EditText) rootView.findViewById(R.id.nameText);
//                String name = mEditText.getText().toString();
//                String[] names = name.split(" ");
//                String firstName = names[0];
//                String sureName = names[1];
//                myMap.put("param1",firstName);
//                myMap.put("param2", sureName);
//                mEditText = (TextView) rootView.findViewById(R.id.emailText);
//                myMap.put("param3", mEditText.getText().toString());
//                mEditText = (TextView) rootView.findViewById(R.id.cityText);
//                myMap.put("param4", mEditText.getText().toString());

        updateUser(request);
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }


    public void setUserDetails() throws JSONException {

        setDetails = new UserDetails(jsonObject);
        System.out.println(setDetails.getCity());
        mEditText = (EditText) rootView.findViewById(R.id.cityText);
        mEditText.setText(setDetails.getCity());
        mEditText = (EditText) rootView.findViewById(R.id.emailText);
        mEditText.setText(setDetails.getEmail());
        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        mEditText.setText(setDetails.getFirstName() + " " + setDetails.getSurname());

    }

    public void updateUser(MultipartRequest request) {

        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        String name = mEditText.getText().toString();
        String[] names = name.split(" ");
        String firstName = names[0];
        String sureName = names[1];

        request.addStringBody("first_name", firstName);
        request.addStringBody("last_name", sureName);
        mEditText = (TextView) rootView.findViewById(R.id.emailText);
        request.addStringBody("email", mEditText.getText().toString());
        mEditText = (TextView) rootView.findViewById(R.id.cityText);
        request.addStringBody("city", mEditText.getText().toString());
    }


}

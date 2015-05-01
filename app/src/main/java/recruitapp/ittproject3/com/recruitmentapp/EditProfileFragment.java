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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.File;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
import recruitapp.ittproject3.com.recruitmentapp.helper.MultipartRequest;
import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
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
    private static final String ARG_SECTION_NUMBER = "section_number";
    private TextView mEditText;
    private View rootView;
    private File myVideoFile;
    private File myImageFile;
    private File myCVFile;
    // This appears to be unused but is
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    // This appears to be unused but is
    private ImageLoader mImageLoaderFlush;
    private Map<String, String> userDetailsMap;
    private SQLiteHandler db;
    private String profileImageDir;
    private String cvFileName;
    private String user ="";


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
        db = new SQLiteHandler(getActivity().getApplicationContext());
        setUserDetails();
        myVideoFile  = new File(getActivity().getExternalCacheDir() + File.separator +"RecruitSwift"+ File.separator + user + File.separator +"intro.mp4");
        myImageFile  = new File(getActivity().getExternalCacheDir() + File.separator + "RecruitSwift" + File.separator + user + File.separator +"profile.jpg");
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        Button mButton = (Button) rootView.findViewById(R.id.saveBtn);
        // onClick for save button
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUser();
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
        }
    }


    public void saveDetails(){

        String cvExists ="";
        String profileImageExists ="";
        userDetailsMap = db.getUserDetails();
        // Get the latest file_paths
        for (Map.Entry<String, String> entry : userDetailsMap.entrySet()) {
            if (entry.getKey().equals("cv_filePath")) {
                cvExists = entry.getValue();
                System.out.println(entry.getValue());
            }
            if (entry.getKey().equals("profile_image_path")) {
                profileImageExists = entry.getValue();
                System.out.println(entry.getValue());
            }
        }

        // If there is a video file add it to the multipart request
        if(myVideoFile.isFile()) {
            MultipartRequest requestVideo = new MultipartRequest(AppConfig.URL_UPDATE, myVideoFile, userDetailsMap,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(getActivity(),
                                        response.toString(), Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {

                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    error.toString(), Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {
                                System.out.println(e);
                            }
                        }
                    }
            );
            // Add the multipart request to a Volley request queue
            VolleyApplication.getInstance().getRequestQueue().add(requestVideo);
        }
        else{
            Toast.makeText(getActivity(), "You need to record a video", Toast.LENGTH_LONG).show();
        }
        // If there is a profile image add it to the multipart request
        if(myImageFile.isFile()) {
            MultipartRequest requestImage = new MultipartRequest(AppConfig.URL_UPDATE_IMAGE, myImageFile, userDetailsMap,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        response.toString(), Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {
                                System.out.println(e);
                            }
                        }
                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    error.toString(), Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {
                                System.out.println(e);
                            }
                        }
                    }
            );
            // Add the multipart request to a Volley request queue
            VolleyApplication.getInstance().getRequestQueue().add(requestImage);
        }else if(profileImageExists.equals("null")){
            Toast.makeText(getActivity(), "You need to choose a profile image", Toast.LENGTH_LONG).show();
        }else{

        }
        // If there is a CV file add it to the multipart request
        if(myCVFile.isFile()) {
            MultipartRequest requestCV = new MultipartRequest(AppConfig.URL_UPDATE_CV, myCVFile, userDetailsMap,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        response, Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {
                                System.out.println(e);
                            }
                        }
                    },

                    new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    error.toString(), Toast.LENGTH_LONG).show();
                            }catch (NullPointerException  e) {
                                System.out.println(e);
                            }
                        }
                    }
            );
            // Add the multipart request to a Volley request queue
            VolleyApplication.getInstance().getRequestQueue().add(requestCV);
        }else if(cvExists.equals("null")){
            Toast.makeText(getActivity(), "You need to choose a CV file", Toast.LENGTH_LONG).show();
        }else{

        }

        // Empty the LruCache, refresh the profile picture
        mImageLoaderFlush = VolleySingleton.getInstance().evictAllImages();
    }

    // Set the fields in the edit profile fragment using the data in the SQLite DB
    public void setUserDetails()  {

        String first_name ="";
        String last_name ="";
        userDetailsMap = db.getUserDetails();
        for (Map.Entry<String, String> entry : userDetailsMap.entrySet()){
            if(entry.getKey().equals("city")){
                mEditText = (EditText) rootView.findViewById(R.id.cityText);
                mEditText.setText(entry.getValue());
            }
            if(entry.getKey().equals("email")){
                mEditText = (TextView) rootView.findViewById(R.id.emailText);
                mEditText.setText(entry.getValue());
                user = entry.getValue();
            }
            if(entry.getKey().equals("first_name")){
                first_name = entry.getValue();
            }
            if(entry.getKey().equals("last_name")){
                last_name =  entry.getValue();
            }
            if(entry.getKey().equals("profile_image_path")){
                profileImageDir =  entry.getValue();
            }
            if(entry.getKey().equals("cv_fileName")){
                cvFileName =  entry.getValue();
            }
        }
        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        mEditText.setText(first_name + " " + last_name);
    }

    //
    public void updateUser() {

        // get the user details from the editText fields and add them to the map and SQLite DB
        boolean go = true;
        userDetailsMap = db.getUserDetails();
        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        String name = mEditText.getText().toString();
        if(name != "") {
            String[] names = name.split(" ");
            //Error checking for name
            if (names.length == 2) {
                String firstName = names[0].substring(0, 1).toUpperCase() + names[0].substring(1);
                String surName = names[1].substring(0, 1).toUpperCase() + names[1].substring(1);
                mEditText.setText(firstName + " " + surName);
                userDetailsMap.put("first_name", firstName);
                userDetailsMap.put("last_name", surName);
            } else {
                mEditText.setText("");
                Toast.makeText(getActivity().getApplicationContext(),
                        "Enter your First name, then a space followed by your Surname, if you have a double barrel surname please hyphenate it",
                        Toast.LENGTH_LONG).show();
                go = false;
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please enter your full name",
                    Toast.LENGTH_LONG).show();
            go = false;
        }
        
        mEditText = (TextView) rootView.findViewById(R.id.emailText);
        userDetailsMap.put("email", mEditText.getText().toString());
        mEditText = (TextView) rootView.findViewById(R.id.cityText);
        String city = mEditText.getText().toString();
        if(city != "") {
            String[] cityMistake = city.split(" ");
            // Error checking for city
            if (cityMistake.length == 1 && !city.isEmpty()) {
                city = cityMistake[0].substring(0, 1).toUpperCase() + cityMistake[0].substring(1);
                mEditText.setText(city);
                userDetailsMap.put("city", city);
            } else {
                mEditText.setText("");
                Toast.makeText(getActivity().getApplicationContext(),
                        "If your city is more than 1 word please hyphenate it",
                        Toast.LENGTH_LONG).show();
                go = false;
            }
        }else{
            Toast.makeText(getActivity().getApplicationContext(),
                    "Please enter the name of your city",
                    Toast.LENGTH_LONG).show();
            go = false;
        }

        userDetailsMap.put("city", mEditText.getText().toString());
        for (Map.Entry<String, String> entry : userDetailsMap.entrySet()){
            if(entry.getKey().equals("profile_image_path")){
                userDetailsMap.put("profile_image_path", entry.getValue());
            }
            if(entry.getKey().equals("cv_fileName")){
                userDetailsMap.put("cv_fileName", entry.getValue());
                cvFileName = entry.getValue();
            }
        }
        db.updateUserDetails(userDetailsMap);
        // Create a file for the CV
        myCVFile  = new File(getActivity().getExternalCacheDir() + File.separator + "RecruitSwift" + File.separator +user + File.separator  + cvFileName);

        if(go)
        saveDetails();
    }



    @Override
    public void onResume(){
        super.onResume();
        // Getting the profile picture
        NetworkImageView avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
        try {
            // Displaying the profile picture
            avatar.setImageUrl(AppConfig.IMAGE_URL + profileImageDir ,mImageLoader);
        }catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),"Error Retrieving Profile Photo", Toast.LENGTH_LONG).show();
        }
    }
}

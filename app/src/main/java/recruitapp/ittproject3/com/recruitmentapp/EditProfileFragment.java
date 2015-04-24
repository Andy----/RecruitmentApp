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
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private Map<String, String> userDetailsMap;
    private Map<String, String> userDeatilsMap2;
    private SQLiteHandler db;
    private String profileImageDir;
    private String cvFileName;
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
        db = new SQLiteHandler(getActivity().getApplicationContext());
        setUserDetails();
        mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
        mImageLoader = VolleySingleton.getInstance().getImageLoader();
        Button mButton = (Button) rootView.findViewById(R.id.saveBtn);
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

        MultipartRequest requestVideo = new MultipartRequest(AppConfig.URL_UPDATE, myVideoFile, userDetailsMap,
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

        MultipartRequest requestImage = new MultipartRequest(AppConfig.URL_UPDATE_IMAGE, myImageFile, userDetailsMap,
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

        MultipartRequest requestCV = new MultipartRequest(AppConfig.URL_UPDATE_CV, myCVFile, userDetailsMap,
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
        VolleyApplication.getInstance().getRequestQueue().add(requestCV);
    }


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

    public void updateUser() {

        userDeatilsMap2 = db.getUserDetails();
        mEditText = (EditText) rootView.findViewById(R.id.nameText);
        String name = mEditText.getText().toString();
        String[] names = name.split(" ");
        String firstName = names[0];
        String sureName = names[1];
        userDetailsMap.put("first_name",firstName);
        userDetailsMap.put("last_name", sureName);
        mEditText = (TextView) rootView.findViewById(R.id.emailText);
        userDetailsMap.put("email", mEditText.getText().toString());
        mEditText = (TextView) rootView.findViewById(R.id.cityText);

        userDetailsMap.put("city", mEditText.getText().toString());
        for (Map.Entry<String, String> entry : userDeatilsMap2.entrySet()){
            if(entry.getKey().equals("profile_image_path")){
                userDetailsMap.put("profile_image_path", entry.getValue());
            }
            if(entry.getKey().equals("cv_fileName")){
                userDetailsMap.put("cv_fileName", entry.getValue());
                cvFileName = entry.getValue();
            }
        }
        db.updateUserDetails(userDetailsMap);
        myCVFile  = new File(getActivity().getExternalCacheDir() + "/RecruitSwift" + File.separator + cvFileName);
        saveDetails();
    }

    @Override
    public void onResume(){
        super.onResume();
        NetworkImageView avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
        try {
            avatar.setImageUrl(AppConfig.IMAGE_URL + profileImageDir ,mImageLoader);
        }catch(Exception e) {
            Toast.makeText(getActivity().getApplicationContext(),"Error Retrieving Profile Photo", Toast.LENGTH_LONG).show();
        }


    }
}

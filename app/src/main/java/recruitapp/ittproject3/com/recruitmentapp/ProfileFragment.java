package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;
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
    // This appears to be unused but is
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private  NetworkImageView avatar;
    private SQLiteHandler db;
    private String profileImageDir;
    private Map<String, String> userDetailsMap;


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
        setUserDetails();

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

    // Set the fields in the edit profile fragment using the data in the SQLite DB
    public void setUserDetails() {

        String first_name ="";
        String last_name ="";
        userDetailsMap = db.getUserDetails();
        for (Map.Entry<String, String> entry : userDetailsMap.entrySet()){
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
            if(entry.getKey().equals("profile_image_path")){
                profileImageDir =  entry.getValue();
                System.out.println(profileImageDir);
            }
        }
        mTextView = (TextView) rootView.findViewById(R.id.nameView);
        mTextView.setText(first_name + " " + last_name);
    }

    @Override
    public void onResume(){
        super.onResume();
        // Getting the profile picture
        avatar = (NetworkImageView)getActivity().findViewById(R.id.profileImage);
        // Displaying the profile picture
        avatar.setImageUrl(AppConfig.IMAGE_URL  + profileImageDir ,mImageLoader);

    }
}

package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import recruitapp.ittproject3.com.recruitmentapp.helper.UserDetails;

/**
 * USER PROFILE FRAGMENT.
 */
public class ProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private JSONObject jsonObject =null;
    private TextView mTextView;

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
        View rootView = inflater.inflate(R.layout.fragment_profile_screen, container, false);
//        mTextView = (TextView) getView().findViewById(R.id.AgeView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        String userProfileString= getArguments().getString("JsonString");
        try {
             jsonObject = new JSONObject(userProfileString);
            setUserDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setUserDetails() throws JSONException {

        UserDetails setDetails = new UserDetails(jsonObject);

        System.out.println(setDetails.getCity());
//        mTextView.setText(setDetails.getCity());
    }
}

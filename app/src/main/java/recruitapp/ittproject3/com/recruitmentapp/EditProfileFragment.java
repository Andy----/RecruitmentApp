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
public class EditProfileFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private JSONObject jsonObject;
    private TextView mTextView = null;
    private View rootView;
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
            String userProfileString= getArguments().getString("JsonString");

            try {
                jsonObject = new JSONObject(userProfileString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setUserDetails() throws JSONException {

        UserDetails setDetails = new UserDetails(jsonObject);
        mTextView = (TextView) rootView.findViewById(R.id.cityText);
        mTextView.setText(setDetails.getCity());
        mTextView = (TextView) rootView.findViewById(R.id.emailText);
        mTextView.setText(setDetails.getEmail());
        mTextView = (TextView) rootView.findViewById(R.id.nameText);
        mTextView.setText(setDetails.getFirstName() + " " + setDetails.getSurname());

    }
}

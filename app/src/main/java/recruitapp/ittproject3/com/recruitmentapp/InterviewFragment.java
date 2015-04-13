package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

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


//        if(db.getJobApplicationRowCount() > 0) {
//            //Set a linearLayout to add buttons
//            LinearLayout linearLayout = new LinearLayout(getActivity());
//            // Set the layout full width, full height
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            linearLayout.setLayoutParams(params);
//            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
//
//            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//            List<JobApplication> jobApplicationList = db.getJobApplicationDetails();
//            for (int i = 0; i < jobApplicationList.size(); i++) {
//                Button button = new Button(getActivity());
//                button.setLayoutParams(params1);
//                linearLayout.addView(button);
//            }
//        }

    }

}
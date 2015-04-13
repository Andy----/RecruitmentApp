package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

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

    private SQLiteHandler db;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_interview_screen, container, false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((UserProfileInterviewScreenActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        db = new SQLiteHandler(getActivity().getApplicationContext());
        LinearLayout ll = (LinearLayout)(this.getActivity().findViewById(R.id.interviewLinearLayout));

            List<JobApplication> jobApplicationList = db.getJobApplicationDetails();

            LinearLayout ll1 = new LinearLayout(this.getActivity());
            ll1.setOrientation(LinearLayout.VERTICAL);

            for(int i=0; i<jobApplicationList.size(); i++){

                TextView tv = new TextView(this.getActivity());
                tv.setText(jobApplicationList.get(i).getJob_description() + " " + jobApplicationList.get(i).getJob_location());
                tv.setTextColor(getResources().getColor(R.color.white));
                ll.addView(tv);

                Button button = new Button(this.getActivity());
                button.setId(i);
                ll1.addView(button);
            }

            Toast.makeText(getActivity().getApplicationContext(), jobApplicationList.size()+" Job Applications On File", Toast.LENGTH_LONG).show();

            ll.addView(ll1);
        }
}
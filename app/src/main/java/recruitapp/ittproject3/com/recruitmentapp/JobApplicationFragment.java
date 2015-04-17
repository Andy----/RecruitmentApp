package recruitapp.ittproject3.com.recruitmentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import recruitapp.ittproject3.com.recruitmentapp.Models.JobApplication;
import recruitapp.ittproject3.com.recruitmentapp.helper.*;

/**
 * INTERVIEW SCREEN FRAGMENT.
 */
public class JobApplicationFragment extends Fragment {
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
    public static JobApplicationFragment newInstance(int sectionNumber) {
        JobApplicationFragment fragment = new JobApplicationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public JobApplicationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_application_screen, container, false);

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
        LinearLayout ll = (LinearLayout)(this.getActivity().findViewById(R.id.jobApplicationLinearLayout));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);

            List<JobApplication> jobApplicationList = db.getJobApplicationDetails();

            if(jobApplicationList != null) {
                for (int i = 0; i < jobApplicationList.size(); i++) {
                    final Long jobId = jobApplicationList.get(i).getJobId();

                    LinearLayout newll = new LinearLayout(this.getActivity());
                    newll.setOrientation(LinearLayout.VERTICAL);
                    newll.setPadding(0, 40, 0, 40);
                    newll.setBackground(getResources().getDrawable(R.drawable.layout_border));

                    TextView jobTitle = new TextView(this.getActivity());
                    jobTitle.setText("Title: \t\t\t\t\t" + jobApplicationList.get(i).getJob_title());
                    jobTitle.setTextColor(getResources().getColor(R.color.white));
                    newll.addView(jobTitle);

                    TextView jobLocation = new TextView(this.getActivity());
                    jobLocation.setText("Location: \t" + jobApplicationList.get(i).getJob_location());
                    jobLocation.setTextColor(getResources().getColor(R.color.white));
                    newll.addView(jobLocation);

                    TextView jobID = new TextView(this.getActivity());
                    jobID.setText("Job ID: \t\t\t" + jobApplicationList.get(i).getJobId());
                    jobID.setTextColor(getResources().getColor(R.color.white));
                    newll.addView(jobID);

                    TextView jobStatus = new TextView(this.getActivity());
                    jobStatus.setText("Status: \t\t\t" + jobApplicationList.get(i).getStatus());
                    jobStatus.setTextColor(getResources().getColor(R.color.white));
//                if(jobApplicationList.get(i).getStatus().equals("submitted")) {jobStatus.setBackgroundColor(getResources().getColor(R.color.danger));}
//                else if(jobApplicationList.get(i).getStatus().equals("accepted")) {jobStatus.setBackgroundColor(getResources().getColor(R.color.warning));}
//                else if(jobApplicationList.get(i).getStatus().equals("interview")) {jobStatus.setBackgroundColor(getResources().getColor(R.color.success));}
                    newll.addView(jobStatus);

                    if (jobApplicationList.get(i).getStatus().equals("interview")) {
                        Button button = new Button(this.getActivity());
                        button.setId(i);
                        button.setText("Start Interview");
                        button.setBackgroundColor(getResources().getColor(R.color.light_blue));
                        button.setTextColor(getResources().getColor(R.color.white));
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                Intent intent = new Intent(getActivity(), InterviewActivity.class);
                                intent.putExtra("jobId", jobId);
                                startActivity(intent);
                            }
                        });

                        newll.addView(button, layoutParams);
                    }

                    ll.addView(newll, layoutParams);
                }
            }

//            Toast.makeText(getActivity().getApplicationContext(), jobApplicationList.size()+" Job Applications On File", Toast.LENGTH_LONG).show();
        }
}
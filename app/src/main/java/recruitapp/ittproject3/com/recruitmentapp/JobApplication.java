package recruitapp.ittproject3.com.recruitmentapp;

/**
 * Created by Andrew on 13/04/2015.
 */
public class JobApplication {

    private Long app_id;
    private Long job_id;
    private String job_title;
    private String job_description;
    private String job_location;

    public JobApplication() {}

    public JobApplication(Long app_id, Long job_id, String job_title, String job_description, String job_location) {
        this.app_id = app_id;
        this.job_id = job_id;
        this.job_title = job_title;
        this.job_description = job_description;
        this.job_location = job_location;
    }

    public Long getAppId() {return app_id;}
    public Long getJobId() {return job_id;}
    public String getJobTitle() {return job_title;}
    public String getJob_description() {return job_description;}
    public String getJob_title() {return job_title;}
    public String getJob_location() {return job_location;}
}

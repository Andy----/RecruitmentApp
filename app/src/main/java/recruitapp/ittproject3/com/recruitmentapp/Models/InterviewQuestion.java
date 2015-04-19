package recruitapp.ittproject3.com.recruitmentapp.Models;

/**
 * Created by Andrew on 18/04/2015.
 */
public class InterviewQuestion {
    private Long questionId;
    private String question;
    private Long jobId;

    public InterviewQuestion(){}

    public InterviewQuestion(Long questionId, String question, Long jobId) {
        this.questionId = questionId;
        this.question = question;
        this.jobId = jobId;
    }

    public Long getQuestionId(){
        return questionId;
    }
    public String getQuestion() {
        return question;
    }
    public Long getJobId() {
        return jobId;
    }
}

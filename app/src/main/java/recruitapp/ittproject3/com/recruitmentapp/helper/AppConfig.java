package recruitapp.ittproject3.com.recruitmentapp.helper;

import android.content.Context;

/**
 * Created by Andrew on 07/04/2015.
 */

public class AppConfig {

    private static final String URLA = "http://192.168.0.27:9000";
    private static final String URL = "http://192.168.1.2:9000";
    public static final String URLB = "http://recruit.x64.me";
    private static final String URLC = "http://johnkiernan.ie";


    // Server user login url
    public static final String URL_LOGIN = URL + "/androidlogin";

    // Server register URL
    public static final String URL_REGISTER = URL + "/androidregister";

    // Server Update URL
    public static final String URL_UPDATE = URL + "/update";

    // Server Update profile image URL
    public static final String URL_UPDATE_IMAGE= URL + "/updateProfileImage";

    // Server Update profile image URL
    public static final String URL_UPDATE_CV= URL + "/updateCV";

    // Server Interview request URL
    public static final String URL_GET_INTERVIEWS = URL + "/androidgetjobapplications";

    // Get Json array of interview questions
    public static final String URL_GET_QUESTIONS = URL + "/androidgetinterviewquestions";

    // Refresh URL
    public static final String URL_REFRESH = URL + "/refresh";

    // Profile Image URL
    public static String IMAGE_URL = URL + "/assets/";

}
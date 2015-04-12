package recruitapp.ittproject3.com.recruitmentapp.helper;

import android.content.Context;

/**
 * Created by Andrew on 07/04/2015.
 */


public class AppConfig {


    private static String URL = "http://192.168.0.27:9000";
    private static String URLA = "http://192.168.1.2:9000";

    // Server user login url
    public static String URL_LOGIN = URL + "/androidlogin";

    // Server register URL
    public static String URL_REGISTER = URL + "/androidregister";

    // Server Update URL
    public static String URL_UPDATE = URL + "/update";

    // Server Interview request URL
    public static String URL_GET_INTERVIEWS = URL + "/getinterviews";
}
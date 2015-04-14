package recruitapp.ittproject3.com.recruitmentapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.AppConfig;

public class VideoPlayerActivity extends ActionBarActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private JSONObject jsonObject;
    private String userProfileString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
//        try {
//            jsonObject = new JSONObject(userProfileString);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            getVideoPath(jsonObject.getString("email"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        // Set the videoView according to the id
        final VideoView videoView =
                (VideoView) findViewById(R.id.videoView1);

        // Set the path to the stored video
        videoView.setVideoPath(getExternalCacheDir() + "/RecruitSwift/intro.mp4");
        MediaController mediaController = new
                MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();
    }


//    private void getVideoPath(final String email) {
//        String tag_string_req = "req_login";
//        Map<String, String> postParams = new HashMap<>();
//        postParams.put("email", email);
//
//        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, AppConfig.URL_REFRESH, new JSONObject(postParams),
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, response.toString());
////                        Toast.makeText(getActivity().getApplicationContext(),
////                                response.toString(), Toast.LENGTH_LONG).show();
//
//                        try {
//                            boolean error = response.getBoolean("error");
//
//                            // Check for error node in json
//                            if (!error) {
//                                jsonObject = new JSONObject(response.toString());
//
//                            } else {
//                                // Error in login. Get the error message
//                                String errorMsg = response.getString("error_msg");
//                                Toast.makeText(getApplicationContext(),
//                                        errorMsg, Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            // JSON error
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
//            }
//        }) {
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Content-Type", "application/json");
//                headers.put( "charset", "utf-8");
//                return headers;
//            }
//        };
//
//        // Adding request to request queue
//        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_video_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

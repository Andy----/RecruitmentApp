package recruitapp.ittproject3.com.recruitmentapp;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;


public class VideoPlayerActivity extends ActionBarActivity {


    private SQLiteHandler db;
    private Map<String, String> userDeatilsMap;
    private String user = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        db = new SQLiteHandler(getApplicationContext());
        // Get the current user
        userDeatilsMap = db.getUserDetails();
        for (Map.Entry<String, String> entry : userDeatilsMap.entrySet()) {
            if (entry.getKey().equals("email")) {

                user = entry.getValue();
            }
        }

        // Set the videoView according to the id
        final VideoView videoView =(VideoView) findViewById(R.id.videoView1);


        // Set the path to the stored video
        videoView.setVideoURI(Uri.parse(getExternalCacheDir() + File.separator +"RecruitSwift" + File.separator + user +File.separator +"intro.mp4"));
        MediaController mediaController = new
        MediaController(this);
        mediaController.setAnchorView(videoView);
        // Add media controls
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

        System.out.println(id);
        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_close) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

package recruitapp.ittproject3.com.recruitmentapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.MultipartRequest;
import recruitapp.ittproject3.com.recruitmentapp.helper.VolleyApplication;


public class JSONActivity extends ActionBarActivity {

    private TextView mTextView;
    Map<String, String> myMap;
    File myFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json);
        myFile  = new File(getExternalCacheDir() + "/RecruitSwift/myvideo.mp4");
        mTextView = (TextView) findViewById(R.id.text);
        myMap = new HashMap<>();
    }

    public void getJsonBtn(View v){

        JsonObjectRequest request = new JsonObjectRequest("http://192.168.1.2:9000/json", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        mTextView.setText(response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                }
        );

//        request.setRetryPolicy(new DefaultRetryPolicy(
//                10000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
       VolleyApplication.getInstance().getRequestQueue().add(request);

    }

    public void postJsonBtn(View v){

        MultipartRequest request = new MultipartRequest("http://192.168.1.2:9000/jsonPost", myFile, myMap,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        mTextView.setText(response.toString());
                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                }
        );
        request.addStringBody("param1", "test-1");
        VolleyApplication.getInstance().getRequestQueue().add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_json, menu);
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

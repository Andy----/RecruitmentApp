package recruitapp.ittproject3.com.recruitmentapp;

import recruitapp.ittproject3.com.recruitmentapp.helper.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;


public class LoginScreenActivity extends Activity {

    // LogCat tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText emailIn, passwordIn;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private JSONObject userDetailsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_screen);

        emailIn = (EditText) findViewById(R.id.emailInput);
        passwordIn = (EditText) findViewById(R.id.passwordInput);
        Button submit = (Button) findViewById(R.id.loginButton);
        Button signUp = (Button) findViewById(R.id.signupButton);

        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());
        session.setLogin(false);
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // IF user is already logged in. Redirect to main activity
            Intent intent = new Intent(LoginScreenActivity.this, UserProfileInterviewScreenActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String email = emailIn.getText().toString();
                String password = passwordIn.getText().toString();
                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please Enter Your Login Details!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        signUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                // Launch main activity
                Intent intent = new Intent(LoginScreenActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }

        });

    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        Map<String, String> postParams = new HashMap<String, String>();
        postParams.put("email", email);
        postParams.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, AppConfig.URL_LOGIN, new JSONObject(postParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.setMessage(response.toString());
                        hideDialog();

                        try {
                            boolean error = response.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                // user successfully logged in
                                // Create login session
                                session.setLogin(true);


                                // SQLite database handler
                                // (Long app_id, String firstName, String lastName, String email, String city, String cvFilePath, String profileImage)

                                db.addUser(response.getLong("app_id"), response.getString("first_name"), response.getString("last_name"), response.getString("email"),
                                        response.getString("city"), response.getString("cvFilePath"), response.getString("profileImage"));

//                                db.addUser((long) 1,"sdf","sdf","sdf","sdf","sdf","sdf");

                                userDetailsObject = new JSONObject(response.toString());
                                // Launch main activity
                                Intent intent = new Intent(LoginScreenActivity.this, UserProfileInterviewScreenActivity.class);
                                intent.putExtra("userDetailsClass",userDetailsObject.toString());

                                startActivity(intent);
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = response.getString("error_msg");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put( "charset", "utf-8");
                return headers;
            }

        };

        // Adding request to request queue
        VolleyApplication.getInstance().addToRequestQueue(jsonObjReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
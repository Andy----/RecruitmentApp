package recruitapp.ittproject3.com.recruitmentapp;

/**
 * Created by Andrew on 08/04/2015.
 */


import java.util.HashMap;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class RegisterActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;

    private Spinner inputTitle;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputCity;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirmPassword;

    private ProgressDialog pDialog;
    private String[] arraySpinner;

    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*
        * Get text input fields from signup form
        * */
        inputFirstName = (EditText) findViewById(R.id.firstName);
        inputLastName = (EditText) findViewById(R.id.lastName);
        inputCity = (EditText) findViewById(R.id.city);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirmPassword = (EditText) findViewById(R.id.passwordConfirm);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        /*
        * ArrayAdapter for title spinner on signup form
        * */
        this.arraySpinner = new String[] {
                "Mr", "Ms"
        };
        inputTitle = (Spinner) findViewById(R.id.reg_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_row, arraySpinner);
        inputTitle.setAdapter(adapter);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
//        session = new SessionManager(getApplicationContext());

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String title = inputTitle.getSelectedItem().toString();;
                String firstName = inputFirstName.getText().toString();
                String lastName = inputLastName.getText().toString();
                String city = inputCity.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();
                String confirmPassword = inputConfirmPassword.getText().toString();

                if (!firstName.isEmpty() && !lastName.isEmpty() && !city.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
                    if(password.equals(confirmPassword)) {
                        if(password.length() >= 6) {
                            registerUser(title, firstName, lastName, city, email, password);
                        } else {
                            Toast.makeText(getApplicationContext(), "Password Must Be At Least 6 Characters!", Toast.LENGTH_LONG) .show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Passwords Do Not Match!", Toast.LENGTH_LONG) .show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Your Details!", Toast.LENGTH_LONG) .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginScreenActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params to register url
     * */
    private void registerUser(final String title, final String firstName, final String lastName, final String city, final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

            Map<String, String> params = new HashMap<String, String>();
            params.put("title", title);
            params.put("first_name", firstName);
            params.put("last_name", lastName);
            params.put("city", city);
            params.put("email", email);
            params.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.POST, AppConfig.URL_REGISTER, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                pDialog.setMessage(response.toString());
                hideDialog();

                try {
                    boolean error = response.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
//                        String uid = response.getString("uid");
//
//                        JSONObject user = response.getJSONObject("user");
//                        Long appId = user.getLong("app_id");
//                        String firstName = user.getString("first_name");
//                        String lastName = user.getString("last_name");
//                        String email = user.getString("email");
//                        String city = user.getString("city");

                        // Inserting row in users table
//                        db.addUser(appId, firstName, lastName, email, city, cvFilePath, profileImage);

                        // Launch login activity
                        Intent intent = new Intent( RegisterActivity.this, LoginScreenActivity.class);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "Signup Successful", Toast.LENGTH_LONG) .show();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = response.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
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
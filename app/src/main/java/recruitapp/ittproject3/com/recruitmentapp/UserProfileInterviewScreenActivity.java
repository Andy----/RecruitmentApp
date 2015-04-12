package recruitapp.ittproject3.com.recruitmentapp;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.SessionManager;
import recruitapp.ittproject3.com.recruitmentapp.helper.UserDetails;


public class UserProfileInterviewScreenActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    static final int REQUEST_VIDEO_CAPTURE = 1;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    File mediaFile;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnLogout;
    private Bundle bundle = new Bundle();
    String JsonString = "";
    JSONObject JsonObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_interview_screen);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        btnLogout = (Button) findViewById(R.id.action_logout);

        // Session manager
        session = new SessionManager(getApplicationContext());

        db = new SQLiteHandler(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Add user details to Bundle
        try {
            JsonObj = new JSONObject(getIntent().getStringExtra("userDetailsClass"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonString = JsonObj.toString();
        bundle.putString("JsonString", JsonString);
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch(position) {
            default:
            case 0:
                fragment = new ProfileFragment().newInstance(position + 1);
                fragment.setArguments(bundle);
                break;
            case 1:
                fragment = new InterviewFragment().newInstance(position + 1);
                break;
            case 2:
                fragment = new EditProfileFragment().newInstance(position + 1);
                fragment.setArguments(bundle);
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            default:
            case 0:
                mTitle = getString(R.string.title_section0);
//                mTitle = "Edit Profile";
                break;
            case 1:
              mTitle = getString(R.string.title_section1);
//                mTitle = "Profile";
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
//                mTitle = "Interview";
                break;

        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_user_profile_screen, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (item.getItemId() == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Image chooser for profile image
    public void profileImageClick() {
    }

    // Creates a directory in the app cache to store the videos if one does not exist
    // Creates the file to store the video and passes control to the android camera
    public void buttonOnClickRecord(View v){
        File newDir = new File(getExternalCacheDir(), "RecruitSwift");
        if(!newDir.isDirectory())
            newDir.mkdirs();
        else
            Toast.makeText(this, "Dir already exist", Toast.LENGTH_LONG).show();

        if(newDir.canWrite())
            mediaFile = new File(newDir, "myvideo.mp4");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            Uri videoUri = Uri.fromFile(mediaFile);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }


    // Call the VideoPlayerActivity and start it
    public void buttonOnClickView(View v){

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        this.startActivity(intent);
        }

    public void buttonOnClickUpload(View v){

        Intent intent = new Intent(this, JSONActivity.class);
        this.startActivity(intent);
    }

    // This Method gives you information about the buttonOnClickRecord method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {

            Toast.makeText(this, "Video saved to:\n" +
                    data.getData(), Toast.LENGTH_LONG).show();
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show();
        }
        }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    public void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(UserProfileInterviewScreenActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }
}

package recruitapp.ittproject3.com.recruitmentapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.SessionManager;

public class UserProfileInterviewScreenActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    static final int REQUEST_VIDEO_CAPTURE = 1;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private File imageFile;
    private File videoFile;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnLogout;
    private ImageView profileImage;
    private Map<String, String> userDeatilsMap;

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

                break;
            case 1:
                fragment = new JobApplicationFragment().newInstance(position + 1);
                break;
            case 2:
                fragment = new EditProfileFragment().newInstance(position + 1);

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
                mTitle = getString(R.string.title_section2);
                break;
            case 1:
              mTitle = getString(R.string.title_section0);
                break;
            case 2:
                mTitle = getString(R.string.title_section1);
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

    public void selectImage(View v) {

        File newDir = new File(getExternalCacheDir(), "RecruitSwift");
        if(!newDir.isDirectory())
            newDir.mkdirs();
        else
            Toast.makeText(this, "Dir already exist", Toast.LENGTH_LONG).show();

        if(newDir.canWrite())
            imageFile = new File(newDir, "profile.jpg");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileInterviewScreenActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent takeProfileImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takeProfileImage.resolveActivity(getPackageManager()) != null) {

                        Uri imageUri = Uri.fromFile(imageFile);
                        takeProfileImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takeProfileImage, 1);
                    }
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent takeProfileImage = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(takeProfileImage, 2);

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
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
            videoFile = new File(newDir, "intro.mp4");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            Uri videoUri = Uri.fromFile(videoFile);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    // Call the VideoPlayerActivity and start it
    public void buttonOnClickView(View v){

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        this.startActivity(intent);
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImage = (ImageView)findViewById(R.id.profileImage);
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
                String imageDir = getExternalCacheDir() + "/RecruitSwift/profile.jpg";
                Bitmap thumbnail = BitmapFactory.decodeFile(imageDir);
                try {
                    ExifInterface exif = new ExifInterface(imageDir);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    }
                    else if (orientation == 3) {
                        matrix.postRotate(180);
                    }
                    else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true); // rotating bitmap
                }
                catch (Exception e) {

                }
                profileImage.setImageBitmap(thumbnail);
                try{
                    String file_path = getExternalCacheDir() + "/RecruitSwift";
                    File dir = new File(file_path);
                    if(!dir.exists())
                        dir.mkdirs();
                    File file = new File(dir, "profile.jpg");
                    FileOutputStream fOut = new FileOutputStream(file);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                    }
                catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image", picturePath + "");
                try {
                    ExifInterface exif = new ExifInterface(picturePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    }
                    else if (orientation == 3) {
                        matrix.postRotate(180);
                    }
                    else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true); // rotating bitmap
                }
                catch (Exception e) {

                }
                profileImage.setImageBitmap(thumbnail);
                try{
                    String file_path = getExternalCacheDir() + "/RecruitSwift";
                    File dir = new File(file_path);
                    if(!dir.exists())
                        dir.mkdirs();
                    File file = new File(dir, "profile.jpg");
                    FileOutputStream fOut = new FileOutputStream(file);
                    thumbnail.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }


            }
            if(requestCode != REQUEST_VIDEO_CAPTURE) {
                String user = "";
                userDeatilsMap = db.getUserDetails();
                for (Map.Entry<String, String> entry : userDeatilsMap.entrySet()) {
                    if (entry.getKey().equals("email")) {
                        user = entry.getValue();
                    }
                }
                userDeatilsMap.put("profile_image_path", "globalUploadFolder" + File.separator + user + File.separator + "profile.jpg");
                System.out.println(imageFile.toString());
                db.updateUserDetails(userDeatilsMap);
            }
        }
        if (requestCode == REQUEST_VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
//                Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            }
             else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    public void logoutUser() {
        session.setLogin(false);

        db.deleteTable("user");
        db.deleteTable("jobapplication");
        db.deleteTable("questiontable");

        // Launching the login activity
        Intent intent = new Intent(UserProfileInterviewScreenActivity.this, LoginScreenActivity.class);
        startActivity(intent);
        finish();
    }

}

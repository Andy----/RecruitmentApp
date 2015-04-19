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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.SessionManager;


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
    private File imageFile;
    private File videoFile;
    private SessionManager session;
    private SQLiteHandler db;
    private Button btnLogout;
    private ImageView profileImage;
    private Map<String, String> userDeatilsMap;
    private String profileImageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_interview_screen);

//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                selectImage();
//            }
//        });
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

    // Image chooser for profile image
//    public void profileImageClick() {
//    }

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

//    public void buttonOnClickUpload(View v){
//
//        Intent intent = new Intent(this, JSONActivity.class);
//        this.startActivity(intent);
//    }

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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        profileImage = (ImageView)findViewById(R.id.profileImage);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == 1) {
//                String imageDir = getExternalCacheDir() + "/RecruitSwift/profile.jpg";
//
//                Bitmap thumbnail = ShrinkBitmap(imageDir, 400, 400);
//                try {
//                    ExifInterface exif = new ExifInterface(imageDir);
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                    Log.d("EXIF", "Exif: " + orientation);
//                    Matrix matrix = new Matrix();
//                    if (orientation == 6) {
//                        matrix.postRotate(90);
//                    }
//                    else if (orientation == 3) {
//                        matrix.postRotate(180);
//                    }
//                    else if (orientation == 8) {
//                        matrix.postRotate(270);
//                    }
//                    thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true); // rotating bitmap
//                }
//                catch (Exception e) {
//
//                }
//                profileImage.setImageBitmap(thumbnail);
//
//
////                try {
////                    Bitmap bitmap;
////                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
////
////                    bitmap = BitmapFactory.decodeFile(imageDir.getAbsolutePath(),
////                            bitmapOptions);
////
////
////                    profileImage = (ImageView)findViewById(R.id.profileImage);
////                    profileImage.setImageBitmap(bitmap);
////
////                    String path = android.os.Environment
////                            .getExternalStorageDirectory()
////                            + File.separator
////                            + "Phoenix" + File.separator + "default";
////                    imageDir.delete();
////                    OutputStream outFile = null;
////                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
////                    try {
////                        outFile = new FileOutputStream(file);
////                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
////                        outFile.flush();
////                        outFile.close();
////                    } catch (FileNotFoundException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
//            } else if (requestCode == 2) {
//
//                Uri selectedImage = data.getData();
//                String[] filePath = { MediaStore.Images.Media.DATA };
//                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
//                c.moveToFirst();
//                int columnIndex = c.getColumnIndex(filePath[0]);
//                String picturePath = c.getString(columnIndex);
//                c.close();
////                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
//                Bitmap thumbnail = ShrinkBitmap(picturePath, 400, 400);
//                Log.w("path of image", picturePath + "");
//                try {
//                    ExifInterface exif = new ExifInterface(picturePath);
//                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
//                    Log.d("EXIF", "Exif: " + orientation);
//                    Matrix matrix = new Matrix();
//                    if (orientation == 6) {
//                        matrix.postRotate(90);
//                    }
//                    else if (orientation == 3) {
//                        matrix.postRotate(180);
//                    }
//                    else if (orientation == 8) {
//                        matrix.postRotate(270);
//                    }
//                    thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true); // rotating bitmap
//                }
//                catch (Exception e) {
//
//                }
//                profileImage.setImageBitmap(thumbnail);
//            }
//            String user ="";
//            userDeatilsMap = db.getUserDetails();
//            for (Map.Entry<String, String> entry : userDeatilsMap.entrySet()){
//                if(entry.getKey().equals("email")){
//                    user =  entry.getValue();
//                }
//            }
//            userDeatilsMap.put("profile_image_path", "globalUploadFolder" + File.separator  + user + File.separator + "profile.jpg");
//            System.out.println(imageFile.toString());
//            db.updateUserDetails(userDeatilsMap);
//        }
//    }



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
    Bitmap ShrinkBitmap(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }
    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}

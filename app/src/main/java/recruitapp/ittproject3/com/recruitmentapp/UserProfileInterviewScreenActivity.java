package recruitapp.ittproject3.com.recruitmentapp;

import android.app.AlertDialog;
import android.content.Context;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import recruitapp.ittproject3.com.recruitmentapp.helper.SQLiteHandler;
import recruitapp.ittproject3.com.recruitmentapp.helper.SessionManager;

public class UserProfileInterviewScreenActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

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
    private String user = "";
    private String imageDir;
    static final int CAMERA_PHOTO = 1;
    static final int GALLERY_PHOTO = 2;
    static final int RECORDED_VIDEO = 3;
    static final int CV_FILE = 4;


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

        userDeatilsMap = db.getUserDetails();
        for (Map.Entry<String, String> entry : userDeatilsMap.entrySet()) {
            if (entry.getKey().equals("email")) {

                user = entry.getValue();
            }
        }
        if (!session.isLoggedIn()) {
            logoutUser();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (position) {
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

    public void selectProfileImage(View v) {

        File newDir = new File(getExternalCacheDir(), "RecruitSwift"+ File.separator + user+ File.separator);
        if (!newDir.isDirectory())
            newDir.mkdirs();
//        else
//            Toast.makeText(this, "Dir already exist", Toast.LENGTH_LONG).show();

        if (newDir.canWrite())
            imageFile = new File(newDir, "profile.jpg");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(UserProfileInterviewScreenActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {

                    Intent takeProfileImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takeProfileImage.resolveActivity(getPackageManager()) != null) {

                        Uri imageUri = Uri.fromFile(imageFile);
                        takeProfileImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(takeProfileImage, CAMERA_PHOTO);
                    }
                } else if (options[item].equals("Choose from Gallery")) {

                    Intent takeProfileImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(takeProfileImage, GALLERY_PHOTO);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    // Creates a directory in the app cache to store the videos if one does not exist
    // Creates the file to store the video and passes control to the android camera
    public void recordIntroVideo(View v) {
        File newDir = new File(getExternalCacheDir(), "RecruitSwift"+ File.separator + user + File.separator);
        if (!newDir.isDirectory())
            newDir.mkdirs();
//        else
//            Toast.makeText(this, "Dir already exist", Toast.LENGTH_LONG).show();

        if (newDir.canWrite())
            videoFile = new File(newDir, "intro.mp4");
        else
            Toast.makeText(this, "Dir not writable", Toast.LENGTH_LONG).show();

        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

            Uri videoUri = Uri.fromFile(videoFile);
            takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            startActivityForResult(takeVideoIntent, RECORDED_VIDEO);
        }
    }

    // Call the VideoPlayerActivity and start it
    public void viewIntroVideo(View v) {

        Intent intent = new Intent(this, VideoPlayerActivity.class);
        this.startActivity(intent);
    }

    public void selectCV(View v) {

        File newDir = new File(getExternalCacheDir(),  File.separator +"RecruitSwift"+ File.separator + user + File.separator);
        if (!newDir.isDirectory())
            newDir.mkdirs();
//        else
//            Toast.makeText(this, "Dir already exist", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, CV_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        imageDir = getExternalCacheDir() + File.separator + "RecruitSwift" + File.separator + user + File.separator + "profile.jpg" + File.separator;

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_PHOTO) {

                Bitmap thumbnail = BitmapFactory.decodeFile(imageDir);
                thumbnail = rotateImage(thumbnail, imageDir);
                profileImage.setImageBitmap(thumbnail);
                saveBitmap(thumbnail);
                Toast.makeText(this, "Press Save to make changes permanent", Toast.LENGTH_LONG).show();

            } else if (requestCode == GALLERY_PHOTO) {

                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                thumbnail = rotateImage(thumbnail, picturePath);
                profileImage.setImageBitmap(thumbnail);
                saveBitmap(thumbnail);
                Toast.makeText(this, "Press Save to make changes permanent", Toast.LENGTH_LONG).show();

            } else if (requestCode == RECORDED_VIDEO) {

                    Toast.makeText(this, "Press Save to make changes permanent", Toast.LENGTH_LONG).show();

            } else if (requestCode == CV_FILE) {

                    Uri selectedFile = data.getData();
                    String filename = getFilename(selectedFile);
                    saveFile(selectedFile, filename);
                    userDeatilsMap.put("cv_filePath", "globalUploadFolder" + File.separator + user + File.separator + filename);
                    userDeatilsMap.put("cv_fileName", filename);
                    db.updateUserDetails(userDeatilsMap);
            }
            if (requestCode == CAMERA_PHOTO || requestCode == GALLERY_PHOTO) {

                userDeatilsMap.put("profile_image_path", "globalUploadFolder" + File.separator + user + File.separator + "profile.jpg");
                db.updateUserDetails(userDeatilsMap);
            }
        }else if (resultCode == RESULT_CANCELED && requestCode == RECORDED_VIDEO) {

            Toast.makeText(this, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show();
        } else if(requestCode == RECORDED_VIDEO) {

            Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show();
        }else if (resultCode == RESULT_CANCELED && requestCode == CAMERA_PHOTO || requestCode == GALLERY_PHOTO) {

            Toast.makeText(this, "Photo Selection cancelled.",
                    Toast.LENGTH_LONG).show();
        } else if(requestCode == CAMERA_PHOTO || requestCode == GALLERY_PHOTO) {

            Toast.makeText(this, "Failed to set profile picture",
                    Toast.LENGTH_LONG).show();
        }else if(requestCode == CV_FILE && resultCode == RESULT_CANCELED) {

            Toast.makeText(this, "CV selection canceled",
                    Toast.LENGTH_LONG).show();
        }else if(requestCode == CV_FILE) {

            Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show();
        }
    }

   public Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {

            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }
        bmpFactoryOptions.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        return bitmap;
    }

    public void saveBitmap(Bitmap thumbnail){

        try {
            String file_path = getExternalCacheDir() + File.separator + "RecruitSwift" + File.separator + user + File.separator;
            File dir = new File(file_path);
            if (!dir.exists())
                dir.mkdirs();
            File file = new File(dir, "profile.jpg");
            FileOutputStream fOut = new FileOutputStream(file);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveFile(Uri sourceUri, String name)
    {
        String sourceFilename= sourceUri.getPath();
        String filePath = getExternalCacheDir().getPath() + File.separator  + "RecruitSwift" + File.separator + user + File.separator;
        String destinationFilename = filePath + name;

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;

        try {
            bis = new BufferedInputStream(new FileInputStream(sourceFilename));
            bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
            byte[] buf = new byte[1024];
            bis.read(buf);
            do {
                bos.write(buf);
            } while(bis.read(buf) != -1);
        } catch (IOException e) {

        } finally {
            try {
                if (bis != null) bis.close();
                if (bos != null) bos.close();
            } catch (IOException e) {

            }
        }
    }


    public Bitmap rotateImage(Bitmap thumbnail, String imageDir){

        try {
            thumbnail = ShrinkBitmap(imageDir, 400, 400);
            ExifInterface exif = new ExifInterface(imageDir);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {

        }
     return thumbnail;
    }
    public String getFilename(Uri uri)
    {
/*  Intent intent = getIntent();
    String name = intent.getData().getLastPathSegment();
    return name;*/

        String fileName = null;
        Context context=getApplicationContext();
        String scheme = uri.getScheme();
        if (scheme.equals("file")) {
            fileName = uri.getLastPathSegment();
        }
        else if (scheme.equals("content")) {
            String[] proj = { MediaStore.Video.Media.TITLE };
            Uri contentUri = null;
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null && cursor.getCount() != 0) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                cursor.moveToFirst();
                fileName = cursor.getString(columnIndex);
            }
        }
        return fileName;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
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

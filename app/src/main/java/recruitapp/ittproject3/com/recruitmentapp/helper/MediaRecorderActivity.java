package recruitapp.ittproject3.com.recruitmentapp.helper;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import recruitapp.ittproject3.com.recruitmentapp.Interview;
import recruitapp.ittproject3.com.recruitmentapp.R;

public class MediaRecorderActivity extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private Button action;
    private Context myContext;
    private LinearLayout cameraPreview;
    private String fileName, currentQuestion;
    private boolean cameraFront = false;
    private boolean recording = false;

    private VideoCaptureASyncTask videoTask;
    private RecordingTimer recTimer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;

        fileName = getIntent().getExtras().getString("fileName");
        currentQuestion = getIntent().getExtras().getString("currentQuestion");
        initialize();
        setCamera(findFrontFacingCamera());

        videoTask = new VideoCaptureASyncTask();
        videoTask.execute();
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                cameraFront = true;
                break;
            }
        }
        return cameraId;
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            CameraInfo info = new CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    public void onResume() {
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            if (findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
            }
            openCamera(findFrontFacingCamera());
            mPreview.refreshCamera(mCamera);
        }
    }

    public void initialize() {
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);

        mPreview = new CameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);

        action = (Button) findViewById(R.id.button_capture);
    }


    public void setCamera(int cameraId) {
        // if the camera preview is the front
        if (cameraFront) {
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

               openCamera(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            if (cameraId >= 0) {
                // open the backFacingCamera
                // set a picture callback
                // refresh the preview

               openCamera(cameraId);
                // mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    OnClickListener stopRecordingListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recording) {
                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                recording = false;
                Toast.makeText(MediaRecorderActivity.this, "Recording Saved", Toast.LENGTH_LONG).show();
                action.setBackgroundColor(Color.parseColor("#D9226bb3"));
                action.setText("Go Back");
                action.setOnClickListener(exitQuestion);
            }
        }
    };

    OnClickListener exitQuestion = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {

        mediaRecorder = new MediaRecorder();

        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Set the output of video to WebM
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.WEBM);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.VP8);
//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.VORBIS);
        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));

        mediaRecorder.setOutputFile(getExternalCacheDir() + "/RecruitSwift" + fileName);
        mediaRecorder.setOrientationHint(270);
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;

    }

    private void openCamera(int cameraId) {
        mCamera = Camera.open(cameraId);
        mCamera.setDisplayOrientation(90);
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    /*
    * AsyncTask - Executes a three second timer before starting video capture
    * Creates question timer thread on post execute
     */
    private class VideoCaptureASyncTask extends AsyncTask<Void, Integer, Void>
    {
        @Override
        protected void onPreExecute() {
            Button action = (Button) findViewById(R.id.button_capture);
            action.setText("Waiting...");
            action.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            // Display a 3 second timer on screen before recording starts
            for (int i = 5; i >=0; i--) {
                try {
                    publishProgress(i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            TextView recordCountDown = (TextView) findViewById(R.id.recordCountDown);
            if(values[0] == 0) {
                recordCountDown.setTextSize(150);
                recordCountDown.setText("GO!");
            } else {
                recordCountDown.setText(Integer.toString(values[0]));
            }
        }

        @Override
        protected void onPostExecute(Void param) {
            // Start capturing video
            if (!prepareMediaRecorder()) {
                Toast.makeText(MediaRecorderActivity.this, "PrepareMediaRecorder() error\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            } else {
                try {
                    TextView recordCountDown = (TextView) findViewById(R.id.recordCountDown);
                    recordCountDown.setText("");

                    Button action = (Button) findViewById(R.id.button_capture);
                    action.setBackgroundColor(Color.parseColor("#C23B22"));
                    action.setText("Finish Recording");
                    action.setOnClickListener(stopRecordingListener);
                    action.setEnabled(true);
                    recording = true;
                    mediaRecorder.start();
                    TextView currentQuestionTV = (TextView) findViewById(R.id.currentQuestionString);
                    currentQuestionTV.setText(currentQuestion);

                    recTimer = new RecordingTimer();
                    recTimer.execute();

                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * AsyncTask - Starts a new Thread for keeping time on each interview question.
     * Stops the recording after 3 minutes
     * */
    private class RecordingTimer extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for(int i=180 ; i >=0; i--) {
                if(recording) {
                    try {
                        publishProgress(i);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else break;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int minutes;
            int seconds;
            TextView questionCountDown = (TextView) findViewById(R.id.questionCountDown);

            minutes = (values[0] % 3600) / 60;
            seconds = (values[0] % 60);
            if(seconds >=0 && seconds < 10) {
                questionCountDown.setText("Time Remaining\n" + Integer.toString(minutes) + ":0" + Integer.toString(seconds));
            } else {
                questionCountDown.setText("Time Remaining\n" + Integer.toString(minutes) + ":" + Integer.toString(seconds));
            }
        }

        @Override
        protected void onPostExecute(Void param) {
            if(recording) {
                mediaRecorder.stop();
                Button action = (Button) findViewById(R.id.button_capture);
                action.setBackgroundColor(Color.parseColor("#D9226bb3"));
                action.setOnClickListener(exitQuestion);
                action.setText("Go Back");
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(videoTask != null) {
            if (videoTask.getStatus() == AsyncTask.Status.RUNNING) {
                videoTask.cancel(true);
            }
        }
        if(recTimer != null) {
            if (recTimer.getStatus() == AsyncTask.Status.RUNNING) {
                recTimer.cancel(true);
            }
        }
//        if(recording) {
//            mediaRecorder.stop();
//        }
        finish();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(videoTask != null) {
            if (videoTask.getStatus() == AsyncTask.Status.RUNNING) {
                videoTask.cancel(true);
            }
        }
        if(recTimer != null) {
            if (recTimer.getStatus() == AsyncTask.Status.RUNNING) {
                recTimer.cancel(true);
            }
        }
//        if(recording) {
//            mediaRecorder.stop();
//        }
    }
}
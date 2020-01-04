package com.innochi.audiorecorderbookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// Inspired by https://developer.android.com/guide/topics/media/mediarecorder#java
public class RecordActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordBookmark";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private String mAudioFileName = null;
    private String mBookmarkFileName = null;

    private boolean mIsRecording = false;
    private MediaRecorder mRecorder = null;
    private Date mRecordStartDate = null;

    // Requesting permission to RECORD_AUDIO
    private boolean mPermissionToRecordAccepted = false;
    private String [] mPermissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                mPermissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!mPermissionToRecordAccepted) finish();
    }

    private void startRecording() {
        ActivityCompat.requestPermissions(this, mPermissions, REQUEST_RECORD_AUDIO_PERMISSION);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsRecording = true;
        updateRecordButtonText();

        // Record to the external cache directory for visibility
        mRecordStartDate = new Date();
        String rootDirectoryPath = AppStorage.getAppRootDirectoryPath(this);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss").format(mRecordStartDate);
        mAudioFileName = rootDirectoryPath + timeStamp + AppStorage.AUDIO_FILE_EXTENSION;
        mBookmarkFileName = rootDirectoryPath + timeStamp + AppStorage.BOOKMARK_FILE_EXTENSION;

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsRecording = false;
        updateRecordButtonText();

        mRecordStartDate = null;
        mAudioFileName = null;
        mBookmarkFileName = null;

        if(mRecorder == null) return;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    public void onRecordClick(View view) {
        if(!mIsRecording) startRecording();
        else stopRecording();
    }

    public void updateRecordButtonText() {
        Button button = findViewById(R.id.startStopRecordButton);
        if(button != null) button.setText(mIsRecording ? R.string.stopRecording : R.string.startRecording);
    }

    public void onBookmarkClick(View view) {
        Date bookmarkDate = new Date();
        long dateDiff = getDateDiff(mRecordStartDate, bookmarkDate, TimeUnit.MILLISECONDS);

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(mBookmarkFileName, true)));
            out.println(dateDiff);
            out.close();
        } catch (IOException e) {
            // TODO: handle error
        }
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMs = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMs, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
    }

    @Override
    public void onStop() {
        super.onStop();

        stopRecording();
    }
}

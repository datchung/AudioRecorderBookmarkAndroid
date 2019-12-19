package com.innochi.audiorecorderbookmark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// Inspired by https://developer.android.com/guide/topics/media/mediarecorder#java
public class RecordActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    private static String bookmarkFileName = null;

    private boolean mIsRecording = false;
    private MediaRecorder recorder = null;
    private Date recordStartDate = null;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsRecording = true;
        updateRecordButtonText();

        // Record to the external cache directory for visibility
        recordStartDate = new Date();
        String rootDirectoryPath = AppStorage.getAppRootDirectoryPath(this);
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(recordStartDate);
        fileName = rootDirectoryPath + timeStamp + AppStorage.AUDIO_FILE_EXTENSION;
        bookmarkFileName = rootDirectoryPath + timeStamp + AppStorage.BOOKMARK_FILE_EXTENSION;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mIsRecording = false;
        updateRecordButtonText();

        recordStartDate = null;
        fileName = null;
        bookmarkFileName = null;

        if(recorder == null) return;
        recorder.stop();
        recorder.release();
        recorder = null;
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
        long dateDiff = getDateDiff(recordStartDate, bookmarkDate, TimeUnit.MILLISECONDS);

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(bookmarkFileName, true)));
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

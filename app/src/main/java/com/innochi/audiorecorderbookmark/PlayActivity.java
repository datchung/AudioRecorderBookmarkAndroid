package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.MemoryFile;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlayActivity extends AppCompatActivity {

    private MusicPlayer mPlayer = null;
    private String mFilePath = null;
    private int mOffsetMs = -5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("filePath");

        TextView titleView = findViewById(R.id.playRecordingTitle);
        if(titleView != null) titleView.setText(FileUtils.getFilenameWithoutDirectoryAndExtension(mFilePath));

        List<Integer> bookmarks = getBookmarks();
        populateBookmarks(bookmarks);

        mPlayer = new MusicPlayer(mFilePath, bookmarks, mOffsetMs);
        mPlayer.startPlaying();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.stopPlaying();
    }

    private List<Integer> getBookmarks() {
        return AppStorage.loadBookmarks(mFilePath.replace(AppStorage.AUDIO_FILE_EXTENSION, AppStorage.BOOKMARK_FILE_EXTENSION));
    }

    private static String msToHhmmss(int ms) {
        int hours   = ((ms / (1000*60*60)) % 24);
        int minutes = ((ms/ (1000*60)) % 60);
        int seconds = (ms/ 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void populateBookmarks(List<Integer> bookmarks) {
        LinearLayout layout = findViewById(R.id.bookmarksLayout);
        for(final Integer bookmark: bookmarks) {
            TextView view = new TextView(this);

            view.setText(msToHhmmss(bookmark));

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int offsetPosition = mPlayer.seekWithOffset(bookmark);
                    updateRecordingPositionDisplay(offsetPosition);
                }});

            view.setPadding(0, 16, 0, 16);

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void updateRecordingPositionDisplay(int position) {
        TextView view = findViewById(R.id.recordingPosition);
        if(view == null) return;

        view.setText(msToHhmmss(position));
    }

    public void onPlayClick(View view) {
        if(mPlayer == null) return;
        if(mPlayer.isPlaying()) return;

        mPlayer.resume();
    }

    public void onPauseClick(View view) {
        mPlayer.pausePlaying();
    }

    public void onStopClick(View view) {
        mPlayer.stopPlaying();
    }

    public void onNextBookmarkClick(View view) { mPlayer.seekToNextBookmark(); }

    public void onPreviousBookmarkClick(View view) { mPlayer.seekToPreviousBookmark(); }
}

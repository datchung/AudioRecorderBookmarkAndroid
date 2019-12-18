package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

    private MediaPlayer mPlayer = null;
    private String mFilePath = null;
    private int mPlayerLength = 0;
    private List<Integer> mBookmarks = null;
    private int mBookmarksCurrentIndex = 0;
    private int mOffsetMs = -5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("filePath");

        startPlaying();

        loadBookmarks();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlaying();
    }

    private void loadBookmarks() {
        String bookmarksFilePath = mFilePath.replace(AppStorage.AUDIO_FILE_EXTENSION, AppStorage.BOOKMARK_FILE_EXTENSION);
        mBookmarks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(bookmarksFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Integer parsed = intTryParse(line, -1);
                if(parsed < 0) continue;
                mBookmarks.add(parsed);
            }

            populateBookmarks();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateBookmarks() {
        LinearLayout layout = findViewById(R.id.bookmarksLayout);
        for(final Integer bookmark: mBookmarks) {
            TextView view = new TextView(this);
            view.setText(bookmark.toString());

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    seekWithOffset(bookmark);
                }});

            view.setPadding(0, 16, 0, 16);

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    // https://stackoverflow.com/a/8392032/4856020
    public Integer intTryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }
    }

    private void pausePlaying() {
        if(mPlayer == null) return;

        mPlayer.pause();
        mPlayerLength = mPlayer.getCurrentPosition();
    }

    private void stopPlaying() {
        if(mPlayer == null) return;

        mPlayer.release();
        mPlayer = null;
    }

    private void seekWithOffset(int position) {
        if(mPlayer == null) return;

        int offsetPostion = position + mOffsetMs;
        if(offsetPostion < 0) offsetPostion = 0;
        mPlayer.seekTo(offsetPostion);
    }

    public void onPlayClick(View view) {
        if(mPlayer == null) {
            startPlaying();
            return;
        }

        if(mPlayer.isPlaying()) return;

        // Resume
        seekWithOffset(mPlayerLength);
        mPlayer.start();
    }

    public void onPauseClick(View view) {
        pausePlaying();
    }

    public void onStopClick(View view) {
        stopPlaying();
    }

    public void onNextBookmarkClick(View view) {
        int bookmarksSize = mBookmarks.size();
        if(mBookmarksCurrentIndex >= bookmarksSize) return;

        seekWithOffset(mBookmarks.get(mBookmarksCurrentIndex));
        if(mBookmarksCurrentIndex < bookmarksSize - 1) ++mBookmarksCurrentIndex;
    }

    public void onPreviousBookmarkClick(View view) {
        if(mBookmarksCurrentIndex < 0) return;

        seekWithOffset(mBookmarks.get(mBookmarksCurrentIndex));
        if(mBookmarksCurrentIndex > 0) --mBookmarksCurrentIndex;
    }
}

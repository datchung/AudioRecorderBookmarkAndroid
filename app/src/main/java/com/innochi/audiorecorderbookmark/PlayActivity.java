package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity {

    private MusicPlayer mPlayer = null;
    private String mFilePath = null;
    private int mOffsetMs = 0;
    private SeekBar mSeekBar = null;
    private TextView mSeekBarTextView = null;
    private Timer mSeekBarTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ActionBarHelper.enableBackButton(this);

        loadPreferences();

        Intent intent = getIntent();
        mFilePath = intent.getStringExtra("filePath");

        TextView titleView = findViewById(R.id.playRecordingTitle);
        if(titleView != null) titleView.setText(FileUtils.getFilenameWithoutDirectoryAndExtension(mFilePath));

        List<Bookmark> bookmarks = getBookmarks();
        populateBookmarks(bookmarks);

        mPlayer = new MusicPlayer(mFilePath, getBookmarksAsMs(bookmarks));
        mPlayer.startPlaying();
        updatePlayButtonState(true);
    }

    private List<Integer> getBookmarksAsMs(List<Bookmark> bookmarks) {
        List<Integer> bookMarksMs = new ArrayList<>();

        for(Bookmark bookmark: bookmarks)
            bookMarksMs.add(bookmark.ms);

        return bookMarksMs;
    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(preferences == null) return;

        try {
            String offsetSecondsString = preferences.getString(getString(R.string.bookmarkOffset_key), "0");
            int offsetSeconds = Integer.parseInt(offsetSecondsString);
            mOffsetMs = offsetSeconds * 1000;
        }
        catch(Exception e) {}
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeSeekBar();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.stopPlaying();
        clearSeekBarTimer();
    }

    private void clearSeekBarTimer() {
        if(mSeekBarTimer == null) return;

        mSeekBarTimer.cancel();
        mSeekBarTimer = null;
    }

    private void initializeSeekBar() {
        mSeekBar = findViewById(R.id.playSeekBar);
        int duration = mPlayer.getDuration();
        mSeekBar.setMax(duration / 1000);

        TextView durationTextView = findViewById(R.id.playRecordingDuration);
        durationTextView.setText(msToHhmmss(duration));

        mSeekBarTextView = findViewById(R.id.playRecordingPosition);

        clearSeekBarTimer();
        mSeekBarTimer = new Timer();
        mSeekBarTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if(mPlayer != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int position = mPlayer.getPosition();
                            int progress = position / 1000;
                            mSeekBar.setProgress(progress);
                            mSeekBarTextView.setText(msToHhmmss(position));
                        }
                    });
                }
            }
        }, 0, 1000);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPlayer.seek(mSeekBar.getProgress() * 1000);
            }
        });
    }

    private List<Bookmark> getBookmarks() {
        List<Bookmark> bookmarks = AppStorage.loadBookmarks(mFilePath.replace(AppStorage.AUDIO_FILE_EXTENSION, AppStorage.BOOKMARK_FILE_EXTENSION));
        List<Bookmark> bookmarksWithOffset = new ArrayList<>();
        for(Bookmark bookmark: bookmarks) {
            int bookmarkWithOffset = bookmark.ms + mOffsetMs;
            if(bookmarkWithOffset < 0) bookmarkWithOffset = 0;
            bookmark.ms = bookmarkWithOffset;
            bookmarksWithOffset.add(bookmark);
        }

        return bookmarksWithOffset;
    }

    private static String msToHhmmss(int ms) {
        int hours   = ((ms / (1000*60*60)) % 24);
        int minutes = ((ms/ (1000*60)) % 60);
        int seconds = (ms/ 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void populateBookmarks(List<Bookmark> bookmarks) {
        LinearLayout layout = findViewById(R.id.bookmarksLayout);
        for(final Bookmark bookmark: bookmarks) {
            TextView view = new TextView(this);

            view.setTag(bookmark.ms);
            view.setText(msToHhmmss(bookmark.ms) + (bookmark.note == null ? "" : " " + bookmark.note));

            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    onBookmarkClick(bookmark.ms);
                }});

            view.setPadding(0, 16, 0, 16);

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private void updatePlayButtonState(boolean isPlaying) {
        ImageButton button = findViewById(R.id.playButton);
        button.setImageResource(isPlaying ? R.drawable.ic_appbar_control_pause : R.drawable.ic_appbar_control_play);
    }

    public void onPlayToggleClick(View view) {
        if(mPlayer == null) {
            updatePlayButtonState(false);
            return;
        }
        if(mPlayer.isPlaying()) {
            mPlayer.pausePlaying();
            updatePlayButtonState(false);
            return;
        }

        try {
            mPlayer.resume();
        }
        catch(Exception e) {
            mPlayer.startPlaying();
        }
        updatePlayButtonState(true);
    }

    public void onStopClick(View view) {
        mPlayer.stopPlaying();
    }

    private void onBookmarkClick(int bookmark) {
        int bookmarkBeforeSeek = mPlayer.getCurrentBookmark();
        mPlayer.seekToBookmark(bookmark);
        View bookmarksView = findViewById(R.id.bookmarksLayout);
        setBookmarkHighlight(bookmarkBeforeSeek, bookmark, bookmarksView);
    }

    public void onNextBookmarkClick(View view) {
        int bookmarkBeforeSeek = mPlayer.getCurrentBookmark();
        int bookmark = mPlayer.seekToNextBookmark();
        View bookmarksView = findViewById(R.id.bookmarksLayout);
        setBookmarkHighlight(bookmarkBeforeSeek, bookmark, bookmarksView);
    }

    public void onPreviousBookmarkClick(View view) {
        int bookmarkBeforeSeek = mPlayer.getCurrentBookmark();
        int bookmark = mPlayer.seekToPreviousBookmark();
        View bookmarksView = findViewById(R.id.bookmarksLayout);
        setBookmarkHighlight(bookmarkBeforeSeek, bookmark, bookmarksView);
    }

    private void setBookmarkHighlight(int bookmarkBeforeSeek, int bookmark, View bookmarksView) {
        if(bookmarkBeforeSeek >= 0 && bookmark >= 0) {
            View bookmarkViewBeforeSeek = bookmarksView.findViewWithTag(bookmarkBeforeSeek);
            if(bookmarkViewBeforeSeek != null) bookmarkViewBeforeSeek.setBackgroundColor(Color.TRANSPARENT);
        }

        if(bookmark >= 0) {
            View bookmarkView = bookmarksView.findViewWithTag(bookmark);
            if(bookmarkView != null) {
                bookmarkView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                scrollToView(bookmarkView);
            }
        }
    }

    private void scrollToView(final View view){
        final ScrollView scrollView = findViewById(R.id.bookmarksScrollView);
        if(scrollView == null) return;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }

//    @Override
//    public void start() {
//        mPlayer.start();
//    }

//    @Override
//    public void pause() {
//        mPlayer.pausePlaying();
//    }

//    @Override
//    public int getDuration() {
//        return mPlayer.getDuration();
//    }

//    @Override
//    public int getCurrentPosition() {
//        return mPlayer.getPosition();
//    }

//    @Override
//    public void seekTo(int i) {
//        mPlayer.seek(i);
//    }

//    @Override
//    public boolean isPlaying() {
//        return mPlayer.isPlaying();
//    }
}

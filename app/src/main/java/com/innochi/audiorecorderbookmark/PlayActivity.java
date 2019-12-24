package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

public class PlayActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private MusicPlayer mPlayer = null;
    private MusicController controller = null;
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

        setController();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayer.stopPlaying();
    }

    private void setController(){
        //set the controller up
        controller = new MusicController(this);

        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPreviousBookmarkClick(v);
                controller.show(0);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextBookmarkClick(v);
                controller.show(0);
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.bookmarksLayout));
        controller.setEnabled(true);
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

            view.setTag(bookmark);
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

    public void onNextBookmarkClick(View view) {
        int bookmarkBeforeSeek = mPlayer.getCurrentBookmark();
        int bookmark = mPlayer.seekToNextBookmark();
        View bookmarksView = findViewById(R.id.bookmarksLayout);

        if(bookmarkBeforeSeek >= 0 && bookmark >= 0) {
            View bookmarkViewBeforeSeek = bookmarksView.findViewWithTag(bookmarkBeforeSeek);
            if(bookmarkViewBeforeSeek != null) bookmarkViewBeforeSeek.setBackgroundColor(Color.WHITE);
        }

        if(bookmark >= 0) {
            View bookmarkView = bookmarksView.findViewWithTag(bookmark);
            if(bookmarkView != null) {
                bookmarkView.setBackgroundColor(Color.GREEN);
                scrollToView(bookmarkView);
            }
        }
    }

    public void onPreviousBookmarkClick(View view) {
        int bookmarkBeforeSeek = mPlayer.getCurrentBookmark();
        int bookmark = mPlayer.seekToPreviousBookmark();
        View bookmarksView = findViewById(R.id.bookmarksLayout);

        if(bookmarkBeforeSeek >= 0 && bookmark >= 0) {
            View bookmarkViewBeforeSeek = bookmarksView.findViewWithTag(bookmarkBeforeSeek);
            if(bookmarkViewBeforeSeek != null) bookmarkViewBeforeSeek.setBackgroundColor(Color.WHITE);
        }

        if(bookmark >= 0) {
            View bookmarkView = bookmarksView.findViewWithTag(bookmark);
            if(bookmarkView != null) {
                bookmarkView.setBackgroundColor(Color.GREEN);
                scrollToView(bookmarkView);
            }
        }
    }

    private void scrollToView(final View view){
        final ScrollView scrollView = (ScrollView)findViewById(R.id.bookmarksScrollView);
        if(scrollView == null) return;

        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }

    @Override
    public void start() {
        mPlayer.start();
    }

    @Override
    public void pause() {
        mPlayer.pausePlaying();
    }

    @Override
    public int getDuration() {
        return mPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mPlayer.getPosition();
    }

    @Override
    public void seekTo(int i) {
        mPlayer.seek(i);
    }

    @Override
    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}

package com.innochi.audiorecorderbookmark;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicPlayer {
    private MediaPlayer mPlayer = null;
    private String mFilePath = null;
    private int mOffsetMs = 0;
    private List<Integer> mBookmarks = null;
    private int mBookmarksCurrentIndex = -1;

    public MusicPlayer(String filePath, List<Integer> bookmarks, int offsetMs) {
        mFilePath = filePath;
        mBookmarks = bookmarks;
        mOffsetMs = offsetMs;
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e("", "prepare() failed");
        }
    }

    public void pausePlaying() {
        if(mPlayer == null) return;

        mPlayer.pause();
    }

    public void stopPlaying() {
        if(mPlayer == null) return;

        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying() {
        return mPlayer == null ? false : mPlayer.isPlaying();
    }

    public void resume() {
        mPlayer.seekTo(mPlayer.getCurrentPosition());
        mPlayer.start();
    }

    public int seekWithOffset(int position) {
        if(mPlayer == null) return 0;

        int offsetPosition = position + mOffsetMs;
        if(offsetPosition < 0) offsetPosition = 0;
        mPlayer.seekTo(offsetPosition);

        return offsetPosition;
    }

    public int seekToNextBookmark() {
        int bookmarksSize = mBookmarks.size();
        if(mBookmarksCurrentIndex >= bookmarksSize - 1) return -1;

        ++mBookmarksCurrentIndex;
        int bookmark = mBookmarks.get(mBookmarksCurrentIndex);
        seekWithOffset(bookmark);
        return bookmark;
    }

    public int seekToPreviousBookmark() {
        if(mBookmarksCurrentIndex < 1) return -1;

        --mBookmarksCurrentIndex;
        int bookmark = mBookmarks.get(mBookmarksCurrentIndex);
        seekWithOffset(bookmark);
        return bookmark;
    }

    public int getCurrentBookmark() {
        if(mBookmarksCurrentIndex < 0) return -1;
        return mBookmarks.get(mBookmarksCurrentIndex);
    }

    public int getPosition(){
        return mPlayer == null ? 0 : mPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mPlayer == null ? 0 : mPlayer.getDuration();
    }

    public void seek(int position){
        if(mPlayer == null) return;
        mPlayer.seekTo(position);
    }

    public void start(){
        if(mPlayer == null) return;
        mPlayer.start();
    }
}

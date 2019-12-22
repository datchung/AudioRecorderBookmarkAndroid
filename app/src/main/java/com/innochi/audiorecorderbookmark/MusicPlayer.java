package com.innochi.audiorecorderbookmark;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {
    private MediaPlayer mPlayer = null;
    private String mFilePath = null;
    private int mPlayerLength = 0;
    private int mOffsetMs = 0;

    public MusicPlayer(String filePath, int offsetMs) {
        mFilePath = filePath;
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
        mPlayerLength = mPlayer.getCurrentPosition();
    }

    public void stopPlaying() {
        if(mPlayer == null) return;

        mPlayer.release();
        mPlayer = null;
    }

    public boolean isPlaying() {
        return mPlayer.isPlaying();
    }

    public void resume() {
        mPlayer.start();
    }

    public void seekWithOffset(int position) {
        if(mPlayer == null) return;

        int offsetPostion = position + mOffsetMs;
        if(offsetPostion < 0) offsetPostion = 0;
        mPlayer.seekTo(offsetPostion);

        //updateRecordingPositionDisplay(offsetPostion);
    }
}

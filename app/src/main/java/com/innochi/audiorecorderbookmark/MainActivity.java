package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onRecordClick(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    public void onPlayClick(View view) {
        Intent intent = new Intent(this, RecordingsActivity.class);
        startActivity(intent);
    }

    public void onSettingsClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}

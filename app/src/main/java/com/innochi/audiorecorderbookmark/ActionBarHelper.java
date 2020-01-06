package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public final class ActionBarHelper {
    public static void enableBackButton(AppCompatActivity context) {
        if(context == null) return;

        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar == null) return;

        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}

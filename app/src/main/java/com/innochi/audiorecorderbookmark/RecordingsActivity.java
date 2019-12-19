package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class RecordingsActivity extends AppCompatActivity {

    private List<File> mFiles = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordings);

        populateRecordings();
    }

    private void populateRecordings() {
        mFiles = AppStorage.getFiles(AppStorage.getAppRootDirectoryPath(this));

        LinearLayout layout = findViewById(R.id.recordingsLayout);

        for (final File file : mFiles) {
            TextView view = new TextView(this);
            view.setText(getFilenameWithoutExtension(file));

            final RecordingsActivity self = this;
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(self, PlayActivity.class);
                    intent.putExtra("filePath", file.getAbsolutePath());
                    startActivity(intent);
                }});

            view.setPadding(0, 16, 0, 16);
//            view.setLayoutParams(new ViewGroup.MarginLayoutParams
//                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private static String getFilenameWithoutExtension(File file) {
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }
}

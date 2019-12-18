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
            view.setText(file.getName());

            final RecordingsActivity self = this;
            view.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(self, PlayActivity.class);
                    intent.putExtra("filePath", file.getAbsolutePath());
                    startActivity(intent);
                }});

            view.setPadding(0, 16, 0, 16);

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}

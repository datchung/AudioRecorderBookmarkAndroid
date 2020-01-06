package com.innochi.audiorecorderbookmark;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        ActionBarHelper.enableBackButton(this);

        populateRecordings();
    }

    private void populateRecordings() {
        mFiles = AppStorage.getFiles(AppStorage.getAppRootDirectoryPath(this));

        LinearLayout layout = findViewById(R.id.recordingsLayout);

        for (final File file : mFiles) {
            View view = getRecordingView(file);

            layout.addView(view,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private View getRecordingView(final File file) {
        // Main layout
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // File name
        TextView textView = new TextView(this);
        textView.setText(FileUtils.getFilenameWithoutExtension(file.getName()));

        // File name click
        final RecordingsActivity self = this;
        textView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(self, PlayActivity.class);
                intent.putExtra("filePath", file.getAbsolutePath());
                startActivity(intent);
            }});

        // Add file name to main layout
        LinearLayout.LayoutParams textViewLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.weight = 1;
        layout.addView(textView, textViewLayoutParams);

        // Delete button
        Button button = new Button(this);
        button.setText(R.string.delete);

        // Delete click
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                confirmDelete(file, layout);
            }});

        // Add delete button to main layout
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.weight = 0;
        layout.addView(button, buttonLayoutParams);

        return layout;
    }

    private void removeRecordingView(View recordingView) {
        LinearLayout recordingsView = findViewById(R.id.recordingsLayout);
        recordingsView.removeView(recordingView);
    }

    private void confirmDelete(final File file, final View view) {
        // https://stackoverflow.com/a/2478662/4856020
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        deleteFile(file);
                        removeRecordingView(view);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.confirmAction)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    private void deleteFile(File file) {
        try {
            // Delete audio
            file.delete();

            // Delete bookmarks
            File bookmarksFile = new File(file.getAbsolutePath().replace(AppStorage.AUDIO_FILE_EXTENSION, AppStorage.BOOKMARK_FILE_EXTENSION));
            if(bookmarksFile.exists()) bookmarksFile.delete();
        }
        catch(Exception e) {
        }
    }
}

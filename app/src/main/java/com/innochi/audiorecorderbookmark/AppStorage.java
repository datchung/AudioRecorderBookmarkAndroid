package com.innochi.audiorecorderbookmark;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class AppStorage {
    public static final String AUDIO_FILE_EXTENSION = ".3gp";
    public static final String BOOKMARK_FILE_EXTENSION = ".txt";

    public static String getAppRootDirectoryPath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + "/";
    }

    public static List<File> getFiles(String rootDirectory) {
        File folder = new File(rootDirectory);
        File[] files = folder.listFiles();
        ArrayList<File> filteredFiles = new ArrayList<>();

        for (final File file : files) {
            if (!file.isFile() || !file.getName().contains(AUDIO_FILE_EXTENSION)) continue;
            filteredFiles.add(file);
        }

        return filteredFiles;
    }
}

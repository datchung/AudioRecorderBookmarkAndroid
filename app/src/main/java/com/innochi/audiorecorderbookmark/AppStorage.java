package com.innochi.audiorecorderbookmark;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    public static List<Integer> loadBookmarks(String bookmarksFilePath) {
        List<Integer> bookmarks = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(bookmarksFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                Integer parsed = intTryParse(line, -1);
                if(parsed < 0) continue;
                bookmarks.add(parsed);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bookmarks;
    }

    // https://stackoverflow.com/a/8392032/4856020
    private static Integer intTryParse(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}

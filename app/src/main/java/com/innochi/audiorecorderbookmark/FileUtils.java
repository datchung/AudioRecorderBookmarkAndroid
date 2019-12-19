package com.innochi.audiorecorderbookmark;

import java.io.File;

public final class FileUtils {
    public static String getFilenameWithoutExtension(String fileName) {
        int pos = fileName.lastIndexOf(".");
        if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    public static String getFilenameWithoutDirectoryAndExtension(String filePath) {
        File file = new File(filePath);
        return getFilenameWithoutExtension(file.getName());
    }
}

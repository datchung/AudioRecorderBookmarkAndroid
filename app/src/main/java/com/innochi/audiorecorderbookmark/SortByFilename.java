package com.innochi.audiorecorderbookmark;

import java.io.File;
import java.util.Comparator;

public class SortByFilename implements Comparator<File> {
    @Override
    public int compare(File file, File t1) {
        return t1.getName().compareTo(file.getName());
    }
}

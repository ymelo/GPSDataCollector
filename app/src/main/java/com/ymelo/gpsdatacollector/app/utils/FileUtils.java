package com.ymelo.gpsdatacollector.app.utils;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yohann on 04/01/15.
 */
public class FileUtils {
    private static String DIR = "gps_data";
    static public FileOutputStream getFileWriter(Context context, String fileName) throws IOException {
        if(context == null)
            return null;
        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE | Context.MODE_APPEND);
        return fos;
    }

    public static FileInputStream getFileInputStream(Context context, String filename) throws FileNotFoundException {
        return context.openFileInput(filename);
    }

    public static String getFileContent(Context context, String filename) throws IOException {
        FileInputStream fis = getFileInputStream(context, filename);
        StringBuffer fileContent = new StringBuffer("");
        byte[] buffer = new byte[1024];
        int n;
        while ((n = fis.read(buffer)) != -1)
        {
            fileContent.append(new String(buffer, 0, n));
        }
        return fileContent.toString();
    }

    public static File[] getFileList(Context context) {
        return context.getFilesDir().listFiles();
    }
}

package com.image.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd_HHmmss");

    /**
     *
     * @param appContext - it has to be Application context
     */
    public static File getAppFilesDir(String subdirName, Context appContext) {
        File dir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = appContext.getExternalFilesDir(subdirName);

            if (dir == null) {
                dir = getInternalFileDir(subdirName, appContext);
            }
        } else {
            dir = getInternalFileDir(subdirName, appContext);
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    /**
     *
     * @param appContext - it has to be Application context
     */
    public static File getCacheDir(Context appContext) {
        File dir;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = appContext.getCacheDir();
        } else {
            dir = appContext.getExternalCacheDir();
        }
        return dir;
    }

    private static File getInternalFileDir(String subdirName, Context appContext) {
        File dir;
        File filesDir = appContext.getFilesDir();
        dir = new File(filesDir, subdirName);
        return dir;
    }

    /**
     *
     * @param appContext - it has to be Application context
     */
    public static File getPublicDir(String subdirName, Context appContext) {
        File dir = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(subdirName);
        }
        return dir;
    }

    public static String getImageName() {
        return "IMG_" + DATE_FORMAT.format(new Date()) + ".jpg";
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

//    /**
//     * Copies file and update progress in IActionTaskUpdateListener
//     *
//     *
//     * @param file  file to update progress in IActionTaskUpdateListener
//     * @param in  input stream for file
//     * @param out  result output stream
//     * @param updateTaskListenerWrapper  progress updater
//     * @param contentLength  total file size
//     */

//    public static void copyFile(File file, InputStream in, OutputStream out, IActionTaskUpdateListener updateTaskListenerWrapper, long contentLength) throws IOException {
//        byte[] buffer = new byte[1024];
//        int read;
//
//        float total = 0f;
//
//        while ((read = in.read(buffer)) != -1) {
//            total += read;
//            updateTaskListenerWrapper.onUpdate(file, total / contentLength, 0);
//            out.write(buffer, 0, read);
//        }
//    }

    public static String readFromAssets(Context context, String filePath) {
        StringBuilder buf = new StringBuilder();
        InputStream json = null;
        BufferedReader in = null;
        try {
            json = context.getAssets().open(filePath);
            in = new BufferedReader(new InputStreamReader(json));
            String str;

            while ((str = in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            Log.d("Exception", e.getMessage());
        }
        return buf.toString();
    }

}

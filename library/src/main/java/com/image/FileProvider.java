package com.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.image.utils.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dima on 4/16/15.
 */
public class FileProvider {

    public static final String TAG = FileProvider.class.getSimpleName();

    public static final String VIDEO_ATTACH = ".mp4";
    public static final String IMAGE_ATTACH = ".iattach";

    private String mPhotoPath;
    private String mVideoPath;

    private Context mAppContext;

    private static FileProvider mInstance;

    private FileProvider(Context appContext) {
        mAppContext = appContext;
        mPhotoPath = FileUtil.getAppFilesDir(".photos", mAppContext).getPath();
        mVideoPath = FileUtil.getAppFilesDir(".video", mAppContext).getPath();
    }

    public static synchronized FileProvider getInstance(Context appContext){
        if(mInstance == null){
            mInstance = new FileProvider(appContext);
        }
        return mInstance;
    }

    public void setPhotoPath(String path){
        mPhotoPath = path;
    }

    public void setVideoPath(String path){
        mVideoPath = path;
    }

    public String getImagePath() {
        return mPhotoPath;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public File getFileForImage() {
        File file = new File(mPhotoPath, System.currentTimeMillis() + IMAGE_ATTACH);
        return file;
    }

    public File getFileForVideo() {
        File file = new File(mVideoPath, System.currentTimeMillis() + VIDEO_ATTACH);
        return file;
    }

    public File getPhotoTmpDir() {
        return FileUtil.getAppFilesDir(".tmp_photo", mAppContext);
    }

    public String createVideoPreview(String videoName, String videoPath) {
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(Uri.parse(videoPath).getPath(), MediaStore.Images.Thumbnails.MINI_KIND);

        if (thumb != null) {
            File f = new File(mPhotoPath, videoName + ".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "", e);
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
            } catch (FileNotFoundException e) {
                Log.e(TAG, "", e);
            }
            if (fos != null) {
                try {
                    fos.write(bitmapdata);
                } catch (IOException e) {
                    Log.e(TAG, "", e);
                }
            }
            return f.getPath();
        }
        return null;
    }
}

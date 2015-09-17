package com.image.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dima on 3/26/15.
 */
public class BitmapUtils {

    public static final String TAG = "RotateBitmapUtils";

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private static Matrix configureMatrix(int rotation, int rotationDegrees) {
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationDegrees);
        }
        return matrix;
    }

    public static Bitmap getRotatedBitmap(Uri selectedImage) {
        Bitmap adjustedBitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            ExifInterface exif = new ExifInterface(selectedImage.getPath());
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            if(rotationInDegrees != 0) {
                rotationInDegrees = 360 - rotationInDegrees;
                Bitmap originalBitmap = BitmapFactory.decodeFile(selectedImage.getPath(), options);
                Matrix matrix = configureMatrix(rotation, rotationInDegrees);
                adjustedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(),
                        originalBitmap.getHeight(), matrix, true);
                originalBitmap.recycle();
                new File(selectedImage.getPath()+ ".jpg").renameTo(new File(selectedImage.getPath()));
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }

        return adjustedBitmap;
    }

    public static void saveImage(String path, Bitmap bitmap){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

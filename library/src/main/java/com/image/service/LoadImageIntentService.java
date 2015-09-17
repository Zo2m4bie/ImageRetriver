package com.image.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.fullscreen.R;
import com.image.FileProvider;
import com.image.ImageRetriever;
import com.image.MediaAttachment;
import com.image.utils.BitmapUtils;
import com.image.utils.UIMessage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dima on 4/1/15.
 */
public class LoadImageIntentService extends IntentService{

    private static final String TAG = LoadImageIntentService.class.getName();

//    private static final String IS_IT_VIDEO = "is_it_video";
    public static final String MEDIA_URI = "media_uri";
    public static final String REQUEST_CODE = "request_code";
    public static final String RESULT_CODE = "result_code";
    public static final String DATA_URI = "data_uri";


    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    private static final int SELECT_VIDEO_ACTIVITY_REQUEST_CODE = 102;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 103;

    private FileProvider mFileProvider;

    public LoadImageIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
        initFileProvider();
        MediaAttachment attachment = processResultImage(intent, requestCode);

        String previewPath = null;
        if (attachment != null &&
                (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE
                        || requestCode == SELECT_VIDEO_ACTIVITY_REQUEST_CODE) ){ // it's video, need create preview
            File file = attachment.getFile();
            previewPath = mFileProvider.createVideoPreview(file.getName(), file.getPath());
            attachment.setPathToPreview(previewPath);
        }
        Intent intentResponse = new Intent();
        intentResponse.setAction(ImageRetriever.INTENT_MEDIA_ATTACH);
        intentResponse.putExtra(ImageRetriever.INTENT_MEDIA_ATTACH, attachment);
//        intentResponse.putExtra(ImageRetriever.INTENT_PATH, previewPath);
        sendStickyBroadcast(intentResponse);
    }

    private void initFileProvider() {
        if(mFileProvider == null) {
            mFileProvider = FileProvider.getInstance(getApplicationContext());
        }
    }

    public MediaAttachment processResultImage(Intent intent, int requestCode) {
        MediaAttachment attachment = null;//intent.getParcelableExtra(MEDIA_ATTACH);
        Uri mMediaUri = intent.getParcelableExtra(MEDIA_URI);
        int resultCode  = intent.getIntExtra(RESULT_CODE, 0);
        Uri data = intent.getParcelableExtra(DATA_URI);
        //Intent data
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE // take image via camera
                    || requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) { // take video via camera

                if ((mMediaUri != null)) {
                    attachment = MediaAttachment.fromFile(new File(mMediaUri.getPath()), false);
                }

            } else if(requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE){ // take image from gallery
                attachment = getAttachmentFromGallery(data, false);

            }else if(requestCode == SELECT_VIDEO_ACTIVITY_REQUEST_CODE){ // take video from gallery
                attachment = getAttachmentFromGallery(data, true);

            }

            if(attachment != null && (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE // take image via camera
                    || requestCode == SELECT_IMAGE_ACTIVITY_REQUEST_CODE)){ // take image from gallery
                rotateImageAndSave(attachment);
            }
        }

        if (attachment == null) {
            if (resultCode != Activity.RESULT_CANCELED) {
                UIMessage.alert(getApplicationContext(), R.string.msg_cant_attach_image);
            }
            return null;
        }
        return attachment;
    }

    private void rotateImageAndSave(MediaAttachment attachment){
        if (attachment != null) {
            Bitmap bitmap = BitmapUtils.getRotatedBitmap(attachment.getUri());
            if (bitmap != null) {
                BitmapUtils.saveImage(attachment.getUri().getPath(), bitmap);
                bitmap.recycle();
                System.gc();//call garbage collector because Samsung allocate too much memory for bitmap
            }
        }
    }
    private MediaAttachment getAttachmentFromGallery(Uri selectedImage, boolean isVideo) {
        if (selectedImage == null) {
            return null;
        }
        MediaAttachment attachment = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getApplicationContext().getContentResolver().query(
                selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        if (filePath == null) {
            filePath = loadFilePathFromCloud(selectedImage, isVideo);
        } else { // copy image to our storage
            filePath = resaveInOurDir(filePath, isVideo);
        }
        if(filePath != null) { // crush must not happened if we didn't have filename
            attachment = MediaAttachment.fromFile(new File(filePath), true);
        }
        return attachment;
    }

    private String resaveInOurDir(String filePath, boolean isVideo) {
        File file = new File(filePath);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            return saveFileToDir(inputStream, isVideo);
        } catch (IOException e) {
            Log.d(TAG, e.toString(), e);
        }
        return null;
    }

    private String loadFilePathFromCloud(Uri selectedImage, boolean isVideo) {
        String filePath = null;
        try {
            InputStream photoStream = getApplicationContext().getContentResolver().openInputStream(selectedImage);
            filePath = saveFileToDir(photoStream, isVideo);
        } catch (IOException e) {
            UIMessage.alert(getApplicationContext(), "Can not attach image");
        }
        return filePath;
    }

    public String saveFileToDir(InputStream inputStream, boolean isVideo) throws IOException {
        String root = (isVideo) ? mFileProvider.getVideoPath() : mFileProvider.getImagePath();
        String imgName = System.currentTimeMillis() + (isVideo ? ".avi" : ".jpg") ;
        File file = new File(root, imgName);

        OutputStream os = new FileOutputStream(file.getPath());

        byte[] b = new byte[2048];
        int length;

        while ((length = inputStream.read(b)) != -1) {
            os.write(b, 0, length);
        }

        inputStream.close();
        os.close();

        return file.getPath();
    }

}

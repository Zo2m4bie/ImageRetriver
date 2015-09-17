package com.image;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.fullscreen.R;
import com.image.callback.ILoadEndCallBack;
import com.image.service.LoadImageIntentService;
import com.image.utils.UIMessage;

import java.io.File;

/**
 * Created by kulik
 * Date: 19.01.15, 14:12
 */
public class ImageRetriever extends BroadcastReceiver {

    private static final String BUNDLE_MEDIA_URI = "media";
    private static final String BUNDLE_WAIT_RESULT = "wait_result";

    public static final String INTENT_MEDIA_ATTACH = "media_attachment";

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    private static final int SELECT_VIDEO_ACTIVITY_REQUEST_CODE = 102;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 103;
    private final Fragment mFragment;

    private Activity mActivity;
    private Uri mMediaUri;

    private boolean mStartListen = false;

    private ILoadEndCallBack mLoadEndCallBack;

    private FileProvider mFileProvider;

    /**
     * @param activity
     * @param onResultFragment - if you want retrive result in activity, keep it null
     */
    public ImageRetriever(Activity activity, Fragment onResultFragment){
        mActivity = activity;
        mFragment = onResultFragment;
        mFileProvider = FileProvider.getInstance(activity.getApplicationContext());
    }

    public void setPhotoPath(String path) {
        mFileProvider.setPhotoPath(path);
    }

    public void setVideoPath(String path) {
        mFileProvider.setVideoPath(path);
    }

    /**
     * Open camera to take a picture
     */
    public void getImageFromCamera() {
        // TODO Async
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File picsFile = mFileProvider.getFileForImage();
        if (picsFile != null) {
            mMediaUri = Uri.fromFile(picsFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            } else {
                mFragment.startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }

        } else {
            UIMessage.alert(mActivity, R.string.sdcard_unavailable);
        }
    }

    /**
     * Open camera to take a video
     */
    public void getVideoFromCamera() {
        // TODO Async
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File picsFile = mFileProvider.getFileForVideo();
        if (picsFile != null) {
            mMediaUri = Uri.fromFile(picsFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
            if (mFragment == null) {
                mActivity.startActivityForResult(cameraIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
            } else {
                mFragment.startActivityForResult(cameraIntent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
            }

        } else {
            UIMessage.alert(mActivity, R.string.sdcard_unavailable);
        }
    }

    /**
     * Open gallery to get a photo
     */
    public void getImageFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");

        if (mFragment == null) {
            mActivity.startActivityForResult(photoPickerIntent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            mFragment.startActivityForResult(photoPickerIntent, SELECT_IMAGE_ACTIVITY_REQUEST_CODE);
        }

    }

    /**
     * Open gallery to get a video
     */
    public void getVideoFromGallery() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("video/*");

        if (mFragment == null) {
            mActivity.startActivityForResult(photoPickerIntent, SELECT_VIDEO_ACTIVITY_REQUEST_CODE);
        } else {
            mFragment.startActivityForResult(photoPickerIntent, SELECT_VIDEO_ACTIVITY_REQUEST_CODE);
        }

    }

    public void onSaveInstanceState(Bundle outState) {
        if(mMediaUri != null) {
            outState.putParcelable(BUNDLE_MEDIA_URI, mMediaUri);
        }
        outState.putBoolean(BUNDLE_WAIT_RESULT, mStartListen);
    }

    public void onViewStateRestored(Bundle savedInstanceState) {
        mMediaUri = savedInstanceState.getParcelable(BUNDLE_MEDIA_URI);
        mStartListen = savedInstanceState.getBoolean(BUNDLE_WAIT_RESULT);
    }

    public Uri getMediaUri(){
        return mMediaUri;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ImageRetriever", "onReceive ");
        context.removeStickyBroadcast(intent);
        if(intent.getAction().equals(INTENT_MEDIA_ATTACH) && mStartListen && !isInitialStickyBroadcast()){
            Log.d("ImageRetriever", "onReceive accept");
            mStartListen = false;
            mLoadEndCallBack.endLoading(
                    (MediaAttachment) intent.getParcelableExtra(INTENT_MEDIA_ATTACH));
        }
    }

    public void setEndCallBack(ILoadEndCallBack addFragmentWithFotterSettings) {
        mLoadEndCallBack = addFragmentWithFotterSettings;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        sendIntent(mActivity, requestCode, resultCode, (data == null) ? null : data.getData());
    }

    public void sendIntent(Context context, int requestCode, int resultCode, Uri data) {
        Intent intent = new Intent(context, LoadImageIntentService.class);
        intent.putExtra(LoadImageIntentService.MEDIA_URI, getMediaUri());
        intent.putExtra(LoadImageIntentService.REQUEST_CODE, requestCode);
        intent.putExtra(LoadImageIntentService.RESULT_CODE, resultCode);
        if(data!= null) {
            intent.putExtra(LoadImageIntentService.DATA_URI, data);
        }
        mStartListen = true;
        context.startService(intent);
    }

    public boolean isWaitForResult() {
        return mStartListen;
    }
}

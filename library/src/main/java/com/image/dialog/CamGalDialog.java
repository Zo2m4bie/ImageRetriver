package com.image.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fullscreen.R;

/**
 * Created by dima on 4/17/15.
 */
public class CamGalDialog extends DialogFragment {
    private static final String TAG = CamGalDialog.class.getSimpleName();

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;

    public static final String GET_PHOTO_FROM_EXTRA = "get_photo_from";
    public static final int FROM_CAMERA = 201;
    public static final int FROM_GALLERY = 202;

    private static final String THEN = "then";
    private static final int PHOTO = 1;
    private static final int VIDEO = 2;
    public static final String NEXT_CLAZZ = "nextClazz";
    public static final String NEXT_CLAZZ_ACTION = "nextClazz_action";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity())
                .items(R.array.cam_gal_items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Intent intent = new Intent();
                        switch (which) {
                            case CAMERA:
                                intent.putExtra(GET_PHOTO_FROM_EXTRA, FROM_CAMERA);
                                mListener.onPickFromCamera();
                                break;
                            case GALLERY:
                                intent.putExtra(GET_PHOTO_FROM_EXTRA, FROM_GALLERY);
                                mListener.onPickFromGallery();
                                break;
                        }
                        String clazz = getArguments().getString(NEXT_CLAZZ);
                        String action = getArguments().getString(NEXT_CLAZZ_ACTION);
                        if (clazz != null) {
                            try {
                                intent.setClass(getActivity(), Class.forName(clazz));
                            } catch (ClassNotFoundException e) {
                                Log.d(TAG, "", e);
                            }
                            if (action != null) {
                                intent.setAction(action);
                            }
                            getActivity().startActivity(intent);
                        }
                    }

                }).build();
    }

    /**
     * @param manager
     * @param nextClazz -- Activity Class that was fired after selection
     */
    public void show(FragmentManager manager, Class<? extends Activity> nextClazz) {
        Bundle args = new Bundle();
        args.putString(NEXT_CLAZZ, nextClazz.getName());
        setArguments(args);
        show(manager, "cam_gal_dialog_then_photo");
    }


    public void show(FragmentManager manager, Class<? extends Activity> nextClazz, String action) {
        Bundle args = new Bundle();
        args.putString(NEXT_CLAZZ, nextClazz.getName());
        args.putString(NEXT_CLAZZ_ACTION, action);
        setArguments(args);
        show(manager, "cam_gal_dialog_then_photo");
    }

    /**
     * Activity Callbacs willbe called
     *
     * @param manager
     */
    public void show(FragmentManager manager) {
        Bundle args = new Bundle();
        setArguments(args);
        show(manager, "cam_gal_dialog_then_photo");
    }


    private static final SourcePickingCallback DUMMY_CALLBACK = new SourcePickingCallback() {
        @Override
        public void onPickFromCamera() {
        }

        @Override
        public void onPickFromGallery() {
        }
    };

    private SourcePickingCallback mListener = DUMMY_CALLBACK;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof SourcePickingCallback) {
            mListener = (SourcePickingCallback) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = DUMMY_CALLBACK;
    }

    public void setSourcePickingCallBack(SourcePickingCallback callback){
        mListener = callback;
    }

    public interface SourcePickingCallback {
        void onPickFromCamera();

        void onPickFromGallery();
    }

    public static int getSourceFrom(Intent intent) {
        return intent.getIntExtra(GET_PHOTO_FROM_EXTRA, -1);
    }

}
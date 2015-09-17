package image.com.imageretreiver;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.image.ImageRetriever;
import com.image.MediaAttachment;
import com.image.callback.ILoadEndCallBack;
import com.image.dialog.CamGalDialog;


public class MainActivity extends ActionBarActivity implements ILoadEndCallBack, CamGalDialog.SourcePickingCallback {

    private Button mGetPhotoVideoBtn;
    private ImageView mImage;

    private ImageRetriever mImageRetriever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGetPhotoVideoBtn = (Button) findViewById(R.id.btn_load_image);
        mImage = (ImageView)findViewById(R.id.iv_image);
        mImageRetriever = new ImageRetriever(this, null);
        if (savedInstanceState != null) { // we have data to restore
            mImageRetriever.onViewStateRestored(savedInstanceState); // restore data in ImageRetriever
            if(mImageRetriever.isWaitForResult()){ // it will be true, if you wait for result before open
                mImageRetriever.setEndCallBack(this); // set your activity like callback, because we wait for result
            }
        }

        mGetPhotoVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CamGalDialog dialog = new CamGalDialog();
                dialog.show(getSupportFragmentManager());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mImageRetriever.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        unregisterReceiver(mImageRetriever);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mImageRetriever, new IntentFilter(ImageRetriever.INTENT_MEDIA_ATTACH));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            mImageRetriever.setEndCallBack(this);
            mImageRetriever.onActivityResult(requestCode, resultCode, data);
//            mImageRetriever.sendIntent(this, requestCode, resultCode, (data == null) ? null : data.getData());
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public void endLoading(MediaAttachment attachment) {
        mImage.setImageURI(attachment.getPreviewUri());
    }


    @Override
    public void onPickFromCamera() {
        mImageRetriever.getImageFromCamera();
    }

    @Override
    public void onPickFromGallery() {
        mImageRetriever.getImageFromGallery();
    }

}

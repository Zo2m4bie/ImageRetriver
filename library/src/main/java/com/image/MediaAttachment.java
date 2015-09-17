package com.image;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by kulik on 18.01.15.
 */
public class MediaAttachment implements Comparable<MediaAttachment>, Parcelable {
    private final String mPath;
    private String mPathToPreview;//almost mPathToPreview equals mPath, but video file have another path for preview
    private long mOrder;
    private boolean isFromGallery;

    public MediaAttachment(String path, long order, boolean isFromGallery) {
        mPath = path;
        mPathToPreview = mPath;
        mOrder = order;
        this.isFromGallery = isFromGallery;
    }

    /*
    Please note that media path will be encoded. Use it carefully
     */
    public Uri getUri() {
        return Uri.fromFile(new File(mPath));
    }
    public Uri getPreviewUri() {
        return Uri.fromFile(new File(mPathToPreview));
    }

    public String getPathToPreview() {
        return mPathToPreview;
    }

    public void setPathToPreview(String mPathToPreview) {
        this.mPathToPreview = mPathToPreview;
    }

    /*
    Use this method to display local media in imageLoader.
     */
//    public String getMediaPath() {
//        return "file://" + mPath;
//    }

    public File getFile() {
        return new File(mPath);
    }

    @Override
    public int compareTo(MediaAttachment another) {
        return (int) (mOrder - another.mOrder);
    }

    public static MediaAttachment fromFile(File imageFile, boolean isFromGallery) {
        return new MediaAttachment(imageFile.getPath(), System.currentTimeMillis(), isFromGallery);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mPath);
        dest.writeLong(mOrder);
        dest.writeByte(isFromGallery ? (byte) 1 : (byte) 0);
        dest.writeString(mPathToPreview);
    }

    private MediaAttachment(Parcel in){
        mPath = in.readString();
        mOrder = in.readLong();
        isFromGallery = in.readByte() != 0;
        mPathToPreview  = in.readString();
    }

    public static final Creator<MediaAttachment> CREATOR = new Creator<MediaAttachment>() {
        public MediaAttachment createFromParcel(Parcel source) {
            return new MediaAttachment(source);
        }

        public MediaAttachment[] newArray(int size) {
            return new MediaAttachment[size];
        }
    };

}

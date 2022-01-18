package com.drudotstech.customgallery.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;


/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/
public class GalleryMediaModel implements Parcelable {

    private String mediaName;
    private String mediaPath;
    private String mediaSize;
    private String mediaUri;
    private String mediaType;
    private boolean selected = false;
    private boolean visible = false;

    public GalleryMediaModel() {

    }

    public GalleryMediaModel(String mediaName, String mediaPath, String mediaSize, String mediaUri, String mediaType, boolean selected) {
        this.mediaName = mediaName;
        this.mediaPath = mediaPath;
        this.mediaSize = mediaSize;
        this.mediaUri = mediaUri;
        this.mediaType = mediaType;
        this.selected = selected;
    }

    public GalleryMediaModel(String mediaName, String mediaPath, String mediaSize, String mediaUri, String mediaType) {
        this.mediaName = mediaName;
        this.mediaPath = mediaPath;
        this.mediaSize = mediaSize;
        this.mediaUri = mediaUri;
        this.mediaType = mediaType;
        this.selected = false;
    }

    public String getMediaName() {
        return mediaName;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public String getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(String mediaUri) {
        this.mediaUri = mediaUri;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaName);
        dest.writeString(this.mediaPath);
        dest.writeString(this.mediaSize);
        dest.writeString(this.mediaUri);
        dest.writeString(this.mediaType);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.mediaName = source.readString();
        this.mediaPath = source.readString();
        this.mediaSize = source.readString();
        this.mediaUri = source.readString();
        this.mediaType = source.readString();
        this.selected = source.readByte() != 0;
        this.visible = source.readByte() != 0;
    }

    protected GalleryMediaModel(Parcel in) {
        this.mediaName = in.readString();
        this.mediaPath = in.readString();
        this.mediaSize = in.readString();
        this.mediaUri = in.readString();
        this.mediaType = in.readString();
        this.selected = in.readByte() != 0;
        this.visible = in.readByte() != 0;
    }

    public static final Parcelable.Creator<GalleryMediaModel> CREATOR = new Parcelable.Creator<GalleryMediaModel>() {
        @Override
        public GalleryMediaModel createFromParcel(Parcel source) {
            return new GalleryMediaModel(source);
        }

        @Override
        public GalleryMediaModel[] newArray(int size) {
            return new GalleryMediaModel[size];
        }
    };
}

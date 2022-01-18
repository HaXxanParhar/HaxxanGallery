package com.drudotstech.customgallery.gallery.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.drudotstech.customgallery.gallery.utils.GalleryConfig;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/


public class GalleryResult implements Parcelable {

    private List<GalleryMediaModel> pictures;
    private GalleryConfig config;

    public List<GalleryMediaModel> getPictures() {
        return pictures;
    }

    public void setPictures(List<GalleryMediaModel> pictures) {
        this.pictures = pictures;
    }

    public GalleryMediaModel getSinglePicture() {
        return pictures == null ? null : pictures.isEmpty() ? null : pictures.get(0);
    }

    public GalleryConfig getConfig() {
        return config;
    }

    public void setConfig(GalleryConfig config) {
        this.config = config;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.pictures);
        dest.writeParcelable(this.config, flags);
    }

    public void readFromParcel(Parcel source) {
        this.pictures = new ArrayList<GalleryMediaModel>();
        source.readList(this.pictures, GalleryMediaModel.class.getClassLoader());
        this.config = source.readParcelable(GalleryConfig.class.getClassLoader());
    }

    public GalleryResult() {
    }

    public GalleryResult(List<GalleryMediaModel> pictures, GalleryConfig config) {
        this.pictures = pictures;
        this.config = config;
    }

    protected GalleryResult(Parcel in) {
        this.pictures = new ArrayList<GalleryMediaModel>();
        in.readList(this.pictures, GalleryMediaModel.class.getClassLoader());
        this.config = in.readParcelable(GalleryConfig.class.getClassLoader());
    }

    public static final Parcelable.Creator<GalleryResult> CREATOR = new Parcelable.Creator<GalleryResult>() {
        @Override
        public GalleryResult createFromParcel(Parcel source) {
            return new GalleryResult(source);
        }

        @Override
        public GalleryResult[] newArray(int size) {
            return new GalleryResult[size];
        }
    };
}

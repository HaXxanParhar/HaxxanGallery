package com.drudotstech.customgallery.gallery.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 12:24 PM
 ******************************************************/


public class GalleryConfig implements Parcelable {

    // ------------------------------------- C O N S T A N T S  ------------------------------------
    public static final String SINGLE_SELECTION = "SINGLE_SELECTION";
    public static final String MULTIPLE_SELECTION = "MULTIPLE_SELECTION";
    public static final String ALL_PICTURES = "ALL_PICTURES";
    public static final String ALBUMS = "ALBUMS";


    // -------------------------------------- V A R I A B L E S ------------------------------------
    private String selectionType = SINGLE_SELECTION; // default selection is single selection
    private String galleryType = ALL_PICTURES; // default gallery type is all pictures
    private int maxSelection = 1; // default max selection is 1


    public GalleryConfig() {
    }


    public GalleryConfig(String selectionType) {
        this.selectionType = selectionType;
        if (selectionType.equals(SINGLE_SELECTION)) {
            maxSelection = 1;
        } else if (selectionType.equals(MULTIPLE_SELECTION)) {
            maxSelection = 5;
        }
    }

    public GalleryConfig(String selectionType, int maxSelection) {
        this.selectionType = selectionType;
        if (selectionType.equals(SINGLE_SELECTION)) {
            this.maxSelection = 1;
        } else {
            this.maxSelection = maxSelection;
        }
    }

    public String getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(String selectionType) {
        this.selectionType = selectionType;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public boolean isSingleSelection() {
        return TextUtils.equals(selectionType, SINGLE_SELECTION);
    }

    public boolean isMultipleSelection() {
        return TextUtils.equals(selectionType, MULTIPLE_SELECTION);
    }


    public String getGalleryType() {
        return galleryType;
    }

    public void setGalleryType(String galleryType) {
        this.galleryType = galleryType;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.selectionType);
        dest.writeString(this.galleryType);
        dest.writeInt(this.maxSelection);
    }

    public void readFromParcel(Parcel source) {
        this.selectionType = source.readString();
        this.galleryType = source.readString();
        this.maxSelection = source.readInt();
    }

    protected GalleryConfig(Parcel in) {
        this.selectionType = in.readString();
        this.galleryType = in.readString();
        this.maxSelection = in.readInt();
    }

    public static final Parcelable.Creator<GalleryConfig> CREATOR = new Parcelable.Creator<GalleryConfig>() {
        @Override
        public GalleryConfig createFromParcel(Parcel source) {
            return new GalleryConfig(source);
        }

        @Override
        public GalleryConfig[] newArray(int size) {
            return new GalleryConfig[size];
        }
    };
}

package com.drudotstech.customgallery.gallery;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import androidx.activity.result.ActivityResultLauncher;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.gallery.album_only.AlbumsActivity;
import com.drudotstech.customgallery.gallery.all_pictures_and_albums.AllPicturesActivity;
import com.drudotstech.customgallery.gallery.utils.GalleryConfig;
import com.drudotstech.customgallery.gallery.utils.GalleryConstants;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/20/2022 at 1:19 PM
 ******************************************************/


public class GalleryBuilder {
    private GalleryConfig galleryConfig;
    private Activity activity;
    private ActivityResultLauncher<Intent> launcher;


    public GalleryBuilder(Activity activity) {
        this.activity = activity;
        galleryConfig = new GalleryConfig();
    }

    public GalleryBuilder(Activity activity, ActivityResultLauncher<Intent> launcher) {
        this.activity = activity;
        this.launcher = launcher;
        galleryConfig = new GalleryConfig();
    }

    public GalleryBuilder setSingleSelection() {
        galleryConfig.setSelectionType(GalleryConfig.SINGLE_SELECTION);
        return this;
    }

    public GalleryBuilder setMultiSelection() {
        galleryConfig.setSelectionType(GalleryConfig.MULTIPLE_SELECTION);
        galleryConfig.setMaxSelection(5);
        return this;
    }
    public GalleryBuilder setMultiSelection(int maxSelection) {
        galleryConfig.setSelectionType(GalleryConfig.MULTIPLE_SELECTION);
        galleryConfig.setMaxSelection(maxSelection);
        return this;
    }

    public GalleryBuilder showAlbumsInGallery() {
        galleryConfig.setGalleryType(GalleryConfig.ALBUMS);
        return this;
    }

    public GalleryBuilder showALLPicturesGallery() {
        galleryConfig.setGalleryType(GalleryConfig.ALL_PICTURES);
        return this;
    }

    public void start() {

        // create the intent
        Intent intent = new Intent(activity, AllPicturesActivity.class);
        if (TextUtils.equals(galleryConfig.getGalleryType(), GalleryConfig.ALBUMS)) {
            intent = new Intent(activity, AlbumsActivity.class);
        }

        intent.putExtra(GalleryConstants.CONFIG_KEY, galleryConfig);

        if (launcher != null) {
            launcher.launch(intent);
        } else {
            activity.startActivityForResult(intent, GalleryConstants.RC_RESULT);
        }
        activity.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right);
    }
}
package com.drudotstech.customgallery.gallery.interfaces;

import com.drudotstech.customgallery.gallery.models.GalleryResult;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:44 PM
 ******************************************************/


public interface GalleryResultsCallback {

    void onResultReceived(boolean status, GalleryResult result);
}

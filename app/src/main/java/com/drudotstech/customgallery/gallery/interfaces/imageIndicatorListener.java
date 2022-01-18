package com.drudotstech.customgallery.gallery.interfaces;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/18/2022 at 3:33 PM
 ******************************************************/

/**
 *
 *
 * This interface helps us to ensure communication between our PictureBrowserFragment and it
 * recyclerView which acts as and indicator to it ViewPager of images
 * each time and item in the recyclerView is clicked we use the
 * OnImageIndicationClicked method to signal the fragment for changes
 */
public interface imageIndicatorListener {

    /**
     *
     * @param ImagePosition position of an item in the RecyclerView Adapter
     */
    void onImageIndicatorClicked(int ImagePosition);
}

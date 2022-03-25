package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.graphics.Movie;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/22/2022 at 11:19 AM
 ******************************************************/


public class GifModel {
    private Movie movie;
    private int resourceId;
    public long clickedTime = 0;


    public GifModel(Movie movie) {
        this.movie = movie;
    }

    public GifModel(int resourceId) {
        this.resourceId = resourceId;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

}

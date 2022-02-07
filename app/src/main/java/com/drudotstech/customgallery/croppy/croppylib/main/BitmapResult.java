package com.drudotstech.customgallery.croppy.croppylib.main;

import android.graphics.Bitmap;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/31/2022 at 7:36 PM
 ******************************************************/


public class BitmapResult {

    private Bitmap bitmap;
    private boolean status;

    public BitmapResult(Bitmap bitmap, boolean status) {
        this.bitmap = bitmap;
        this.status = status;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

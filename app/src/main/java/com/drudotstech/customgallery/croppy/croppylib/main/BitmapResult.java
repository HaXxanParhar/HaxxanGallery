package com.drudotstech.customgallery.croppy.croppylib.main;

import android.graphics.Bitmap;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/31/2022 at 7:36 PM
 ******************************************************/


public class BitmapResult {

    private boolean status;
    private Bitmap bitmap;
    private Exception exception;

    public BitmapResult(boolean status,Bitmap bitmap) {
        this.status = status;
        this.bitmap = bitmap;
    }

    public BitmapResult(boolean status, Exception exception) {
        this.status = status;
        this.exception = exception;
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

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

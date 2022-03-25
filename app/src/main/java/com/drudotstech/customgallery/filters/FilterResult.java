package com.drudotstech.customgallery.filters;

import android.graphics.Bitmap;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/25/2022 at 2:18 PM
 ******************************************************/


public class FilterResult {
    boolean status;
    Bitmap bitmap;
    Exception exception;

    public FilterResult(boolean status, Bitmap bitmap) {
        this.status = status;
        this.bitmap = bitmap;
    }

    public FilterResult(boolean status, Exception exception) {
        this.status = status;
        this.exception = exception;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }
}

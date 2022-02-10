package com.drudotstech.customgallery.croppy.croppylib.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.drudotstech.customgallery.utils.BlurUtil;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/28/2022 at 9:18 PM
 ******************************************************/


public class BlurTask extends AsyncTask<Void, Void, BitmapResult> {

    private final Context context;
    private final int iterations;
    private final BlurBitmapCallback callback;
    private Bitmap bitmap;

    public BlurTask(Context context, Bitmap bitmap, BlurBitmapCallback callback) {
        this.context = context;
        this.bitmap = bitmap;
        this.iterations = 1;
        this.callback = callback;
    }

    public BlurTask(Context context, Bitmap bitmap, int iterations, BlurBitmapCallback callback) {
        this.context = context;
        this.bitmap = bitmap;
        this.iterations = iterations;
        this.callback = callback;
    }

    @Override
    protected BitmapResult doInBackground(Void... voids) {
        try {
            bitmap = new BlurUtil().blur(context, bitmap, iterations);
            return new BitmapResult(true, bitmap);
        } catch (Exception e) {
            return new BitmapResult(false, e);
        }

    }

    @Override
    protected void onPostExecute(BitmapResult bitmapResult) {
        super.onPostExecute(bitmapResult);
        callback.onBlurCompleted(bitmapResult);
    }
}

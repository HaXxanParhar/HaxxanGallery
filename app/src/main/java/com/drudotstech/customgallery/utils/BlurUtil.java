package com.drudotstech.customgallery.utils;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/24/2022 at 3:25 PM
 ******************************************************/


import android.content.Context;
import android.graphics.Bitmap;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

public class BlurUtil {
    private static final float BLUR_RADIUS = 25f;

    public static Bitmap blur(Context context, Bitmap image, int iterations) {
        int width = Math.round(image.getWidth());
        int height = Math.round(image.getHeight());
        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        for (int i = 1; i <= iterations; i++) {
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
//        Radius out of range (0 < r <= 25)
            intrinsicBlur.setRadius(BLUR_RADIUS);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(inputBitmap);
            if (i == iterations) {
                tmpOut.copyTo(outputBitmap);
            }
        }

        return outputBitmap;
    }
}
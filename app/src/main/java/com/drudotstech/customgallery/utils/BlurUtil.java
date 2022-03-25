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

    public Bitmap blur(Context context, Bitmap bitmap, float value) {
        float blurRadius = 25f;

        int iterations = (int) value;
        if (iterations == 1) {
            float offset = value - iterations; // to get the decimal values
            blurRadius = offset * 25;
        }

        int width = Math.round(bitmap.getWidth());
        int height = Math.round(bitmap.getHeight());
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        for (int i = 1; i <= iterations; i++) {
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
//        Radius out of range (0 < r <= 25)
            intrinsicBlur.setRadius(blurRadius);
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
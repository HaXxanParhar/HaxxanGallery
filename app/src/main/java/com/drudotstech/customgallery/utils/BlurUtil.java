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
    float blurRadius = 25f;

    public Bitmap blur(Context context, Bitmap bitmap, float value) {

        int iterations = (int) value;
        float offset = value - iterations; // to get the decimal values
        float extraBlurRadius = offset * 25;

        // create input and output Bitmaps
        int width = Math.round(bitmap.getWidth());
        int height = Math.round(bitmap.getHeight());
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // init RenderScript
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // create allocations for input and output Bitmaps
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // Run the Blur with 25 Radius for 'iterations' times
        for (int i = 1; i <= iterations; i++) {
//        Radius range : 0 - 25
            intrinsicBlur.setRadius(blurRadius);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(inputBitmap);
        }

        // Blur once more with Extra Blur
        if (extraBlurRadius > 0) {
            intrinsicBlur.setRadius(extraBlurRadius);
            intrinsicBlur.setInput(tmpIn);
            intrinsicBlur.forEach(tmpOut);
            tmpOut.copyTo(inputBitmap);
        }

        // copy the blur result in outputBitmap
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }
}
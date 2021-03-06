package com.drudotstech.customgallery.mycanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicConvolve3x3;

import com.drudotstech.customgallery.mycanvas.models.LayerModel;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/10/2022 at 11:15 AM
 ******************************************************/


public class PaintFactory {

    private static final double[] DELTA_INDEX = {
            0, 0.01, 0.02, 0.04, 0.05, 0.06, 0.07, 0.08, 0.1, 0.11,
            0.12, 0.14, 0.15, 0.16, 0.17, 0.18, 0.20, 0.21, 0.22, 0.24,
            0.25, 0.27, 0.28, 0.30, 0.32, 0.34, 0.36, 0.38, 0.40, 0.42,
            0.44, 0.46, 0.48, 0.5, 0.53, 0.56, 0.59, 0.62, 0.65, 0.68,
            0.71, 0.74, 0.77, 0.80, 0.83, 0.86, 0.89, 0.92, 0.95, 0.98,
            1.0, 1.06, 1.12, 1.18, 1.24, 1.30, 1.36, 1.42, 1.48, 1.54,
            1.60, 1.66, 1.72, 1.78, 1.84, 1.90, 1.96, 2.0, 2.12, 2.25,
            2.37, 2.50, 2.62, 2.75, 2.87, 3.0, 3.2, 3.4, 3.6, 3.8, 4.0,
            4.3, 4.7, 4.9, 5.0, 5.5, 6.0, 6.5, 6.8, 7.0, 7.3, 7.5, 7.8,
            8.0, 8.4, 8.7, 9.0, 9.4, 9.6, 9.8, 10.0};


    public static void adjustHue(ColorMatrix cm, float value) {
        // value will come in 0-100 so make it -180 - 180
        value = (value * 1.8f * 2) - 180;

        value = cleanValue(value, 180f) / 180f * (float) Math.PI;
        if (value == 0) {
            return;
        }

        float cosVal = (float) Math.cos(value);
        float sinVal = (float) Math.sin(value);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        final float v = lumR + cosVal * (1 - lumR) + sinVal * (-lumR);
        final float v1 = lumG + cosVal * (-lumG) + sinVal * (-lumG);
        final float v2 = lumB + cosVal * (-lumB) + sinVal * (1 - lumB);

        final float v3 = lumR + cosVal * (-lumR) + sinVal * (0.143f);
        final float v4 = lumG + cosVal * (1 - lumG) + sinVal * (0.140f);
        final float v5 = lumB + cosVal * (-lumB) + sinVal * (-0.283f);

        final float v6 = lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR));
        final float v7 = lumG + cosVal * (-lumG) + sinVal * (lumG);
        final float v8 = lumB + cosVal * (1 - lumB) + sinVal * (lumB);
        float[] mat = new float[]
                {
                        v, v1, v2, 0, 0,
                        v3, v4, v5, 0, 0,
                        v6, v7, v8, 0, 0,
                        0f, 0f, 0f, 1f, 0f,
                        0f, 0f, 0f, 0f, 1f};
        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustBrightness(ColorMatrix cm, float value) {
        // value will come in 0-100 so make it -100 to 100
        value = (value * 2) - 100;
        value = cleanValue(value, 100);
        if (value == 0) {
            return;
        }

        float[] mat = new float[]
                {
                        1, 0, 0, 0, value,
                        0, 1, 0, 0, value,
                        0, 0, 1, 0, value,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustContrast(ColorMatrix cm, int value) {
        // value will come in 0-100 so make it -100 to 100
        value = (value * 2) - 100;
        value = (int) cleanValue(value, 100);
        if (value == 0) {
            return;
        }
        float x;
        if (value < 0) {
            x = 127 + (float) value / 100 * 127;
        } else {
            x = (float) DELTA_INDEX[value];
            x = x * 127 + 127;
        }

        float[] mat = new float[]
                {
                        x / 127, 0, 0, 0, 0.5f * (127 - x),
                        0, x / 127, 0, 0, 0.5f * (127 - x),
                        0, 0, x / 127, 0, 0.5f * (127 - x),
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
    }

    public static void adjustSaturation(ColorMatrix cm, float value) {
        // value will come in 0-100 so make it -100 to 100
        value = (value * 2) - 100;
        value = cleanValue(value, 100);
        if (value == 0) {
            return;
        }

        float x = 1 + ((value > 0) ? 3 * value / 100 : value / 100);
        float lumR = 0.3086f;
        float lumG = 0.6094f;
        float lumB = 0.0820f;

        float[] mat = new float[]
                {
                        lumR * (1 - x) + x, lumG * (1 - x), lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x) + x, lumB * (1 - x), 0, 0,
                        lumR * (1 - x), lumG * (1 - x), lumB * (1 - x) + x, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 0, 0, 0, 1
                };
        cm.postConcat(new ColorMatrix(mat));
    }


    protected static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    public static ColorFilter adjustColor(LayerModel layer) {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, layer.hue);
        adjustContrast(cm, (int) layer.contrast);
        adjustBrightness(cm, layer.brightness);
        adjustSaturation(cm, layer.saturation);
        return new ColorMatrixColorFilter(cm);
    }

    public static ColorFilter adjustColor(int brightness, int contrast, int saturation, int hue) {
        ColorMatrix cm = new ColorMatrix();
        adjustHue(cm, hue);
        adjustContrast(cm, contrast);
        adjustBrightness(cm, brightness);
        adjustSaturation(cm, saturation);
        return new ColorMatrixColorFilter(cm);
    }


    public static Paint changeSaturation(float satProgress) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(satProgress / 50);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        return paint;
    }

    public static Paint changeContrast(float contrastProgress) {
        float cb = contrastProgress == 0 ? 0 : -(contrastProgress / 1.8f) * 5;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrastProgress, 0, 0, 0, cb,
                        0, contrastProgress, 0, 0, cb,
                        0, 0, contrastProgress, 0, cb,
                        0, 0, 0, contrastProgress, 0
                });

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        return paint;
    }

    // low
    public static void loadBitmapSharp(Context context, Bitmap bitmap) {
        float[] sharp = {-0.60f, -0.60f, -0.60f, -0.60f, 5.81f, -0.60f,
                -0.60f, -0.60f, -0.60f};
        //you call the method above and just paste the bitmap you want to apply it and the float of above
        bitmap = doSharpen(context, bitmap, sharp);
    }

    // High
    public static Bitmap loadBitmapSharpHigh(Context context, Bitmap bitmap) {
        float[] sharp = {-0.15f, -0.15f, -0.15f,
                -0.15f, 2.2f, -0.15f,
                -0.15f, -0.15f, -0.15f};
        //you call the method above and just paste the bitmap you want to apply it and the float of above
        return doSharpen(context, bitmap, sharp);
    }

    public static Bitmap doSharpen(Context context, Bitmap original, float[] radius) {
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(radius);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }

    public static Bitmap doSharpen(Context context, Bitmap original, float multiplier) {
        if (multiplier <= 0)
            return original;
        float[] sharp = {0, -multiplier, 0, -multiplier, 5f * multiplier, -multiplier, 0, -multiplier, 0};
        Bitmap bitmap = Bitmap.createBitmap(
                original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(context);

        Allocation allocIn = Allocation.createFromBitmap(rs, original);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(sharp);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }

}

package com.drudotstech.customgallery.mycanvas;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 12:43 PM
 ******************************************************/


public class CanvasUtils {

    private static final String TAG = "CANVAS";
    public static float offsetFromScreen = 0f; // 44 + 72 | action bar & bottom bar


    public static Bitmap createBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        return Bitmap.createBitmap(bm, 0, 0, width, height, null, false);
    }

    public static Bitmap copyBitmap(Bitmap bm) {
        return bm.copy(bm.getConfig(), false);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = (newWidth) / width;
        float scaleHeight = (newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static Bitmap getScreenFillBitmap(Context context, Bitmap bm) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth;
        float scaleHeight;
        scaleHeight = ((float) getScreenHeight(context)) / height;
        scaleWidth = ((float) getScreenWidth(context)) / width;

        // when image has more width, scale according to height to fill the screen
        if (scaleHeight >= scaleWidth) {
            scaleWidth = scaleHeight;
            // when image has more height, scale according to width to fill the screen
        } else {
            scaleHeight = scaleWidth;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static Bitmap getScreenFitBitmap(Context context, Bitmap bm) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = 1f, scaleHeight = 1f;

        // when image has more width than scale according to width scale
        if (width >= height) {
            scaleWidth = ((float) getScreenWidth(context)) / width;
            scaleHeight = scaleWidth;
            // when image has more height than scale according to height scale
        } else {
            scaleHeight = ((float) getScreenHeight(context)) / height;
            scaleWidth = scaleHeight;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    public static RectF getCenteredRect(Context context, Bitmap bm) {

        // original w h of bitmap
        float width = bm.getWidth();
        float height = bm.getHeight();


        // screen height
        final float finalHeight = getScreenHeight(context) - offsetFromScreen;

        // screen width
        final int screenWidth = getScreenWidth(context);

        // find the width and height scale value that can make the bitmap width/height equal to screen width/height
        float widthScale = screenWidth / width;
        float heightScale = finalHeight / height;


        // pick the smaller scale for both height & width to make them fit to the screen
        if (heightScale <= widthScale) {
            //noinspection SuspiciousNameCombination
            widthScale = heightScale;
            // when image has more height, scale according to width to fill the screen
        } else {
            //noinspection SuspiciousNameCombination
            heightScale = widthScale;
        }

        // the final height & width with proper aspect ratio fit to screen
        width = (width * widthScale);
        height = (height * heightScale);

        final float halfWidth = width / 2.0f;
        final float halfHeight = height / 2.0f;

        float centerX = screenWidth / 2.0f;
        float centerY = finalHeight / 2.0f;

        // creating rectangle from the center
        final float left = centerX - halfWidth;
        final float top = centerY - halfHeight;
        final float right = centerX + halfWidth;
        final float bottom = centerY + halfHeight;

        return new RectF(left, top, right, bottom);
    }

    public static RectF getCenteredRect(Bitmap bm, int screenWidth, int screenHeight) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        final float halfWidth = width / 2.0f;
        final float halfHeight = height / 2.0f;

        float centerX = screenWidth / 2.0f;
        float centerY = screenHeight / 2.0f;


        return new RectF(
                centerX - halfWidth,
                centerY - halfHeight,
                centerX + halfWidth,
                centerY + halfHeight);
    }

    public static RectF getCenteredRectFromBitmap(Context context, int width, int height) {

        final float halfWidth = width / 2.0f;
        final float halfHeight = height / 2.0f;

        final int screenWidth = getScreenWidth(context);
        final int screenHeight = getScreenHeight(context);

        float centerX = screenWidth / 2.0f;
        float centerY = screenHeight / 2.0f;

        // creating left,top,right,bottom point from the center (centerX,centerY)
        float left = centerX - halfWidth;
        float top = centerY - halfHeight;
        float right = centerX + halfWidth;
        float bottom = centerY + halfHeight;

        // make the rect within the screen bounds
        left = Math.max(0f, left);
        top = Math.max(0f, top);
        right = Math.min(screenWidth, right);
        bottom = Math.min(screenHeight, bottom);

        return new RectF(
                left,
                top,
                right,
                bottom);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static float toPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmapFromVector(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        Log.e(TAG, "getBitmap: 1");
        return bitmap;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmapFromVector(VectorDrawable vectorDrawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId) {
        Log.e(TAG, "getBitmap: 2");
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmapFromVector((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static Bitmap getBitmapFromVector(Context context, int drawableId, int width, int height) {
        Log.e(TAG, "getBitmap: 2");
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawable) {
            return getBitmapFromVector((VectorDrawable) drawable, width, height);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static Bitmap getBitmapFromPNG(Context context, int drawableId, float width, float height) {
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        return getResizedBitmap(bitmap, width, height);
    }

    public static Bitmap getBitmapFromPNG(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }


    public static RectF toRectF(Rect rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    public static Rect toRect(RectF rectF) {
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

}

package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 1:34 PM
 ******************************************************/


@SuppressLint("ViewConstructor")
public class StickerView extends View {

    public static final int BITMAP = 1;
    public static final int TEXT = 2;
    private static final float MIN_SCALE = 0.5f; // lesser value = more sensitive
    private static final float MAX_SCALE = 5; // lesser value = more sensitive
    private static final String TAG = "yam";
    public int stickerType; // bitmap or text
    public String text = "5:34 PM";

    public int left, top, right, bottom, height, width;
    public RectF rect;
    public Context context;
    public Bitmap bitmap;
    public int screenWidth;
    public int screenHeight;
    public float centerX, centerY;
    public boolean isScaled;// if scaling is required
    public boolean isTranslated;// if translation is required
    public boolean isRotated;// if rotation is required

    public RectF canvasRect;

    public Paint textPaint;
    public Paint secondaryTextPaint;
    public Paint borderPaint;
    public Paint redPaint;
    public int margin = 2;

    public Rect textRect;
    public RectF borderRect;
    public RectF startingRectF; // to save the init rectf before making changes to bitmap

    public boolean isClicked;
    public float scale = 1;
    private Matrix matrix;
    private float[] matrixValues = new float[9];


    public StickerView(Context context, @Nullable AttributeSet attrs, Context context1) {
        super(context, attrs);
        this.context = context1;
    }

    public StickerView(Context context, String text) {
        super(context);
        this.context = context;
        this.text = text;
        this.stickerType = TEXT;
    }

    public StickerView(Context context, Bitmap bitmap) {
        super(context);
        this.context = context;
        this.bitmap = bitmap;
        this.stickerType = BITMAP;

        init(context);

        width = bitmap.getWidth();
        height = bitmap.getHeight();

        // to make it in center
        left = (int) (centerX - (width / 2));
        top = (int) (centerY - (height / 2));
        right = left + width;
        bottom = top + height;

        rect = new RectF(left, top, right, bottom);
        textRect = new Rect(0, 0, 500, 500);
        updateBorderRect(context);
    }

    public StickerView(StickerView s) {
        super(s.context);
        this.context = s.context;
        this.bitmap = s.bitmap;
        this.stickerType = s.stickerType;

        width = s.width;
        height = s.height;

        // to make it in center
        left = s.left;
        top = s.top;
        right = s.right;
        bottom = s.bottom;

        rect = s.rect;

        screenWidth = s.screenWidth;
        screenHeight = s.screenHeight;
        centerX = s.centerX;
        centerY = s.centerY;
        isScaled = s.isScaled;// if scaling is required
        isTranslated = s.isTranslated;// if translation is required
        isRotated = s.isRotated;// if rotation is required

        canvasRect = s.canvasRect;

        borderPaint = s.borderPaint;
        redPaint = s.redPaint;
        margin = s.margin;

        borderRect = s.borderRect;
        startingRectF = s.startingRectF; // to save the init rectf before making changes to bitmap

        isClicked = s.isClicked;
        scale = s.scale;
        matrix = s.matrix;
        matrixValues = s.matrixValues;
    }


    private RectF toRectF(Rect rect) {
        return new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }

    public void updateBorderRect(Context context) {
        int marginPx = (int) CanvasUtils.toPx(context, margin);
//        borderRect = new RectF(rect.left - marginPx, rect.top - marginPx, rect.right + marginPx, rect.bottom + marginPx);
        borderRect = new RectF(rect.left, rect.top, rect.right, rect.bottom);
    }


    private void init(Context context) {
        screenWidth = CanvasUtils.getScreenWidth(context);
        screenHeight = CanvasUtils.getScreenHeight(context);
        centerX = (float) (screenWidth / 2.0);
        centerY = (float) (screenHeight / 2.0);

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStrokeWidth(CanvasUtils.toPx(context, 2f));

        redPaint = new Paint();
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setColor(Color.RED);
        redPaint.setAntiAlias(true);
        redPaint.setStrokeWidth(CanvasUtils.toPx(context, 3f));

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.time_font));
        textPaint.setTextSize(200);

        secondaryTextPaint = new Paint();
        secondaryTextPaint.setColor(Color.WHITE);
        secondaryTextPaint.setTypeface(ResourcesCompat.getFont(context, R.font.metropolis_bold));
        secondaryTextPaint.setTextSize(50);

        setRotation(0);

        matrix = new Matrix();
        scale = 1.0f;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        canvas.save();
//        canvas.clipRect(canvasRect); // to clip the main bitmap area

        Log.d(TAG, "------------------------------- on Draw --------------------------------------");
        Log.d(TAG, "Left : " + rect.left + "  |  Top : " + rect.top + "  |  Right : " + rect.right + "  |  Bottom : " + rect.bottom);
        matrix.setTranslate(rect.left, rect.top);

        matrix.postRotate(getRotation(), rect.centerX(), rect.centerY());

        matrix.postScale(scale, scale, rect.centerX(), rect.centerY());

//        if (textPaint != null && !TextUtils.isEmpty(text))
        textPaint.setTextSize(200 * scale);
        textPaint.getTextBounds(text, 0, text.length(), textRect);
//        secondaryTextPaint.setTextSize(50 * scale);
        canvas.drawText(text, textRect.centerX(), textRect.centerY(), textPaint);
//        canvas.drawText("PM", rect.left + rect.width(), rect.top, secondaryTextPaint);
//        canvas.drawBitmap(bitmap, matrix, null);


//        canvas.restore();


//        else {
//            canvas.drawBitmap(bitmap, null, rect, null);
//        }

        if (startingRectF != null) {
            matrix.getValues(matrixValues);

            //------------------- Scaling --------------------------------
            float scaledX = matrixValues[Matrix.MSCALE_X];
            float scaledY = matrixValues[Matrix.MSCALE_Y];
            scaledX = getBoundedScale(scaledX);
            scaledY = getBoundedScale(scaledY);

            Log.d("haxx", "Befor --> Scale X : " + scaledX + "  Scale Y : " + scaledY);

            scaledX = ((startingRectF.width() * scaledX) - startingRectF.width()) / 2.0f;
            scaledY = ((startingRectF.height() * scaledY) - startingRectF.height()) / 2.0f;

            Log.d("haxx", "After --> Scale X : " + scaledX + "  Scale Y : " + scaledY);

            borderRect.left = startingRectF.left - scaledX;
            borderRect.top = startingRectF.top - scaledY;
            borderRect.right = startingRectF.right + scaledX;
            borderRect.bottom = startingRectF.bottom + scaledY;
            Log.d(TAG, "---------- Scaled with X : " + scaledX + " and Y  " + scaledY + "  of   " + scale + "  ---------");
            Log.d(TAG, "Left : " + borderRect.left + "  |  Top : " + borderRect.top
                    + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);

            //------------------- Translating --------------------------------
//            if (isTranslated) {
            float translatedX = matrixValues[Matrix.MTRANS_X];
            float translatedY = matrixValues[Matrix.MTRANS_Y];
            Log.d("haxx", "--> Translated X : " + translatedX + "  Translated Y : " + translatedY);

            borderRect.right = translatedX + borderRect.width();
            borderRect.bottom = translatedY + borderRect.height();
            borderRect.left = translatedX;
            borderRect.top = translatedY;
            Log.d(TAG, "---------- Translated by X : " + translatedX + " and Y  " + translatedY + "---------");
            Log.d(TAG, "Left : " + borderRect.left + "  |  Top : " + borderRect.top
                    + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);

            Log.d("haxx", "Befor Rect  --> Left : " + borderRect.left + "  |  Top : "
                    + borderRect.top + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);
//                isTranslated = false;
//            }
//            rect.left = startingRectF.left / scale;
//            rect.top = startingRectF.top / scale;
//            rect.right = startingRectF.right * scale;
//            rect.bottom = startingRectF.bottom * scale;

//            int scaledX = (int) ((startingRectF.width() * scale) - startingRectF.width());
//            int scaledY = (int) ((startingRectF.height() * scale) - startingRectF.height());
//
//            rect.left = startingRectF.left - scaledX;
//            rect.top = startingRectF.top - scaledY;
//            rect.right = startingRectF.right + scaledX;
//            rect.bottom = startingRectF.bottom + scaledY;
        }

        if (isClicked) {
            canvas.drawRect(borderRect.left, borderRect.top, borderRect.right, borderRect.bottom, redPaint);
            Log.d(TAG, "---------- TEST_BORDER ---------");
            Log.d(TAG, "TEST_BORDER draw : Left : " + borderRect.left + "  |  Top : " + borderRect.top + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);
        } else {
            if (textRect != null)
                canvas.drawRect(textRect, borderPaint);
            canvas.drawRect(borderRect.left, borderRect.top, borderRect.right, borderRect.bottom, borderPaint);
        }
    }

    private Rect toRect(RectF rectF) {
        return new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    public void updateStartingRect() {
//        scale = 1;
        if (startingRectF == null) {
            startingRectF = new RectF(rect);
        } else {
            startingRectF.left = rect.left;
            startingRectF.top = rect.top;
            startingRectF.right = rect.right;
            startingRectF.bottom = rect.bottom;
        }
    }

    private float getBoundedScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }
}

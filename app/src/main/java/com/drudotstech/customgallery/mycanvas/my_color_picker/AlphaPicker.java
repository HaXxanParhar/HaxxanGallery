package com.drudotstech.customgallery.mycanvas.my_color_picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/15/2022 at 4:40 PM
 ******************************************************/


public class AlphaPicker extends BasePicker {

    /**
     * Paint object for drawing
     */
    private Paint paint;

    /**
     * float array to get the HSV values from the color
     */
    private float[] hsv;

    /**
     * base color value for which the alpha will be applied
     */
    private int baseColor;


    /**
     * Change Listener for the Saturation
     */
    private AlphaChangeListener alphaChangeListener;


    public AlphaPicker(Context context) {
        super(context);

        initPaints();
    }

    public AlphaPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.BLUE);

        initPaints();
    }

    public AlphaPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.BLUE);

        initPaints();
    }

    public AlphaPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.BLUE);

        initPaints();
    }

    private void initPaints() {
        // init hsv
        hsv = new float[]{1, 1, 1};
        // convert color to HSV
        Color.colorToHSV(baseColor, hsv);

        // init paint
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public void updatePickerHueSaturation(int color) {

        baseColor = color;
        // convert color to HSV
        Color.colorToHSV(baseColor, hsv);
        // get alpha according to current position
        final int alpha = getCurrentAlpha();
        // update the thumb paint color as well
        setThumbPaintColor(Color.HSVToColor(alpha, hsv));
        // draw again
        drawPicker();
    }

    @Override
    protected void drawPicker(Canvas canvas) {

        for (int x = 0; x < pickerWidth; x++) {
            // Alpha has the range of 0 to 255
            float value = x / pickerWidth * 255;
            paint.setColor(Color.HSVToColor((int) value, hsv));
            canvas.drawLine(x, pickerStartY, x, pickerStartY + pickerHeight, paint);
        }
    }

    @Override
    protected void onPositionChanged(float position) {
        // Alpha has the range of 0 to 255
        final int value = (int) (position / 100 * 255);
        final int color = Color.HSVToColor(value, hsv);
        setThumbPaintColor(color);

        if (alphaChangeListener != null)
            alphaChangeListener.onValueChanged(color, value);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void OnPickerChange(float x, float y) {

    }

    @Override
    public void updatePosition(float position) {
        super.updatePosition(position);
        // Alpha has the range of 0 to 255
        final int value = (int) (position / 100 * 255);
        final int color = Color.HSVToColor(value, hsv);
        setThumbPaintColor(color);
    }


    public void setAlphaChangeListener(AlphaChangeListener alphaChangeListener) {
        this.alphaChangeListener = alphaChangeListener;
    }

    public Paint getPaint() {
        return paint;
    }

    public int getCurrentAlpha() {
        // Alpha has the range of 0 to 255
        return (int) (getPosition() / 100 * 255);
    }

    public interface AlphaChangeListener {
        void onValueChanged(int color, int value);
    }
}

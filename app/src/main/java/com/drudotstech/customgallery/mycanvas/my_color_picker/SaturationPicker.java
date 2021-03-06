package com.drudotstech.customgallery.mycanvas.my_color_picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/15/2022 at 4:40 PM
 ******************************************************/


public class SaturationPicker extends BasePicker {

    /**
     * Bitmap object to draw the hue over for retrieving the hue from pixels
     */
    private final Bitmap hueBitmap = null;
    /**
     * Paint object for drawing
     */
    private Paint paint;
    /**
     * float array to get the HSV values from the color
     */
    private float[] hsv;
    /**
     * Change Listener for the Saturation
     */
    private SaturationChangeListener saturationChangeListener;
    /**
     * Paint object for drawing
     */
    private float baseColor;

    /**
     * connect HuePicker get the color value from both Hue (H) and Saturation (SV)
     */
    private HuePicker huePicker;

    /**
     * connect AlphaPicker get the color value from color (HSV) and Alpha
     */
    private AlphaPicker alphaPicker;


    public SaturationPicker(Context context) {
        super(context);
        baseColor = Color.RED;
        initPaints();
    }

    @SuppressLint("Recycle")
    public SaturationPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.GREEN);
        initPaints();
    }

    public SaturationPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.BLUE);
        initPaints();
    }

    public SaturationPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.RED);
        initPaints();
    }

    private void initPaints() {
        hsv = new float[]{baseColor, 1, 1};
        paint = new Paint();
        paint.setAntiAlias(true);

//        drawPicker();
    }

    public void updateBaseColor(float newBaseColor) {
        this.baseColor = newBaseColor;
        hsv = new float[]{baseColor, getSaturationFromPosition(getPosition()), getValueFromPosition(getPosition())};
        setThumbPaintColor(Color.HSVToColor(hsv));
        drawPicker();
    }

    @Override
    protected void drawPicker(Canvas canvas) {

        for (int x = 0; x < pickerWidth; x++) {

            // represent the width in 0-100 values
            float position = x / pickerWidth * 100;

            // get S & V values for position
            getSVFromPosition(position, hsv);

            // create color from the HSV values
            paint.setColor(Color.HSVToColor(hsv));

            // draw the vertical lines of pickerHeight with the color
            canvas.drawLine(x, pickerStartY, x, pickerStartY + pickerHeight, paint);
        }
    }

    private void getSVFromPosition(float position, float[] hsv) {

        // bound the position to 0-100
        position = Math.max(0, Math.min(100, position));

        // for 0-50 it is saturation (S in HSV)
        if (position <= 50) {
            float saturation = getSaturationFromPosition(position);
            hsv[1] = saturation;
            hsv[2] = 1;
        } else {        // for 50-100 it is value (V in HSV)
            float value = getValueFromPosition(position);
            hsv[1] = 1;
            hsv[2] = value;
        }
    }

    private float getValueFromPosition(float position) {

        // bound the position to 50-100
        position = Math.max(50, Math.min(100, position));

        // formula to get value (in 0-1 range) from position [50-100]
        // V = 1 - ( ( P - 50 ) / 50);
        return 1 - ((position - 50) / 50);
    }

    private float getSaturationFromPosition(float position) {
        // bound the position to 50-100
        position = Math.max(0, Math.min(50, position));

        // formula to get saturation (in 0-1 range) from position [0-50]
        // S = P / 50;
        return position / 50;
    }


    @Override
    protected void onPositionChanged(float position) {
        getSVFromPosition(position, hsv);

        // update thumbColor
        int color = Color.HSVToColor(hsv);
        setThumbPaintColor(color);

        // if huePicker is not null, set the HueValue
        if (huePicker != null)
            hsv[0] = huePicker.getCurrentHue();

        // if alphaPicker is not null, set alpha value from alpha picker
        if (alphaPicker != null) {
            final int alpha = alphaPicker.getCurrentAlpha();
            // the color value to be returned by the listener
            color = Color.HSVToColor(alpha, hsv);
            // update alpha picker
            alphaPicker.updateBaseColor(color);
        }


        // SV values
        final float saturation = getCurrentSaturation();
        final float value = getCurrentValue();

        if (saturationChangeListener != null)
            saturationChangeListener.onSaturationChanged(color, saturation, value);
    }


    @Override
    public void updatePosition(float position) {
        super.updatePosition(position);
        final float saturation = position / 100;
        getSVFromPosition(position, hsv);
        final int color = Color.HSVToColor(hsv);
        setThumbPaintColor(color);
    }

    public void connectHuePicker(HuePicker huePicker) {
        this.huePicker = huePicker;
    }


    public float getCurrentValue() {
        return getValueFromPosition(getPosition());
    }

    public float getCurrentSaturation() {
        return getSaturationFromPosition(getPosition());
    }

    public void setSaturationChangeListener(SaturationChangeListener saturationChangeListener) {
        this.saturationChangeListener = saturationChangeListener;
    }

    public void connectAlphaPicker(AlphaPicker alphaPicker) {
        this.alphaPicker = alphaPicker;
    }

    public void connect(HuePicker huePicker, AlphaPicker alphaPicker) {
        this.huePicker = huePicker;
        this.alphaPicker = alphaPicker;
    }

    public interface SaturationChangeListener {
        void onSaturationChanged(int color, float saturation, float value);
    }
}

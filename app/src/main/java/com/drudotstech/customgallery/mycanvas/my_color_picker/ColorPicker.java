package com.drudotstech.customgallery.mycanvas.my_color_picker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/15/2022 at 4:40 PM
 ******************************************************/


public class ColorPicker extends BasePicker {

    /**
     * Paint object for drawing
     */
    private Paint paint;
    /**
     * float array to get the HSV values from the color
     */
    private float[] hsv = {1, 1, 1};
    /**
     * Integer List of Hue colors
     */
    private List<Integer> colors = null;

    /**
     * Hue Change Listener
     */
    private ColorChangeListener colorChangeListener;

    /**
     * connect SaturationPicker to get the color value from both Hue (H) and Saturation (SV)
     */
    private SaturationPicker saturationPicker;

    /**
     * connect AlphaPicker get the color value from color (HSV) and Alpha
     */
    private AlphaPicker alphaPicker;

    /**
     * The width of sides where white and black color should show
     */
    private int blackWhiteWidth = -1;


    public ColorPicker(Context context) {
        super(context);
//        initValues();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValues();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValues();
    }

    public ColorPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initValues();
    }

    private void initValues() {

        final float hue = getHueFromPosition(getPosition());
        hsv = new float[]{hue, 1, 1};
        paint = new Paint();
        paint.setAntiAlias(true);

        int baseColor = Color.HSVToColor(hsv);
        setThumbPaintColor(baseColor);
    }

    @Override
    protected void drawPicker(Canvas canvas) {
        if (blackWhiteWidth == -1) {
            blackWhiteWidth = (int) (pickerWidth * 0.2f); // 20% (Total 4 0% -> 10 for each black and white))
        }
        if (colors == null) {
            colors = new ArrayList<>();
            for (int x = 0; x < pickerWidth; x++) {

                if (x < blackWhiteWidth) {
                    hsv[0] = 0;
                    hsv[2] = 1;

                    // 0 is white 1 is full color
                    if (x < blackWhiteWidth / 3) {
                        hsv[1] = 0;
                    } else {
                        hsv[1] = (x - (blackWhiteWidth / 3.0f)) / ((float) blackWhiteWidth / 3 * 2);
                    }
                } else if (pickerWidth - x < blackWhiteWidth) {
                    hsv[0] = 0;
                    hsv[1] = 1;

                    final float v = x - (pickerWidth - blackWhiteWidth);
                    // 0 is black 1 is color
                    if (v > blackWhiteWidth * 2 / 3f) { // show black in last one/third
                        hsv[2] = 0;
                    } else {
                        hsv[2] = 1 - (v / (blackWhiteWidth * 2 / 3f));// show color in first two third
                    }
                } else {
                    hsv[0] = (((x - blackWhiteWidth) * 360f) / (pickerWidth - blackWhiteWidth * 2));
                    hsv[1] = 1;
                    hsv[2] = 1;
                }
                paint.setColor(Color.HSVToColor(hsv));
                colors.add(Color.HSVToColor(hsv));
                canvas.drawLine(x, pickerStartY, x, pickerStartY + pickerHeight, paint);
            }
        } else {
            for (int i = 0; i < colors.size(); i++) {
                paint.setColor(colors.get(i));
                canvas.drawLine(i, pickerStartY, i, pickerStartY + pickerHeight, paint);
            }
        }
    }

    @Override
    protected void onPositionChanged(float position) {
        hsv[0] = getHueFromPosition(position);
        int color = Color.HSVToColor(hsv);
        setThumbPaintColor(color);

        // creating color from values of saturation Picker and Alpha Picker
        if (saturationPicker != null) {
            hsv[1] = saturationPicker.getCurrentSaturation();
            hsv[2] = saturationPicker.getCurrentValue();
            // update saturation picker
            saturationPicker.updateBaseColor(getCurrentHue());
        }

        int alpha = 255;
        if (alphaPicker != null) {
            alpha = alphaPicker.getCurrentAlpha();
        }

        color = Color.HSVToColor(alpha, hsv);
        if (alphaPicker != null) {
            // update the alpha picker
            alphaPicker.updateBaseColor(color, false);
        }

        if (colorChangeListener != null)
            colorChangeListener.onColorChanged(color);
    }

    @Override
    public void updatePosition(float position) {
        super.updatePosition(position);
        hsv[0] = getHueFromPosition(position);
        final int color = Color.HSVToColor(hsv);
        setThumbPaintColor(color);
    }

    public int getCurrentColor() {
        hsv[0] = getCurrentHue();
        hsv[1] = 1;
        hsv[2] = 1;

        // creating color from values of saturation Picker and Alpha Picker
        if (saturationPicker != null) {
            hsv[1] = saturationPicker.getCurrentSaturation();
            hsv[2] = saturationPicker.getCurrentValue();
            // update saturation picker
            saturationPicker.updateBaseColor(getCurrentHue());
        }

        int alpha = 255;
        if (alphaPicker != null) {
            alpha = alphaPicker.getCurrentAlpha();
        }

        return Color.HSVToColor(alpha, hsv);
    }


    public float getCurrentHue() {
        return getHueFromPosition(getPosition());
    }

    private float getHueFromPosition(float position) {

        float sideWidth = blackWhiteWidth / pickerWidth * 100;
        if (position < sideWidth) {
            hsv[0] = 0;
            hsv[2] = 1;

            // 0 is white 1 is full color
            if (position < sideWidth / 3) {
                hsv[1] = 0;
            } else {
                hsv[1] = (position - (sideWidth / 3.0f)) / ((float) sideWidth / 3 * 2);
            }
        } else if (100 - position < sideWidth) {
            hsv[0] = 0;
            hsv[1] = 1;

            final float v = position - (100 - sideWidth);
            // 0 is black 1 is color
            if (v > sideWidth * 2 / 3f) { // show black in last one/third
                hsv[2] = 0;
            } else {
                hsv[2] = 1 - (v / (sideWidth * 2 / 3f));// show color in first two third
            }
        } else {
            hsv[0] = (position - sideWidth) * 6;
            hsv[1] = 1;
            hsv[2] = 1;
        }

        return hsv[0];
    }

    public void setHueChangeListener(ColorChangeListener colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
    }

    public void connect(SaturationPicker saturationPicker, AlphaPicker alphaPicker) {
        this.saturationPicker = saturationPicker;
        this.alphaPicker = alphaPicker;
    }

    public void connect( AlphaPicker alphaPicker) {
        this.alphaPicker = alphaPicker;
    }

    public void connect(SaturationPicker saturationPicker) {
        this.saturationPicker = saturationPicker;
    }

    public interface ColorChangeListener {
        void onColorChanged(int color);
    }
}

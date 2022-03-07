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


public class HuePicker extends BasePicker {

    /**
     * Paint object for drawing
     */
    private Paint paint;
    /**
     * float array to get the HSV values from the color
     */
    private float[] hsv;
    /**
     * Integer List of Hue colors
     */
    private List<Integer> colors = null;

    /**
     * Hue Change Listener
     */
    private HueChangeListener hueChangeListener;

    /**
     * connect SaturationPicker to get the color value from both Hue (H) and Saturation (SV)
     */
    private SaturationPicker saturationPicker;

    /**
     * connect AlphaPicker get the color value from color (HSV) and Alpha
     */
    private AlphaPicker alphaPicker;


    public HuePicker(Context context) {
        super(context);
//        initValues();
    }

    public HuePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initValues();
    }

    public HuePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initValues();
    }

    public HuePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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

        if (colors == null) {
            colors = new ArrayList<>();
            for (int x = 0; x < pickerWidth; x++) {
                hsv[0] = (x * 360f) / pickerWidth;
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
        final float hue = (position / 100) * 360;
        hsv[0] = hue;
        hsv[1] = 1;
        hsv[2] = 1;
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

        if (hueChangeListener != null)
            hueChangeListener.onHueChanged(color, hue);
    }

    @Override
    public void updatePosition(float position) {
        super.updatePosition(position);
        final float hue = (position / 100) * 360;
        hsv[0] = hue;
        final int color = Color.HSVToColor(hsv);
        setThumbPaintColor(color);
    }


    public float getCurrentHue() {
        return getHueFromPosition(getPosition());
    }

    private float getHueFromPosition(float position) {
        return position * 360.0f / 100.0f;
    }

    public void setHueChangeListener(HueChangeListener hueChangeListener) {
        this.hueChangeListener = hueChangeListener;
    }

    public void connect(SaturationPicker saturationPicker, AlphaPicker alphaPicker) {
        this.saturationPicker = saturationPicker;
        this.alphaPicker = alphaPicker;
    }

    public interface HueChangeListener {
        void onHueChanged(int color, float hue);
    }
}

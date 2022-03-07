package com.drudotstech.customgallery.mycanvas.my_color_picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/15/2022 at 4:40 PM
 ******************************************************/


public class NumberPicker extends BasePicker {

    /**
     * Paint object for drawing
     */
    private Paint paint;

    /**
     * Base color for drawing picker
     */
    private int baseColor;

    private PositionChangeListener positionChangeListener;

    public NumberPicker(Context context) {
        super(context);
        baseColor = Color.RED;
//        initValues();
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.RED);
        typedArray.recycle();
        initValues();
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.RED);
        typedArray.recycle();
        initValues();
    }

    public NumberPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        baseColor = typedArray.getColor(R.styleable.BasePicker_baseColor, Color.RED);
        typedArray.recycle();
        initValues();
    }

    private void initValues() {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(baseColor);

        setThumbPaintColor(baseColor);
    }

    public void setPositionChangeListener(PositionChangeListener positionChangeListener) {
        this.positionChangeListener = positionChangeListener;
    }

    @Override
    protected void drawPicker(Canvas canvas) {
        canvas.drawRect(0, pickerStartY, pickerWidth, pickerStartY + pickerHeight, paint);
    }

    @Override
    protected void onPositionChanged(float position) {
        if (positionChangeListener != null) {
            positionChangeListener.OnPositionChanged(position);
        }
    }

    @Override
    public void updatePosition(float position) {
        super.updatePosition(position);
    }

    public interface PositionChangeListener {
        void OnPositionChanged(float position);
    }

}

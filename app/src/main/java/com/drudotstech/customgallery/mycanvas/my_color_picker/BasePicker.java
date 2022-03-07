package com.drudotstech.customgallery.mycanvas.my_color_picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/15/2022 at 3:38 PM
 ******************************************************/


public abstract class BasePicker extends View {

    /**
     * Stroke Width of the Thumb
     */
    protected int strokeWidth = 3;

    /**
     * The height and the width of picker only
     */
    protected float pickerWidth, pickerHeight;

    /**
     * the starting point y (vertical) of picker within the complete view
     */
    protected float pickerStartY;

    /**
     * The height and the width of complete view
     */
    protected float width, height;

    /**
     * In the range of 0 to 100
     */
    private float position;

    /**
     * Center X,Y points of the picker
     */
    private float centerX, centerY;

    /**
     * Paint for the Thumb circle
     */
    private Paint thumbPaint;

    /**
     * Paint for the Thumb circle stroke
     */
    private Paint thumbStrokePaint;

    /**
     * Radius of the Thumb. This variable only stores the original value
     */
    private float smallerRadius;

    /**
     * Radius of the Thumb when it is selected. This variable only stores the  value
     */
    private float biggerRadius;

    /**
     * Radius of the Thumb used for drawing
     */
    private float tempRadius;

    public BasePicker(Context context) {
        super(context);
        position = 50;
//        init(width, height);
        initPaints();
    }

    public BasePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        position = a.getInt(R.styleable.BasePicker_position, 50);
//        init(width, height);
        a.recycle();
        initPaints();
    }

    public BasePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        position = a.getInt(R.styleable.BasePicker_position, 50);
//        init(width, height);
        a.recycle();
        initPaints();
    }

    public BasePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BasePicker);
        position = a.getInt(R.styleable.BasePicker_position, 50);
//        init(width, height);
        a.recycle();
        initPaints();
    }

    public BasePicker(Context context, float width, float height, int position) {
        super(context);
        this.position = position;
        initPaints();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        init(w, h);
    }

    private void init(float width, float height) {
        pickerHeight = height / 2.0f;
        pickerWidth = width;
        pickerStartY = (height - pickerHeight) / 2.0f;

        // circle size = 0.6 * 2 ---> 1.2 of picker height (radius is half the circle)
        smallerRadius = pickerHeight * 0.6f;
        // bigger circle size = 0.9 * 2 ---> 1.8 of picker height (radius is half the circle)
        biggerRadius = pickerHeight * 0.9f;
        tempRadius = smallerRadius;

        centerX = width / 2.0f;
        centerY = height / 2.0f;
    }

    private void initPaints() {
        thumbPaint = new Paint();
        thumbPaint.setColor(Color.RED);
        thumbPaint.setStyle(Paint.Style.FILL);

        thumbStrokePaint = new Paint();
        thumbStrokePaint.setColor(Color.WHITE);
        thumbStrokePaint.setStrokeWidth(strokeWidth);
        thumbStrokePaint.setStyle(Paint.Style.STROKE);
    }

    public void drawPicker() {
        invalidate();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        // clip the picker canvas from left right to keep space for the thumb movement of position 0 or 100
        canvas.clipRect(getThumbRadius(), 0, width - getThumbRadius(), height);
        drawPicker(canvas);
        canvas.restore();

        drawThumb(canvas);
    }

    private void drawThumb(Canvas canvas) {

        // get x value from the position value
        float cx = getCxFromPosition(position);

        // bound the thumb within the picker bounds
        cx = Math.max(tempRadius + strokeWidth, Math.min(pickerWidth - (tempRadius + strokeWidth), cx));

        // draw the circle and stroke
        canvas.drawCircle(cx, centerY, tempRadius, thumbPaint);
        canvas.drawCircle(cx, centerY, tempRadius, thumbStrokePaint);
    }

    private float getCxFromPosition(float position) {
        return position * (width / 100.0f);
    }

    private float getPositionFromCx(float cx) {
        return cx * 100.0f / width;
    }


    /**
     * Implement this method in the child class
     */
    protected abstract void drawPicker(Canvas canvas);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                tempRadius = biggerRadius;
                invalidate();
                break;


            case MotionEvent.ACTION_UP:
                tempRadius = smallerRadius;
                invalidate();
                break;


            case MotionEvent.ACTION_MOVE:
//                OnPickerChange(x, y);
                x = getBoundedX(x);
                position = getPositionFromCx(x);
                Log.d("XY_TEST", " -------> " + position);

                onPositionChanged(position);

                invalidate();
                break;
        }
        return true;
    }

    protected abstract void onPositionChanged(float position);

    //  region ------------------------G E T T E R S   &   S E T T E R S-------------------------------

    private float getBoundedX(float x) {
        return Math.max(0, Math.min(width, x));
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public void updatePosition(float position) {
        this.position = position;
        invalidate();
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public Paint getThumbPaint() {
        return thumbPaint;
    }

    public void setThumbPaint(Paint thumbPaint) {
        this.thumbPaint = thumbPaint;
    }

    public Paint getThumbStrokePaint() {
        return thumbStrokePaint;
    }

    public void setThumbStrokePaint(Paint thumbStrokePaint) {
        this.thumbStrokePaint = thumbStrokePaint;
    }

    public float getSmallerRadius() {
        return smallerRadius;
    }

    public void setSmallerRadius(float smallerRadius) {
        this.smallerRadius = smallerRadius;
    }

    public void setThumbPaintColor(int color) {
        thumbPaint.setColor(color);
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    protected float getThumbRadius() {
        return biggerRadius + strokeWidth;
    }

    //    endregion

}

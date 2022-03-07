package com.drudotstech.customgallery.mycanvas;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/4/2022 at 5:03 PM
 ******************************************************/


public class MyBrushView extends View {

    private final int minSize = 1;
    private int size;
    private int color;
    private int scale = 2;

    private Path path;
    private Paint paint;
    private List<Point> points;

    private int height, width;

    public MyBrushView(Context context) {
        super(context);
        color = Color.WHITE;
        size = minSize;
    }

    public MyBrushView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyBrushView);
        color = typedArray.getColor(R.styleable.MyBrushView_brushColor, Color.WHITE);
        size = Math.max(minSize, typedArray.getInt(R.styleable.MyBrushView_brushSize, 1));
        typedArray.recycle();
    }

    public MyBrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyBrushView);
        color = typedArray.getColor(R.styleable.MyBrushView_brushColor, Color.WHITE);
        size = Math.max(minSize, typedArray.getInt(R.styleable.MyBrushView_brushSize, 1));
        typedArray.recycle();
    }

    public MyBrushView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyBrushView);
        color = typedArray.getColor(R.styleable.MyBrushView_brushColor, Color.WHITE);
        size = Math.max(minSize, typedArray.getInt(R.styleable.MyBrushView_brushSize, 1));
        typedArray.recycle();
    }


    private void init() {

        final int screenWidth = MyUtils.getScreenWidth(getContext());
        final int screenHeight = MyUtils.getScreenHeight(getContext());
        int cx = screenWidth / 2;
        int cy = screenHeight / 2;
        cx = cx - 40 * scale;
        cy = cy - 40 * scale;

        path = new Path();

        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(size);
        paint.setAntiAlias(true);

        points = new ArrayList<>();
        points.add(new Point((cx + 40 * scale), (cy)));
        points.add(new Point((cx + 39 * scale), (cy + scale)));
        points.add(new Point((cx + 38 * scale), (cy + 2 * scale)));
        points.add(new Point((cx + 37 * scale), (cy + 3 * scale)));
        points.add(new Point((cx + 36 * scale), (cy + 4 * scale)));
        points.add(new Point((cx + 35 * scale), (cy + 5 * scale)));
        points.add(new Point((cx + 34 * scale), (cy + 6 * scale)));
        points.add(new Point((cx + 33 * scale), (cy + 7 * scale)));
        points.add(new Point((cx + 32 * scale), (cy + 8 * scale)));
        points.add(new Point((cx + 31 * scale), (cy + 9 * scale)));
        points.add(new Point((cx + 30 * scale), (cy + 10 * scale)));
        points.add(new Point((cx + 29 * scale), (cy + 11 * scale)));
        points.add(new Point((cx + 28 * scale), (cy + 12 * scale)));
        points.add(new Point((cx + 27 * scale), (cy + 13 * scale)));
        points.add(new Point((cx + 26 * scale), (cy + 14 * scale)));
        points.add(new Point((cx + 27 * scale), (cy + 15 * scale)));
        points.add(new Point((cx + 28 * scale), (cy + 16 * scale)));
        points.add(new Point((cx + 29 * scale), (cy + 17 * scale)));
        points.add(new Point((cx + 30 * scale), (cy + 18 * scale)));
        points.add(new Point((cx + 31 * scale), (cy + 19 * scale)));
        points.add(new Point((cx + 32 * scale), (cy + 20 * scale)));
        points.add(new Point((cx + 33 * scale), (cy + 21 * scale)));
        points.add(new Point((cx + 34 * scale), (cy + 22 * scale)));
        points.add(new Point((cx + 35 * scale), (cy + 23 * scale)));
        points.add(new Point((cx + 36 * scale), (cy + 24 * scale)));
        points.add(new Point((cx + 37 * scale), (cy + 25 * scale)));
        points.add(new Point((cx + 38 * scale), (cy + 26 * scale)));
        points.add(new Point((cx + 39 * scale), (cy + 27 * scale)));
        points.add(new Point((cx + 40 * scale), (cy + 28 * scale)));
        points.add(new Point((cx + 41 * scale), (cy + 29 * scale)));
        points.add(new Point((cx + 42 * scale), (cy + 30 * scale)));
        points.add(new Point((cx + 43 * scale), (cy + 31 * scale)));
        points.add(new Point((cx + 44 * scale), (cy + 32 * scale)));
        points.add(new Point((cx + 45 * scale), (cy + 33 * scale)));
        points.add(new Point((cx + 46 * scale), (cy + 34 * scale)));
        points.add(new Point((cx + 45 * scale), (cy + 35 * scale)));
        points.add(new Point((cx + 43 * scale), (cy + 36 * scale)));
        points.add(new Point((cx + 41 * scale), (cy + 37 * scale)));
        points.add(new Point((cx + 39 * scale), (cy + 38 * scale)));
        points.add(new Point((cx + 37 * scale), (cy + 39 * scale)));
        points.add(new Point((cx + 34 * scale), (cy + 40 * scale)));
        points.add(new Point((cx + 31 * scale), (cy + 41 * scale)));
        points.add(new Point((cx + 28 * scale), (cy + 42 * scale)));
        points.add(new Point((cx + 23 * scale), (cy + 43 * scale)));
        points.add(new Point((cx + 18 * scale), (cy + 44 * scale)));

        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);

        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        width = w;
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paint);
    }

    public void updateSize(int size) {
        this.size = Math.max(minSize, size);
        if (paint == null) {
            init();
        } else {
            paint.setStrokeWidth(this.size);
        }
        invalidate();
    }

    public void updateColor(int color) {
        this.color = color;
        if (paint == null) {
            init();
        } else {
            paint.setColor(color);
        }
        invalidate();
    }


}

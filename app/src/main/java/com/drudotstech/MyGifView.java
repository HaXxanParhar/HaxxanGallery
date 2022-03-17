package com.drudotstech;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedImageDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/17/2022 at 4:11 PM
 ******************************************************/


public class MyGifView extends View {

    private static final int DEFAULT_MOVIEW_DURATION = 1000;
    AnimatedImageDrawable drawable;
    private int resourceId;
    private Movie movie;

    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;
    private Paint paint = new Paint();

    private float scale;
    private float screenWidth, screenHeight, centerX, centerY;
    private float scaleX, scaleY;
    private float width, height;


    public MyGifView(Context context) {
        super(context);
        resourceId = R.drawable.gif;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public MyGifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyGifView);
        resourceId = typedArray.getResourceId(R.styleable.MyGifView_android_src, R.drawable.gif);
        typedArray.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public MyGifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyGifView);
        resourceId = typedArray.getResourceId(R.styleable.MyGifView_android_src, R.drawable.gif);
        typedArray.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void init() {
        movie = Movie.decodeStream(getResources().openRawResource(resourceId));
        scaleX = width / movie.width();
        scaleY = height / movie.height();

        screenWidth = CanvasUtils.getScreenWidth(getContext());
        screenHeight = CanvasUtils.getScreenHeight(getContext());

        centerX = screenWidth / 2;
        centerY = screenHeight / 2;

        requestLayout();
    }

    public void setImageResource(int resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (movie != null) {
            updateAnimationTime();
            drawGif(canvas);
            invalidate();
        } else {
            drawGif(canvas);
        }
    }

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = movie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawGif(Canvas canvas) {
        movie.setTime(mCurrentAnimationTime);

//        canvas.save();
        canvas.scale(scaleX, scaleY);
        movie.draw(canvas, 0, 0);
//        movie.draw(canvas, 0, 0);
//        canvas.restore();
    }
}

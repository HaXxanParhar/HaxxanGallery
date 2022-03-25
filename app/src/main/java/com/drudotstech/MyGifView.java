package com.drudotstech;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;
import com.drudotstech.customgallery.mycanvas.models.PlayStatus;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/17/2022 at 4:11 PM
 ******************************************************/


public class MyGifView extends View {

    private static final int DEFAULT_MOVIE_DURATION = 1000;
    private int resourceId;
    private Movie movie;
    private long movieStart = 0;
    private int currentAnimationTime = 0;

    private float scale = 1;
    private float screenWidth, screenHeight, centerX, centerY;
    private float scaleX, scaleY;
    private float width, height;

    private boolean isSelected;
    private PlayStatus playStatus = PlayStatus.STOPPED;


    public MyGifView(Context context, float width, float height, int resourceId) {
        super(context);
        this.width = width;
        this.height = height;
        this.resourceId = resourceId;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        init();
    }

    public MyGifView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyGifView);
        resourceId = typedArray.getResourceId(R.styleable.MyGifView_android_src, R.drawable.loading10);
        typedArray.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public MyGifView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyGifView);
        resourceId = typedArray.getResourceId(R.styleable.MyGifView_android_src, R.drawable.loading10);
        typedArray.recycle();
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    private void init() {
        if (resourceId != -1)
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
        init();
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
        this.resourceId = -1;
        init();
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
            Log.d("TEST_MOVIE", String.format("Time : %d ---> %d", movie.duration(), currentAnimationTime));
            drawGif(canvas);
            if (playStatus == PlayStatus.PLAYING)
                invalidate();
        }
    }

    private void updateAnimationTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (movieStart == 0) {
            movieStart = now;
        }
        int dur = movie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIE_DURATION;
        }
        currentAnimationTime = (int) ((now - movieStart) % dur);
    }

    private void drawGif(Canvas canvas) {
        movie.setTime(currentAnimationTime);
        canvas.scale(scale * scaleX, scale * scaleY);
        movie.draw(canvas, 0, 0);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void play() {
        if (movie == null) return;
        playStatus = PlayStatus.PLAYING;
        invalidate();
    }

    public void pause() {
        if (movie == null) return;
        playStatus = PlayStatus.PAUSED;
        invalidate();
    }

    public void stop() {
        if (movie == null) return;
        playStatus = PlayStatus.STOPPED;
        movieStart = 0;
        requestLayout();
    }

    public boolean isPlaying() {
        return playStatus == PlayStatus.PLAYING;
    }
}

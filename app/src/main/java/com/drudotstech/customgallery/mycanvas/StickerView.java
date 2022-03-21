package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 1:34 PM
 ******************************************************/


@SuppressLint("ViewConstructor,ObsoleteSdkInt")
public class StickerView extends View {

    // region --> V A R I A B L E S <
    public static final int DEFAULT_MOVIE_DURATION = 1000;
    public static final int BITMAP = 1;
    public static final int TEXT = 2;
    public static final int EMOJI = 3;
    public static final int EDITABLE_TEXT = 4;
    public static final int GIF = 5;

    private static final String TAG = "yam";
    private static final int MIN_TEXT_SIZE = 10; // minimum text size for text and emojis
    private static final int MAX_EMOJI_SIZE = 400; // max text size for emojis
    private static final int TEXT_MARGIN = 40; // the margin from screen for text,emoji
    // variables for GIF
    private static final boolean SHOW_BORDERS = false;
    public int stickerType; // bitmap or text
    public String text = "5:34 PM";
    public int left, top, right, bottom, height, width;
    public RectF rect;
    public Context context;
    public Bitmap bitmap;
    public float screenWidth;
    public float screenHeight;
    public float marginedScreenWidth;
    public float marginedScreenHeight;
    public float centerX, centerY;
    public boolean isScaled;// if scaling is required
    public boolean isTranslated;// if translation is required
    public boolean isRotated;// if rotation is required
    public RectF canvasRect;
    public TextPaint staticLayoutPaint;
    public Paint textPaint;
    public Paint secondaryTextPaint;
    public Paint borderPaint;
    public Paint redPaint;
    public Paint bluePaint;
    public int margin = 2;
    public int defaultTextSize = 200; // the base text size for Text and emojis
    public Rect textRect;
    public RectF borderRect;
    public RectF startingRectF; // to save the init rectf before making changes to bitmap
    public StaticLayout staticLayout; // static layout for multiline text
    public boolean isSelected;
    public float scale = 1;
    public Movie movie;
    public long movieStart = 0;
    public int currentAnimationTime = 0;
    private Matrix matrix;
    private float[] matrixValues = new float[9];
    // variables for the GIFs
    private int resourceId;
    private int gifScaleX, gifScaleY, gifWidth, gifHeight;

    // endregion

    /**
     * Pass the Sticker Text and canvas rect
     */
    public StickerView(Context context, String text, Paint paint, RectF canvasRect) {
        super(context);
        this.context = context;
        this.stickerType = TEXT;
        this.canvasRect = canvasRect;
        this.text = text;
        this.textPaint = paint;

        // init paints and other items
        init(context);

        // the Rect for bitmap and text
        initRects(context);
    }

    /**
     * Pass the Sticker Text, canvas rect and sticker Type. Type can only be TEXT, EDITABLE_TEXT or EMOJI
     */
    public StickerView(Context context, String text, Paint paint, RectF canvasRect, int type) {
        super(context);
        this.context = context;

        // if type is not TEXT nor EMOJI
        if (type != TEXT && type != EMOJI && type != EDITABLE_TEXT)
            this.stickerType = TEXT;
        else
            this.stickerType = type;

        this.canvasRect = canvasRect;
        this.text = text;
        this.textPaint = paint;

        // init paints and other items
        init(context);

        // the Rect for bitmap and text
        initRects(context);
    }

    /**
     * Pass the Sticker Text, default textSize, canvas rect and sticker Type. Type can only be TEXT or EMOJI or EDITABLE_TEXT
     */
    public StickerView(Context context, String text, Paint paint, RectF canvasRect, int type, int defaultTextSize) {
        super(context);
        this.context = context;
        this.defaultTextSize = defaultTextSize;

        // if type is not TEXT nor EMOJI
        if (type != TEXT && type != EMOJI && type != EDITABLE_TEXT)
            this.stickerType = TEXT;
        else
            this.stickerType = type;

        this.canvasRect = canvasRect;
        this.text = text;
        this.textPaint = paint;

        // init paints and other items
        init(context);

        // the Rect for bitmap and text
        initRects(context);
    }

    /**
     * Pass the Sticker bitmap and canvas rect
     */
    public StickerView(Context context, Bitmap bitmap, RectF canvasRect) {
        super(context);
        this.context = context;
        this.stickerType = BITMAP;
        this.canvasRect = canvasRect;
        this.bitmap = bitmap;

        init(context);

        initRects(context);
    }

    /**
     * Pass the GIF resourceId and canvas rect
     */
    public StickerView(Context context, Movie movie, RectF canvasRect) {
        super(context);
        this.context = context;
        this.stickerType = GIF;
        this.movie = movie;
        this.resourceId = -1;
        this.gifWidth = width;
        this.gifHeight = height;
        this.canvasRect = canvasRect;

        init(context);

        initRects(context);
    }

    /**
     * Pass the GIF resourceId and canvas rect
     */
    public StickerView(Context context, int resourceId, RectF canvasRect) {
        super(context);
        this.context = context;
        this.stickerType = GIF;
        this.resourceId = resourceId;
        this.gifWidth = -1;
        this.gifHeight = -1;
        this.canvasRect = canvasRect;

        init(context);

        initRects(context);
    }

    /**
     * Copy Constructor of the StickerView
     */
    public StickerView(StickerView s) {
        super(s.context);
        this.context = s.context;
        this.bitmap = s.bitmap;
        this.text = s.text;
        this.textPaint = s.textPaint;
        this.secondaryTextPaint = s.secondaryTextPaint;
        this.textRect = s.textRect;
        this.stickerType = s.stickerType;

        staticLayout = s.staticLayout;
        staticLayoutPaint = s.staticLayoutPaint;
        bluePaint = s.bluePaint;

        width = s.width;
        height = s.height;

        left = s.left;
        top = s.top;
        right = s.right;
        bottom = s.bottom;

        rect = s.rect;
        canvasRect = s.canvasRect;

        screenWidth = s.screenWidth;
        screenHeight = s.screenHeight;

        marginedScreenWidth = s.marginedScreenWidth;
        marginedScreenHeight = s.marginedScreenHeight;

        defaultTextSize = s.defaultTextSize;

        centerX = s.centerX;
        centerY = s.centerY;
        isScaled = s.isScaled;// if scaling is required
        isTranslated = s.isTranslated;// if translation is required
        isRotated = s.isRotated;// if rotation is required

        borderPaint = s.borderPaint;
        redPaint = s.redPaint;
        margin = s.margin;

        borderRect = s.borderRect;
        startingRectF = s.startingRectF; // to save the starting rectf before making changes to bitmap

        isSelected = s.isSelected;
        scale = s.scale;
        matrix = s.matrix;
        matrixValues = s.matrixValues;


        this.resourceId = s.resourceId;
        this.movie = s.movie;
        this.movieStart = s.movieStart;
        this.currentAnimationTime = s.currentAnimationTime;
        this.gifScaleX = s.gifScaleX;
        this.gifScaleY = s.gifScaleY;
        this.gifWidth = s.gifWidth;
        this.gifHeight = s.gifHeight;
    }

    private void initRects(Context context) {

        // Bitmap
        if (stickerType == BITMAP) {
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            // to make it in center
            left = (int) (centerX - (width / 2));
            top = (int) (centerY - (height / 2));
            right = left + width;
            bottom = top + height;
            rect = new RectF(left, top, right, bottom);
            borderRect = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        }

        // GIF
        else if (stickerType == GIF) {
            if (resourceId != -1)
                movie = Movie.decodeStream(getResources().openRawResource(resourceId));
            width = movie.width();
            height = movie.height();


            gifScaleX = (int) (gifWidth / movie.width());
            gifScaleY = (int) (gifHeight / movie.height());

            // to make it in center
            left = (int) (centerX - (width / 2));
            top = (int) (centerY - (height / 2));
            right = left + width;
            bottom = top + height;
            rect = new RectF(left, top, right, bottom);
            borderRect = new RectF(rect.left, rect.top, rect.right, rect.bottom);
        }

        // Text sticker i.e. Text Emoji
        else if (stickerType == TEXT || stickerType == EMOJI || stickerType == EDITABLE_TEXT) {
            textRect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), textRect);

            final float halfWidth = textRect.width() / 2.0f;
            final float halfHeight = textRect.height() / 2.0f;

            rect = new RectF();
            rect.left = centerX - halfWidth;
            rect.right = centerX + halfWidth;
            rect.top = centerY - halfHeight;
            rect.bottom = centerY + halfHeight;

            borderRect = new RectF();
            borderRect.left = centerX - halfWidth;
            borderRect.right = centerX + halfWidth;
            borderRect.top = centerY - halfHeight;
            borderRect.bottom = centerY + halfHeight;

            secondaryTextPaint = new Paint();
            secondaryTextPaint.setColor(Color.WHITE);
            secondaryTextPaint.setTypeface(ResourcesCompat.getFont(context, R.font.metropolis_bold));
            secondaryTextPaint.setTextSize(50);

            buildStaticLayout();
        }
    }

    private void buildStaticLayout() {
        staticLayoutPaint = new TextPaint(textPaint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            staticLayout = StaticLayout.Builder
                    .obtain(text, 0, text.length(), staticLayoutPaint, (int) marginedScreenWidth)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setIncludePad(false)
                    .setLineSpacing(0, 1)
                    .build();
        } else {
            staticLayout = new StaticLayout(text, 0, text.length(), staticLayoutPaint,
                    (int) marginedScreenWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f,
                    0, false);
        }
    }

    private void init(Context context) {
        screenWidth = canvasRect.width();
        screenHeight = canvasRect.height();

        marginedScreenWidth = screenWidth - TEXT_MARGIN * 2;
        marginedScreenHeight = screenHeight - TEXT_MARGIN * 2;

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

        bluePaint = new Paint();
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setColor(Color.BLUE);
        bluePaint.setAntiAlias(true);
        bluePaint.setStrokeWidth(CanvasUtils.toPx(context, 2f));

        setRotation(0);

        matrix = new Matrix();
        scale = 1.0f;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(TAG, "------------------------------- on Draw --------------------------------------");
        Log.d(TAG, "Left : " + rect.left + "  |  Top : " + rect.top + "  |  Right : " + rect.right + "  |  Bottom : " + rect.bottom);

        //------------------------------------- T E X T --------------------------------------------
        if (stickerType == TEXT || stickerType == EMOJI || stickerType == EDITABLE_TEXT) {

            // scaling
            float newFontSize = defaultTextSize * scale;

            if (stickerType == EMOJI) // for emojis max size is 256 px
                newFontSize = Math.min(MAX_EMOJI_SIZE, newFontSize);

            textPaint.setTextSize(Math.max(MIN_TEXT_SIZE, newFontSize));

            // update the staticLayout
            buildStaticLayout();

            // getting the rect after scaling for the width of text
            staticLayoutPaint.getTextBounds(text, 0, text.length(), textRect);

            // rect is the translated rect, so make the borderRect with center X,Y of rect
            // When translated this rect changes, we need to update the borderRect accordingly
            // because borderRect is used for drawing the sticker (Bitmap, text, emoji)
            final float centerX = rect.centerX();
            final float centerY = rect.centerY();

            // textRect has the width of the Text from the Paint. Useful if text is not taking the
            // whole screen width. If that case textRect.width() will exceed the screen width.
            // So use the screenWidth is that case.
            int textWidth = (int) Math.min(marginedScreenWidth, textRect.width());
            final int w = (int) (textWidth / 2.0f);

            // staticLayout has the height of the Text. Useful for multiline text
            final int h = (int) (staticLayout.getHeight() / 2.0f);

            borderRect.left = centerX - w;
            borderRect.right = centerX + w;
            borderRect.top = centerY - h;
            borderRect.bottom = centerY + h;

            canvas.save();

            switch (textPaint.getTextAlign()) {
                case LEFT:
                    canvas.translate(borderRect.left, borderRect.top);
                    break;
                case CENTER:
                    canvas.translate(borderRect.centerX(), borderRect.top);
                    break;
                case RIGHT:
                    canvas.translate(borderRect.right, borderRect.top);
                    break;
            }

            staticLayout.draw(canvas);
            canvas.restore();

        }

        //------------------------------------- B I T M A P --------------------------------------------

        else if (stickerType == BITMAP || stickerType == GIF) {

            matrix.setTranslate(rect.left, rect.top);
//            matrix.postRotate(getRotation(), rect.centerX(), rect.centerY());
            matrix.postScale(scale, scale, rect.centerX(), rect.centerY());
            if (stickerType == BITMAP) {
                canvas.drawBitmap(bitmap, matrix, null);
            } else {
                if (movie != null) {
                    updateAnimationTime();
                    movie.setTime(currentAnimationTime);
//                    canvas.translate(rect.left, rect.top);

                    canvas.scale(scale, scale, rect.centerX(), rect.centerY());
//                    canvas.scale(scale * gifScaleX, scale * gifScaleY, rect.centerX(), rect.centerY());
                    movie.draw(canvas, rect.left, rect.top);
//                    drawGif(canvas);
//                    invalidate();
                }
            }


            // update the border Rect based on the Rotation & Scale
            if (startingRectF != null) {
                matrix.getValues(matrixValues);

                //------------------- Scaling --------------------------------
                float scaledX = 1;
                float scaledY = 1;
//                if (stickerType == BITMAP) {
                scaledX = matrixValues[Matrix.MSCALE_X];
                scaledY = matrixValues[Matrix.MSCALE_Y];
//                } else {
//                    scaledX = scale;
//                    scaledY = scale;
//                }
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

                Log.d("haxx", "Before Rect  --> Left : " + borderRect.left + "  |  Top : "
                        + borderRect.top + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);
            }
        }


        if (SHOW_BORDERS) {
            if (isSelected) {
                canvas.drawRect(borderRect, redPaint);
                canvas.drawRoundRect(rect, 30, 30, redPaint);
                Log.d(TAG, "---------- TEST_BORDER ---------");
                Log.d(TAG, "TEST_BORDER draw : Left : " + borderRect.left + "  |  Top : " + borderRect.top + "  |  Right : " + borderRect.right + "  |  Bottom : " + borderRect.bottom);
            } else {
                canvas.drawRect(borderRect, bluePaint);
                canvas.drawRoundRect(rect, 30, 30, bluePaint);
            }
        }

//        canvas.drawCircle(centerX, centerY, 3f, bluePaint);
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
        canvas.translate(rect.left, rect.top);
        canvas.scale(scale * gifScaleX, scale * gifScaleY, rect.centerX(), rect.centerY());
        movie.draw(canvas, left, top);
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
        return MyCanvas.getBoundedScale(scale);
    }
}

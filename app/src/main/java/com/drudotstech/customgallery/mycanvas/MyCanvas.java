package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.mycanvas.models.CanvasState;
import com.drudotstech.customgallery.mycanvas.models.LayerModel;
import com.drudotstech.customgallery.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/2/2022 at 12:37 PM
 ******************************************************/

public class MyCanvas extends View {

    // region --> V A R I A B L E S <--
    private static final String TAG = "Haxx";

    /**
     * constant to assign invalid value
     */
    private static final int INVALID_VALUE = -1;

    /**
     * Scale factor.  lesser value = more sensitive
     */
    private static final int SCALING_SENSITIVITY = 200;

    /**
     * min scale factor
     */
    private static final float MIN_SCALE = 0.3f;

    /**
     * max scale factor
     */
    private static final float MAX_SCALE = 15;

    /**
     * the delay (ms) between action down and up that will count as click
     */
    private static final int CLICK_DELAY = 200;
    private static final float TOUCH_TOLERANCE = 0;

    /**
     * The Context
     */
    private final Context context;
    private final int minSize = 1;
    /**
     * rect of the canvas
     */
    public RectF screenRect;
    /**
     * Flag to indicate whether sticker selection is enabled
     */
    private boolean isSelectionEnabled = true;
    /**
     * delete bitmap
     */
    private Bitmap deleteBitmap;
    /**
     * delete icon size
     */
    private float deleteIconSize = 60f;
    /**
     * distance from where the deletion area will show
     */
    private float showDeleteAreaDistance = deleteIconSize * 4;
    /**
     * delete area
     */
    private RectF deleteRect;
    /**
     * delete icon rect
     */
    private RectF deleteIconRect;
    /**
     * paint for the delete area
     */
    private Paint deletePaint;
    /**
     * center point of deleted aread. This will be used to calculate the distance of
     * sticker from the center of deleted area
     */
    private PointF deleteCenterPoint;
    /**
     * flag to indicate whether to show delete area
     */
    private boolean showDeleteArea = false;
    /**
     * anti alise paint for bitmaps
     */
    private Paint bitmapPaint;
    /**
     * the rect of the main bitmap
     */
    private RectF mainRect;
    /**
     * background i.e. blurred image
     */
    private Bitmap backgroundBitmap;
    /**
     * List of layers
     */
    private List<LayerModel> layers = new ArrayList<>();
    /**
     * object of selected layer
     */
    private LayerModel selectedLayer;
    /**
     * the index of the selected layer from the layers list
     */
    private int selectedLayerIndex = INVALID_VALUE;
    /**
     * rect differences from the touch x,y. useful for drawing bitmap accurately after touch
     */
    private float dLeft, dTop, dRight, dBottom;
    /**
     * variable to store the time of action down
     */
    private long actionDownTime;

    // for rotation & scaling
    /* flag to indicate if second finger (touch pointer) is on screen or not. No on Screen = True.
     * this is used to avoid accidentally moving the sticker when pointers changes.
     * I.e. When user touch finger1 the sticker will be drawn according to finger1 coordinates,
     * If user touch down finger2 and move up finger1, now the sticker will draw according to the only
     * finger on screen which is finger2.
     * This behaviour makes the sticker move from finger1 to finger2 abruptly, which is bad UX.
     * To avoid this we use this flag to indicate that the finger1 is up now, so instead of drawing
     * according to finger2, draw according to finger1
     *  */
    private boolean isFirstPointerUpNow;
    /**
     * First touch point. This is used to calculate the scale and rotation angle
     */
    private PointF firstPoint = new PointF();
    /**
     * Second touch point after first touch. I.e. when user uses two fingers to pinch
     * This is used to calculate the scale and rotation angle
     */
    private PointF secondPoint = new PointF();
    /**
     * secondary touch pointer identifier 1
     */
    private int pointerId1 = INVALID_VALUE;
    /**
     * secondary touch pointer identifier 2
     */
    private int pointerId2 = INVALID_VALUE;
    /**
     * angle of rotation
     */
    private float angle;
    /**
     * to store starting scale before applying new scale
     */
    private float startingScale = INVALID_VALUE;
    /**
     * to store starting angle before applying new rotation
     */
    private float startingRotation = INVALID_VALUE;
    /**
     * Green paint to draw something in green
     */
    private Paint greenPaint;
    // Drawing with Brush
    private boolean isDrawingEnabled;
    private float mCurX = 0f;
    private float mCurY = 0f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private Path mPath;
    private Paint drawingPaint;
    private int size;
    private int color;


    // Listeners
    /**
     * Listener for Layer click. Whenever any layer is clicked, this listener is called
     */
    private OnLayerClickListener onLayerClickListener;

    /**
     * Listener for Layer selected. Whenever a layer is selected, this listener is called
     */
    private OnLayerSelectListener onLayerSelectListener;

    // endregion

    public MyCanvas(Context context) {
        super(context);
        this.context = context;

        deletePaint = new Paint();
        deletePaint.setStyle(Paint.Style.STROKE);
        deletePaint.setColor(Color.BLUE);
        deletePaint.setStrokeWidth(CanvasUtils.toPx(context, 1f));

        greenPaint = new Paint();
        greenPaint.setStyle(Paint.Style.STROKE);
        greenPaint.setColor(Color.GREEN);
        greenPaint.setStrokeWidth(CanvasUtils.toPx(context, 2f));

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
    }


    //region --> G E T T E R S   &   S E T T E R S <--

    public static float getBoundedScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }

    public void init() {
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        final int width = layoutParams.width;
        final int height = layoutParams.height;
        screenRect = new RectF(0, 0, width, height);

        // create a rect for delete area
        float heightOffset = height * 0.10f; // to get the 10% of the height
        float widthOffset = width * 0.25f; // to get the 25% of the width
        float left = (width / 2.0f) - (widthOffset / 2.0f);
        float right = (width / 2.0f) + (widthOffset / 2.0f);
        float top = height - heightOffset;
        float bottom = height;
        final int bottomMargin = 40;
        deleteRect = new RectF(left, top - bottomMargin, right, bottom - bottomMargin);

        float halfSize = deleteIconSize / 2;
        deleteBitmap = CanvasUtils.getBitmapFromPNG(context, R.drawable.trash, deleteIconSize, deleteIconSize);

        // create centered rect
        deleteIconRect = new RectF(
                deleteRect.centerX() - halfSize,
                deleteRect.centerY() - halfSize,
                deleteRect.centerX() + halfSize,
                deleteRect.centerY() + halfSize);

        deleteCenterPoint = new PointF(deleteIconRect.centerX(), deleteIconRect.centerY());

        mPath = new Path();
        drawingPaint = new Paint();
        drawingPaint.setStrokeWidth(5);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setColor(Color.WHITE);
        drawingPaint.setAntiAlias(true);
        drawingPaint.setDither(true);
        drawingPaint.setAlpha(0xff);
//        drawingPaint.setStrokeJoin(Paint.Join.BEVEL);
//        drawingPaint.setStrokeCap(Paint.Cap.BUTT);
//        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
//        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setStrokeCap() {
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
        drawingPaint.setStrokeJoin(Paint.Join.BEVEL);
    }

    public void setWhitener() {
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
        drawingPaint.setShadowLayer(20, 10, 10, drawingPaint.getColor());
//        drawingPaint.setMaskFilter(new BlurMaskFilter(100, BlurMaskFilter.Blur.SOLID));
    }

    public void setBothRound() {
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setNoneRound() {
        drawingPaint.setStrokeJoin(Paint.Join.BEVEL);
        drawingPaint.setStrokeCap(Paint.Cap.SQUARE);
    }

    public Paint getDrawingPaintCopy() {
        Paint paint = new Paint();
        if (drawingPaint != null) {
            paint.setStyle(drawingPaint.getStyle());
            paint.setStrokeWidth(drawingPaint.getStrokeWidth());
            paint.setColor(drawingPaint.getColor());
//            paint.setColor(Color.WHITE);
            paint.setAntiAlias(drawingPaint.isAntiAlias());
            paint.setDither(drawingPaint.isDither());
            paint.setStrokeJoin(drawingPaint.getStrokeJoin());
            paint.setStrokeCap(drawingPaint.getStrokeCap());
            paint.setMaskFilter(drawingPaint.getMaskFilter());
//            paint.setShadowLayer(50, 0, 0, Color.WHITE);

//            paint.setShadowLayer(
//                    drawingPaint.getShadowLayerRadius(),
//                    drawingPaint.getShadowLayerDx(),
//                    drawingPaint.getShadowLayerDy(),
//                    drawingPaint.getShadowLayerColor()
//                    );
        }
        return paint;
    }


    public void addBackgroundBitmap(Bitmap bitmap) {
        backgroundBitmap = bitmap;
        invalidate();
    }

    public void addLayer(LayerModel layer) {

        // first unselect all the sticker layers
        for (LayerModel layerModel : layers) {
            if (layerModel.type == LayerModel.STICKER
                    && layerModel.sticker != null
                    && layerModel.sticker.isSelected()) {
                layerModel.sticker.setSelected(false);
            }
        }
        selectedLayer = null;

        // add new layer in the layers
        switch (layer.type) {
            case LayerModel.FILTER:
                mainRect = layer.mainRect;
                // if filter layer is already added in the layers
                if (layers.size() >= 1 && layers.get(0) != null && layers.get(0).type == LayerModel.FILTER) {
                    // set the layer on the existing layer
                    layers.set(0, layer);
                } else {
                    // add new layer
                    layers.add(0, layer);
                }
                break;

            case LayerModel.PAINT:
            case LayerModel.STICKER:
                layer.sticker.canvasRect = mainRect;//todo: verify if we need this line or not
                layer.sticker.setSelected(true);
                layers.add(layer);

                // also update the selected layer
                selectedLayer = layers.get(layers.size() - 1);
                break;
        }
        invalidate();
    }

    public Bitmap getFinalBitmap() {
        //this.measure(100, 100);
        //this.layout(0, 0, 100, 100);
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bmp;
    }

    public CanvasState getCurrentCanvasState() {
        return new CanvasState(backgroundBitmap, layers);
    }

    public void updateFromCanvasState(CanvasState canvasState) {
        layers = new ArrayList<>();
        for (LayerModel layer : canvasState.getLayers()) {
            if (layer != null) {
                if (layer.type == LayerModel.STICKER) {
                    LayerModel newLayer = new LayerModel(new StickerView(layer.sticker));
                    this.layers.add(newLayer);
                } else {
                    this.layers.add(layer.copy());
                }
            }
        }
        backgroundBitmap = canvasState.getBackgroundBitmap();
        invalidate();
    }

    public void resetStickerSelection() {
        selectedLayerIndex = INVALID_VALUE;
        selectedLayer = null;
    }

    public boolean isSelectionEnabled() {
        return isSelectionEnabled;
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        isSelectionEnabled = selectionEnabled;
    }

    public void setOnLayerClickListener(OnLayerClickListener onLayerClickListener) {
        this.onLayerClickListener = onLayerClickListener;
    }

    public void setOnLayerSelectListener(OnLayerSelectListener onLayerSelectListener) {
        this.onLayerSelectListener = onLayerSelectListener;
    }

    public boolean isDrawingEnabled() {
        return isDrawingEnabled;
    }

    public void setDrawingEnabled(boolean drawingEnabled) {
        isDrawingEnabled = drawingEnabled;
    }

    // endregion

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw blur background
        if (backgroundBitmap != null)
            canvas.drawBitmap(backgroundBitmap, null, screenRect, bitmapPaint);


        // draw rest of the layers
        if (layers != null && !layers.isEmpty()) {
            LayerModel firstLayer = layers.get(0);
            for (LayerModel layer : layers) {

                switch (layer.type) {

                    case LayerModel.FILTER:
                        canvas.drawBitmap(layer.mainBitmap, null, layer.mainRect, bitmapPaint);
//                        canvas.drawLine(0, screenRect.centerY(), screenRect.width(), screenRect.centerY(), deletePaint);
//                        canvas.drawLine(screenRect.centerX(), 0, screenRect.centerX(), screenRect.height(), deletePaint);

                        if (deleteBitmap != null && showDeleteArea) {
                            canvas.drawBitmap(deleteBitmap, null, deleteIconRect, null);
//                            canvas.drawRect(deleteRect, deletePaint);
                        }
                        break;

                    case LayerModel.STICKER:
                        //Lay the view out with the known dimensions
                        layer.sticker.layout(layer.sticker.left, layer.sticker.top,
                                layer.sticker.right, layer.sticker.bottom);

                        //Translate the canvas so the view is drawn at the proper coordinates
                        canvas.save();
                        canvas.translate(0, 0);

                        //Draw the View and clear the translation
                        layer.sticker.draw(canvas);
                        canvas.restore();
                        break;

                    case LayerModel.PAINT:
                        canvas.drawBitmap(firstLayer.mainBitmap, null, firstLayer.mainRect, layer.paint);
                        break;

                    case LayerModel.DRAWING:
                        canvas.drawPath(layer.path, layer.paint);
                        break;
                }
            }
        }
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        if (isSelectionEnabled) {
            try {

                final float x = event.getX();
                final float y = event.getY();
//                final float X = event.getRawX();
//                final float Y = event.getRawY();


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        // If drawing on canvas is enabled
                        if (isDrawingEnabled) {
                            mPath = new Path();
                            // create a new drawing layer and add in layers
                            LayerModel layerModel = new LayerModel(getDrawingPaintCopy(), mPath);
                            layers.add(layerModel);

                            mPath.moveTo(x, y);
                            mCurX = x;
                            mCurY = y;

                            invalidate();
                        } else {


                            // note the action down time
                            actionDownTime = System.currentTimeMillis();

                            // first touch pointer
                            pointerId1 = event.getPointerId(event.getActionIndex());

                            // get sticker index on which user touched for selection/unselection
                            int touchedViewIndex = findSelectedViewIndex(x, y);

                            // nothing is selected already
                            if (selectedLayer == null) {

                                // touch a view/sticker
                                if (touchedViewIndex != INVALID_VALUE) {

                                    // move the touched view on top of the layers & update the previous Index
                                    selectedLayerIndex = moveViewToTop(touchedViewIndex);

                                    // get selected view from the top
                                    selectedLayer = layers.get(layers.size() - 1);

                                    // make the touched view -> selected
                                    selectedLayer.sticker.setSelected(true);

                                    // notify the listener
                                    if (onLayerSelectListener != null)
                                        onLayerSelectListener.onLayerSelected(selectedLayer, layers.size() - 1);

                                    // save the starting rect
                                    selectedLayer.sticker.updateStartingRect();

                                    // calculate the difference from touch place to selected Rect
                                    dLeft = (x - selectedLayer.sticker.rect.left);
                                    dRight = (x - selectedLayer.sticker.rect.right);
                                    dTop = (y - selectedLayer.sticker.rect.top);
                                    dBottom = (y - selectedLayer.sticker.rect.bottom);
                                    invalidate();
                                }
                            }

                            // Some View is selected already
                            else {

                                // if clicked on a view/sticker & not on empty screen
                                if (touchedViewIndex != INVALID_VALUE) {

                                    // first make the previous selected View -> unselected
                                    if (selectedLayerIndex != touchedViewIndex && selectedLayerIndex != -1)
                                        layers.get(selectedLayerIndex).sticker.setSelected(false);

                                    // move the touched view on top of the layers & update the previous Index
                                    selectedLayerIndex = moveViewToTop(touchedViewIndex);

                                    // get selected view from the top
                                    selectedLayer = layers.get(layers.size() - 1);

                                    // make the touched view -> selected
                                    selectedLayer.sticker.setSelected(true);

                                    // notify the listener
                                    if (onLayerSelectListener != null)
                                        onLayerSelectListener.onLayerSelected(selectedLayer, layers.size() - 1);

                                    // save the starting rect
                                    selectedLayer.sticker.updateStartingRect();

                                    // calculate the difference from touch place to selected Rect
                                    dLeft = (x - selectedLayer.sticker.rect.left);
                                    dRight = (x - selectedLayer.sticker.rect.right);
                                    dTop = (y - selectedLayer.sticker.rect.top);
                                    dBottom = (y - selectedLayer.sticker.rect.bottom);
                                }

                                // clicked on the empty screen, unselect the selected one
                                else {
                                    if (selectedLayerIndex != INVALID_VALUE) {
                                        layers.get(selectedLayerIndex).sticker.setSelected(false);
                                        selectedLayerIndex = INVALID_VALUE;
                                        selectedLayer = null;
                                    }
                                }
                                invalidate();
                            }
                        }

                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:

                        pointerId2 = event.getPointerId(event.getActionIndex());
                        Log.v("TEST_GESTURES", "ACTION_POINTER_DOWN with p2 : " + pointerId2);
                        if (selectedLayer != null) {
                            getRawPoint(event, pointerId1, firstPoint);
                            getRawPoint(event, pointerId2, secondPoint);

                            // save the starting rect
                            selectedLayer.sticker.updateStartingRect();

                            // calculate the difference from touch place to selected Rect
                            dLeft = (x - selectedLayer.sticker.rect.left);
                            dRight = (x - selectedLayer.sticker.rect.right);
                            dTop = (y - selectedLayer.sticker.rect.top);
                            dBottom = (y - selectedLayer.sticker.rect.bottom);
                        }
                        break;


                    case MotionEvent.ACTION_POINTER_UP:
                        // reset the 2nd touch pointer
                        pointerId2 = INVALID_VALUE;
                        startingRotation = INVALID_VALUE;
                        startingScale = INVALID_VALUE;
                        Log.v("TEST_GESTURES", "ACTION_POINTER_UP p2 Cleared!");
                        isFirstPointerUpNow = true;

                        break;

                    case MotionEvent.ACTION_UP:

                        if (isDrawingEnabled) {
                            final LayerModel layerModel = layers.get(layers.size() - 1);
                            layerModel.path.lineTo(mCurX, mCurY);
                            invalidate();

                        } else {

                            Log.v("TEST_GESTURES", "ACTION_UP p1 Cleared!");

                            // reset the first touch pointer
                            pointerId1 = INVALID_VALUE;
                            startingRotation = INVALID_VALUE;
                            startingScale = INVALID_VALUE;

                            if (selectedLayer != null) {

                                // check if it is a click
                                long now = System.currentTimeMillis();

                                // it is a click if upTime - downTime is within the Click Delay
                                if (onLayerClickListener != null && now - actionDownTime <= CLICK_DELAY) {
                                    onLayerClickListener.onLayerClick(selectedLayer);
                                }


                                // check if the sticker is within the delete area
                                PointF stickerCenterPoint = new PointF(
                                        selectedLayer.sticker.borderRect.centerX(),
                                        selectedLayer.sticker.borderRect.centerY());

                                float distance = getDistanceBetweenPoints(deleteCenterPoint, stickerCenterPoint);

                                // delete the layer
                                if (distance <= (deleteIconSize * 2)) {
                                    deleteLayer(selectedLayerIndex);
                                    selectedLayer = null;
                                    selectedLayerIndex = INVALID_VALUE;

                                    invalidate();

                                    // vibrate
                                    MyUtils.vibrate(context, 100);

                                    // reset the icon size to normal
                                    float halfSize = deleteIconSize / 2;
                                    deleteIconRect = new RectF(
                                            (deleteRect.centerX() - halfSize),
                                            (deleteRect.centerY() - halfSize),
                                            (deleteRect.centerX() + halfSize),
                                            (deleteRect.centerY() + halfSize));
                                }
                                showDeleteArea = false;
                            }

                            int index = findSelectedViewIndex(x, y);
                            Log.d("Haxx", "----------o ^ o--------------- " + index);
                            if (index == -1) break;
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:

                        if (isDrawingEnabled) {

                            float dx = Math.abs(x - mCurX);
                            float dy = Math.abs(y - mCurY);

                            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                                final LayerModel layer = layers.get(layers.size() - 1);

                                BrushStore.straightLine(mCurX, mCurY, x, y, layer);

                                mCurX = x;
                                mCurY = y;
                                invalidate();
                            }
                        } else {

                            // only perform actions if view is clicked/selected
                            if (selectedLayer != null && selectedLayer.sticker.isSelected()) {

                                // When second finger is up, recalculate the differences
                                if (isFirstPointerUpNow) {
                                    // calculate the difference from touch place to selected Rect
                                    dLeft = (x - selectedLayer.sticker.rect.left);
                                    dRight = (x - selectedLayer.sticker.rect.right);
                                    dTop = (y - selectedLayer.sticker.rect.top);
                                    dBottom = (y - selectedLayer.sticker.rect.bottom);
                                    isFirstPointerUpNow = false;
                                }


                                // ---------------------------------- Translating ------------------------------

                                // updating the view position when Action Up | Translating
                                RectF newRect = new RectF();

                                // to draw the bitmap from center of the touch point
//                    newRect.left = (int) (rawX - selectedView.rect.width() / 2.0);
//                    newRect.top = (int) (rawY - selectedView.rect.height() / 2.0);
//                    newRect.right = (int) newRect.left + selectedView.rect.width();
//                    newRect.bottom = (int) newRect.top + selectedView.rect.height();

                                // to draw the bitmap from left top of the touch point
//                    newRect.left = (int) (rawX);
//                    newRect.top = (int) (rawY);
//                    newRect.right = (int) newRect.left + selectedView.width;
//                    newRect.bottom = (int) newRect.top + selectedView.height;

                                // to draw the bitmap on the same place as before and move accordingly
                                newRect.left = (x - dLeft);
                                newRect.right = (x - dRight);
                                newRect.top = (y - dTop);
                                newRect.bottom = (y - dBottom);

                                selectedLayer.sticker.rect = newRect;
                                selectedLayer.sticker.isTranslated = true;

                                Log.d(TAG, "---------- new rect---------");
                                Log.d(TAG, "Left : " + selectedLayer.sticker.rect.left
                                        + "  |  Top : " + selectedLayer.sticker.rect.top
                                        + "  |  Right : " + selectedLayer.sticker.rect.right
                                        + "  |  Bottom : " + selectedLayer.sticker.rect.bottom);


                                // --------------------------- Rotation & Scaling ------------------------------
                                if (pointerId1 != INVALID_VALUE && pointerId2 != INVALID_VALUE) {
                                    PointF newSecondPoint = new PointF();
                                    PointF newFirstPoint = new PointF();

                                    // get touch points to calculate angle and distance
                                    getRawPoint(event, pointerId1, newFirstPoint);
                                    getRawPoint(event, pointerId2, newSecondPoint);

                                    angle = angleBetweenLines(secondPoint, firstPoint, newSecondPoint, newFirstPoint);

                                    // set the starting rotation
                                    if (startingRotation == INVALID_VALUE) {
                                        startingRotation = selectedLayer.sticker.getRotation();
                                    } else {
                                        // add new rotation in the starting rotation
                                        selectedLayer.sticker.setRotation(startingRotation + angle);
                                        selectedLayer.sticker.isRotated = true;
                                    }


                                    // scaling the image
                                    float distance = distanceBetweenLines(secondPoint, firstPoint, newSecondPoint, newFirstPoint);

                                    // set the bitmap original scale as starting scale
                                    if (startingScale == INVALID_VALUE) {
                                        startingScale = selectedLayer.sticker.scale;
                                    } else {
                                        // calculate the scale from the distance
                                        float scale = distance / SCALING_SENSITIVITY;
                                        Log.d(TAG, " Scale :  " + scale + " ------- Distance : --> " + distance);
                                        scale = startingScale + scale; // add new scale in starting scale

                                        // set the new scale with within min & max limits
                                        scale = getBoundedScale(scale);
                                        selectedLayer.sticker.scale = scale;
                                        selectedLayer.sticker.isScaled = true;
                                    }
                                }

                                //------------------------ Delete Sticker ------------------------------
                                // check if the sticker is near the delete area
                                PointF stickerCenterPoint = new PointF(
                                        selectedLayer.sticker.borderRect.centerX(),
                                        selectedLayer.sticker.borderRect.centerY());
                                float distance = getDistanceBetweenPoints(deleteCenterPoint, stickerCenterPoint);

                                // show/hide the delete area
                                if (distance <= showDeleteAreaDistance) {
                                    showDeleteArea = true;

                                } else {
                                    showDeleteArea = false;
                                }

                                // show the change
                                invalidate();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        // reset values
                        pointerId1 = INVALID_VALUE;
                        pointerId2 = INVALID_VALUE;
                        startingRotation = INVALID_VALUE;
                        startingScale = INVALID_VALUE;
                        showDeleteArea = false;
                        if (selectedLayer != null) {
                            selectedLayer.sticker.isScaled = false;
                            selectedLayer.sticker.isTranslated = false;
                        }
                        break;

                    default:
                        return false;
                }

            } catch (Exception e) {
                final String message = e.getMessage();
                Log.d("Exception : ", message);
                e.printStackTrace();
                Toast.makeText(context, "Exception on Touch : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return true;
        } else {
            return false;
        }
    }

    private void straigtPen(float x, float y, LayerModel layer) {
        layer.path.lineTo(x, y);
        for (int i = 1; i < layer.paint.getStrokeWidth(); i++) {
            layer.path.moveTo(mCurX, mCurY + (i * 2));
            layer.path.lineTo(x, y + (i * 2));
            layer.path.moveTo(mCurX, mCurY - (i * 2));
            layer.path.lineTo(x, y - (i * 2));
        }
        layer.path.moveTo(mCurX, mCurY);
    }

    private void curvedPen(float x, float y, LayerModel layer) {
        layer.path.lineTo(x, y);
        int xOffset = (int) (layer.paint.getStrokeWidth() / 3);


        for (int j = (-1 * xOffset); j < xOffset; j++) {
            for (int i = 1; i < layer.paint.getStrokeWidth() / 2; i++) {
                // single line
                layer.path.moveTo(mCurX, mCurY + i + j);
                layer.path.lineTo(x, y + i + j);
                layer.path.moveTo(mCurX, mCurY - i + j);
                layer.path.lineTo(x, y - i + j);
            }
        }
        layer.path.moveTo(mCurX, mCurY);
    }


    // region --> H E L P E R S   M E T H O D S   F O R    G E S T U R E S <--

    private int findSelectedViewIndex(float x, float y) {
        int selectedIndex = INVALID_VALUE;

        // travers from top to bottom
        for (int i = layers.size() - 1; i >= 0; i--) {
            final LayerModel layerModel = layers.get(i);
            if (layerModel.type == LayerModel.STICKER // layer is a sticker
                    && isInsideRect(x, y, layerModel.sticker.borderRect)) {//touch is within the sticker area
                selectedIndex = i;
                break;
            }
        }
        return selectedIndex;
    }

    private int moveViewToTop(int index) {
        if (layers != null && layers.size() > index) {
            final LayerModel removeView = layers.remove(index); // removed from index
            layers.add(removeView); // and added on top of the list
            return layers.size() - 1;
        } else {
            return INVALID_VALUE;
        }
    }

    private boolean isInsideRect(float x, float y, RectF rect) {
        Log.d(TAG, "TEST_BORDER chek : Left : " + rect.left + "  |  Top : " + rect.top + "  |  Right : " + rect.right + "  |  Bottom : " + rect.bottom);
        return x >= rect.left && x <= rect.right && y >= rect.top && y <= rect.bottom;
    }

    void getRawPoint(MotionEvent ev, int index, PointF point) {
        final int[] location = {0, 0};
        selectedLayer.sticker.getLocationOnScreen(location);

        float x = ev.getX(index);
        float y = ev.getY(index);

        double angle = Math.toDegrees(Math.atan2(y, x));

        final float length = PointF.length(x, y);

        x = (float) (length * Math.cos(Math.toRadians(angle))) + location[0];
        y = (float) (length * Math.sin(Math.toRadians(angle))) + location[1];

        point.set(x, y);
    }

    private float angleBetweenLines(PointF secondPoint, PointF firstPoint, PointF newSecondPoint, PointF nweFirstPoint) {
        float angle1 = (float) Math.atan2((secondPoint.y - firstPoint.y), (secondPoint.x - firstPoint.x));
        float angle2 = (float) Math.atan2((newSecondPoint.y - nweFirstPoint.y), (newSecondPoint.x - nweFirstPoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    private float distanceBetweenLines(PointF secondPoint, PointF firstPoint, PointF newSecondPoint, PointF newFirstPoint) {
        float beforeDistance = getDistanceBetweenPoints(secondPoint, firstPoint);
        float afterDistance = getDistanceBetweenPoints(newSecondPoint, newFirstPoint);
        final float diff = afterDistance - beforeDistance; // distance covered
        Log.d(TAG, " Distance :  " + beforeDistance + "  -  " + afterDistance + "  =  " + diff);
        return diff;
    }

    private float getDistanceBetweenPoints(PointF secondPoint, PointF firstPoint) {
        return (float) Math.sqrt( // sqrt of sum of x, y points differences
                Math.pow( // power of difference of x points
                        Math.max(firstPoint.x, secondPoint.x) // x1
                                -                                     // difference
                                Math.min(firstPoint.x, secondPoint.x) // x2
                        , 2) // power of 2
                        +
                        Math.pow( // power of differences of y points
                                Math.max(firstPoint.y, secondPoint.y) // y 1
                                        -                             // difference
                                        Math.min(firstPoint.y, secondPoint.y) // y2
                                , 2) // power of 2
        );
    }

    private void deleteLayer(int selectedLayerIndex) {
        layers.remove(selectedLayerIndex);
    }

    // endregion

    // region  --> E F F E C T S <--

    public void rotateImage(int rotation) {
        if (layers != null && !layers.isEmpty() && layers.get(0).type == LayerModel.FILTER) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            final LayerModel layer = layers.get(0);
            Bitmap temp = layer.mainBitmap.copy(layer.mainBitmap.getConfig(), true);
            layer.mainBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, false);
            invalidate();
        }
    }

    public void flipHorizontal() {
        if (layers != null && !layers.isEmpty() && layers.get(0).type == LayerModel.FILTER) {
            Matrix matrix = new Matrix();
            final LayerModel layer = layers.get(0);

            Bitmap temp = layer.mainBitmap.copy(layer.mainBitmap.getConfig(), true);
            matrix.postScale(-1, 1, temp.getWidth() / 2.0f, temp.getHeight() / 2.0f);

            layer.mainBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, false);
            invalidate();
        }
    }

    public void flipVertical() {
        if (layers != null && !layers.isEmpty() && layers.get(0).type == LayerModel.FILTER) {
            Matrix matrix = new Matrix();
            final LayerModel layer = layers.get(0);

            Bitmap temp = layer.mainBitmap.copy(layer.mainBitmap.getConfig(), true);
            matrix.postScale(1, -1, temp.getWidth() / 2.0f, temp.getHeight() / 2.0f);

            layer.mainBitmap = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), matrix, false);
            invalidate();
        }
    }

    public void blur(Bitmap blurredBitmap, int value) {
        if (layers != null && !layers.isEmpty() && layers.get(0).type == LayerModel.FILTER) {
            final LayerModel layer = layers.get(0);
            layer.blurAmount = value;
            layer.mainBitmap = blurredBitmap;
            invalidate();
        }
    }

    public void adjustColor(float value, int type) {
        //get the paint layer & change the saturation value
        LayerModel layerModel = layers.get(1);

        // change the value
        switch (type) {
            case LayerModel.BRIGHTNESS:
                layerModel.brightness = value;
                break;
            case LayerModel.CONTRAST:
                layerModel.contrast = value;
                break;
            case LayerModel.SATURATION:
                layerModel.saturation = value;
                break;
            case LayerModel.WARMTH:
                layerModel.hue = value;
                break;
        }

        // get & set color filter
        ColorFilter colorFilter = PaintFactory.adjustColor(layerModel);
        layerModel.paint.setColorFilter(colorFilter);
        layers.set(1, layerModel);
        invalidate(); // invalidate to show changes
    }

    public void adjustColors() {
        //get the paint layer & change the saturation value
        LayerModel layerModel = layers.get(1);
        // get & set color filter
        ColorFilter colorFilter = PaintFactory.adjustColor(layerModel);
        layerModel.paint.setColorFilter(colorFilter);
        layers.set(1, layerModel);
        invalidate(); // invalidate to show changes
    }

    public void addPaintLayer() {
        if (paintNotAdded()) { // paint layers is not added yet
            ColorFilter colorFilter = PaintFactory.adjustColor(50, 50, 50, 50);
            Paint paint = new Paint();
            paint.setColorFilter(colorFilter);
            LayerModel layerModel = new LayerModel(paint);
            layers.add(1, layerModel);
        }
        invalidate();
    }

    public LayerModel getPaintLayer() {
        if (paintNotAdded()) { // paint layers is not added yet
            ColorFilter colorFilter = PaintFactory.adjustColor(50, 50, 50, 50);
            Paint paint = new Paint();
            paint.setColorFilter(colorFilter);
            LayerModel layerModel = new LayerModel(paint);
            layers.add(1, layerModel);
        }
        final LayerModel copy = layers.get(1).copy();
        return copy;
    }

    public void setPaintLayer(LayerModel layerModel) {
        if (paintNotAdded()) { // paint layers is not added yet
            layers.add(1, layerModel);
        } else {
            layers.set(1, layerModel);
        }
        adjustColors();
    }

    public int findLayer(int LayerType) {
        int index = -1;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).type == LayerType) {
                index = i;
                break;
            }
        }

        return index;
    }

    public int findStickerViewLayer(int stickerType) {
        int index = -1;
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).type == LayerModel.STICKER
                    && layers.get(i).sticker != null
                    && layers.get(i).sticker.stickerType == stickerType) {
                index = i;
                break;
            }
        }

        return index;
    }

    private boolean paintNotAdded() {
        boolean found = false;
        for (LayerModel layer : layers) {
            if (layer.type == LayerModel.PAINT) {
                found = true;
                break;
            }
        }
        return !found;
    }

    public LayerModel getFirstLayer() {
        if (layers == null || layers.isEmpty())
            return null;
        else return layers.get(0);
    }

    public LayerModel getLastLayer() {
        if (layers == null || layers.isEmpty())
            return null;
        else return layers.get(layers.size() - 1);
    }

    public void updateSize(int size) {
        this.size = Math.max(minSize, size);
        if (drawingPaint == null) {
            init();
        } else {
            drawingPaint.setStrokeWidth(this.size);
        }
        invalidate();
    }

    public void updateColor(int color) {
        this.color = color;
        if (drawingPaint == null) {
            init();
        } else {
            drawingPaint.setColor(color);
        }
        invalidate();
    }

    //endregion

    // region --> L I S T E N E R S <--

    public interface OnLayerClickListener {
        void onLayerClick(LayerModel layerModel);
    }

    public interface OnLayerSelectListener {
        void onLayerSelected(LayerModel layerModel, int layerIndex);
    }

    // endregion
}

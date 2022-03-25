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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.drudotstech.customgallery.R;
import com.drudotstech.customgallery.filters.FilterModel;
import com.drudotstech.customgallery.mycanvas.models.CanvasState;
import com.drudotstech.customgallery.mycanvas.models.DrawingType;
import com.drudotstech.customgallery.mycanvas.models.LayerModel;
import com.drudotstech.customgallery.mycanvas.models.MyPoint;
import com.drudotstech.customgallery.mycanvas.models.PointType;
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

    /**
     * The increase in the size of brush. Doodle will have the given size but for brush it will increase time multiplier
     */
    private static final float BRUSH_SIZE_MULTIPLIER = 1.6f;

    /**
     * The Context
     */
    private final Context context;

    /**
     * The minimum size a of brush i.e. Brush, Doodle, Whitener
     */
    private final int minBrushSize = 1;

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
     * anti alias paint for bitmaps
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

    //------------- for rotation & scaling -----------------
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

    //------------- for brush and drawing  -----------------

    /**
     * Flag to indicate user is drawing.
     */
    private boolean isDrawingEnabled;

    /**
     * The drawing type.
     */
    private DrawingType drawingType;

    /**
     * Current X coordinate of touch while drawing
     */
    private float curX = 0f;

    /**
     * Current Y coordinate of touch while drawing
     */
    private float curY = 0f;

    /**
     * Temp variable to store the Path of a drawing. This path will be added to layer once single drawing is over
     */
    private Path path;

    /**
     * The Paint for the drawing. User will change this paint. Paint will be added to layer once single drawing is over
     */
    private Paint drawingPaint;

    /**
     * Stroke bitmap of the Brush,
     */
    private Bitmap brushStrokeBitmap;

    /**
     * drawable Id of the Stroke bitmap of the Brush
     */
    private int strokeDrawableId;

    /**
     * List of points for drawing bitmaps
     */
    private List<MyPoint> points;

    /**
     * The size of the drawing bitmap.
     */
    private float drawingSize = 12;

    /**
     * The bitmap to display when whitener is selected
     */
    private Bitmap whitenerBrush;

    /**
     * Flag to indicate if the invalidate needs to be called again immediately after one pass.
     * Used for redrawing GIFs again and again
     */
    private boolean invalidateAgain;


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

    /**
     * Constructor to initialize the view programmatically.
     */
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

        whitenerBrush = CanvasUtils.getBitmapFromVector(context, R.drawable.ic_whitener_brush2);
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
        float heightOffset = height * 0.20f; // to get the 20% of the height
        float widthOffset = width * 0.10f; // to get the 10% of the width
        float left = width - widthOffset;
        float right = width;
        float top = height / 2f - (heightOffset / 2f);
        float bottom = height / 2f + (heightOffset / 2f);
        final int rightMargin = 40;
        deleteRect = new RectF(left - rightMargin, top, right - rightMargin, bottom);

        float halfSize = deleteIconSize / 2;
        deleteBitmap = CanvasUtils.getBitmapFromPNG(context, R.drawable.trash, deleteIconSize, deleteIconSize);

        // create centered rect
        deleteIconRect = new RectF(
                deleteRect.centerX() - halfSize,
                deleteRect.centerY() - halfSize,
                deleteRect.centerX() + halfSize,
                deleteRect.centerY() + halfSize);

        deleteCenterPoint = new PointF(deleteIconRect.centerX(), deleteIconRect.centerY());

        path = new Path();
        drawingPaint = new Paint();
        drawingPaint.setStrokeWidth(5);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setColor(Color.WHITE);
        drawingPaint.setAntiAlias(true);
        drawingPaint.setDither(true);
        drawingPaint.setAlpha(0xff);

        points = new ArrayList<>();

        strokeDrawableId = R.drawable.ic_brush_stroke_01;
        brushStrokeBitmap = CanvasUtils.getBitmapFromVector(context, strokeDrawableId, (int) drawingSize, (int) drawingSize); //, drawingSize, drawingSize
    }

    public void setStrokeCap() {
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
        drawingPaint.setStrokeJoin(Paint.Join.BEVEL);
    }

    public void setWhitenerConfig() {
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(drawingSize);
        drawingPaint.setColor(Color.WHITE);
        drawingPaint.setAlpha(4);
        drawingPaint.setShadowLayer(40, 0, 0, Color.WHITE);
    }

    public void setDoodleConfig() {
        drawingPaint.setStrokeJoin(Paint.Join.ROUND);
        drawingPaint.setStrokeCap(Paint.Cap.ROUND);
        drawingPaint.setStyle(Paint.Style.STROKE);
        drawingPaint.setStrokeWidth(drawingSize);
        drawingPaint.setShadowLayer(0, 0, 0, Color.WHITE);
        drawingPaint.setAlpha(255);
    }

    public void setBrushConfig() {
        drawingPaint.setStrokeJoin(Paint.Join.BEVEL);
        drawingPaint.setStrokeCap(Paint.Cap.SQUARE);
        drawingPaint.setStyle(Paint.Style.FILL);
        drawingPaint.setAlpha(255);
        drawingPaint.setShadowLayer(0, 0, 0, Color.WHITE);
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
            case LayerModel.MAIN_LAYER:
                mainRect = layer.mainRect;
                // if filter layer is already added in the layers
                if (layers.size() >= 1 && layers.get(0) != null && layers.get(0).type == LayerModel.MAIN_LAYER) {
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

//            case LayerModel.GIF:
//                layer.gifView.setSelected(true);
//                layers.add(layer);
//                // also update the selected layer
//                selectedLayer = layers.get(layers.size() - 1);
//                break;

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

    public void enableDrawing(boolean drawingEnabled, DrawingType drawingType) {
        isDrawingEnabled = drawingEnabled;
        this.drawingType = drawingType;
    }

    public void disableDrawing() {
        isDrawingEnabled = false;
        points.clear();
    }

    // endregion

    /**
     * This is where drawing is done. Everything from drawing background, layers and much more
     */
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

                    case LayerModel.MAIN_LAYER:
                        canvas.drawBitmap(layer.bitmap, null, layer.mainRect, bitmapPaint);
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

                        // invalidate again if stickerView is a GIF
                        invalidateAgain = layer.sticker.stickerType == StickerView.GIF;
                        break;

                    case LayerModel.PAINT:
                        canvas.drawBitmap(firstLayer.bitmap, null, firstLayer.mainRect, layer.paint);
                        break;

                    case LayerModel.DOODLE:
                        canvas.drawPath(layer.path, layer.paint);
                        break;

                    case LayerModel.DRAWING:
                        for (MyPoint point : layer.points) {
                            if (point.isFirst()) {
                                canvas.drawBitmap(layer.head, point.x, point.y, layer.paint);
                            } else if (point.isLast()) {
                                canvas.drawBitmap(layer.head, point.x, point.y, layer.paint);
                            } else {
//                                canvas.save();
//                                canvas.rotate(angle, point.x, point.y);
                                canvas.drawBitmap(layer.head, point.x, point.y, layer.paint);
//                                canvas.restore();
                            }
                        }
                        break;

                    case LayerModel.GIF:
                        if (layer.gifView != null)
                            layer.gifView.draw(canvas);
                        break;
                }
            }
        }

        // draw whitener brush
        if (whitenerBrush != null && isDrawingEnabled && drawingType == DrawingType.WHITENER) {
            canvas.drawBitmap(whitenerBrush,
                    curX - (whitenerBrush.getWidth() / 2f),
                    curY - (whitenerBrush.getHeight() / 2f),
                    null);
        }

        if (invalidateAgain) {
            invalidateAgain = false;
            invalidate();
        }
    }

    /**
     * To handle touch interactions i.e. touch down, touch move, touch up etc
     */
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        if (isSelectionEnabled) {
            try {

                final float x = event.getX();
                final float y = event.getY();

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:

                        // If drawing on canvas is enabled
                        if (isDrawingEnabled) {

                            // For Doodle, path is used to draw
                            switch (drawingType) {
                                case DOODLE:
                                case WHITENER:

                                    // create a new path
                                    path = new Path();
                                    // create a new layer and add in layers
                                    LayerModel layerModel = new LayerModel(new Paint(drawingPaint), path);
                                    layers.add(layerModel);
                                    path.moveTo(x, y);
                                    break;


                                // For Brush, points are used to draw Bitmaps
                                case BRUSH:

                                    // create new points list
                                    List<MyPoint> points = new ArrayList<>();
                                    MyPoint point = new MyPoint(x, y, PointType.FIRST);
                                    points.add(point);

                                    // create a new layer and add in layers
                                    LayerModel model = new LayerModel(points, MyUtils.copyBitmap(brushStrokeBitmap), new Paint(drawingPaint));
                                    layers.add(model);
                                    break;
                            }

                            // update the current x,y coordinates
                            curX = x;
                            curY = y;
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

                            // add the last point in the path
                            switch (drawingType) {
                                case DOODLE:
                                case WHITENER:
                                    final LayerModel layerModel = layers.get(layers.size() - 1);
                                    layerModel.path.lineTo(curX, curY);
                                    break;

                                // add the last point in the points list
                                case BRUSH:
                                    MyPoint point = new MyPoint(x, y, PointType.LAST);
                                    points.add(point);
                                    break;
                            }
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

                            switch (drawingType) {
                                case BRUSH:
                                    final LayerModel lastLayer = getLastLayer();
                                    double distance = BrushStore.distanceBetween(curX, curY, x, y);
                                    double radius = brushStrokeBitmap.getWidth() / 2f;
                                    int count = (int) Math.ceil(distance / radius);
                                    float angle = (float) BrushStore.angleOf(curX, curY, x, y);
                                    Log.d("ANGLE_TEST", "--------------------------------------------------------------------------------------");
                                    Log.d("ANGLE_TEST", String.format("%d ------> %.2f , %.2f  -   %.2f , %.2f  ====> of %.2f distance with  %.2f degree", count, curX, curY, x, y, distance, angle));

                                    // adding the point in between these two points
                                    int i = 1;
                                    while (!(radius * i >= distance)) {
                                        final float x2 = (float) BrushStore.getX2(angle, radius * i, curX);
                                        final float y2 = (float) BrushStore.getY2(angle, radius * i, curY);
                                        MyPoint point = new MyPoint(x2, y2);
                                        Log.d("ANGLE_TEST", String.format("---> %d --> %.2f , %.2f of %f", i, x2, y2, radius * i));
                                        lastLayer.points.add(point);
                                        i++;
                                    }
                                    // adding the last point
                                    MyPoint point = new MyPoint(x, y);
                                    lastLayer.points.add(point);
                                    break;

                                case WHITENER:
                                case DOODLE:
                                    // create a curve line from current x,y to next x,y
                                    path.quadTo(curX, curY, (x + curX) / 2, (y + curY) / 2);
                                    break;
                            }

                            // updating the current x,y coordinates
                            curX = x;
                            curY = y;
                            invalidate();
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

    private float angleBetweenLines(PointF secondPoint, PointF firstPoint, PointF
            newSecondPoint, PointF nweFirstPoint) {
        float angle1 = (float) Math.atan2((secondPoint.y - firstPoint.y), (secondPoint.x - firstPoint.x));
        float angle2 = (float) Math.atan2((newSecondPoint.y - nweFirstPoint.y), (newSecondPoint.x - nweFirstPoint.x));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    private float distanceBetweenLines(PointF secondPoint, PointF firstPoint, PointF
            newSecondPoint, PointF newFirstPoint) {
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

    private void deleteLastLayer() {
        layers.remove(layers.size() - 1);
    }

    /**
     * Undo only for Layer type = DRAWING right now. Will extend the fucntionality for more layers
     */
    public void undo() {
        final LayerModel lastLayer = getLastLayer();
        if (lastLayer != null &&
                (lastLayer.type == LayerModel.DRAWING || lastLayer.type == LayerModel.DOODLE)) {
            deleteLastLayer();
            invalidate();
        }
    }

    // endregion

    // region  --> E F F E C T S <--


    public void rotateImage(int rotation) {
        if (layers != null && !layers.isEmpty() && layers.get(0) != null) {

            // get First layer
            final LayerModel firstLayer = getFirstLayer();
            if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                    && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {

                // get existing matrix from the MainBitmap Class
                Matrix matrix = firstLayer.bitmapManager.getMatrix();
                if (matrix == null) matrix = new Matrix();

                // apply the rotation to the matrix
                matrix.postRotate(rotation);

                firstLayer.bitmapManager.getEditedBitmapFromMatrix(context, matrix, bitmap -> {
                    // update changes in the first layer
                    firstLayer.bitmap = bitmap;
                    invalidate();
                });
            }
        }
    }

    public void flipHorizontal() {

        if (layers != null && !layers.isEmpty() && layers.get(0) != null) {

            // get First layer
            final LayerModel firstLayer = getFirstLayer();
            if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                    && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {

                // get existing matrix from the MainBitmap Class
                Matrix matrix = firstLayer.bitmapManager.getMatrix();
                if (matrix == null) matrix = new Matrix();

                Bitmap cachedBitmap = firstLayer.bitmapManager.getCachedBitmap();
                if (cachedBitmap == null)
                    cachedBitmap = firstLayer.bitmapManager.getOriginalBitmap();

                // apply the flip horizontally to the matrix by scaling with negative x
                matrix.postScale(-1, 1, cachedBitmap.getWidth() / 2.0f, cachedBitmap.getHeight() / 2.0f);

                firstLayer.bitmapManager.getEditedBitmapFromMatrix(context, matrix, bitmap -> {
                    // update changes in the first layer
                    firstLayer.bitmap = bitmap;
                    invalidate();
                });
            }
        }


//        if (layers != null && !layers.isEmpty() && layers.get(0).type == LayerModel.MAIN_LAYER) {
//
//            Bitmap cachedBitmap = MainBitmap.getCachedBitmap();
//            if (cachedBitmap == null)
//                cachedBitmap = MainBitmap.getOriginalBitmap();
//
//            // get existing matrix from the MainBitmap Class
//            Matrix matrix = MainBitmap.getMatrix();
//            if (matrix == null) matrix = new Matrix();
//
//            // apply the flip horizontally to the matrix by scaling with negative x
//            matrix.postScale(-1, 1, cachedBitmap.getWidth() / 2.0f, cachedBitmap.getHeight() / 2.0f);
//
//            MainBitmap.getEditedBitmapFromMatrix(context, matrix, new MainBitmap.GetMainBitmapCallback() {
//                @Override
//                public void getBitmap(Bitmap bitmap) {
//                    // add the bitmap in the layer and invalidate
//                    final LayerModel layer1 = layers.get(0);
//                    layer1.bitmap = bitmap;
//                    invalidate();
//                }
//            });
//        }
    }

    public void flipVertical() {
        if (layers != null && !layers.isEmpty() && layers.get(0) != null) {

            // get First layer
            final LayerModel firstLayer = getFirstLayer();
            if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                    && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {

                // get existing matrix from the MainBitmap Class
                Matrix matrix = firstLayer.bitmapManager.getMatrix();
                if (matrix == null) matrix = new Matrix();

                Bitmap cachedBitmap = firstLayer.bitmapManager.getCachedBitmap();
                if (cachedBitmap == null)
                    cachedBitmap = firstLayer.bitmapManager.getOriginalBitmap();

                // apply the flip vertically to the matrix by scaling with negative x
                matrix.postScale(1, -1, cachedBitmap.getWidth() / 2.0f, cachedBitmap.getHeight() / 2.0f);

                firstLayer.bitmapManager.getEditedBitmapFromMatrix(context, matrix, bitmap -> {
                    // update changes in the first layer
                    firstLayer.bitmap = bitmap;
                    invalidate();
                });
            }
        }
    }

    public void blur(int value) {

        if (layers != null && !layers.isEmpty() && layers.get(0) != null) {

            // get First layer
            final LayerModel firstLayer = getFirstLayer();
            if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                    && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {

                // Apply the Blur
                firstLayer.bitmapManager.getEditedBitmapFromBlur(context, value, bitmap -> {
                    // update changes in the first layer
                    firstLayer.bitmap = bitmap;
                    invalidate();
                });
            }
        }
    }

    public void sharp(int value) {
        if (layers != null && !layers.isEmpty() && layers.get(0) != null) {

            // get First layer
            final LayerModel firstLayer = getFirstLayer();
            if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                    && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {

                // Apply the Blur
                firstLayer.bitmapManager.getEditedBitmapFromSharp(context, value, bitmap -> {
                    // update changes in the first layer
                    firstLayer.bitmap = bitmap;
                    invalidate();
                });
            }
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
        /*
          Stroke Size of the Drawing
         */
        drawingSize = Math.max(minBrushSize, size);
        if (drawingType == DrawingType.BRUSH)
            drawingSize = drawingSize * BRUSH_SIZE_MULTIPLIER;// increase stroke size for brush
        if (drawingType == DrawingType.DOODLE)// change the paint size
            drawingPaint.setStrokeWidth(drawingSize);
        else if (drawingType == DrawingType.BRUSH) { // change the bitmaps size
            brushStrokeBitmap = CanvasUtils.getBitmapFromVector(context, strokeDrawableId, (int) drawingSize, (int) drawingSize);
        }

        invalidate();
    }

    public void updateColor(int color) {
        /*
          Stroke Color of the Drawing
         */
        if (drawingType == DrawingType.DOODLE) {
            drawingPaint.setColor(color); // change the paint color
            drawingPaint.setColorFilter(null);
        } else if (drawingType == DrawingType.BRUSH) { // change the bitmap color by applying Tint
            drawingPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        }
        invalidate();
    }

    public void updateBrushStroke(int strokeDrawableId) {
        this.strokeDrawableId = strokeDrawableId;
        brushStrokeBitmap = CanvasUtils.getBitmapFromVector(context, strokeDrawableId, (int) drawingSize, (int) drawingSize); //, drawingSize, drawingSize
    }

    public void applyFilter(FilterModel filter) {
        final LayerModel firstLayer = getFirstLayer();
        if (firstLayer != null && firstLayer.type == LayerModel.MAIN_LAYER
                && firstLayer.bitmapManager != null && firstLayer.bitmap != null) {
            firstLayer.bitmapManager.getEditedBitmapFromFilter(context, filter, bitmap -> {
                firstLayer.bitmap = bitmap;
                invalidate();
            });
        }
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

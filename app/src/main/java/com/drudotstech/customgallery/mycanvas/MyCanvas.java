package com.drudotstech.customgallery.mycanvas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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

    private static final String TAG = "Haxx";
    private static final int INVALID_VALUE = -1;
    private static final int SCALING_SENSITIVITY = 100; // lesser value = more sensitive
    private static final float MIN_SCALE = 0.3f; // min scale factor
    private static final float MAX_SCALE = 15; // the max scale factor
    private static final int CLICK_DELAY = 200;// the delay (ms) between action down and up that will count as click
    private final Context context;
    // region --> V A R I A B L E S <
    public RectF screenRect; // rect of the canvas
    private List<Float> listX = new ArrayList<>();
    private List<Float> listY = new ArrayList<>();
    private boolean isSelectionEnabled = false;


    private Bitmap deleteBitmap; // delete bitmap
    private float deleteIconSize = 60f; // delete icon size
    private float showDeleteAreaDistance = deleteIconSize * 4; // distance from where the deletion area will show
    private RectF deleteRect; // delete area
    private RectF deleteIconRect; // delete icon rect
    private Paint deletePaint; // paint for the delete area
    private PointF deleteCenterPoint;
    private boolean showDeleteArea = false;

    private Paint bitmapPaint; // anti alise paint for bitmaps
    private RectF mainRect; // the rect of the main bitmap
    private Bitmap backgroundBitmap; // background i.e. blurred image
    private List<LayerModel> layers = new ArrayList<>(); // layers
    private LayerModel selectedLayer; // object of selected layer
    private int selectedLayerIndex = INVALID_VALUE; // the index of the selected layer from the layers list
    private float dLeft, dTop, dRight, dBottom; // rect differences from the touch x,y. useful for drawing bitmap accurately after touch
    private long actionDownTime;

    // for rotation & scaling
    private PointF secondPoint = new PointF(); // temp point
    private PointF firstPoint = new PointF(); // temp point
    private int pointerId1 = INVALID_VALUE; // secondary touch pointer identifier
    private int pointerId2 = INVALID_VALUE; // secondary touch pointer identifier
    private float angle; // angle of rotation
    private float startingScale = INVALID_VALUE; // to store starting scale before applying new scale
    private float startingRotation = INVALID_VALUE; // to store starting angle before applying new rotation
    private Paint greenPaint;

    // Listeners
    private OnLayerClickListener onLayerClickListener;
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

    public static float getBoundedScale(float scale) {
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }

    //region --> G E T T E R S   &   S E T T E R S <--

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
            if (layer.type == LayerModel.STICKER) {
                LayerModel newLayer = new LayerModel(new StickerView(layer.sticker));
                this.layers.add(newLayer);
            } else {
                this.layers.add(layer.copy());
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

    // endregion

    public void setOnLayerSelectListener(OnLayerSelectListener onLayerSelectListener) {
        this.onLayerSelectListener = onLayerSelectListener;
    }

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
                }
            }
        }
//        canvas.drawCircle(centerX, centerY, CanvasUtils.toPx(context, 50f), paint);


//        for (int i = 0; i < listX.size(); i++) {
//            canvas.drawPoint(listX.get(i), listY.get(i), greenPaint);
//        }

    }

    // region --> H E L P E R S   M E T H O D S   F O R    G E S T U R E S <--

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        if (isSelectionEnabled) {
            try {

                final float rawX = event.getX();
                final float rawY = event.getY();
                final float X = event.getRawX();
                final float Y = event.getRawY();


                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.d("Haxx", "--- TEST_BORDER : " + rawX + " , " + rawY + "   |   " + X + " , " + Y + " ===> " + (rawX - X) + "  ,   " + (rawY - Y) + " ----- ");

                        // note the action down time
                        actionDownTime = System.currentTimeMillis();

                        // first touch pointer
                        pointerId1 = event.getPointerId(event.getActionIndex());

                        // get sticker index on which user touched for selection/unselection
                        int touchedViewIndex = findSelectedViewIndex(rawX, rawY);

                        Log.d("TEST_MULTIPLE_STICKERS", "--------------------------------- Down ---------------------------------------");
                        Log.d("TEST_MULTIPLE_STICKERS", "down Index : " + touchedViewIndex + " | Previous : " + selectedLayerIndex);

                        // nothing is selected already
                        if (selectedLayer == null) {
                            Log.d("TEST_MULTIPLE_STICKERS", " Selected == null");

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

                                Log.d("TEST_MULTIPLE_STICKERS", "Updated Previous : " + selectedLayerIndex);


                                // calculate the difference from touch place to selected Rect
                                dLeft = (rawX - selectedLayer.sticker.rect.left);
                                dRight = (rawX - selectedLayer.sticker.rect.right);
                                dTop = (rawY - selectedLayer.sticker.rect.top);
                                dBottom = (rawY - selectedLayer.sticker.rect.bottom);
                                invalidate();
                            }
                        }

                        // Some View is selected already
                        else {
                            Log.d("TEST_MULTIPLE_STICKERS", " Selected != null");

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
                                dLeft = (rawX - selectedLayer.sticker.rect.left);
                                dRight = (rawX - selectedLayer.sticker.rect.right);
                                dTop = (rawY - selectedLayer.sticker.rect.top);
                                dBottom = (rawY - selectedLayer.sticker.rect.bottom);
                            }

                            // clicked on the empty screen, unselect the selected one
                            else {
                                if (selectedLayerIndex != INVALID_VALUE) {
                                    layers.get(selectedLayerIndex).sticker.setSelected(false);
                                    selectedLayerIndex = INVALID_VALUE;
                                }
                            }
                            invalidate();
                        }
                        break;

                    case MotionEvent.ACTION_POINTER_DOWN:
                        Log.v(TAG, "ACTION_POINTER_DOWN");

                        pointerId2 = event.getPointerId(event.getActionIndex());
                        if (selectedLayer != null) {
                            getRawPoint(event, pointerId1, firstPoint);
                            getRawPoint(event, pointerId2, secondPoint);

                            // save the starting rect
                            selectedLayer.sticker.updateStartingRect();
                        }

                        break;

                    case MotionEvent.ACTION_UP:

                        listX.clear();
                        listY.clear();

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

                        int index = findSelectedViewIndex(rawX, rawY);
                        Log.d("Haxx", "----------o ^ o--------------- " + index);
                        if (index == -1) break;

                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        // reset the 2nd touch pointer
                        pointerId2 = INVALID_VALUE;
                        startingRotation = INVALID_VALUE;
                        startingScale = INVALID_VALUE;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        listX.add(rawX);
                        listY.add(rawY);
                        // only perform actions if view is clicked/selected
                        if (selectedLayer != null && selectedLayer.sticker.isSelected()) {

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
                            newRect.left = (rawX - dLeft);
                            newRect.right = (rawX - dRight);
                            newRect.top = (rawY - dTop);
                            newRect.bottom = (rawY - dBottom);

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
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        // reset values
                        listX.clear();
                        listY.clear();
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
        if (!isPaintAdded()) { // paint layers is not added yet
            ColorFilter colorFilter = PaintFactory.adjustColor(50, 50, 50, 50);
            Paint paint = new Paint();
            paint.setColorFilter(colorFilter);
            LayerModel layerModel = new LayerModel(paint);
            layers.add(1, layerModel);
        }
        invalidate();
    }

    public LayerModel getPaintLayer() {
        if (!isPaintAdded()) { // paint layers is not added yet
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
        if (!isPaintAdded()) { // paint layers is not added yet
            layers.add(1, layerModel);
        } else {
            layers.set(1, layerModel);
        }
        adjustColors();
    }

    private boolean isPaintAdded() {
        boolean found = false;
        for (LayerModel layer : layers) {
            if (layer.type == LayerModel.PAINT) {
                found = true;
                break;
            }
        }
        return found;
    }

    public LayerModel getLastLayer() {
        if (layers == null || layers.isEmpty())
            return null;
        else return layers.get(layers.size() - 1);
    }

    //endregion

    // region --> L I S T E N E R S

    public interface OnLayerClickListener {
        void onLayerClick(LayerModel layerModel);
    }

    public interface OnLayerSelectListener {
        void onLayerSelected(LayerModel layerModel, int layerIndex);
    }

    // endregion
}

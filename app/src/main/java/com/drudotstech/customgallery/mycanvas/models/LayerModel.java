package com.drudotstech.customgallery.mycanvas.models;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.drudotstech.MyGifView;
import com.drudotstech.customgallery.filters.FilterType;
import com.drudotstech.customgallery.mycanvas.CanvasUtils;
import com.drudotstech.customgallery.mycanvas.StickerView;

import java.util.List;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/7/2022 at 5:43 PM
 ******************************************************/


public class LayerModel {
    public static final int STICKER = 1;
    public static final int FILTER = 2;
    public static final int PAINT = 3;
    public static final int BRIGHTNESS = 4;
    public static final int SATURATION = 5;
    public static final int CONTRAST = 6;
    public static final int WARMTH = 7;
    public static final int DRAWING = 8;
    public static final int DOODLE = 9;
    public static final int GIF = 10;

    public int type;

    // when type is Filter
    public Bitmap mainBitmap;
    public FilterType filterType;
    public RectF mainRect;
    public int blurAmount;

    // when type is Paint
    public Paint paint;
    public float saturation;
    public float brightness;
    public float contrast;
    public float hue;

    // when type is Sticker
    public StickerView sticker;
    public TextInfo textInfo;

    // When type is doodle
    public Path path;

    // When type is drawing
    public List<MyPoint> points;
    public Bitmap head;

    // When type is Gif
    public MyGifView gifView;


    public LayerModel(StickerView sticker) {
        this.type = STICKER;
        this.sticker = sticker;
    }


    public LayerModel(List<MyPoint> points, Bitmap head, Paint paint) {
        type = DRAWING;
        this.points = points;
        this.head = head;
        this.paint = paint;
    }

    public LayerModel(Paint paint, Path path) {
        type = DOODLE;
        this.paint = paint;
        this.path = path;
    }

    public LayerModel(StickerView sticker, TextInfo textInfo) {
        this.type = STICKER;
        this.sticker = sticker;
        this.textInfo = textInfo;
    }

    public LayerModel(MyGifView gifView) {
        this.type = GIF;
        this.gifView = gifView;
    }

    public LayerModel(Paint paint) {
        this.type = PAINT;
        this.paint = paint;
        this.brightness = 50;
        this.saturation = 50;
        this.contrast = 50;
        this.hue = 50;
    }

    public LayerModel(Paint paint, float saturation, float brightness, float contrast, float hue) {
        this.type = PAINT;
        this.paint = paint;
        this.saturation = saturation;
        this.brightness = brightness;
        this.contrast = contrast;
        this.hue = hue;
    }

    public LayerModel(Bitmap mainBitmap, RectF rectF, FilterType filterType) {
        this.type = FILTER;
        this.mainBitmap = mainBitmap;
        this.mainRect = rectF;
        this.filterType = filterType;
        this.blurAmount = 0;
    }

    public LayerModel copy() {
        LayerModel copy;
        switch (type) {
            case STICKER:
                copy = new LayerModel(new StickerView(sticker));
                if (textInfo != null)
                    copy.textInfo = textInfo.copy();
                break;

            case FILTER:
                copy = new LayerModel(CanvasUtils.copyBitmap(mainBitmap), mainRect, filterType);
                break;

            case PAINT:
                Paint paint = new Paint();
                paint.setColorFilter(this.paint.getColorFilter());
                copy = new LayerModel(paint, saturation, brightness, contrast, hue);
                break;

            default:
                return null;
        }
        return copy;
    }
}

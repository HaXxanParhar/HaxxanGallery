package com.drudotstech.customgallery.mycanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

import com.drudotstech.customgallery.filters.FilterModel;
import com.drudotstech.customgallery.filters.FilterTask;
import com.drudotstech.customgallery.mycanvas.models.EffectType;
import com.drudotstech.customgallery.utils.BlurUtil;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/22/2022 at 4:01 PM
 ******************************************************/


/**
 * This class will return the Main bitmap on which editing is performed.
 * Effects like Blur & Filters etc are performed on bitmap. We can not add them as a layer in MyCanvas's layers.
 * One more crucial requirement is to make each effect reversable. I.e. If blur of 20 is applied to a bitmap once
 * and again when user apply blur of 30, Instead of making 30 blur on already blurred image, we need to apply it
 * on the original bitmap. If blur of 0 is selected which means No blur, then image should become original bitmap.
 * To make reversable effect, I created this class.
 * The idea is simple, keep record of each effect that needs to applied and when original bitmap is required,
 * return the original bitmap after applying the recorded effects.
 * Here we're applying multiple effects on each image, like Blur, Filter, Sharp and applying all of them each time
 * is not ideal so to make this process faster, we will keep a 'CachedBitmap' after applying the recorded effects.
 * And when new effect is to be applied, instead of applying all the effects from start we will just apply the
 * new effect on cached bitmap.
 */
public class BitmapManager {

    private static final int SHARP_MULTIPLIER = 20;
    private static final int BLUR_MULTIPLIER = 10;

    private EffectType currentEffect = EffectType.NONE;
    private EffectType lastEffect = EffectType.NONE;

    private Bitmap originalBitmap;
    private Bitmap cachedBitmap;
//    private Bitmap tempBitmap;

    private FilterModel filter;
    private Matrix matrix;
    private float blurValue = -1;
    private float sharpValue = -1;

    public BitmapManager(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public void getEditedBitmapFromFilter(Context context, FilterModel filter, GetMainBitmapCallback callback) {
        this.filter = filter;
        currentEffect = EffectType.FILTER;
        getEditedBitmap(context, callback);
    }

    public void getEditedBitmapFromMatrix(Context context, Matrix matrix, GetMainBitmapCallback callback) {
        this.matrix = matrix;
        currentEffect = EffectType.MATRIX;
        getEditedBitmap(context, callback);
    }

    public void getEditedBitmapFromBlur(Context context, int value, GetMainBitmapCallback callback) {
        this.blurValue = value;
        currentEffect = EffectType.BLUR;
        getEditedBitmap(context, callback);
    }

    public void getEditedBitmapFromSharp(Context context, int value, GetMainBitmapCallback callback) {
        this.sharpValue = value;
        currentEffect = EffectType.SHARPEN;
        getEditedBitmap(context, callback);
    }

    private void getEditedBitmap(Context context, GetMainBitmapCallback callback) {

        // New effect: Hence create new cached bitmap
        if (lastEffect == EffectType.NONE || currentEffect != lastEffect) {
            if (cachedBitmap != null)
                cachedBitmap.recycle();
            cachedBitmap = null;
            createCachedBitmap(context, currentEffect, callback);
        }

        // last effect is to be applied again
        else {
            applyEffects(context, callback);
        }
    }

    private void createCachedBitmap(Context context, EffectType effectType, GetMainBitmapCallback callback) {

        // create a cached bitmap from original
        cachedBitmap = CanvasUtils.getScreenFitBitmap(context, originalBitmap);

        CompletableFuture.supplyAsync(new Supplier<Bitmap>() {
            @Override
            public Bitmap get() {
                if (effectType != EffectType.MATRIX && matrix != null) {
                    cachedBitmap = Bitmap.createBitmap(cachedBitmap, 0, 0, cachedBitmap.getWidth(), cachedBitmap.getHeight(), matrix, false);
                }
                return cachedBitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (effectType != EffectType.BLUR && blurValue != -1) {
                    cachedBitmap = new BlurUtil().blur(context, bitmap, blurValue / BLUR_MULTIPLIER);
                }
                return cachedBitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (effectType != EffectType.FILTER && filter != null) {
                    cachedBitmap = new FilterTask(bitmap, filter, null).applyFilter(context);
                }
                return cachedBitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (effectType != EffectType.SHARPEN && sharpValue != -1) {
                    cachedBitmap = PaintFactory.doSharpen(context, bitmap, sharpValue / SHARP_MULTIPLIER);
                }
                return cachedBitmap;
            }
        }).thenAccept(new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap bitmap) {
                cachedBitmap = bitmap;
                applyEffects(context, callback);
            }
        });
    }

    private void applyEffects(Context context, GetMainBitmapCallback callback) {

        // create a temp bitmap for playing around
        final Bitmap tempBitmap = CanvasUtils.getScreenFitBitmap(context, cachedBitmap);

        CompletableFuture.supplyAsync(new Supplier<Bitmap>() {
            @Override
            public Bitmap get() {
                if (currentEffect == EffectType.MATRIX && matrix != null) {
                    return Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, false);
                }
                return tempBitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (currentEffect == EffectType.BLUR && blurValue != -1) {
                    bitmap = new BlurUtil().blur(context, bitmap, blurValue / BLUR_MULTIPLIER);
                }
                return bitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (currentEffect == EffectType.FILTER && filter != null) {
                    bitmap = new FilterTask(bitmap, filter, null).applyFilter(context);
                }
                return bitmap;
            }
        }).thenApply(new Function<Bitmap, Bitmap>() {
            @Override
            public Bitmap apply(Bitmap bitmap) {
                if (currentEffect == EffectType.SHARPEN && sharpValue != -1) {
                    bitmap = PaintFactory.doSharpen(context, bitmap, sharpValue / SHARP_MULTIPLIER);
                }
                return bitmap;
            }
        }).thenAccept(new Consumer<Bitmap>() {
            @Override
            public void accept(Bitmap bitmap) {
                callback.getBitmap(bitmap);
            }
        });

        // also update the last effect
        lastEffect = currentEffect;
    }

    private Bitmap applyFilterEffect(Context context, Bitmap tempBitmap) {
        if (currentEffect == EffectType.FILTER && filter != null) {
            tempBitmap = new FilterTask(tempBitmap, filter, null).applyFilter(context);
        }
        return tempBitmap;
    }

    private Bitmap applyBlurEffect(Context context, Bitmap tempBitmap) {
        if (currentEffect == EffectType.BLUR && blurValue != -1) {
            tempBitmap = new BlurUtil().blur(context, tempBitmap, blurValue);
        }
        return tempBitmap;
    }

    private Bitmap applyMatrixEffect(Bitmap tempBitmap) {
        if (currentEffect == EffectType.MATRIX && matrix != null) {
            tempBitmap = Bitmap.createBitmap(tempBitmap, 0, 0, tempBitmap.getWidth(), tempBitmap.getHeight(), matrix, false);
        }
        return tempBitmap;
    }

    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public void setOriginalBitmap(Bitmap originalBitmap) {
        this.originalBitmap = originalBitmap;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public EffectType getCurrentEffect() {
        return currentEffect;
    }

    public void setCurrentEffect(EffectType currentEffect) {
        this.currentEffect = currentEffect;
    }

    public EffectType getLastEffect() {
        return lastEffect;
    }

    public void setLastEffect(EffectType lastEffect) {
        this.lastEffect = lastEffect;
    }

    public Bitmap getCachedBitmap() {
        return cachedBitmap;
    }

    public void setCachedBitmap(Bitmap cachedBitmap) {
        this.cachedBitmap = cachedBitmap;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public int getBlurValue() {
        return (int) blurValue;
    }

    public void setBlurValue(int blurValue) {
        this.blurValue = blurValue;
    }

    public float getSharpValue() {
        return sharpValue;
    }

    public void setSharpValue(int sharpValue) {
        this.sharpValue = sharpValue;
    }

    public interface GetMainBitmapCallback {
        void getBitmap(Bitmap bitmap);
    }
}

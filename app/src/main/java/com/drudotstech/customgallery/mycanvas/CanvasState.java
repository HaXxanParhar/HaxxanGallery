package com.drudotstech.customgallery.mycanvas;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/* ********* Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 5:28 PM
 ******************************************************/


/**
 * This class represents the state of the canvas
 */
public class CanvasState {


    private Bitmap backgroundBitmap;
    private List<LayerModel> layers;


    public CanvasState(Bitmap backgroundBitmap, List<LayerModel> layers) {
        this.backgroundBitmap = backgroundBitmap.copy(backgroundBitmap.getConfig(), true);
        this.layers = new ArrayList<>();
        for (LayerModel layer : layers) {
            if (layer.type == LayerModel.STICKER) {
                LayerModel newLayer = new LayerModel(new StickerView(layer.sticker));
                this.layers.add(newLayer);
            } else {
                this.layers.add(layer.copy());
            }
        }
    }

    public Bitmap getBackgroundBitmap() {
        return backgroundBitmap;
    }

    public void setBackgroundBitmap(Bitmap backgroundBitmap) {
        this.backgroundBitmap = backgroundBitmap;
    }

    public List<LayerModel> getLayers() {
        return layers;
    }

    public void setLayers(List<LayerModel> layers) {
        this.layers = layers;
    }

}

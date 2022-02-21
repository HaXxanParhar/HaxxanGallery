package com.drudotstech.customgallery.mycanvas.models;

import android.graphics.Color;
import android.graphics.Typeface;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/18/2022 at 12:41 PM
 ******************************************************/


public class TextInfo {

    /**
     * The item index from 'FontAdapter'.
     */
    public int fontIndex;

    /**
     * The typeface for the text. Either font or Typeface can be used
     */
    public Typeface typeface;

    /**
     * The H S V values of the color for the text
     */
    public float[] hsv;

    /**
     * The alpha of the color for the text
     */
    public int alpha;

    /**
     * The positions of the color pickers
     */
    public float huePosition, saturationPosition, alphaPosition;

    public TextInfo() {
        hsv = new float[]{1, 1, 1};
        alpha = 255;
    }

    public TextInfo(int fontIndex, Typeface typeface, float[] hsv, int alpha, float huePosition, float saturationPosition, float alphaPosition) {
        this.fontIndex = fontIndex;
        this.typeface = typeface;
        this.hsv = hsv;
        this.alpha = alpha;
        this.huePosition = huePosition;
        this.saturationPosition = saturationPosition;
        this.alphaPosition = alphaPosition;
    }

    public int getColor() {
        return Color.HSVToColor(alpha, hsv);
    }

    public TextInfo copy() {
        return new TextInfo(fontIndex, typeface,
                new float[]{hsv[0], hsv[1], hsv[2]},
                alpha, huePosition, saturationPosition, alphaPosition);
    }
}

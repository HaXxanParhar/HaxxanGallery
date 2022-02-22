package com.drudotstech.customgallery.mycanvas.models;

import android.graphics.Paint.Align;

import com.drudotstech.customgallery.R;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/22/2022 at 5:31 PM
 ******************************************************/


public class AlignModel {
    private Align align;
    private int src;
    private boolean isSelected;


    public AlignModel() {
        align = Align.LEFT;
        src = R.drawable.ic_baseline_format_align_left_24;
    }

    public AlignModel(Align align, int src) {
        this.align = align;
        this.src = src;
    }

    public AlignModel(Align align, int src, boolean isSelected) {
        this.align = align;
        this.src = src;
        this.isSelected = isSelected;
    }

    public Align getAlign() {
        return align;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getSrc() {
        return src;
    }

    public void setSrc(int src) {
        this.src = src;
    }
}

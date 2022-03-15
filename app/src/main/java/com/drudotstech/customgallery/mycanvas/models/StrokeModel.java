package com.drudotstech.customgallery.mycanvas.models;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/22/2022 at 5:31 PM
 ******************************************************/


public class StrokeModel {
    private int src;
    private boolean isSelected;

    public StrokeModel(int src) {
        this.src = src;
    }

    public StrokeModel(int src, boolean isSelected) {
        this.src = src;
        this.isSelected = isSelected;
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

package com.drudotstech.customgallery.mycanvas.models;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/22/2022 at 5:31 PM
 ******************************************************/


public class StrokeModel {
    private int headSrc;
    private boolean isSelected;

    public StrokeModel(int src) {
        this.headSrc = src;
    }

    public StrokeModel(int src, boolean isSelected) {
        this.headSrc = src;
        this.isSelected = isSelected;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getHeadSrc() {
        return headSrc;
    }

    public void setHeadSrc(int headSrc) {
        this.headSrc = headSrc;
    }
}

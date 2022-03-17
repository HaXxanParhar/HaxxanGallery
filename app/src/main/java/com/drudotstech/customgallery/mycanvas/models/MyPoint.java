package com.drudotstech.customgallery.mycanvas.models;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/14/2022 at 12:50 PM
 ******************************************************/


public class MyPoint {
    public float x, y;
    public float angle;
    private PointType pointType;
    private float dx, dy;


    public MyPoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.pointType = PointType.MIDDLE;
    }

    public MyPoint(float x, float y, PointType pointType) {
        this.x = x;
        this.y = y;
        this.pointType = pointType;
    }

    public float getDx() {
        return dx;
    }

    public void setDx(float dx) {
        this.dx = dx;
    }

    public float getDy() {
        return dy;
    }

    public void setDy(float dy) {
        this.dy = dy;
    }

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public boolean isFirst() {
        return pointType == PointType.FIRST;
    }

    public boolean isLast() {
        return pointType == PointType.LAST;
    }

    public boolean isMiddle() {
        return pointType == PointType.MIDDLE;
    }
}

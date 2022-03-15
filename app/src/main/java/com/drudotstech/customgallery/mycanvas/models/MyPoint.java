package com.drudotstech.customgallery.mycanvas.models;

/********** Developed by Drudots Technology **********
 * Created by : usman on 3/14/2022 at 12:50 PM
 ******************************************************/


public class MyPoint {
    public float x,y;
    public float angle;
    private boolean isHead;
    private float dx,dy;


    public MyPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public MyPoint(float x, float y, boolean isHead) {
        this.x = x;
        this.y = y;
        this.isHead = isHead;
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

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }


}

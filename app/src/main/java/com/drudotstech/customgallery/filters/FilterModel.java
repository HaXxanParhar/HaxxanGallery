package com.drudotstech.customgallery.filters;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/27/2022 at 4:03 PM
 ******************************************************/


public class FilterModel {
    private FilterType filterType;
    private String name;
    private int drawable;
    private boolean isSelected;
    private float y, cb, cr;
    private boolean isBlackWhite;

    public FilterModel(FilterType filterType, String name, int drawable, boolean isSelected) {
        this.filterType = filterType;
        this.name = name;
        this.drawable = drawable;
        this.isSelected = isSelected;
    }

    public FilterModel(String name, int drawable, float y, float cb, float cr, boolean isBlackWhite) {
        this.filterType = FilterType.YCBCR;
        this.name = name;
        this.drawable = drawable;
        this.y = y;
        this.cb = cb;
        this.cr = cr;
        this.isBlackWhite = isBlackWhite;
    }

    public FilterModel(String name, int drawable, float y, float cb, float cr, boolean isBlackWhite, boolean isSelected) {
        this.filterType = FilterType.YCBCR;
        this.name = name;
        this.drawable = drawable;
        this.y = y;
        this.cb = cb;
        this.cr = cr;
        this.isBlackWhite = isBlackWhite;
        this.isSelected = isSelected;
    }

    public FilterModel(FilterType filterType, String name, int drawable) {
        this.filterType = filterType;
        this.name = name;
        this.drawable = drawable;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getCb() {
        return cb;
    }

    public void setCb(float cb) {
        this.cb = cb;
    }

    public float getCr() {
        return cr;
    }

    public void setCr(float cr) {
        this.cr = cr;
    }

    public boolean isBlackWhite() {
        return isBlackWhite;
    }

    public void setBlackWhite(boolean blackWhite) {
        isBlackWhite = blackWhite;
    }
}

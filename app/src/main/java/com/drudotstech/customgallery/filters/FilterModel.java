package com.drudotstech.customgallery.filters;

/********** Developed by Drudots Technology **********
 * Created by : usman on 1/27/2022 at 4:03 PM
 ******************************************************/


public class FilterModel {
    private FilterType filterType;
    private String name;
    private int drawable;
    private boolean isSelected;

    public FilterModel(FilterType filterType, String name, int drawable, boolean isSelected) {
        this.filterType = filterType;
        this.name = name;
        this.drawable = drawable;
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
}

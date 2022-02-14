package com.drudotstech.customgallery.mycanvas.bottom_sheets;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/11/2022 at 7:06 PM
 ******************************************************/


public class WidgetModel {
    String text;
    int font;

    public WidgetModel(String text, int font) {
        this.text = text;
        this.font = font;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFont() {
        return font;
    }

    public void setFont(int font) {
        this.font = font;
    }
}

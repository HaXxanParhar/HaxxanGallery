package com.drudotstech.customgallery.mycanvas.bottom_sheets;

import android.graphics.Bitmap;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/8/2022 at 6:40 PM
 ******************************************************/


public interface SelectWidgetCallback {
    void onWidgetSelected(WidgetModel widgetModel, Bitmap bitmap);
}

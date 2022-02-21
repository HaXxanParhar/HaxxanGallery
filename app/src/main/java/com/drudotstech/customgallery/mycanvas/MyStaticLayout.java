package com.drudotstech.customgallery.mycanvas;

import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

/********** Developed by Drudots Technology **********
 * Created by : usman on 2/17/2022 at 4:44 PM
 ******************************************************/


public class MyStaticLayout extends StaticLayout {
    public MyStaticLayout(CharSequence source, TextPaint paint, int width, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        super(source, paint, width, align, spacingmult, spacingadd, includepad);
    }

    public MyStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad);
    }

    public MyStaticLayout(CharSequence source, int bufstart, int bufend, TextPaint paint, int outerwidth, Alignment align, float spacingmult, float spacingadd, boolean includepad, TextUtils.TruncateAt ellipsize, int ellipsizedWidth) {
        super(source, bufstart, bufend, paint, outerwidth, align, spacingmult, spacingadd, includepad, ellipsize, ellipsizedWidth);
    }


}

package com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.extensions

import android.content.Context
import android.util.TypedValue
import com.drudotstech.customgallery.R

fun Context.fetchAccentColor(): Int {
    val typedValue = TypedValue()
    val a = obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}
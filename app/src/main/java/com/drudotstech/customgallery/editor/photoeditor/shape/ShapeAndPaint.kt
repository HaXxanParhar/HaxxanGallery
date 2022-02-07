package com.drudotstech.customgallery.editor.photoeditor.shape

import android.graphics.Paint

/**
 * Simple data class to be put in an ordered Stack
 */
open class ShapeAndPaint(
    val shape: AbstractShape,
    val paint: Paint
)
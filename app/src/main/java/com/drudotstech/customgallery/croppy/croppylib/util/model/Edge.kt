package com.drudotstech.customgallery.croppy.croppylib.util.model

import com.drudotstech.customgallery.croppy.croppylib.util.model.Edge.*

enum class Edge {
    NONE,
    LEFT,
    TOP,
    RIGHT,
    BOTTOM
}

fun Edge.opposite() {
    when (this) {
        LEFT -> RIGHT
        TOP -> BOTTOM
        RIGHT -> LEFT
        BOTTOM -> TOP
        NONE -> NONE
    }
}
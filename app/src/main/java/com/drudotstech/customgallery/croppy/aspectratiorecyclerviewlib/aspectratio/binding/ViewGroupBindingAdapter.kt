package com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.binding

import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter
import com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.AspectRatioItemViewState

@BindingAdapter("aspectSize")
fun setAspectSize(layout: RelativeLayout, aspectRatioItemViewState: AspectRatioItemViewState) {
    layout.layoutParams = layout.layoutParams
        .apply {
            height = aspectRatioItemViewState.getAspectHeight(layout.context)
            width = aspectRatioItemViewState.getAspectWidth(layout.context)
        }
}

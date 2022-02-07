package com.drudotstech.customgallery.croppy.aspectratiorecyclerviewlib.aspectratio.model

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.drudotstech.customgallery.R

data class AspectRatioItem constructor(
    @DimenRes val aspectRatioSelectedWidthRes: Int = R.dimen.width_aspect_square,
    @DimenRes val aspectRatioUnselectedHeightRes: Int = R.dimen.height_aspect_square,
    @DrawableRes val socialMediaImageRes: Int = R.drawable.ic_aspect_icon_facebook,
    @StringRes var aspectRatioNameRes: Int = R.string.portrait,
    var activeColor: Int = R.color.app_color,
    var passiveColor: Int = R.color.app_color,
    var socialActiveColor: Int = R.color.app_color,
    var socialPassiveColor: Int = R.color.app_color,
    var aspectRatio: AspectRatio

) {
    constructor(@StringRes aspectRatioNameRes: Int, aspectRatio: AspectRatio) :
            this(
                R.dimen.width_aspect_square,
                R.dimen.height_aspect_square,
                R.drawable.ic_aspect_icon_facebook,
                aspectRatioNameRes,
                R.color.app_color,
                R.color.app_color,
                R.color.app_color,
                R.color.app_color,
                aspectRatio
            )

}
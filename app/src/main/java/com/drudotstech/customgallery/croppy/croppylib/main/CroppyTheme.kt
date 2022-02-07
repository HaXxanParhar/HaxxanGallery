package com.drudotstech.customgallery.croppy.croppylib.main

import android.os.Parcelable
import androidx.annotation.ColorRes
import com.drudotstech.customgallery.R
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CroppyTheme(@ColorRes val accentColor: Int) : Parcelable {

    companion object {
        fun default() = CroppyTheme(R.color.blue)
    }
}
package com.drudotstech.customgallery.croppy.croppylib

import android.app.Activity
import com.drudotstech.customgallery.croppy.croppylib.main.CropRequest
import com.drudotstech.customgallery.croppy.croppylib.main.CroppyActivity

object Croppy {

    fun start(activity: Activity, cropRequest: CropRequest) {
        CroppyActivity.newIntent(context = activity, cropRequest = cropRequest)
            .also { activity.startActivityForResult(it, cropRequest.requestCode) }
    }
}
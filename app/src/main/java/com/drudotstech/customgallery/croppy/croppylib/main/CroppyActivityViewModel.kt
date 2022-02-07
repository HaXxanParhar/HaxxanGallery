package com.drudotstech.customgallery.croppy.croppylib.main

import android.app.Application
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.drudotstech.customgallery.croppy.croppylib.ui.CroppedBitmapData
import com.drudotstech.customgallery.croppy.croppylib.util.bitmap.BitmapUtils
import com.drudotstech.customgallery.croppy.croppylib.util.file.FileCreator
import com.drudotstech.customgallery.croppy.croppylib.util.file.FileExtension
import com.drudotstech.customgallery.croppy.croppylib.util.file.FileOperationRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CroppyActivityViewModel(val app: Application) : AndroidViewModel(app) {

    private val disposable = CompositeDisposable()

    private val saveBitmapLiveData = MutableLiveData<Uri>()

    fun getSaveBitmapLiveData(): LiveData<Uri> = saveBitmapLiveData

    fun saveBitmap(cropRequest: CropRequest, croppedBitmapData: CroppedBitmapData) {

        when (cropRequest) {
            is CropRequest.Manual -> {
                disposable.add(
                    BitmapUtils
                    .saveBitmap(croppedBitmapData, cropRequest.destinationUri.toFile())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { saveBitmapLiveData.value = cropRequest.destinationUri })
            }
            is CropRequest.Auto -> {
                val destinationUri = FileCreator.createFile(
                    FileOperationRequest(
                        cropRequest.storageType,
                        System.currentTimeMillis().toString(),
                        FileExtension.PNG
                    ),
                    app.applicationContext
                ).toUri()

                disposable.add(
                    BitmapUtils
                    .saveBitmap(croppedBitmapData, destinationUri.toFile())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { saveBitmapLiveData.value = destinationUri })

            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (disposable.isDisposed.not()) {
            disposable.dispose()
        }
    }

}
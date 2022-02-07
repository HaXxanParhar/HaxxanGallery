package com.drudotstech.customgallery.editor.photoediting.filters

import com.drudotstech.customgallery.editor.photoeditor.PhotoFilter

interface FilterListener {
    fun onFilterSelected(photoFilter: PhotoFilter?)
}
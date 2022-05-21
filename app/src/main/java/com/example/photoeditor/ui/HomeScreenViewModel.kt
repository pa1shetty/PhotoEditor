package com.example.photoeditor.ui

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photoeditor.model.PhotoDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {

    private var photoDetails = PhotoDetails()

    private val _imageBitmap = MutableLiveData<Bitmap>()
    val imageBitmap: LiveData<Bitmap> = _imageBitmap

    fun saveBitmap(imageBitmapValue: Bitmap) {
        viewModelScope.launch(Dispatchers.Default) {
            _imageBitmap.postValue(imageBitmapValue)
        }
    }

    private fun Bitmap.flip(x: Float, y: Float, cx: Float, cy: Float): Bitmap {
        return Bitmap.createBitmap(
            this,
            0,
            0,
            width,
            height,
            Matrix().apply { postScale(x, y, cx, cy) },
            true
        )
    }

    fun flipLeft() {
        viewModelScope.launch(Dispatchers.Default) {
            imageBitmap.value?.let { bitmap ->
                saveBitmap(bitmap.flip(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f))
            }
        }
    }

    fun flipRight() {
        viewModelScope.launch(Dispatchers.Default) {
            imageBitmap.value?.let { bitmap ->
                saveBitmap(bitmap.flip(1f, -1f, bitmap.width / 2f, bitmap.height / 2f))
            }
        }
    }

    fun getPhotoDetails() = photoDetails
    fun setPhotoDetails(photoDetails: PhotoDetails) {
        this.photoDetails = photoDetails
    }

    fun cropImage(x: Int, y: Int, width: Int, height: Int) {
        viewModelScope.launch(Dispatchers.Default) {
            imageBitmap.value?.let {
                saveBitmap(
                    Bitmap.createBitmap(
                        it,
                        x,
                        y,
                        width,
                        height
                    )
                )
            }
        }
    }

}
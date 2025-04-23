package com.example.lovepoly.ui.level

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class Level1ViewModel : ViewModel() {
    private val _rotationX = MutableLiveData<Float>()
    val rotationX: LiveData<Float> = _rotationX

    private val _rotationY = MutableLiveData<Float>()
    val rotationY: LiveData<Float> = _rotationY

    fun updateRotation(x: Float, y: Float) {
        _rotationX.value = x
        _rotationY.value = y
    }
}

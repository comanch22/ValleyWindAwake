package com.comanch.valley_wind_awake.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutAppViewModel() : ViewModel() {

    private val _ossLicense = MutableLiveData<Int?>()
    val ossLicense: LiveData<Int?>
        get() = _ossLicense

    fun ossLicense() {
        _ossLicense.value = 1
    }
}
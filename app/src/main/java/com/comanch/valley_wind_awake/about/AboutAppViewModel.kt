package com.comanch.valley_wind_awake.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AboutAppViewModel() : ViewModel() {

    private val _cancel = MutableLiveData<Int?>()
    val cancel: LiveData<Int?>
        get() = _cancel

    fun cancelAbout() {
        _cancel.value = 1
    }
}
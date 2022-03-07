package com.comanch.valley_wind_awake.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comanch.valley_wind_awake.LiveDataEvent
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailViewModel(val database: TimeDataDao) : ViewModel() {

    private val _navigateToList = MutableLiveData<LiveDataEvent<Int?>>()
    val navigateToList: LiveData<LiveDataEvent<Int?>>
        get() = _navigateToList

    private val _setPause = MutableLiveData<LiveDataEvent<Int?>>()
    val setPause: LiveData<LiveDataEvent<Int?>>
        get() = _setPause

    private val _offAlarm = MutableLiveData<LiveDataEvent<Int?>>()
    val offAlarm: LiveData<LiveDataEvent<Int?>>
        get() = _offAlarm

    private val _stopPlay = MutableLiveData<Long?>()
    val stopPlay: LiveData<Long?>
        get() = _stopPlay


    fun offSignal() {

        _offAlarm.value = LiveDataEvent(1)
        _navigateToList.value = LiveDataEvent(1)
    }

    fun delaySignal() {

        _setPause.value = LiveDataEvent(1)
        _navigateToList.value = LiveDataEvent(1)
    }

    fun startDelay(mDelay: Long) {

        viewModelScope.launch {
            delay(mDelay * 60000)
            _stopPlay.value = 1
        }
    }

}
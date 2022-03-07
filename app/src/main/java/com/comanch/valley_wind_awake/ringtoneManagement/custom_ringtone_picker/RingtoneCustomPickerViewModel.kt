package com.comanch.valley_wind_awake.ringtoneManagement.custom_ringtone_picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comanch.valley_wind_awake.LiveDataEvent
import com.comanch.valley_wind_awake.dataBase.RingtoneDataDao
import com.comanch.valley_wind_awake.dataBase.RingtoneData
import kotlinx.coroutines.launch

class RingtoneCustomPickerViewModel(val database: RingtoneDataDao) : ViewModel() {

    private var selectedRingtone: RingtoneData? = null
    private var setRestorePlayerFlag: Boolean = false

    private val _itemActiveState = MutableLiveData<LiveDataEvent<Boolean>>()
    val itemActiveState: LiveData<LiveDataEvent<Boolean>>
        get() = _itemActiveState

    private val _setTouchSound = MutableLiveData<LiveDataEvent<Int>>()
    val setTouchSound: LiveData<LiveDataEvent<Int>>
        get() = _setTouchSound

    private val _restorePlayerFlag = MutableLiveData<LiveDataEvent<Boolean>>()
    val restorePlayerFlag: LiveData<LiveDataEvent<Boolean>>
        get() = _restorePlayerFlag

    private val _currentRingTone = MutableLiveData<RingtoneData?>()
    val currentRingTone: LiveData<RingtoneData?>
        get() = _currentRingTone

    fun onItemClicked(ringtoneData: RingtoneData) {

        _currentRingTone.value = ringtoneData
        selectedRingtone = ringtoneData
    }

    fun setMelody() {

        viewModelScope.launch {
            selectedRingtone?.let {
                it.setUriFromMusicId(it.musicId)
                selectedRingtone?.active = 0
                selectedRingtone?.isCustom = 1
                database.insert(it)
            }
        }
    }

    fun restoreStateForCustomRingtoneFragment() {
        _restorePlayerFlag.value = LiveDataEvent(setRestorePlayerFlag)
    }

    fun resetCurrentRingTone() {
        _currentRingTone.value = null
    }

    fun setTouchSound() {
        _setTouchSound.value = LiveDataEvent(1)
    }

    fun setItemActiveState() {
        _itemActiveState.value = LiveDataEvent(true)
    }

    fun setRestorePlayerFlag(b: Boolean) {
        setRestorePlayerFlag = b
    }
}
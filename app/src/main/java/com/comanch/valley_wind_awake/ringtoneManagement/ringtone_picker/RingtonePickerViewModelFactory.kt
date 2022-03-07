package com.comanch.valley_wind_awake.ringtoneManagement.ringtone_picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comanch.valley_wind_awake.dataBase.RingtoneDataDao

class RingtonePickerViewModelFactory(private val dataSource: RingtoneDataDao) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RingtonePickerViewModel::class.java)) {
            return RingtonePickerViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
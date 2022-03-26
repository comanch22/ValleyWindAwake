package com.comanch.valley_wind_awake.frontListFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comanch.valley_wind_awake.dataBase.TimeDataDao

class ListViewModelFactory (
    private val dataSource: TimeDataDao,
    private val defaultRingtoneUri: String,
    private val defaultRingtoneTitle: String
    ) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListViewModel::class.java)) {
                return ListViewModel(dataSource, defaultRingtoneUri, defaultRingtoneTitle) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
}
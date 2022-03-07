package com.comanch.valley_wind_awake.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.comanch.valley_wind_awake.dataBase.TimeDataDao

class DetailViewModelFactory(private val dataSource: TimeDataDao) : ViewModelProvider.Factory
    {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(dataSource) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
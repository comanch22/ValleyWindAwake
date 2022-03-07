package com.comanch.valley_wind_awake.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AboutAppViewModelFactory() : ViewModelProvider.Factory
    {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AboutAppViewModel::class.java)) {
                return AboutAppViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
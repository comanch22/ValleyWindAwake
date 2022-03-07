package com.comanch.valley_wind_awake.keyboard

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.comanch.valley_wind_awake.dataBase.TimeDataDao

class KeyboardViewModelFactory(private val dataSource: TimeDataDao,
                               owner: SavedStateRegistryOwner,
                               defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs)
{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(KeyboardViewModel::class.java)) {
            return KeyboardViewModel(dataSource, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
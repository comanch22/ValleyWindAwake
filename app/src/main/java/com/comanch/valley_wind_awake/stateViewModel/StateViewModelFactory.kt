package com.comanch.valley_wind_awake.stateViewModel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class StateViewModelFactory(owner: SavedStateRegistryOwner,
                            defaultArgs: Bundle? = null) : AbstractSavedStateViewModelFactory(owner, defaultArgs)
{
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(StateViewModel::class.java)) {
            return StateViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.comanch.valley_wind_awake.aboutFragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class AboutAppViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val viewModel by lazy { AboutAppViewModel() }

    @Test
    fun check_cancelAbout(){

        viewModel.ossLicense()
        val value = viewModel.ossLicense.getOrAwaitValue()
        assertEquals(1, value)
    }
}
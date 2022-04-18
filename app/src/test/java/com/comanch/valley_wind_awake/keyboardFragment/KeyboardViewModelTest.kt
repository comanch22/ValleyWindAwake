package com.comanch.valley_wind_awake.keyboardFragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.comanch.valley_wind_awake.MainCoroutineRule
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class KeyboardViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: TimeDataDao
    private lateinit var viewModel: KeyboardViewModel

    @Before
    fun setup(){

        database = mock()
        viewModel = KeyboardViewModel(SavedStateHandle(), database)
    }

    @Test
    fun check_setIs24HourFormat() {

        viewModel.setIs24HourFormat(true)
        val actualTrue = viewModel.is24HourFormat.getOrAwaitValue()
        assertEquals(true, actualTrue)

        viewModel.setIs24HourFormat(false)
        val actualFalse = viewModel.is24HourFormat.getOrAwaitValue()
        assertEquals(false, actualFalse)
    }


    @Test
    fun check_setTimerNumbers() {

        val item = TimeData()
        item.hhmm24 = "1111"
        item.hhmm12 = "2222"

        viewModel.setIs24HourFormat(true)
        viewModel.setTimerNumbers(item)

        val actualS124 = viewModel.s1.getOrAwaitValue()
        assertEquals("1", actualS124)
        val actualS224 = viewModel.s2.getOrAwaitValue()
        assertEquals("1", actualS224)
        val actualS324 = viewModel.s3.getOrAwaitValue()
        assertEquals("1", actualS324)
        val actualS424 = viewModel.s4.getOrAwaitValue()
        assertEquals("1", actualS424)

        viewModel.setIs24HourFormat(false)
        viewModel.setTimerNumbers(item)

        val actualS112 = viewModel.s1.getOrAwaitValue()
        assertEquals("2", actualS112)
        val actualS212 = viewModel.s2.getOrAwaitValue()
        assertEquals("2", actualS212)
        val actualS312 = viewModel.s3.getOrAwaitValue()
        assertEquals("2", actualS312)
        val actualS412 = viewModel.s4.getOrAwaitValue()
        assertEquals("2", actualS412)
    }

    @Test
    fun check_prepareSave() {

        viewModel.upDateTimeView("1234")
        viewModel.prepareSave()

        val actualhhmmSave = viewModel.hhmmSave.getOrAwaitValue()
        assertEquals("1234", actualhhmmSave)
    }

    @Test
    fun check_upDateTimeView() {

        viewModel.upDateTimeView("1234")

        val actualS1 = viewModel.s1.getOrAwaitValue()
        assertEquals("1", actualS1)

        val actualS2 = viewModel.s2.getOrAwaitValue()
        assertEquals("2", actualS2)

        val actualS3 = viewModel.s3.getOrAwaitValue()
        assertEquals("3", actualS3)

        val actualS4 = viewModel.s4.getOrAwaitValue()
        assertEquals("4", actualS4)

    }

}
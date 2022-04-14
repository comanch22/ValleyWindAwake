package com.comanch.valley_wind_awake.stateViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.LiveDataEvent
import com.comanch.valley_wind_awake.frontListFragment.ListViewModel
import com.comanch.valley_wind_awake.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class StateViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: StateViewModel

    @Before
    fun setup(){

        viewModel = StateViewModel()
    }

    @Test
    fun check_restoreState() {

        viewModel.setIs24HourFormat(true)
        viewModel.setNumbersTimer("1111AM")
        viewModel.setMonday(true)
        viewModel.setTuesday(true)
        viewModel.setWednesday(true)
        viewModel.setThursday(true)
        viewModel.setFriday(true)
        viewModel.setSaturday(true)
        viewModel.setSunday(true)
        viewModel.setSpecialDate("11112022")
        viewModel.setRingtoneTitle("testRingtoneTitle")

        viewModel.restoreStateForKeyboardFragment()

        val actual24HourFormat = viewModel.is24HourFormat.getOrAwaitValue()
        assertEquals(true, actual24HourFormat.getContentIfNotHandled())

        val actualNumbersTimer = viewModel.numbersTimer.getOrAwaitValue()
        assertEquals("1111AM", actualNumbersTimer.getContentIfNotHandled())

        val actualMonday = viewModel.monday.getOrAwaitValue()
        assertEquals(true, actualMonday.getContentIfNotHandled())

        val actualTuesday = viewModel.tuesday.getOrAwaitValue()
        assertEquals(true, actualTuesday.getContentIfNotHandled())

        val actualWednesday = viewModel.wednesday.getOrAwaitValue()
        assertEquals(true, actualWednesday.getContentIfNotHandled())

        val actualThursday = viewModel.thursday.getOrAwaitValue()
        assertEquals(true, actualThursday.getContentIfNotHandled())

        val actualFriday = viewModel.friday.getOrAwaitValue()
        assertEquals(true, actualFriday.getContentIfNotHandled())

        val actualSaturday = viewModel.saturday.getOrAwaitValue()
        assertEquals(true, actualSaturday.getContentIfNotHandled())

        val actualSunday = viewModel.sunday.getOrAwaitValue()
        assertEquals(true, actualSunday.getContentIfNotHandled())

        val actualSpecialDate = viewModel.specialDate.getOrAwaitValue()
        assertEquals("11112022", actualSpecialDate.getContentIfNotHandled())

        val actualRingtoneTitle = viewModel.ringtoneTitle.getOrAwaitValue()
        assertEquals("testRingtoneTitle", actualRingtoneTitle.getContentIfNotHandled())

    }

    @Test
    fun check_restoreSpecialDateAndTimer() {

        viewModel.setNumbersTimer("1000AM")
        viewModel.setSpecialDate("22222022")

        viewModel.restoreSpecialDateAndTimer()

        val actualNumbersTimer = viewModel.numbersTimer.getOrAwaitValue()
        assertEquals("1000AM", actualNumbersTimer.getContentIfNotHandled())

        val actualSpecialDate = viewModel.specialDate.getOrAwaitValue()
        assertEquals("22222022", actualSpecialDate.getContentIfNotHandled())
    }
}
package com.comanch.valley_wind_awake.stateViewModel

import com.comanch.valley_wind_awake.frontListFragment.ListViewModel
import com.comanch.valley_wind_awake.getOrAwaitValue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class StateViewModelTest{

    private lateinit var viewModel: StateViewModel
    
    @Before
    fun setup(){

        viewModel = StateViewModel()
    }

    @Test
    fun check_restoreState() {

        viewModel.setIs24HourFormat(true)
        viewModel.setNumbersTimer("1111")
      /*  val actual = viewModel.nearestDate.getOrAwaitValue()
        assertEquals("", actual)*/
    }
}
package com.comanch.valley_wind_awake.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.MainCoroutineRule
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class DetailViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: TimeDataDao
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setup(){

        database = mock()
        viewModel = DetailViewModel(database)
    }

    @Test
    fun check_offSignal() {

        viewModel.offSignal()
        val navigateToList = viewModel.navigateToList.getOrAwaitValue()
        assertEquals(navigateToList.getContentIfNotHandled(), 1)
        val offAlarm = viewModel.offAlarm.getOrAwaitValue()
        assertEquals(offAlarm.getContentIfNotHandled(), 1)
    }

    @Test
    fun check_delaySignal() {

        viewModel.delaySignal()
        val navigateToList = viewModel.navigateToList.getOrAwaitValue()
        assertEquals(navigateToList.getContentIfNotHandled(), 1)
        val setPause = viewModel.setPause.getOrAwaitValue()
        assertEquals(setPause.getContentIfNotHandled(), 1)
    }

    @Test
    fun check_startDelay() = runBlockingTest {

        viewModel.startDelay(0)
        val stopPlay = viewModel.stopPlay.getOrAwaitValue()
        assertEquals(stopPlay.getContentIfNotHandled(), 1)
    }
}
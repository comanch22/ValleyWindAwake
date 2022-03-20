package com.comanch.valley_wind_awake.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.getOrAwaitValue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

class DetailViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private lateinit var database: TimeDataDao
    private lateinit var viewModel: DetailViewModel
   // private lateinit var observer: Observer<LiveDataEvent<Int?>>

    @Before
    fun setup(){

      //  observer = mock()
        database = mock()
        viewModel = DetailViewModel(database)
      //  viewModel.navigateToList.observeForever(observer)
      //  viewModel.offAlarm.observeForever(observer)
      //  viewModel.setPause.observeForever(observer)
      //  viewModel.stopPlay.observeForever(observer)
    }

    @Test
    fun check_cancelAbout(){

        viewModel.offSignal()
        viewModel.delaySignal()
       // viewModel.startDelay(1)
        val navigateToList = viewModel.navigateToList.getOrAwaitValue()
        assertEquals(navigateToList.getContentIfNotHandled(), 1)
        val offAlarm = viewModel.offAlarm.getOrAwaitValue()
        assertEquals(offAlarm.getContentIfNotHandled(), 1)
        val setPause = viewModel.setPause.getOrAwaitValue()
        assertEquals(setPause.getContentIfNotHandled(), 1)
       // val stopPlay = viewModel.stopPlay.getOrAwaitValue()
      // assertEquals(stopPlay.getContentIfNotHandled(), 1)
    }
}
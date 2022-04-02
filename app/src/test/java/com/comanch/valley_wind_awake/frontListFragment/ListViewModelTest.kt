package com.comanch.valley_wind_awake.frontListFragment

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.MainCoroutineRule
import com.comanch.valley_wind_awake.alarmFragment.DetailViewModel
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ListViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: TimeDataDao
    private lateinit var viewModel: ListViewModel

    @Before
    fun setup(){

        database = mock()
        viewModel = ListViewModel(database)
    }

    @Test
    fun check_setNearestDate() {

        viewModel.setNearestDate(true)
        val actual = viewModel.nearestDate.getOrAwaitValue()
        assertEquals("", actual)
    }

    @Test
    fun check_resetItemActive() {

        viewModel.resetItemActive()
        val actual = viewModel.itemActive.getOrAwaitValue()
        assertEquals(null, actual)
    }

    @Test
    fun check_resetDeleteAllItems() {

        viewModel.resetDeleteAllItems()
        val actual = viewModel.deleteAllItems.getOrAwaitValue()
        assertEquals(null, actual)
    }


}
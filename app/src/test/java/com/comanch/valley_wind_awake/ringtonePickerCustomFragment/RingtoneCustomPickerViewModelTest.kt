package com.comanch.valley_wind_awake.ringtonePickerCustomFragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.MainCoroutineRule
import com.comanch.valley_wind_awake.dataBase.RingtoneDataDao
import com.comanch.valley_wind_awake.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class RingtoneCustomPickerViewModelTest{

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: RingtoneDataDao
    private lateinit var viewModel: RingtoneCustomPickerViewModel

    @Before
    fun setup(){

        database = mock()
        viewModel = RingtoneCustomPickerViewModel(database)
    }

    @Test
    fun check_restoreStateForCustomRingtoneFragment() {

        viewModel.restoreStateForCustomRingtoneFragment()
        val actualFalse = viewModel.restorePlayerFlag.getOrAwaitValue()
        assertEquals(false, actualFalse.getContentIfNotHandled())

        viewModel.setRestorePlayerFlag(true)
        viewModel.restoreStateForCustomRingtoneFragment()
        val actualTrue = viewModel.restorePlayerFlag.getOrAwaitValue()
        assertEquals(true, actualTrue.getContentIfNotHandled())
    }

    @Test
    fun check_resetCurrentRingTone() {

        viewModel.resetCurrentRingTone()
        val actual = viewModel.currentRingTone.getOrAwaitValue()
        assertEquals(null, actual)
    }

    @Test
    fun check_setItemActiveState() {

        viewModel.setItemActiveState()
        val actual = viewModel.itemActiveState.getOrAwaitValue()
        assertEquals(true, actual.getContentIfNotHandled())
    }


}
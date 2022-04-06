package com.comanch.valley_wind_awake.ringtonePickerFragment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.comanch.valley_wind_awake.MainCoroutineRule
import com.comanch.valley_wind_awake.dataBase.RingtoneData
import com.comanch.valley_wind_awake.dataBase.RingtoneDataDao
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.getOrAwaitValue
import com.comanch.valley_wind_awake.ringtonePickerCustomFragment.RingtoneCustomPickerViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class RingtonePickerViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var database: RingtoneDataDao
    private lateinit var viewModel: RingtonePickerViewModel

    @Before
    fun setup(){

        database = mock()
        viewModel = RingtonePickerViewModel(database)
    }

    @Test
    fun check_onItemClicked(){

        viewModel.onItemClicked(RingtoneData())
        val actualTypeName = viewModel.currentRingTone.getOrAwaitValue()
        assertEquals(RingtoneData().javaClass.simpleName, actualTypeName?.javaClass?.simpleName)
    }

    @Test
    fun check_setMelody(){

        val itemActive = RingtoneData()
        itemActive.active = 1
        itemActive.uriAsString = "testUri"
        itemActive.title = "testTitle"

        viewModel.onItemClicked(itemActive)
        viewModel.setMelody()

        val actualSetRingtoneUriActive = viewModel.setRingtoneUri.getOrAwaitValue()
        assertEquals("testUri", actualSetRingtoneUriActive.getContentIfNotHandled())

        val actualSetRingtoneTitleActive = viewModel.setRingtoneTitle.getOrAwaitValue()
        assertEquals("testTitle", actualSetRingtoneTitleActive.getContentIfNotHandled())

        val itemNoActive = RingtoneData()
        itemNoActive.active = 0
        itemNoActive.uriAsString = ""
        itemNoActive.title = ""

        viewModel.onItemClicked(itemNoActive)
        viewModel.setMelody()

        val actualSetRingtoneUriNoActive = viewModel.setRingtoneUri.getOrAwaitValue()
        assertEquals("", actualSetRingtoneUriNoActive.getContentIfNotHandled())

        val actualSetRingtoneTitleNoActive = viewModel.setRingtoneTitle.getOrAwaitValue()
        assertEquals("", actualSetRingtoneTitleNoActive.getContentIfNotHandled())
    }

    @Test
    fun check_addMelody(){

        viewModel.addMelody()
        val actualAddMelody = viewModel.chooseRingtone.getOrAwaitValue()
        assertEquals(1, actualAddMelody.getContentIfNotHandled())
    }

    @Test
    fun check_deleteMelody() {

        viewModel.delete(1L, 1)
        val actualToastNull = viewModel.toast.getOrAwaitValue()
        assertEquals(null, actualToastNull)

        viewModel.delete(1L, 2)
        val actualToastCannot = viewModel.toast.getOrAwaitValue()
        assertEquals("cannot be deleted", actualToastCannot)

        viewModel.delete(null, null)
        val actualToastChoose = viewModel.toast.getOrAwaitValue()
        assertEquals("choose a ringtone", actualToastChoose)
    }


    @Test
    fun check_deleteItem() = runBlocking {

        viewModel.deleteItem(RingtoneData())
        val actualDelete = viewModel.delete.getOrAwaitValue()
        assertEquals(1, actualDelete)

        viewModel.deleteItem(null)
        val actualToast = viewModel.toast.getOrAwaitValue()
        assertEquals("choose a ringtone", actualToast)

    }
}
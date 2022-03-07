package com.comanch.valley_wind_awake.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comanch.valley_wind_awake.LiveDataEvent
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import kotlinx.coroutines.launch


class ListViewModel(
    val dataSource: TimeDataDao,
    private val defaultRingtoneUri: String,
    private val defaultRingtoneTitle: String
) : ViewModel() {

    val items = dataSource.getAllItems()

    private var isDeleteMode = false

    private val _navigateToKeyboardFragment = MutableLiveData<LiveDataEvent<Long?>>()
    val navigateToKeyboardFragment: LiveData<LiveDataEvent<Long?>>
        get() = _navigateToKeyboardFragment

    private var _timeToast = MutableLiveData<LiveDataEvent<TimeData?>>()
    val timeToast: LiveData<LiveDataEvent<TimeData?>>
        get() = _timeToast

    private val _nearestDate = MutableLiveData<String?>()
    val nearestDate: LiveData<String?>
        get() = _nearestDate

    private var _itemActive = MutableLiveData<TimeData?>()
    val itemActive: LiveData<TimeData?>
        get() = _itemActive

    private var _deleteAllItems = MutableLiveData<List<TimeData>?>()
    val deleteAllItems: LiveData<List<TimeData>?>
        get() = _deleteAllItems

    private val _offAlarm = MutableLiveData<TimeData?>()
    val offAlarm: LiveData<TimeData?>
        get() = _offAlarm

    fun setNearestDate(is24HourFormat: Boolean) {

        viewModelScope.launch {
            val listItems = dataSource.getListItems()
            listItems?.sortedBy {
                it.nearestDate
            }
            listItems?.filter { it.active }?.sortedBy {
                it.nearestDate
            }?.let {
                if (it.isNotEmpty()) {
                    if (is24HourFormat) {
                        _nearestDate.value = it[0].nearestDateStr
                    } else {
                        _nearestDate.value = it[0].nearestDateStr12
                    }
                } else {
                    _nearestDate.value = ""
                }
            }
        }
    }

    fun setDeleteItemsMode(b: Boolean) {
        isDeleteMode = b
    }

    fun insertItem() {

        viewModelScope.launch {
            val item = TimeData()
            item.ringtoneUri = defaultRingtoneUri
            item.ringtoneTitle = defaultRingtoneTitle
            dataSource.insert(item)
        }
    }

    fun clear() {

        viewModelScope.launch {
            _deleteAllItems.value = dataSource.getListItems()
        }
    }

    fun deleteAll() {

        viewModelScope.launch {
            dataSource.clear()
        }

    }

    fun onItemClicked(itemId: Long) {

        if (!isDeleteMode) {
            _navigateToKeyboardFragment.value = LiveDataEvent(itemId)
        }
    }

    fun updateSwitchCheck(item: TimeData) {
        _itemActive.value = item
    }

    fun offAlarmDeleteItem(itemId: Long) {

        viewModelScope.launch {
            val item = dataSource.get(itemId) ?: return@launch
            _offAlarm.value = item
        }
    }

    fun deleteItem(item: TimeData) {

        viewModelScope.launch {
            dataSource.delete(item)
        }
    }

    fun resetItemActive() {
        _itemActive.value = null
    }

    fun resetDeleteAllItems() {
        _deleteAllItems.value = null
    }

    fun showDiffTimeToast(timeId: Long) {

        viewModelScope.launch {
            val item = dataSource.get(timeId) ?: return@launch
            _timeToast.value = LiveDataEvent(item)
        }
    }
}

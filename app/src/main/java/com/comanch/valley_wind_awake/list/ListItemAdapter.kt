package com.comanch.valley_wind_awake.list

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.databinding.FrontListItemBinding


class ItemListener(val clickListener: (itemId: Long) -> Unit) {
    fun onClick(time: TimeData) = clickListener(time.timeId)
}

class SwitchListener(val switchListener: (item: TimeData) -> Unit) {
    fun onSwitch(time: TimeData) = switchListener(time)
}

class DeleteListener(val deleteListener: (itemId: Long) -> Unit) {
    fun deleteItem(time: TimeData) = deleteListener(time.timeId)
}

class ListItemAdapter(
    private val clickListener: ItemListener,
    private val switchListener: SwitchListener,
    private val deleteListener: DeleteListener,
    private val colorOn: Int,
    private val colorOff: Int,
    private val backgroundColor: Int,
    private var is24HourFormat: Boolean,
    private val timeInstance: Long,
    private val language: String?

) : ListAdapter<DataItem, RecyclerView.ViewHolder>(
    SleepNightDiffCallback()
) {

    private var isDeleteMode = false
    private var mRecyclerView: RecyclerView? = null

    fun setData(list: List<TimeData>?) {

        val items = list?.map { DataItem.AlarmItem(it) }
        submitList(items)
    }

    fun setIs24HourFormat(b: Boolean) {

        is24HourFormat = b
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {

        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    fun setDeleteMode(b: Boolean) {

        isDeleteMode = b
        notifyItemRangeChanged(0, itemCount)
    }

    fun refreshList() {

        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is ViewHolder -> {
                val item = getItem(position) as DataItem.AlarmItem
                holder.bind(
                    item.timeData,
                    clickListener,
                    switchListener,
                    deleteListener,
                    isDeleteMode,
                    colorOn,
                    colorOff,
                    backgroundColor,
                    is24HourFormat,
                    timeInstance,
                    language
                )
            }
        }
    }

    class ViewHolder private constructor(val binding: FrontListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val drawableAlarm64 = R.drawable.ic_baseline_alarm_on_64
        private val drawableAlarm48 = R.drawable.ic_baseline_alarm_on_48

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding =
                    FrontListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

        fun bind(
            item: TimeData,
            clickListener: ItemListener,
            switchListener: SwitchListener,
            deleteListener: DeleteListener,
            isDeleteMode: Boolean,
            colorOn: Int,
            colorOff: Int,
            backgroundColor: Int,
            is24HourFormat: Boolean,
            timeInstance: Long,
            language: String?
        ) {

            binding.item = item
            if (item.delayTime > 0L && item.delayTime > timeInstance) {
                binding.isDelayed.visibility = View.VISIBLE
                setContentDescription(item, language, is24HourFormat, true)
            } else {
                binding.isDelayed.visibility = View.INVISIBLE
                setContentDescription(item, language, is24HourFormat, false)
            }

            if (is24HourFormat) {
                val hhmm = item.hhmm24
                binding.textViewNumberOne.text = hhmm[0].toString()
                binding.textViewNumberTwo.text = hhmm[1].toString()
                binding.textViewNumberThree.text = hhmm[2].toString()
                binding.textViewNumberFour.text = hhmm[3].toString()
                binding.ampm.visibility = View.INVISIBLE
            } else {
                val hhmm = item.hhmm12
                binding.textViewNumberOne.text = hhmm[0].toString()
                binding.textViewNumberTwo.text = hhmm[1].toString()
                binding.textViewNumberThree.text = hhmm[2].toString()
                binding.textViewNumberFour.text = hhmm[3].toString()
                binding.ampm.visibility = View.VISIBLE
                binding.ampm.text = item.ampm
            }

            if (item.specialDateStr.length > 10) {
                binding.selectedDate.text = item.specialDateStr.substring(0, 10)
            }

            setBackgroundDays(
                binding.textViewMonday,
                item.mondayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewTuesday,
                item.tuesdayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewWednesday,
                item.wednesdayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewThursday,
                item.thursdayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewFriday,
                item.fridayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewSaturday,
                item.saturdayOn,
                colorOn,
                colorOff,
                backgroundColor
            )
            setBackgroundDays(
                binding.textViewSunday,
                item.sundayOn,
                colorOn,
                colorOff,
                backgroundColor
            )

            if (isDeleteMode) {
                binding.switchActive.visibility = View.GONE
                binding.deleteItem.visibility = View.VISIBLE
                if (language == "ru_RU") {
                    binding.itemLayout.contentDescription = " Включен режим удаления будильников. Будильник из списка в режиме выбора для удаления. " +
                            " Время будильника. " +
                            "${binding.textViewNumberOne.text}${binding.textViewNumberTwo.text} часов" +
                            "${binding.textViewNumberThree.text}${binding.textViewNumberFour.text} минут"
                } else {
                    binding.itemLayout.contentDescription =
                        " Alarm removal mode is enabled. Alarm clock from the list in the selection mode for deletion." +
                                " Alarm clock time. " +
                                "${binding.textViewNumberOne.text}${binding.textViewNumberTwo.text} hours" +
                                "${binding.textViewNumberThree.text}${binding.textViewNumberFour.text} minutes"
                }

            } else {
                binding.deleteItem.visibility = View.GONE
                binding.switchActive.visibility = View.VISIBLE
                if (item.active) {
                    binding.switchActive.setBackgroundResource(drawableAlarm64)
                    if (language == "ru_RU") {
                        binding.switchActive.contentDescription = " будильник включен. "
                    } else {
                        binding.switchActive.contentDescription = " the alarm is on. "
                    }
                } else {
                    binding.switchActive.setBackgroundResource(drawableAlarm48)
                    if (language == "ru_RU") {
                        binding.switchActive.contentDescription = " будильник выключен. "
                    } else {
                        binding.switchActive.contentDescription = " the alarm is off. "
                    }
                }
            }

            binding.clickListener = clickListener
            binding.switchListener = switchListener
            binding.deleteListener = deleteListener
        }

        private fun setBackgroundDays(
            view: TextView,
            on: Boolean,
            colorOn: Int,
            colorOff: Int,
            backgroundColor: Int
        ): Boolean {
            return if (on) {
                setBackgroundDaysOn(view, colorOn)
                true
            } else {
                setBackgroundDaysOff(view, colorOff, backgroundColor)
                false
            }
        }

        private fun setBackgroundDaysOn(view: TextView, colorOn: Int) {
            view.setBackgroundResource(R.drawable.rectangle_list_background_day)
            view.setTypeface(null, Typeface.BOLD)
            view.setTextColor(colorOn)
        }

        private fun setBackgroundDaysOff(view: TextView, colorOff: Int, backgroundColor: Int) {
            view.setBackgroundColor(backgroundColor)
            view.setTypeface(null, Typeface.NORMAL)
            view.setTextColor(colorOff)
        }

        private fun setContentDescription(
            item: TimeData,
            language: String?,
            is24HourFormat: Boolean,
            isDelayed: Boolean
        ) {

            binding.itemLayout.contentDescription =
                if (language == "ru_RU") {
                    when {
                        is24HourFormat && isDelayed && item.active -> {
                            item.contentDescriptionRu24 + " сигнал отложен. будильник включен. "
                        }
                        is24HourFormat && isDelayed && !item.active -> {
                            item.contentDescriptionRu24 + " сигнал отложен. будильник выключен. "
                        }
                        is24HourFormat && !isDelayed && item.active -> {
                            item.contentDescriptionRu24 + " будильник включен. "
                        }
                        is24HourFormat && !isDelayed && !item.active -> {
                            item.contentDescriptionRu24 + " будильник выключен. "
                        }
                        !is24HourFormat && isDelayed && item.active -> {
                            item.contentDescriptionRu12 + " сигнал отложен. будильник включен. "
                        }
                        !is24HourFormat && isDelayed && !item.active -> {
                            item.contentDescriptionRu12 + " сигнал отложен. будильник выключен. "
                        }
                        !is24HourFormat && !isDelayed && item.active -> {
                            item.contentDescriptionRu12 + " будильник включен. "
                        }
                        !is24HourFormat && !isDelayed && !item.active -> {
                            item.contentDescriptionRu12 + " будильник выключен. "
                        }
                        else -> {
                            " ошибка, состояние будильника неизвестно. "
                        }
                    }
                } else {
                    when {
                        is24HourFormat && isDelayed && item.active -> {
                            item.contentDescriptionEn24 + " the signal is delayed. the alarm clock is on. "
                        }
                        is24HourFormat && isDelayed && !item.active -> {
                            item.contentDescriptionEn24 + " the signal is delayed. the alarm clock is off. "
                        }
                        is24HourFormat && !isDelayed && item.active -> {
                            item.contentDescriptionEn24 + " the alarm clock is on. "
                        }
                        is24HourFormat && !isDelayed && !item.active -> {
                            item.contentDescriptionEn24 + " the alarm clock is off. "
                        }
                        !is24HourFormat && isDelayed && item.active -> {
                            item.contentDescriptionEn12 + " the signal is delayed. the alarm clock is on. "
                        }
                        !is24HourFormat && isDelayed && !item.active -> {
                            item.contentDescriptionEn12 + " the signal is delayed. the alarm clock is off. "
                        }
                        !is24HourFormat && !isDelayed && item.active -> {
                            item.contentDescriptionEn12 + " the alarm clock is on. "
                        }
                        !is24HourFormat && !isDelayed && !item.active -> {
                            item.contentDescriptionEn12 + " the alarm clock is off. "
                        }
                        else -> {
                            " error, the alarm clock status is unknown. "
                        }
                    }
                }
        }

    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {

    override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
        return oldItem == newItem
    }
}

sealed class DataItem {

    abstract val id: Long

    data class AlarmItem(val timeData: TimeData) : DataItem() {
        override val id = timeData.timeId
    }
}
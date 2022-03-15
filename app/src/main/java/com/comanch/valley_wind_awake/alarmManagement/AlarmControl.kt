package com.comanch.valley_wind_awake.alarmManagement

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.preference.PreferenceManager
import com.comanch.valley_wind_awake.IntentKeys
import com.comanch.valley_wind_awake.MainActivity
import com.comanch.valley_wind_awake.PreferenceKeys
import com.comanch.valley_wind_awake.broadcastreceiver.AlarmReceiver
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.dataBase.TimeData
import java.text.SimpleDateFormat
import java.util.*

class AlarmControl(val context: Context) {

    var timeData: TimeData? = null
    val dataSource = DataControl.getInstance(context.applicationContext).timeDatabaseDao

    constructor(_context: Context, _timeData: TimeData) : this(_context) {
        this.timeData = _timeData
    }

    suspend fun restartAlarm() {

        val alarmList = dataSource.getListItems()
        alarmList?.forEach {
            it.active = true
            dataSource.update(it)
            val updateItem = dataSource.get(it.timeId)
            if (updateItem != null) {
                onAlarm(updateItem)
            }
        }
    }

    private fun createCalendarList(item: TimeData): MutableList<Calendar> {

        val calendarList = mutableListOf<Calendar>()
        setContentDescriptionPart1(item)
        createSeveralCalendars(item, calendarList)
        setNearestDate(item, calendarList)
        return calendarList
    }

    private fun setNearestDate(item: TimeData, calendarList: MutableList<Calendar>) {

        calendarList.sortBy {
            it.timeInMillis
        }
        if (calendarList.size > 0) {
            item.nearestDate = calendarList[0].timeInMillis
            item.nearestDateStr = SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.US
            ).format(calendarList[0].time)
            item.nearestDateStr12 = SimpleDateFormat(
                "dd-MM-yyyy hh:mm a", Locale.US
            ).format(calendarList[0].time)

            item.contentDescriptionRu12 +=
                ". ближайщий сигнал этого будильника. " +
                        SimpleDateFormat(
                            "dd-MM-yyyy hh:mm a", Locale("ru", "RU")
                        ).format(calendarList[0].time)
            item.contentDescriptionRu24 +=
                ". ближайщий сигнал этого будильника. " +
                        SimpleDateFormat(
                            "dd-MM-yyyy HH:mm", Locale("ru", "RU")
                        ).format(calendarList[0].time)
            item.contentDescriptionEn12 +=
                ". the nearest of this alarm clock. " +
                        SimpleDateFormat(
                            "dd-MM-yyyy hh:mm a", Locale.US
                        ).format(calendarList[0].time)
            item.contentDescriptionEn24 +=
                ". the nearest of this alarm clock. " +
                        SimpleDateFormat(
                            "dd-MM-yyyy HH:mm", Locale.US
                        ).format(calendarList[0].time)
            val cal = Calendar.getInstance()
            cal.timeInMillis = item.nearestDate
        }
    }

    private fun createSeveralCalendars(
        item: TimeData,
        calendarList: MutableList<Calendar>
    ) {

        setContentDescriptionPart2(item,
            " отмечены дни недели. ",
            " the days of the week are marked. ")

        var oneInstance = true

        if (item.mondayOn) {
            setDayOfWeek(Calendar.MONDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " понедельник. ",
                " monday. ")
            oneInstance = false
        }

        if (item.tuesdayOn) {
            setDayOfWeek(Calendar.TUESDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " вторник. ",
                " tuesday. ")
            oneInstance = false
        }

        if (item.wednesdayOn) {
            setDayOfWeek(Calendar.WEDNESDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " среда. ",
                " wednesday. ")
            oneInstance = false
        }

        if (item.thursdayOn) {
            setDayOfWeek(Calendar.THURSDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " четверг. ",
                " thursday. ")
            oneInstance = false
        }

        if (item.fridayOn) {
            setDayOfWeek(Calendar.FRIDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " пятница. ",
                " friday. ")
            oneInstance = false
        }

        if (item.saturdayOn) {
            setDayOfWeek(Calendar.SATURDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " суббота. ",
                " saturday. ")
            oneInstance = false
        }

        if (item.sundayOn) {
            setDayOfWeek(Calendar.SUNDAY, item, calendarList)
            setContentDescriptionPart2(item,
                " воскресенье. ",
                " sunday. ")
            oneInstance = false
        }

        if (item.delayTime != 0L) {
            val calendar = Calendar.getInstance()
            if (calendar.timeInMillis <= item.delayTime) {
                calendar.timeInMillis = item.delayTime
                calendar.clear(Calendar.MILLISECOND)
                calendarList.add(calendar)
            }
        }

        if (item.specialDate > 0L) {
            val calendar = Calendar.getInstance()

            item.contentDescriptionRu12 +=
                "дополнительно указанная дата срабатывания сигнала. " +
                        SimpleDateFormat(
                            "yyyy-MM-dd", Locale("ru", "RU")
                        ).format(item.specialDate) + ". "
            item.contentDescriptionRu24 +=
                "дополнительно указанная дата срабатывания сигнала. " +
                        SimpleDateFormat(
                            "yyyy-MM-dd", Locale("ru", "RU")
                        ).format(item.specialDate) + ". "
            item.contentDescriptionEn12 +=
                "additionally, the specified date of the alarm. " +
                        SimpleDateFormat(
                            "yyyy-MM-dd", Locale.US
                        ).format(item.specialDate) + ". "
            item.contentDescriptionEn24 +=
                "additionally, the specified date of the alarm. " +
                        SimpleDateFormat(
                            "yyyy-MM-dd", Locale.US
                        ).format(item.specialDate) + ". "

            calendar.timeInMillis = item.specialDate
            calendar.clear(Calendar.MILLISECOND)
            if (calendarList.find {
                    it.clear(Calendar.MILLISECOND)
                    it.time.compareTo(calendar.time) == 0
                } == null && calendar.timeInMillis > Calendar.getInstance().timeInMillis) {
                calendarList.add(calendar)
            }
        }

        if (oneInstance && item.specialDate == 0L && item.delayTime == 0L) {

            val calendar = Calendar.getInstance()
            setTimeCalendar(calendar, item)
            if (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
                calendar.add(Calendar.DATE, 1)
            }
            calendarList.add(calendar)
        }

        item.oneInstance = calendarList.size <= 1 && oneInstance
    }

    private fun setDayOfWeek(day: Int, item: TimeData, calendarList: MutableList<Calendar>) {

        val calendar = Calendar.getInstance()
        setTimeCalendar(calendar, item)
        calendar.set(Calendar.DAY_OF_WEEK, day)
        if (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
            calendar.add(Calendar.DATE, 7)
        }
        calendarList.add(calendar)
    }

    private fun setTimeCalendar(calendar: Calendar, item: TimeData): Calendar {

        val hhmm = item.hhmm24
        calendar.set(Calendar.HOUR_OF_DAY, "${hhmm[0]}${hhmm[1]}".toInt())
        calendar.set(Calendar.MINUTE, "${hhmm[2]}${hhmm[3]}".toInt())
        calendar.set(Calendar.SECOND, 0)
        return calendar
    }

    suspend fun schedulerAlarm(typeOperation: AlarmTypeOperation): String {

        val item = timeData?.timeId?.let { dataSource.get(it) } ?: return "error"
        when (typeOperation) {
            AlarmTypeOperation.SAVE -> {
                if (item.oneInstance &&
                    item.specialDate != 0L &&
                    item.specialDate < Calendar.getInstance().timeInMillis
                ) {
                    return "incorrect date"
                }
                onAlarm(item)
                item.active = true
                dataSource.update(item)
            }
            AlarmTypeOperation.OFF -> {
                if (item.oneInstance) {
                    item.active = false
                } else {
                    onAlarm(item)
                }
                dataSource.update(item)
            }

            AlarmTypeOperation.DELETE -> {
                offAlarm(item)
            }
            AlarmTypeOperation.SWITCH -> {
                if (item.active) {
                    offAlarm(item)
                    item.active = false
                    dataSource.update(item)
                    return "success off"
                } else {
                    if (item.oneInstance &&
                        item.specialDate != 0L &&
                        item.specialDate < Calendar.getInstance().timeInMillis
                    ) {
                        return "incorrect date"
                    }
                    onAlarm(item)
                    item.active = true
                    dataSource.update(item)
                    return "success on"
                }
            }
            AlarmTypeOperation.PAUSE -> {
                offAlarm(item)
                item.active = false
                delaySignal(item)
                onAlarm(item)
                item.active = true
                dataSource.update(item)
            }
        }
        return "success"
    }

    private fun onAlarm(item: TimeData) {

        val calendarList = createCalendarList(item)
        if (calendarList.size > 0) {
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var count = 1

            calendarList.forEach {
                val requestCode = "${item.requestCode}$count".toInt()
                val requestCodeInfo = "${item.requestCode}$count$count".toInt()
                val pendingIntent = createPendingIntent(requestCode)
                val pendingIntentInfo = createPendingIntentInfo(requestCodeInfo)
                val alarmInfo = AlarmManager.AlarmClockInfo(it.timeInMillis, pendingIntentInfo)
                am.setAlarmClock(alarmInfo, pendingIntent)
                count++
            }
        }
    }

    private fun offAlarm(item: TimeData) {

        item.delayTime = 0L
        val calendarList = createCalendarList(item)
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var count = 1
        calendarList.forEach { _ ->
            val requestCode = "${item.requestCode}$count".toInt()
            val requestCodeInfo = "${item.requestCode}$count$count".toInt()
            val pendingIntent = createPendingIntent(requestCode)
            val pendingIntentInfo = createPendingIntentInfo(requestCodeInfo)
            am.cancel(pendingIntent)
            am.cancel(pendingIntentInfo)
            count++
        }
    }

    private fun delaySignal(item: TimeData) {

        val defaultPreference = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PreferenceKeys.pauseDuration, "5") ?: "5"
        item.delayTime = Calendar.getInstance().timeInMillis + defaultPreference.toInt() * 60000
    }

    private fun createPendingIntent(requestCode: Int): PendingIntent {

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = IntentKeys.SetAlarm
        intent.putExtra(IntentKeys.timeId, timeData?.timeId)
        intent.putExtra(IntentKeys.ringtoneUri, timeData?.ringtoneUri)
        intent.putExtra(IntentKeys.timeStr, "${timeData?.s1}${timeData?.s2}${timeData?.s3}${timeData?.s4}")
        intent.putExtra(IntentKeys.Alarm_R, true)
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun createPendingIntentInfo(requestCode: Int): PendingIntent {

        val intentInfo = Intent(context, MainActivity::class.java)
        intentInfo.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            requestCode,
            intentInfo,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun setContentDescriptionPart1(item: TimeData) {
        item.contentDescriptionRu12 =
            "будильник из списка. " +
                    "время будильника ${item.hhmm12[0]}${item.hhmm12[1]} часов " +
                    "${item.hhmm12[2]}${item.hhmm12[3]} минут " +
                    "двенадцатичасовой формат" +
                    "${item.ampm[0]}. ${item.ampm[1]}. " + ". "
        item.contentDescriptionRu24 =
            "будильник из списка. " +
                    "время будильника ${item.hhmm24[0]}${item.hhmm24[1]} часов " +
                    "${item.hhmm24[2]}${item.hhmm24[3]} минут " + ". "
        item.contentDescriptionEn12 =
            "alarm clock from the list. " +
                    "alarm clock time ${item.hhmm12[0]}${item.hhmm12[1]} hours " +
                    "${item.hhmm12[2]}${item.hhmm12[3]} minutes " +
                    "twelve - hour format" +
                    "${item.ampm[0]}. ${item.ampm[1]}. " + ". "
        item.contentDescriptionEn24 =
            "alarm clock from the list. " +
                    "alarm clock time ${item.hhmm24[0]}${item.hhmm24[1]} hours " +
                    "${item.hhmm24[2]}${item.hhmm24[3]} minutes " + ". "
    }

    private fun setContentDescriptionPart2(item: TimeData, strRu: String, strEn: String) {

            item.contentDescriptionRu12 += strRu
            item.contentDescriptionRu24 += strRu
            item.contentDescriptionEn12 += strEn
            item.contentDescriptionEn24 += strEn
    }
}
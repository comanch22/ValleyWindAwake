package com.comanch.valley_wind_awake.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import com.comanch.valley_wind_awake.IntentKeys
import com.comanch.valley_wind_awake.alarmManagement.AlarmControl
import com.comanch.valley_wind_awake.alarmManagement.AlarmTypeOperation
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.dataBase.DataControl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            when (intent.action) {

                "android.intent.action.BOOT_COMPLETED" -> {
                    restartAlarms(context?.applicationContext)
                }
                IntentKeys.SetAlarm -> {

                    startRingtoneService(
                        context?.applicationContext,
                        intent.getLongExtra(IntentKeys.timeId, -1),
                        intent.getStringExtra(IntentKeys.timeStr) ?: "0000",
                        intent.getStringExtra(IntentKeys.ringtoneUri) ?: ""
                    )
                }
                IntentKeys.offAlarm -> {

                    intent.getStringExtra(IntentKeys.timeId)?.let { offAlarm(context, it) }
                    if (intent.getBooleanExtra("notification", false)){
                        if (context != null) {
                            NotificationManagerCompat.from(context).cancel(17131415)
                        }
                    }
                }
                IntentKeys.offDuplicateSignal -> {

                    intent.getStringExtra(IntentKeys.timeId)
                        ?.let { offDuplicateSignal(context, it) }
                }
                IntentKeys.offAlarmFromTimer -> {

                    intent.getStringExtra(IntentKeys.timeId)?.let { offAlarm(context, it) }
                }
                IntentKeys.pauseAlarm -> {

                    pauseAlarmFromNotification(context, intent.getStringExtra(IntentKeys.timeId) ?: "")
                }
            }
        }
    }

    private fun restartAlarms(context: Context?) {

        context?.applicationContext?.let {
            val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
            mCoroutineScope.launch {
                AlarmControl(it).restartAlarm()
            }
        }
    }

    private fun pauseAlarmFromNotification(context: Context?, timeId: String) {

        if (timeId.isNotEmpty() && timeId.toLongOrNull() != null) {
            context?.applicationContext?.let {
                val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
                mCoroutineScope.launch {
                    val dataSource =
                        DataControl.getInstance(context.applicationContext).timeDatabaseDao
                    val item = dataSource.get(timeId.toLong()) ?: return@launch
                    AlarmControl(it, item).schedulerAlarm(AlarmTypeOperation.PAUSE)
                    stopRingtoneService(context)
                }
            }
            if (context != null) {
                NotificationManagerCompat.from(context).cancel(17131415)
            }
        }
    }

    private fun offAlarm(context: Context?, timeId: String) {

        if (timeId.isNotEmpty() && timeId.toLongOrNull() != null) {
            stopRingtoneService(context)
            context?.applicationContext?.let {
                val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
                mCoroutineScope.launch {
                    val dataSource =
                        DataControl.getInstance(context.applicationContext).timeDatabaseDao
                    val item = dataSource.get(timeId.toLong()) ?: return@launch
                    AlarmControl(it, item).schedulerAlarm(AlarmTypeOperation.OFF)
                }
            }
            if (context != null) {
                NotificationManagerCompat.from(context).cancel(17131415)
            }
        }
    }

    private fun offDuplicateSignal(context: Context?, timeId: String) {
        if (timeId.isNotEmpty() && timeId.toLongOrNull() != null) {
            context?.applicationContext?.let {
                val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
                mCoroutineScope.launch {
                    val dataSource =
                        DataControl.getInstance(context.applicationContext).timeDatabaseDao
                    val item = dataSource.get(timeId.toLong()) ?: return@launch
                    AlarmControl(it, item).schedulerAlarm(AlarmTypeOperation.OFF)
                }
            }
        }
    }

    private fun startRingtoneService(
        context: Context?,
        timeId: Long,
        timeStr: String,
        ringtoneUri: String
    ) {

        val intent = Intent(context, RingtoneService::class.java).apply {
            action = IntentKeys.playAction
            putExtra(IntentKeys.ringtoneUri, ringtoneUri)
            putExtra(IntentKeys.timeId, timeId)
            putExtra(IntentKeys.timeStr, timeStr)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context?.startForegroundService(intent)
        } else {
            context?.startService(intent)
        }
    }

    private fun stopRingtoneService(context: Context?) {

        val intent = Intent(context?.applicationContext, RingtoneService::class.java).apply {
            action = IntentKeys.stopAction
        }
        context?.startService(intent)
    }
}


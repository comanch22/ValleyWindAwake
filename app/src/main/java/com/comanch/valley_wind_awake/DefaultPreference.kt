package com.comanch.valley_wind_awake

import android.content.SharedPreferences
import com.comanch.valley_wind_awake.stringKeys.PreferenceKeys
import javax.inject.Inject

class DefaultPreference @Inject constructor(private val preference: SharedPreferences) {

    lateinit var key: String

    fun getString(): String? {

        return when (key) {
            PreferenceKeys.signalDuration -> {
                preference.getString(PreferenceKeys.signalDuration, "2")
            }
            PreferenceKeys.pauseDuration -> {
                preference.getString(PreferenceKeys.pauseDuration, "5")
            }
            PreferenceKeys.defaultRingtoneUri -> {
                preference.getString(PreferenceKeys.defaultRingtoneUri, "")
            }
            PreferenceKeys.defaultRingtoneTitle -> {
                preference.getString(PreferenceKeys.defaultRingtoneTitle, "")
            }
            else -> {
                null
            }
        }
    }

    fun getInt(): Int? {

        return when (key) {
            //
            else -> {
                null
            }
        }
    }

    fun putInt(value: Int){

        with(preference.edit()){
            putInt(key, value)
            apply()
        }
    }

    fun putString(value: String){

        with(preference.edit()){
            putString(key, value)
            apply()
        }
    }
}

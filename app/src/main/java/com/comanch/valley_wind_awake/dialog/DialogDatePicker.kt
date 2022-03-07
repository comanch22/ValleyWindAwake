package com.comanch.valley_wind_awake.dialog

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.DialogInterface
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.provider.Settings
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.comanch.valley_wind_awake.FragmentResultKey
import com.comanch.valley_wind_awake.R
import java.util.*


class DialogDatePicker : DialogFragment(), OnDateSetListener {

    private var isTouchSoundsEnabledSystem: Boolean = false
    private var soundPool: SoundPool? = null
    private var soundButtonTap: Int? = null
    private var soundMap: HashMap<Int, Int>? = null
    private val maxSoundPoolStreams = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundMap = hashMapOf()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            ).setMaxStreams(maxSoundPoolStreams)
            .build()

        soundPool?.setOnLoadCompleteListener { _, sampleId, status ->
            soundMap?.put(sampleId, status)
        }

        setTouchSound()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), this, year, month, day)
        datePicker.setButton(
            DialogInterface.BUTTON_NEUTRAL,
            getString(R.string.clear_calendar)
        ) { _, _ ->
            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            setFragmentResult(
                FragmentResultKey.selectedDateKey,
                bundleOf(FragmentResultKey.selectedDateExtraKey to FragmentResultKey.clear)
            )
        }
        datePicker.apply {
            setOnShowListener {
                this.getButton(DialogInterface.BUTTON_POSITIVE)
                    .isSoundEffectsEnabled = false
                this.getButton(DialogInterface.BUTTON_NEGATIVE)
                    .isSoundEffectsEnabled = false
                this.getButton(DialogInterface.BUTTON_NEUTRAL)
                    .isSoundEffectsEnabled = false
            }
        }
        return datePicker
    }

    override fun onResume() {
        super.onResume()
        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0
    }

    override fun onCancel(dialog: DialogInterface) {

        if (isTouchSoundEnable(soundButtonTap)) {
            soundButtonTap?.let { id -> playSound(id) }
        }
        super.onCancel(dialog)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {

        if (isTouchSoundEnable(soundButtonTap)) {
            soundButtonTap?.let { id -> playSound(id) }
        }
        val dateBundle = Bundle()
        dateBundle.putInt(FragmentResultKey.year, year)
        dateBundle.putInt(FragmentResultKey.month, month)
        dateBundle.putInt(FragmentResultKey.day, day)
        setFragmentResult(
            FragmentResultKey.selectedDateKey,
            bundleOf(FragmentResultKey.selectedDateExtraKey to bundleOf(FragmentResultKey.dateBundle to dateBundle))
        )
    }

    private fun playSound(id: Int) {
        soundPool?.play(id, 1F, 1F, 1, 0, 1F)
    }

    private fun setTouchSound() {

        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0

        soundButtonTap = soundPool?.load(context, R.raw.navigation_forward_selection_minimal, 1)
    }

    private fun isTouchSoundEnable(soundId: Int?): Boolean {
        return soundMap?.get(soundId) == 0
                && isTouchSoundsEnabledSystem
    }
}
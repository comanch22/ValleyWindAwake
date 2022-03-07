package com.comanch.valley_wind_awake.dialog

import android.app.Dialog
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.preference.PreferenceManager
import com.comanch.valley_wind_awake.AppStyleKey
import com.comanch.valley_wind_awake.FragmentResultKey
import com.comanch.valley_wind_awake.R
import java.util.HashMap

class DialogDeleteAllAlarms : DialogFragment() {

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

        val builder =
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), getStyle()))

        builder.setTitle(setSpan(getString(R.string.delete_all_items)))
            .setMessage("")
            .setIcon(R.drawable.ic_baseline_access_alarm_24_blue)
            .setPositiveButton(R.string.delete_ok
            ) { _, _ ->
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                setFragmentResult(
                    FragmentResultKey.deleteAllItemsKey,
                    bundleOf(FragmentResultKey.deleteAllItemsExtraKey to FragmentResultKey.ok)
                )
            }
            .setNegativeButton(R.string.delete_cancel
            ) { _, _ ->
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
            }

        return builder.create().apply {

            val colorBackground = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorPrimaryVariant, colorBackground, true)
           // window?.setBackgroundDrawableResource(colorBackground.resourceId)

            val colorText = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorSecondary, colorText, true)

            setOnShowListener {
                this.getButton(AlertDialog.BUTTON_POSITIVE).apply {
                    this.setTextColor(colorText.data)
                    this.isSoundEffectsEnabled = false
                }
                this.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
                    this.setTextColor(colorText.data)
                    this.isSoundEffectsEnabled = false
                }
            }
        }
    }

    private fun getStyle(): Int{

        val defaultPreference = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        if (defaultPreference != null) {
            return when (defaultPreference.getString(AppStyleKey.appStyle, AppStyleKey.blue)) {
                AppStyleKey.blue -> {
                    R.style.AlertDialogCustom
                }
                AppStyleKey.green -> {
                    R.style.AlertDialogCustom2
                }
                AppStyleKey.gray -> {
                    R.style.AlertDialogCustom3
                }
                else -> {
                    R.style.AlertDialogCustom
                }
            }
        }
        return R.style.AlertDialogCustom
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

    private fun setSpan(str: String, BOLD: Boolean = false): SpannableString {

        val colorText = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorSecondary, colorText, true)
        val spannable = SpannableString(str)
        val length = str.length
        spannable.setSpan(
            ForegroundColorSpan(colorText.data),
            0,
            length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (BOLD) {
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                0,
                length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannable
    }
}

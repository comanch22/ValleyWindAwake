package com.comanch.valley_wind_awake.dialogFragments

import android.app.Dialog
import android.graphics.Typeface
import android.os.Bundle
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
import com.comanch.valley_wind_awake.stringKeys.AppStyleKey
import com.comanch.valley_wind_awake.stringKeys.FragmentResultKey
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.SoundPoolForFragments
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DialogDeleteAllAlarms : DialogFragment() {

    @Inject
    lateinit var soundPoolContainer: SoundPoolForFragments

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        soundPoolContainer.soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            soundPoolContainer.soundMap[sampleId] = status
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder =
            AlertDialog.Builder(ContextThemeWrapper(requireContext(), getStyle()))

        builder.setTitle(setSpan(getString(R.string.delete_all_items)))
            .setMessage("")
            .setIcon(R.drawable.ic_baseline_access_alarm_24_blue)
            .setPositiveButton(R.string.delete_ok
            ) { _, _ ->
                soundPoolContainer.playSoundIfEnable(soundPoolContainer.soundButtonTap)
                setFragmentResult(
                    FragmentResultKey.deleteAllItemsKey,
                    bundleOf(FragmentResultKey.deleteAllItemsExtraKey to FragmentResultKey.ok)
                )
            }
            .setNegativeButton(R.string.delete_cancel
            ) { _, _ ->
                soundPoolContainer.playSoundIfEnable(soundPoolContainer.soundButtonTap)
            }

        return builder.create().apply {

            val colorBackground = TypedValue()
            requireContext().theme.resolveAttribute(R.attr.colorPrimaryVariant, colorBackground, true)

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

    override fun onResume() {
        super.onResume()
        soundPoolContainer.setTouchSound()
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

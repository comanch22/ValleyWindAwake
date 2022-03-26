package com.comanch.valley_wind_awake.settingsFragment

import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.provider.Settings
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.*
import com.comanch.valley_wind_awake.stringKeys.AppStyleKey
import com.comanch.valley_wind_awake.stringKeys.FragmentResultKey
import com.comanch.valley_wind_awake.stringKeys.PreferenceKeys
import com.comanch.valley_wind_awake.dialogFragments.DialogRestartActivity
import com.comanch.valley_wind_awake.keyboardFragment.Correspondent
import com.comanch.valley_wind_awake.R

class SettingsFragment : PreferenceFragmentCompat() {

    private var isTouchSoundsEnabledSystem: Boolean = false
    private var soundPool: SoundPool? = null
    private var soundButtonTap: Int? = null
    private var soundMap: HashMap<Int, Int>? = null
    private val maxSoundPoolStreams = 1
    private var previousAppStylePref: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultPreference = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        previousAppStylePref = defaultPreference?.getString(AppStyleKey.appStyle, AppStyleKey.blue) ?: AppStyleKey.blue

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

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToDestination(SettingsFragmentDirections.actionSettingsFragmentToListFragment())
        }
        callback.isEnabled = true

        setFragmentResultListener(FragmentResultKey.restartActivity) { _, bundle ->
            when (bundle.get(FragmentResultKey.restartActivityExtraKey)) {
                FragmentResultKey.ok -> {
                    activity?.let {
                        val intent = it.intent
                        intent.addFlags(
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    or Intent.FLAG_ACTIVITY_NO_ANIMATION
                        )
                        it.overridePendingTransition(0, 0)
                        it.finish()

                        it.overridePendingTransition(0, 0)
                        startActivity(intent)
                    }
                }
            }
        }

        val backButton = findPreference<Preference>(PreferenceKeys.backButton)
        backButton?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                //it.summary = "вернуться назад"
                navigateToDestination(SettingsFragmentDirections.actionSettingsFragmentToListFragment())
                true
            }
        }

        val ringtoneVolumeSelection = findPreference<Preference>(PreferenceKeys.defaultRingtoneUri)
        ringtoneVolumeSelection?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                navigateToDestination(
                    SettingsFragmentDirections.actionSettingsFragmentToRingtonePickerFragment(
                        -1,
                        "",
                        Correspondent.SettingsFragment
                    )
                )
                true
            }

        }

        val buttonSoundEnable = findPreference<Preference>("buttonSoundEnable")
        buttonSoundEnable?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                true
            }
        }

        buttonSoundEnable?.let {
            it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _,
                                                                                    newValue ->
                if (newValue == false) {
                    if (isTouchSoundEnable(soundButtonTap)) {
                        soundButtonTap?.let { id -> playSound(id) }
                    }
                } else {
                    if (isTouchSoundEnable(soundButtonTap)) {
                        soundButtonTap?.let { id -> playSound(id) }
                    }
                }
                true
            }
        }

        val appStyle = findPreference<Preference>(AppStyleKey.appStyle)
        appStyle?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference,
                                                    newValue ->
                preference.summary = newValue.toString()
                val dialogPicker = DialogRestartActivity(previousAppStylePref)
                parentFragmentManager.let { fragmentM ->
                    dialogPicker.show(fragmentM, "dialogPicker")
                }
                true
            }

        appStyle?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                true
            }
        }

        val signalDuration = findPreference<Preference>(PreferenceKeys.signalDuration)
        signalDuration?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference,
                                                    newValue ->
                preference.summary = newValue.toString()
                true
            }

        signalDuration?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                true
            }
        }

        val pauseDuration = findPreference<Preference>(PreferenceKeys.pauseDuration)
        pauseDuration?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { preference,
                                                    newValue ->
                preference.summary = newValue.toString()
                true
            }

        pauseDuration?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                true
            }
        }

        val isVibrate = findPreference<Preference>(PreferenceKeys.isVibrate)
        isVibrate?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener() { _,
                                                    newValue ->
                with(defaultPreference?.edit()) {
                    this?.putBoolean(PreferenceKeys.isVibrate, newValue as Boolean)
                    this?.apply()
                }
                true
            }

        isVibrate?.let {
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }
                true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val colorBackground = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimaryVariant, colorBackground, true)
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(colorBackground.data)
        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        val defaultPreference = PreferenceManager.getDefaultSharedPreferences(context)

        val backButton = Preference(context)
        backButton.icon = ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_baseline_keyboard_arrow_left_24,
            context.theme
        )
        backButton.key = PreferenceKeys.backButton
        backButton.title = resources.getString(R.string.settings_title)
        backButton.summary = resources.getString(R.string.back_button)
        backButton.layoutResource = R.layout.preference_custom_layout

        val ringtoneVolumeSelection = Preference(context)
        ringtoneVolumeSelection.key = PreferenceKeys.defaultRingtoneUri
        ringtoneVolumeSelection.title = resources.getString(R.string.settings_ringtone_choice)
        ringtoneVolumeSelection.summary = defaultPreference.getString("defaultRingtoneTitle", "")
        ringtoneVolumeSelection.layoutResource = R.layout.preference_custom_layout
        ringtoneVolumeSelection.setDefaultValue("")

        val appStyle = ListPreference(context)
        appStyle.key = AppStyleKey.appStyle
        appStyle.title = resources.getString(R.string.settings_app_style)
        appStyle.summary = defaultPreference.getString(AppStyleKey.appStyle, AppStyleKey.blue)
        appStyle.dialogTitle = resources.getString(R.string.application_style)
        appStyle.layoutResource = R.layout.preference_custom_layout
        appStyle.setEntries(R.array.app_style)
        appStyle.setEntryValues(R.array.app_style)
        appStyle.setDefaultValue(AppStyleKey.blue)

        val signalDuration = ListPreference(context)
        signalDuration.key = PreferenceKeys.signalDuration
        signalDuration.title = resources.getString(R.string.signal_duration)
        signalDuration.summary = defaultPreference.getString(PreferenceKeys.signalDuration, "2")
        signalDuration.dialogTitle = resources.getString(R.string.choose_signal_duration)
        signalDuration.layoutResource = R.layout.preference_custom_layout
        signalDuration.setEntries(R.array.signal_duration)
        signalDuration.setEntryValues(R.array.signal_duration)
        signalDuration.setDefaultValue("2")

        val pauseDuration = ListPreference(context)
        pauseDuration.key = PreferenceKeys.pauseDuration
        pauseDuration.title = resources.getString(R.string.pause_duration)
        pauseDuration.summary = defaultPreference.getString(PreferenceKeys.pauseDuration, "5")
        pauseDuration.dialogTitle = resources.getString(R.string.choose_pause_duration)
        pauseDuration.layoutResource = R.layout.preference_custom_layout
        pauseDuration.setEntries(R.array.pause_duration)
        pauseDuration.setEntryValues(R.array.pause_duration)
        pauseDuration.setDefaultValue("5")

        val isVibrate = SwitchPreference(context)
        isVibrate.key = PreferenceKeys.isVibrate
        isVibrate.title = resources.getString(R.string.vibration_signal)
        isVibrate.layoutResource = R.layout.switch_custom

        screen.addPreference(backButton)
        screen.addPreference(ringtoneVolumeSelection)
        screen.addPreference(appStyle)
        screen.addPreference(signalDuration)
        screen.addPreference(pauseDuration)
        screen.addPreference(isVibrate)

        preferenceScreen = screen
    }

    override fun onResume() {
        super.onResume()
        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0
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

    private fun navigateToDestination(destination: NavDirections) = with(findNavController()) {

        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
    }
}


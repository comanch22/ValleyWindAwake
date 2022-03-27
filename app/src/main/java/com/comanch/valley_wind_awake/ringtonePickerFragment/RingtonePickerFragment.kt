package com.comanch.valley_wind_awake.ringtonePickerFragment


import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import android.Manifest
import android.provider.Settings
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.dataBase.RingtoneData
import com.comanch.valley_wind_awake.databinding.RingtonePickerFragmentBinding
import com.comanch.valley_wind_awake.keyboardFragment.Correspondent
import com.comanch.valley_wind_awake.stringKeys.PreferenceKeys

class RingtonePickerFragment : Fragment() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val args: RingtonePickerFragmentArgs by navArgs()
    private var defaultListOfRingtones: MutableList<RingtoneData> = mutableListOf()
    private var preferenceDefaultRingtone: SharedPreferences? = null
    private var ringtonePickerViewModel: RingtonePickerViewModel? = null
    private var previousRingTone: RingtoneData? = null
    private var mService: RingtoneService? = null
    private var audioManager: AudioManager? = null
    private var currentVolume: Int? = null
    private var maxVolume: Int? = null
    private var minVolume: Int = 1
    private var selectedPlayerVolume = 20

    // touch sound
    private var isTouchSoundsEnabledSystem: Boolean = false
    private var soundPool: SoundPool? = null
    private var soundCancel: Int? = null
    private var soundButtonTap: Int? = null
    private var soundMap: HashMap<Int, Int>? = null
    private val maxSoundPoolStreams = 1
    //

    private var isPlaying: Boolean = false
    private var isSaveState: Boolean = false
    private var mBound: Boolean = false
    private var isCustomChooseVolume: Boolean = false

    private var ringtoneSeekPosition: Int = 0
    private val itemNotActive: Int = 0
    private val zeroPosition: Int = 0
    private var counterPosition = 0
    private var currentRingTonePositionInList: Int = -1
    private val impossiblePositionInList: Int = -1
    private val impossibleMelodyId: Long = -2
    private val itemActive: Int = 1

    private val emptyString: String = ""
    private var ringtoneUri: String = ""

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {

            val binder = service as RingtoneService.LocalBinder
            mService = binder.getService()
            mBound = true
            startServiceAfterRotation()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    navigateToDestination(
                        RingtonePickerFragmentDirections.actionRingtonePickerFragmentToRingtoneCustomPickerFragment(
                            args.itemId,
                            args.ringtoneTitle
                        )
                    )
                } else {
                    Toast.makeText(context, R.string.noAccessRights, Toast.LENGTH_LONG).show()
                }
            }

        super.onCreate(savedInstanceState)

        val action =
            if (args.correspondent == Correspondent.SettingsFragment) {
                RingtonePickerFragmentDirections.actionRingtonePickerFragmentToSettingsFragment()
            } else {
                RingtonePickerFragmentDirections.actionRingtonePickerFragmentToKeyboardFragment(
                    args.itemId,
                    Correspondent.RingtoneFragment,
                    emptyString,
                    args.ringtoneTitle
                )
            }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToDestination(action)
        }
        callback.isEnabled = true

        ringtoneUri = savedInstanceState?.getString("ringtoneUri") ?: emptyString
        ringtoneSeekPosition = savedInstanceState?.getInt("ringtoneSeekPosition") ?: zeroPosition
        isPlaying = savedInstanceState?.getBoolean("isPlaying") ?: false
        isSaveState = savedInstanceState?.getBoolean("isSaveState") ?: false
        defaultListOfRingtones = getDefaultRingtoneList(defaultListOfRingtones)
        currentRingTonePositionInList =
            savedInstanceState?.getInt("currentRingTonePositionInList") ?: -1

        audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager?.let {
            currentVolume = audioManager?.getStreamVolume(AudioManager.STREAM_ALARM)
            maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_ALARM)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                maxVolume = maxVolume?.minus(1)
            }
        }
        preferenceDefaultRingtone = PreferenceManager.getDefaultSharedPreferences(requireContext())

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding: RingtonePickerFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.ringtone_picker_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSourceMelody = DataControl.getInstance(application).ringtoneDatabaseDao
        val viewModelFactory = RingtonePickerViewModelFactory(dataSourceMelody)
        ringtonePickerViewModel =
            ViewModelProvider(this, viewModelFactory)[RingtonePickerViewModel::class.java]
        binding.ringtonePickerViewModel = ringtonePickerViewModel

        val colorAccent = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimaryVariant, colorAccent, true)

        val adapter =
            RingtoneAdapter(ItemListener { ringtone ->
                ringtonePickerViewModel?.onItemClicked(ringtone)
            }, defaultListOfRingtones, colorAccent.data)

        binding.RingtoneList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner

        if (args.correspondent == Correspondent.SettingsFragment) {
            binding.fabAdd.isInvisible = true
            binding.fabDelete.isInvisible = true
        }

        ringtonePickerViewModel?.setRestorePlayerFlag(isSaveState)
        ringtonePickerViewModel?.restorePlayerFlag?.observe(viewLifecycleOwner) {

            it.getContentIfNotHandled()?.let {
                if (isSaveState && isPlaying) {
                    mService?.startPlayAfterRotation(ringtoneUri, ringtoneSeekPosition)
                    previousRingTone = setActiveForItem(adapter, itemActive)
                    previousRingTone?.let { it1 -> ringtonePickerViewModel?.onItemClicked(it1) }
                }
            }
        }

        ringtonePickerViewModel?.setTouchSoundAndVolume()
        ringtonePickerViewModel?.setTouchSoundAndVolume?.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {

                getVolumeFromSettings().let {
                    binding.seekbar.progress = it
                    binding.seekbarValue.text = it.toString()
                }
                if (currentVolume != null && maxVolume != null) {
                    binding.seekbar.progress =
                        currentVolume ?: (maxVolume!!.plus(minVolume)).div(2)
                    binding.seekbar.max = maxVolume!!
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        binding.seekbar.min = minVolume
                    }
                    binding.seekbarValue.text = currentVolume.toString()
                }
                setTouchSound()
            }
        }

        ringtonePickerViewModel?.itemActiveState?.observe(viewLifecycleOwner) {

            it.getContentIfNotHandled()?.let {
                if (!isSaveState) {
                    setActiveForItem(adapter, itemNotActive)
                }
            }
        }

        Intent(context, RingtoneService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        ringtonePickerViewModel?.currentRingTone?.observe(viewLifecycleOwner) {
            it?.let {

                if (!isSaveState) {
                    if (isCustomChooseVolume && mService?.isPlaying() == true) {
                        currentRingTonePositionInList = it.position
                    } else {
                        mService?.stopPlay()
                        if (it.active == itemActive) {
                            it.active = itemNotActive
                            currentRingTonePositionInList = impossiblePositionInList
                        } else {
                            mService?.setUri(it.uriAsString)
                            mService?.startPlay()
                            it.active = itemActive
                            currentRingTonePositionInList = it.position
                        }
                    }
                    previousRingTone?.position?.let { pos -> adapter.notifyItemChanged(pos) }
                    it.position.let { pos -> adapter.notifyItemChanged(pos) }

                    if (previousRingTone != it) {
                        previousRingTone?.active = itemNotActive
                        previousRingTone = it
                    }
                    isCustomChooseVolume = false
                }else{
                    isSaveState = false
                }
            }
        }

        ringtonePickerViewModel?.items?.observe(viewLifecycleOwner) {
            it?.let {
                adapter.setData(it, counterPosition)
            }
        }

        ringtonePickerViewModel?.delete?.observe(viewLifecycleOwner) {
            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            mService?.stopPlay()
        }

        ringtonePickerViewModel?.chooseRingtone?.observe(viewLifecycleOwner) {
            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            it.getContentIfNotHandled()?.let {
                when {
                    context?.let { it_ ->
                        ContextCompat.checkSelfPermission(
                            it_,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } == PackageManager.PERMISSION_GRANTED -> {
                        navigateToDestination(
                            RingtonePickerFragmentDirections.actionRingtonePickerFragmentToRingtoneCustomPickerFragment(
                                args.itemId,
                                args.ringtoneTitle
                            )
                        )

                    }
                    shouldShowRequestPermissionRationale("READ_EXTERNAL_STORAGE") -> {
                    }
                    else -> {
                        requestPermissionLauncher.launch(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    }
                }
            }
        }

        ringtonePickerViewModel?.toast?.observe(viewLifecycleOwner) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }

        ringtonePickerViewModel?.setRingtoneTitle?.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let { ringtoneTitle ->
                if (ringtoneTitle != "" && args.correspondent == Correspondent.SettingsFragment) {
                    with(preferenceDefaultRingtone?.edit()) {
                        this?.putString(PreferenceKeys.defaultRingtoneTitle, ringtoneTitle)
                        this?.apply()
                    }
                }
            }
        }

        ringtonePickerViewModel?.setRingtoneUri?.observe(viewLifecycleOwner) { content ->

            if (isTouchSoundEnable(soundCancel)) {
                soundCancel?.let { id -> playSound(id) }
            }
            content.getContentIfNotHandled()?.let { ringtoneUri ->
                if (ringtoneUri == "") {
                    Toast.makeText(context, R.string.noRingtoneSelected, Toast.LENGTH_LONG)
                        .show()
                } else {
                    when (args.correspondent) {
                        Correspondent.SettingsFragment -> {
                            with(preferenceDefaultRingtone?.edit()) {
                                this?.putString(
                                    PreferenceKeys.defaultRingtoneUri, ringtoneUri)
                                this?.apply()
                            }
                            navigateToDestination(
                                RingtonePickerFragmentDirections.actionRingtonePickerFragmentToSettingsFragment()
                            )
                        }
                        else -> {
                            navigateToDestination(
                                RingtonePickerFragmentDirections.actionRingtonePickerFragmentToKeyboardFragment(
                                    args.itemId,
                                    Correspondent.RingtoneFragment,
                                    ringtoneUri,
                                    previousRingTone?.title ?: ""
                                )
                            )
                        }
                    }
                }
            }
        }

        binding.fabDelete.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            mService?.stopPlay()
            ringtonePickerViewModel?.deleteMelody()
        }

        binding.Cancel.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            if (args.correspondent == Correspondent.SettingsFragment) {
                navigateToDestination(
                    RingtonePickerFragmentDirections.actionRingtonePickerFragmentToSettingsFragment()
                )
            } else {
                navigateToDestination(
                    RingtonePickerFragmentDirections.actionRingtonePickerFragmentToKeyboardFragment(
                        args.itemId,
                        Correspondent.RingtoneFragment,
                        emptyString,
                        args.ringtoneTitle
                    )
                )
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (isTouchSoundEnable(soundButtonTap)) {
                    soundButtonTap?.let { id -> playSound(id) }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    binding.seekbarValue.text = progress.toString()
                    audioManager?.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        progress,
                        0
                    )
                } else {
                    val mProgress = if (progress == 0) 1 else progress
                    binding.seekbarValue.text = mProgress.toString()
                    audioManager?.setStreamVolume(
                        AudioManager.STREAM_ALARM,
                        mProgress,
                        0
                    )
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                val ringtone =
                    adapter.getIt(if (currentRingTonePositionInList >= 0) currentRingTonePositionInList else 0)
                ringtone?.let {
                    isCustomChooseVolume = true
                    ringtonePickerViewModel?.onItemClicked(ringtone)
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                context?.let { context ->
                    PreferenceManager.getDefaultSharedPreferences(context)?.let {
                        with(it.edit()) {
                            putInt("mediaPlayerVolume", selectedPlayerVolume)
                            apply()
                        }
                    }
                }
            }

        })

        binding.arrowBack.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            if (args.correspondent == Correspondent.SettingsFragment) {
                navigateToDestination(
                    RingtonePickerFragmentDirections.actionRingtonePickerFragmentToSettingsFragment()
                )
            } else {
                navigateToDestination(
                    RingtonePickerFragmentDirections.actionRingtonePickerFragmentToKeyboardFragment(
                        args.itemId,
                        Correspondent.RingtoneFragment,
                        emptyString,
                        args.ringtoneTitle
                    )
                )
            }
        }

        return binding.root
    }

    override fun onResume() {

        super.onResume()
        ringtonePickerViewModel?.setItemActiveState()
        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0
    }

    override fun onPause() {

        isPlaying = mService?.isPlaying() ?: false
        ringtoneUri = mService?.getStringUri() ?: emptyString
        ringtoneSeekPosition = mService?.getPausePosition() ?: zeroPosition
        isSaveState = false

        ringtonePickerViewModel?.resetCurrentRingTone()
        mService?.offMediaPlayer()

        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putBoolean("isPlaying", isPlaying)
        outState.putBoolean("isPause", false)
        outState.putBoolean("isSaveState", true)
        outState.putInt("currentRingTonePositionInList", currentRingTonePositionInList)
        outState.putString("ringtoneUri", ringtoneUri)
        outState.putInt("ringtoneSeekPosition", ringtoneSeekPosition)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {

        offMediaUnbindService()
        super.onDestroy()
    }

    private fun offMediaUnbindService() {

        if (mBound) {
            mService?.offMediaPlayer()
            context?.unbindService(connection)
            mBound = false
        }
    }

    private fun getDefaultRingtoneList(defaultListOfRingtones: MutableList<RingtoneData>): MutableList<RingtoneData> {

        val ringtoneManager = RingtoneManager(context)
        val cursor = ringtoneManager.cursor
        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i)
            defaultListOfRingtones.add(
                RingtoneData(
                    title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    uriAsString = ringtoneManager.getRingtoneUri(i).toString(),
                    melodyId = impossibleMelodyId
                )
            )
        }
        return defaultListOfRingtones
    }

    private fun setActiveForItem(adapter: RingtoneAdapter, active: Int): RingtoneData? {

        return adapter.getIt(currentRingTonePositionInList)?.let { ringtoneData ->
            ringtoneData.active = active
            adapter.notifyItemChanged(ringtoneData.position)
            ringtoneData
        }
    }

    private fun startServiceAfterRotation() {
        ringtonePickerViewModel?.restoreStateForRingtoneFragment()
    }

    private fun navigateToDestination(destination: NavDirections) = with(findNavController()) {

        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
        offMediaUnbindService()
    }

    private fun getVolumeFromSettings(): Int {
        return PreferenceManager.getDefaultSharedPreferences(requireContext())
            .getInt("mediaPlayerVolume", 20)
    }

    private fun playSound(id: Int) {
        soundPool?.play(id, 1F, 1F, 1, 0, 1F)
    }

    private fun setTouchSound() {

        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0

        soundCancel = soundPool?.load(context, R.raw.navigation_cancel, 1)
        soundButtonTap = soundPool?.load(context, R.raw.navigation_forward_selection_minimal, 1)
    }

    private fun isTouchSoundEnable(soundId: Int?): Boolean {

        return soundMap?.get(soundId) == 0
                && isTouchSoundsEnabledSystem
    }
}
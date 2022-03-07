package com.comanch.valley_wind_awake.ringtoneManagement.custom_ringtone_picker

import android.content.*
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.dataBase.RingtoneData
import com.comanch.valley_wind_awake.databinding.RingtoneCustomPickerFragmentBinding
import com.comanch.valley_wind_awake.keyboard.Correspondent
import com.comanch.valley_wind_awake.list.ListOfCustomRingtones


class RingtoneCustomPickerFragment : Fragment() {

    private val args: RingtoneCustomPickerFragmentArgs by navArgs()
    private var ringtoneCustomPickerViewModel: RingtoneCustomPickerViewModel? = null
    private var previousRingTone: RingtoneData? = null
    private var mService: RingtoneService? = null
    private var listOfRingtones: List<RingtoneData>? = null

    // touch sound
    private var isTouchSoundsEnabledSystem: Boolean = false
    private var soundPool: SoundPool? = null
    private var soundCancel: Int? = null
    private var soundButtonTap: Int? = null
    private var soundMap: HashMap<Int, Int>? = null
    private val maxSoundPoolStreams = 1
    //

    private var mBound: Boolean = false
    private var isPlaying: Boolean = false
    private var isSaveState: Boolean = false

    private var ringtoneSeekPosition: Int = 0
    private var currentRingTonePositionInList: Int = -1
    private val itemActive: Int = 1
    private val itemNotActive: Int = 0
    private val zeroPosition: Int = 0
    private val impossiblePositionInList: Int = -1

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

        super.onCreate(savedInstanceState)

        val action =
            RingtoneCustomPickerFragmentDirections.actionRingtoneCustomPickerFragmentToRingtonePickerFragment(
                args.itemId,
                args.ringtoneTitle,
                Correspondent.RingtoneCustomFragment
            )
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToRingtonePickerFragment(action)
        }
        callback.isEnabled = true

        ringtoneUri = savedInstanceState?.getString("ringtoneUri") ?: emptyString
        ringtoneSeekPosition = savedInstanceState?.getInt("ringtoneSeekPosition") ?: zeroPosition
        currentRingTonePositionInList =
            savedInstanceState?.getInt("currentRingTonePositionInList") ?: impossiblePositionInList
        isPlaying = savedInstanceState?.getBoolean("isPlaying") ?: false
        isSaveState = savedInstanceState?.getBoolean("isSaveState") ?: false

        context?.let {
            listOfRingtones = ListOfCustomRingtones(it).getListOfRingtones()
        }


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

        val binding: RingtoneCustomPickerFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.ringtone_custom_picker_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSourceMelody = DataControl.getInstance(application).ringtoneDatabaseDao
        val viewModelFactory = RingtoneCustomPickerViewModelFactory(dataSourceMelody)
        ringtoneCustomPickerViewModel =
            ViewModelProvider(this, viewModelFactory)[RingtoneCustomPickerViewModel::class.java]
        binding.ringtoneCustomPickerViewModel = ringtoneCustomPickerViewModel

        val adapter =
            RingtoneCustomAdapter(ItemListener { ringtone ->
                ringtoneCustomPickerViewModel?.onItemClicked(ringtone)
            })

        binding.RingtoneList.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner

        listOfRingtones?.let {
            adapter.setData(listOfRingtones)
        }

        ringtoneCustomPickerViewModel?.setTouchSound()
        ringtoneCustomPickerViewModel?.setRestorePlayerFlag(isSaveState)
        ringtoneCustomPickerViewModel?.restorePlayerFlag?.observe(viewLifecycleOwner) {

            it.getContentIfNotHandled()?.let {
                if (isPlaying && isSaveState) {
                    mService?.startPlayAfterRotation(ringtoneUri, ringtoneSeekPosition)
                    previousRingTone = setActiveForItem(adapter, itemActive)
                }
            }
        }

        Intent(context, RingtoneService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        ringtoneCustomPickerViewModel?.setTouchSound?.observe(viewLifecycleOwner) {

            it.getContentIfNotHandled()?.let {
                setTouchSound()
            }
        }

        ringtoneCustomPickerViewModel?.itemActiveState?.observe(viewLifecycleOwner) {

            it.getContentIfNotHandled()?.let {
                if (!isSaveState) {
                    setActiveForItem(adapter, itemNotActive)
                }
            }
        }

        ringtoneCustomPickerViewModel?.currentRingTone?.observe(viewLifecycleOwner) {

            it?.let {
                mService?.stopPlay()
                if (it.active == itemActive) {
                    it.active = itemNotActive
                    currentRingTonePositionInList = impossiblePositionInList
                } else {
                    mService?.setUri(it.musicId)
                    mService?.startPlay()
                    it.active = itemActive
                    currentRingTonePositionInList = it.position
                }

                previousRingTone?.position?.let { pos -> adapter.notifyItemChanged(pos) }
                it.position.let { pos -> adapter.notifyItemChanged(pos) }

                if (previousRingTone != it) {
                    previousRingTone?.active = itemNotActive
                    previousRingTone = it
                }
            }
        }

        binding.Cancel.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }

            navigateToRingtonePickerFragment(
                RingtoneCustomPickerFragmentDirections.actionRingtoneCustomPickerFragmentToRingtonePickerFragment(
                    args.itemId,
                    args.ringtoneTitle,
                    Correspondent.RingtoneCustomFragment
                )
            )
        }

        binding.Ok.setOnClickListener {

            if (isTouchSoundEnable(soundCancel)) {
                soundCancel?.let { id -> playSound(id) }
            }

            ringtoneCustomPickerViewModel?.setMelody()
            navigateToRingtonePickerFragment(
                RingtoneCustomPickerFragmentDirections.actionRingtoneCustomPickerFragmentToRingtonePickerFragment(
                    args.itemId,
                    args.ringtoneTitle,
                    Correspondent.RingtoneCustomFragment
                )
            )
        }

        binding.arrowBack.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }

            navigateToRingtonePickerFragment(
                RingtoneCustomPickerFragmentDirections.actionRingtoneCustomPickerFragmentToRingtonePickerFragment(
                    args.itemId,
                    args.ringtoneTitle,
                    Correspondent.RingtoneCustomFragment
                )
            )
        }

        return binding.root
    }

    override fun onResume() {

        super.onResume()
        ringtoneCustomPickerViewModel?.setItemActiveState()
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

        ringtoneCustomPickerViewModel?.resetCurrentRingTone()
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

    private fun navigateToRingtonePickerFragment(destination: NavDirections) =
        with(findNavController()) {

            currentDestination?.getAction(destination.actionId)
                ?.let { navigate(destination) }
            offMediaUnbindService()
        }

    private fun setActiveForItem(adapter: RingtoneCustomAdapter, active: Int): RingtoneData? {

        return adapter.getIt(currentRingTonePositionInList)?.let { ringtoneData ->
            ringtoneData.active = active
            adapter.notifyItemChanged(ringtoneData.position)
            ringtoneData
        }
    }

    private fun startServiceAfterRotation() {
        ringtoneCustomPickerViewModel?.restoreStateForCustomRingtoneFragment()
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
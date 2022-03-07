package com.comanch.valley_wind_awake.list

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.comanch.valley_wind_awake.*
import com.comanch.valley_wind_awake.alarmManagement.AlarmControl
import com.comanch.valley_wind_awake.alarmManagement.AlarmTypeOperation
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.databinding.FrontListFragmentBinding
import com.comanch.valley_wind_awake.dialog.DialogDeleteAllAlarms
import com.comanch.valley_wind_awake.keyboard.Correspondent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class ListFragment : Fragment() {

    private var adapter: ListItemAdapter? = null
    private lateinit var listViewModel: ListViewModel
    private var deleteModeOn = false
    private var defaultRingtoneUri: String = ""
    private var defaultRingtoneTitle: String = ""
    private var isTouchSoundsEnabledSystem: Boolean = false
    private var soundPool: SoundPool? = null
    private var soundCancel: Int? = null
    private var soundStart: Int? = null
    private var soundButtonTap: Int? = null
    private var soundUiTap: Int? = null
    private var soundStateUp: Int? = null
    private var soundStateDown: Int? = null
    private var soundNavigation: Int? = null
    private var soundMap: HashMap<Int, Int>? = null
    private val maxSoundPoolStreams = 1
    private var isCreated: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val defaultPreference = context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
        defaultRingtoneUri = defaultPreference?.getString(PreferenceKeys.defaultRingtoneUri, "") ?: ""
        defaultRingtoneTitle = defaultPreference?.getString("defaultRingtoneTitle", "") ?: ""

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

        val binding: FrontListFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.front_list_fragment, container, false
        )

        binding.list.setHasFixedSize(true)

        val application = requireNotNull(this.activity).application
        val dataSource = DataControl.getInstance(application).timeDatabaseDao
        val viewModelFactory =
            ListViewModelFactory(dataSource, defaultRingtoneUri, defaultRingtoneTitle)
        listViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[ListViewModel::class.java]
        binding.listViewModel = listViewModel

        setFragmentResultListener(FragmentResultKey.deleteAllItemsKey) { _, bundle ->
            when (bundle.get(FragmentResultKey.deleteAllItemsExtraKey)) {
                FragmentResultKey.ok -> {
                    listViewModel.clear()
                }
            }
        }

        binding.toolbar.inflateMenu(R.menu.app_menu)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_done -> {
                    if (isTouchSoundEnable(soundButtonTap)) {
                        soundButtonTap?.let { id -> playSound(id) }
                    }

                    val dialogPicker = DialogDeleteAllAlarms()
                    parentFragmentManager.let { fragmentM ->
                        dialogPicker.show(fragmentM, "dialogPicker")
                    }
                    true
                }
                R.id.action_settings -> {
                    if (isTouchSoundEnable(soundNavigation)) {
                        soundNavigation?.let { id -> playSound(id) }
                    }
                    this.findNavController().navigate(
                        ListFragmentDirections.actionListFragmentToSettingsFragment()
                    )
                    true
                }
                R.id.about_app -> {
                    if (isTouchSoundEnable(soundNavigation)) {
                        soundNavigation?.let { id -> playSound(id) }
                    }
                    this.findNavController().navigate(
                        ListFragmentDirections.actionListFragmentToAboutAppFragment()
                    )
                    true
                }
                else -> false
            }
        }

        val colorOn = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorSecondary, colorOn, true)

        val colorOff = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, colorOff, true)

        val backgroundColor = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimaryVariant, backgroundColor, true)

        val is24HourFormat = DateFormat.is24HourFormat(requireContext())

        adapter = ListItemAdapter(
            ItemListener { itemId ->
                listViewModel.onItemClicked(itemId)
            },
            SwitchListener { item ->
                listViewModel.updateSwitchCheck(item)
            },
            DeleteListener { itemId ->
                listViewModel.offAlarmDeleteItem(itemId)
            },
            colorOn.data,
            colorOff.data,
            backgroundColor.data,
            is24HourFormat,
            Calendar.getInstance().timeInMillis
        )

        binding.list.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner
        deleteModeOn = savedInstanceState?.getBoolean("deleteModeOn", false) == true
        if (!deleteModeOn) {
            binding.ButtonDone.visibility = View.INVISIBLE
            binding.toolbar.visibility = View.VISIBLE
            binding.ButtonDelete.visibility = View.VISIBLE
            binding.ButtonPlus.visibility = View.VISIBLE
        } else {
            binding.ButtonDone.visibility = View.VISIBLE
            binding.toolbar.visibility = View.INVISIBLE
            binding.ButtonDelete.visibility = View.INVISIBLE
            binding.ButtonPlus.visibility = View.INVISIBLE
        }
        adapter?.setDeleteMode(deleteModeOn)
        listViewModel.setDeleteItemsMode(deleteModeOn)

        listViewModel.items.observe(viewLifecycleOwner) { list ->
            list?.let {
                adapter?.setData(it)
            }
            lifecycleScope.launch {
                delay(200)
                binding.list.smoothScrollToPosition(0)
            }
            setTouchSound()
            listViewModel.setNearestDate(DateFormat.is24HourFormat(requireContext()))
        }

        listViewModel.nearestDate.observe(viewLifecycleOwner) { str ->
            str?.let {
                if (it.isEmpty()) {
                    binding.toolbarNearestDate.text = ""
                    binding.toolbarTitle.visibility = View.INVISIBLE
                    binding.toolbarTitle.text = ""
                } else {
                    binding.toolbarTitle.text = resources.getString(R.string.the_nearest_signal)
                    binding.toolbarNearestDate.text = it
                    binding.toolbarTitle.visibility = View.VISIBLE
                }
            }
        }

        listViewModel.navigateToKeyboardFragment.observe(viewLifecycleOwner) { itemId ->

            itemId?.getContentIfNotHandled()?.let {
                if (isTouchSoundEnable(soundStart)) {
                    soundStart?.let { id -> playSound(id) }
                }
                this.findNavController().navigate(
                    ListFragmentDirections.actionListFragmentToKeyboardFragment(
                        it,
                        Correspondent.ListFragment,
                        "",
                        ""
                    )
                )
            }
        }

        listViewModel.deleteAllItems.observe(viewLifecycleOwner) { listTimeData ->
            listTimeData?.let {
                lifecycleScope.launch {
                    context?.applicationContext.let { appContext ->
                        if (appContext != null) {
                            it.forEach {
                                if (AlarmControl(appContext, it)
                                        .schedulerAlarm(AlarmTypeOperation.DELETE).isNotEmpty()
                                ) {
                                }
                            }
                            listViewModel.deleteAll()
                            listViewModel.resetDeleteAllItems()
                        }
                    }
                }
            }
        }

        listViewModel.offAlarm.observe(viewLifecycleOwner) {
            if (isTouchSoundEnable(soundUiTap)) {
                soundUiTap?.let { id -> playSound(id) }
            }
            it?.let {
                lifecycleScope.launch {
                    context?.applicationContext.let { appContext ->
                        if (appContext != null) {
                            AlarmControl(appContext, it)
                                .schedulerAlarm(AlarmTypeOperation.DELETE).isNotEmpty()
                            listViewModel.deleteItem(it)
                        }
                    }
                }
            }
        }

        listViewModel.itemActive.observe(viewLifecycleOwner) { timeData ->
            timeData?.let {
                if (timeData.active) {
                    if (isTouchSoundEnable(soundStateDown)) {
                        soundStateDown?.let { id -> playSound(id) }
                    }
                } else {
                    if (isTouchSoundEnable(soundStateUp)) {
                        soundStateUp?.let { id -> playSound(id) }
                    }
                }

                lifecycleScope.launch {
                    context?.applicationContext.let { appContext ->
                        if (appContext != null) {
                            when (AlarmControl(
                                appContext,
                                timeData
                            ).schedulerAlarm(AlarmTypeOperation.SWITCH)
                            ) {
                                OperationKey.successOff -> {
                                    listViewModel.resetItemActive()
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.alarm_is_off),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                OperationKey.successOn -> {
                                    listViewModel.resetItemActive()
                                    listViewModel.showDiffTimeToast(it.timeId)
                                }
                                OperationKey.incorrectDate -> {
                                    Toast.makeText(
                                        context,
                                        resources.getString(R.string.time_is_over),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }

        listViewModel.timeToast.observe(viewLifecycleOwner) { timeData ->

            timeData.getContentIfNotHandled()?.let {
                Toast.makeText(
                    context,
                    DateDifference().getResultString(
                        it.nearestDate - Calendar.getInstance().timeInMillis,
                        resources.getString(R.string.days),
                        resources.getString(R.string.hours),
                        resources.getString(R.string.min)
                    ),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.ButtonDelete.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            if (deleteModeOn) {
                binding.ButtonDone.visibility = View.INVISIBLE
                binding.toolbar.visibility = View.VISIBLE
                binding.ButtonDelete.visibility = View.VISIBLE
                binding.ButtonPlus.visibility = View.VISIBLE
                deleteModeOn = false
            } else {
                binding.ButtonDone.visibility = View.VISIBLE
                binding.toolbar.visibility = View.INVISIBLE
                binding.ButtonDelete.visibility = View.INVISIBLE
                binding.ButtonPlus.visibility = View.INVISIBLE
                deleteModeOn = true
            }
            adapter?.setDeleteMode(deleteModeOn)
            listViewModel.setDeleteItemsMode(deleteModeOn)
        }

        binding.ButtonDone.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            binding.ButtonDone.visibility = View.INVISIBLE
            binding.toolbar.visibility = View.VISIBLE
            binding.ButtonDelete.visibility = View.VISIBLE
            binding.ButtonPlus.visibility = View.VISIBLE
            deleteModeOn = false
            adapter?.setDeleteMode(deleteModeOn)
            listViewModel.setDeleteItemsMode(deleteModeOn)
        }

        binding.ButtonPlus.setOnClickListener {

            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            listViewModel.insertItem()
        }

        binding.arrowBack.setOnClickListener {
            if (isTouchSoundEnable(soundButtonTap)) {
                soundButtonTap?.let { id -> playSound(id) }
            }
            activity?.finish()
        }
        return binding.root
    }

    override fun onStart() {

        super.onStart()
        if (isCreated) {
            val currentIs24HourFormat = DateFormat.is24HourFormat(requireContext())
            adapter?.setIs24HourFormat(currentIs24HourFormat)
            adapter?.refreshList()
            listViewModel.setNearestDate(currentIs24HourFormat)
        } else {
            isCreated = true
        }
    }

    override fun onResume() {

        super.onResume()
        isTouchSoundsEnabledSystem = Settings.System.getInt(
            activity?.contentResolver,
            Settings.System.SOUND_EFFECTS_ENABLED, 1
        ) != 0
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putBoolean("deleteModeOn", deleteModeOn)
        super.onSaveInstanceState(outState)
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
        soundStart = soundPool?.load(context, R.raw.navigation_forward_selection, 1)
        soundButtonTap = soundPool?.load(context, R.raw.navigation_forward_selection_minimal, 1)
        soundUiTap = soundPool?.load(context, R.raw.ui_refresh_feed, 1)
        soundNavigation = soundPool?.load(context, R.raw.navigation_transition_right, 1)
        soundStateUp = soundPool?.load(context, R.raw.state_change_confirm_up, 1)
        soundStateDown = soundPool?.load(context, R.raw.state_change_confirm_down, 1)
    }

    private fun isTouchSoundEnable(soundId: Int?): Boolean {

        return soundMap?.get(soundId) == 0
                && isTouchSoundsEnabledSystem
    }
}




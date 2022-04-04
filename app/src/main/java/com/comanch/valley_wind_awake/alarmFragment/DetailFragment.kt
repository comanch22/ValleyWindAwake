package com.comanch.valley_wind_awake.alarmFragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.comanch.valley_wind_awake.DefaultPreference
import com.comanch.valley_wind_awake.NavigationBetweenFragments
import com.comanch.valley_wind_awake.stringKeys.IntentKeys
import com.comanch.valley_wind_awake.stringKeys.PreferenceKeys
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.broadcastreceiver.AlarmReceiver
import com.comanch.valley_wind_awake.databinding.DetailFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()
    private val detailViewModel: DetailViewModel by viewModels()

    @Inject
    lateinit var navigation: NavigationBetweenFragments

    @Inject
    lateinit var preferences: DefaultPreference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: DetailFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.detail_fragment, container, false
        )

        binding.detailViewModel = detailViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val pauseDurationPreference = getPauseDurationPreference()
        val signalDurationPreference = getSignalDurationPreference()

        binding.delaySignal.text = setDelaySignalText(pauseDurationPreference)

        detailViewModel.startDelay(signalDurationPreference.toLong())
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        detailViewModel.navigateToList.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                stopPlayRingtone()
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                navigation.navigateToDestination(
                    this, DetailFragmentDirections.actionDetailFragmentToListFragment()
                )
            }
        }

        detailViewModel.setPause.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                val pauseIntent = createIntentAlarmReceiver(IntentKeys.pauseAlarm, args.itemId)
                context?.sendBroadcast(pauseIntent)
            }
        }

        detailViewModel.offAlarm.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                val offIntent = createIntentAlarmReceiver(IntentKeys.offAlarm, args.itemId)
                context?.sendBroadcast(offIntent)
            }
        }

        detailViewModel.stopPlay.observe(viewLifecycleOwner) { item ->
            item?.let {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        return binding.root
    }

    override fun onPause() {

        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onPause()
    }

    private fun stopPlayRingtone() {

        context?.startService(createIntentRingtoneService(IntentKeys.stopAction))
    }

    fun createIntentRingtoneService(actionKey: String): Intent{

        return Intent(requireContext(), RingtoneService::class.java).apply {
            action = actionKey
        }
    }

    fun createIntentAlarmReceiver(actionKey: String, timeId: Long): Intent{

        return Intent(context, AlarmReceiver::class.java).apply {
            action = actionKey
            putExtra(IntentKeys.timeId, timeId.toString())
        }
    }

    fun getPauseDurationPreference(): String{

        preferences.key = PreferenceKeys.pauseDuration
        return preferences.getString() ?: "5"
    }

    fun getSignalDurationPreference(): String{

        preferences.key = PreferenceKeys.signalDuration
        return preferences.getString() ?: "2"
    }

    private fun setDelaySignalText(pauseDuration: String): String {

        return "${resources.getString(R.string.delay_signal)} $pauseDuration ${
            resources.getString(R.string.min)
        }"
    }
}
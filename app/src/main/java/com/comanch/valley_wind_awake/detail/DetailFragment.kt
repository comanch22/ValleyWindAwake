package com.comanch.valley_wind_awake.detail

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import com.comanch.valley_wind_awake.IntentKeys
import com.comanch.valley_wind_awake.PreferenceKeys
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.broadcastreceiver.AlarmReceiver
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.databinding.DetailFragmentBinding

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: DetailFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.detail_fragment, container, false
        )

        val application = requireNotNull(this.activity).application
        val dataSource = DataControl.getInstance(application).timeDatabaseDao
        val viewModelFactory =
            DetailViewModelFactory(dataSource)
        val detailViewModel =
            ViewModelProvider(
                this, viewModelFactory
            )[DetailViewModel::class.java]

        val pauseDurationPreference = context?.let {
            PreferenceManager.getDefaultSharedPreferences(it)
                .getString(PreferenceKeys.pauseDuration, "5")
        } ?: "5"
        val text = "${resources.getString(R.string.delay_signal)} $pauseDurationPreference ${
            resources.getString(R.string.min)
        }"
        binding.delaySignal.text = text

        binding.detailViewModel = detailViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val signalDurationPreference = context?.let {
            PreferenceManager.getDefaultSharedPreferences(it)
                .getString(PreferenceKeys.signalDuration, "2")
        } ?: "2"
        detailViewModel.startDelay(signalDurationPreference.toLong())

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        detailViewModel.navigateToList.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                stopPlayRingtone()
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                this.findNavController()
                    .navigate(DetailFragmentDirections.actionDetailFragmentToListFragment())
            }
        }

        detailViewModel.setPause.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                val offIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = IntentKeys.pauseAlarm
                    putExtra(IntentKeys.timeId, (args.itemId).toString())
                }
                context?.sendBroadcast(offIntent)
            }
        }

        detailViewModel.offAlarm.observe(viewLifecycleOwner) { content ->
            content.getContentIfNotHandled()?.let {
                val offIntent = Intent(context, AlarmReceiver::class.java).apply {
                    action = IntentKeys.offAlarm
                    putExtra(IntentKeys.timeId, (args.itemId).toString())
                }
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
        val intent = Intent(context?.applicationContext, RingtoneService::class.java).apply {
            action = IntentKeys.stopAction
        }
        context?.startService(intent)
    }

}
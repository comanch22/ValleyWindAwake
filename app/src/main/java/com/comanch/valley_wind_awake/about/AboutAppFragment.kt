package com.comanch.valley_wind_awake.about


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.databinding.AboutAppFragmentBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutAppFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigateToDestination(AboutAppFragmentDirections.actionAboutAppFragmentToListFragment())
        }
        callback.isEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: AboutAppFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.about_app_fragment, container, false
        )

        val aboutAppViewModel: AboutAppViewModel by viewModels()

        binding.aboutAppViewModel = aboutAppViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        aboutAppViewModel.ossLicense.observe(viewLifecycleOwner) {
            startActivity(Intent(context, OssLicensesMenuActivity::class.java))
        }

        binding.arrowBackAboutApp.setOnClickListener {
            navigateToDestination(AboutAppFragmentDirections.actionAboutAppFragmentToListFragment())
        }
        return binding.root
    }

    private fun navigateToDestination(destination: NavDirections) = with(findNavController()) {
        currentDestination?.getAction(destination.actionId)
            ?.let { navigate(destination) }
    }
}
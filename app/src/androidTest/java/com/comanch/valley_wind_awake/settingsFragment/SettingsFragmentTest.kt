package com.comanch.valley_wind_awake.settingsFragment

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun check_pressBack() {

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
        }

        Espresso.pressBack()
        Assert.assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

}
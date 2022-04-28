package com.comanch.valley_wind_awake.settingsFragment

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.DefaultPreference
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import com.comanch.valley_wind_awake.stringKeys.AppStyleKey
import com.comanch.valley_wind_awake.stringKeys.PreferenceKeys
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SettingsFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var preferences: DefaultPreference

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun check_pressBack() {

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
        }

        pressBack()
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_ringtoneVolumeSelection() {

        var titleChoice = ""

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
            titleChoice = resources.getString(R.string.settings_ringtone_choice)
        }

        onView(withText(titleChoice)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    @Test
    fun check_appStyleSelect() {

        var titleAppStyle = ""
        var choiceStyle = ""
        var restartApp = ""
        var ok = ""
        val gray = AppStyleKey.gray
        val blue = AppStyleKey.blue
        val green = AppStyleKey.green

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            titleAppStyle = resources.getString(R.string.settings_app_style)
            choiceStyle = resources.getString(R.string.application_style)
            restartApp = resources.getString(R.string.restart_activity)
            ok = resources.getString(R.string.delete_ok)
        }

        onView(withText(titleAppStyle)).perform(click())
        onView(withText(choiceStyle)).check(matches(isDisplayed()))
        onView(withText(gray)).perform(click())
        onView(withText(restartApp)).check(matches(isDisplayed()))
        onView(withText(ok)).perform(click())

        assertEquals(AppStyleKey.gray, preferences.getString(AppStyleKey.appStyle))

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock)

        onView(withText(gray)).check(matches(isDisplayed()))
        onView(withText(titleAppStyle)).perform(click())
        onView(withText(choiceStyle)).check(matches(isDisplayed()))
        onView(withText(blue)).perform(click())
        onView(withText(restartApp)).check(matches(isDisplayed()))
        onView(withText(ok)).perform(click())

        assertEquals(AppStyleKey.blue, preferences.getString(AppStyleKey.appStyle))

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock)

        onView(withText(blue)).check(matches(isDisplayed()))
        onView(withText(titleAppStyle)).perform(click())
        onView(withText(choiceStyle)).check(matches(isDisplayed()))
        onView(withText(green)).perform(click())
        onView(withText(restartApp)).check(matches(isDisplayed()))
        onView(withText(ok)).perform(click())

        assertEquals(AppStyleKey.green, preferences.getString(AppStyleKey.appStyle))

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock)
        onView(withText(green)).check(matches(isDisplayed()))
    }

    @Test
    fun check_signalDuration() {

        var titleSignalDuration = ""
        var choiceSignalDuration = ""
        var titlePauseDuration = ""

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
            titleSignalDuration = resources.getString(R.string.signal_duration)
            choiceSignalDuration = resources.getString(R.string.choose_signal_duration)
            titlePauseDuration = resources.getString(R.string.pause_duration)
        }

        // a simple way to avoid matching values
        onView(withText(titlePauseDuration)).perform(click())
        onView(withText("30")).perform(click())
        //

        onView(withText(titleSignalDuration)).perform(click())
        onView(withText(choiceSignalDuration)).check(matches(isDisplayed()))
        onView(withText("1")).perform(click())
        assertEquals("1", preferences.getString(PreferenceKeys.signalDuration))
        onView(withText("1")).check(matches(isDisplayed()))

        onView(withText(titleSignalDuration)).perform(click())
        onView(withText(choiceSignalDuration)).check(matches(isDisplayed()))
        onView(withText("2")).perform(click())
        assertEquals("2", preferences.getString(PreferenceKeys.signalDuration))
        onView(withText("2")).check(matches(isDisplayed()))

        onView(withText(titleSignalDuration)).perform(click())
        onView(withText(choiceSignalDuration)).check(matches(isDisplayed()))
        onView(withText("3")).perform(click())
        assertEquals("3", preferences.getString(PreferenceKeys.signalDuration))
        onView(withText("3")).check(matches(isDisplayed()))

        onView(withText(titleSignalDuration)).perform(click())
        onView(withText(choiceSignalDuration)).check(matches(isDisplayed()))
        onView(withText("5")).perform(click())
        assertEquals("5", preferences.getString(PreferenceKeys.signalDuration))
        onView(withText("5")).check(matches(isDisplayed()))

        onView(withText(titleSignalDuration)).perform(click())
        onView(withText(choiceSignalDuration)).check(matches(isDisplayed()))
        onView(withText("7")).perform(click())
        assertEquals("7", preferences.getString(PreferenceKeys.signalDuration))
        onView(withText("7")).check(matches(isDisplayed()))

    }

    @Test
    fun check_pauseDuration() {

        var titleSignalDuration = ""
        var titlePauseDuration = ""
        var choicePauseDuration = ""

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
            titleSignalDuration = resources.getString(R.string.signal_duration)
            titlePauseDuration = resources.getString(R.string.pause_duration)
            choicePauseDuration = resources.getString(R.string.choose_pause_duration)
        }

        // a simple way to avoid matching values
        onView(withText(titleSignalDuration)).perform(click())
        onView(withText("1")).perform(click())
        //

        onView(withText(titlePauseDuration)).perform(click())
        onView(withText(choicePauseDuration)).check(matches(isDisplayed()))
        onView(withText("3")).perform(click())
        assertEquals("3", preferences.getString(PreferenceKeys.pauseDuration))
        onView(withText("3")).check(matches(isDisplayed()))

        onView(withText(titlePauseDuration)).perform(click())
        onView(withText(choicePauseDuration)).check(matches(isDisplayed()))
        onView(withText("5")).perform(click())
        assertEquals("5", preferences.getString(PreferenceKeys.pauseDuration))
        onView(withText("5")).check(matches(isDisplayed()))

        onView(withText(titlePauseDuration)).perform(click())
        onView(withText(choicePauseDuration)).check(matches(isDisplayed()))
        onView(withText("10")).perform(click())
        assertEquals("10", preferences.getString(PreferenceKeys.pauseDuration))
        onView(withText("10")).check(matches(isDisplayed()))

        onView(withText(titlePauseDuration)).perform(click())
        onView(withText(choicePauseDuration)).check(matches(isDisplayed()))
        onView(withText("15")).perform(click())
        assertEquals("15", preferences.getString(PreferenceKeys.pauseDuration))
        onView(withText("15")).check(matches(isDisplayed()))

        onView(withText(titlePauseDuration)).perform(click())
        onView(withText(choicePauseDuration)).check(matches(isDisplayed()))
        onView(withText("30")).perform(click())
        assertEquals("30", preferences.getString(PreferenceKeys.pauseDuration))
        onView(withText("30")).check(matches(isDisplayed()))

    }

    @Test
    fun check_isVibrate() {

        var titleIsVibrate = ""
        val isVibrate = preferences.getBoolean(PreferenceKeys.isVibrate)

        launchFragmentInHiltContainer<SettingsFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.settingsFragment)
            titleIsVibrate = resources.getString(R.string.vibration_signal)
        }

        onView(withText(titleIsVibrate)).perform(click())
        Thread.sleep(1000)
        assertEquals(!isVibrate, preferences.getBoolean(PreferenceKeys.isVibrate))
        if (isVibrate) {
            onView(withId(R.id.switchCustom)).check(matches(hasDescendant(isNotChecked())))
        }else{
            onView(withId(R.id.switchCustom)).check(matches(hasDescendant(isChecked())))
        }
    }
}
package com.comanch.valley_wind_awake.alarmFragment

import android.content.Intent
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.DefaultPreference
import com.comanch.valley_wind_awake.stringKeys.IntentKeys
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
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
class DetailFragmentAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var preferences: DefaultPreference

    @Before
    fun init() {
        hiltRule.inject()
    }

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    private val args by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
        }
    }

    @Test
    fun detailFragment_check_delay_signal_text() {

        preferences.key = PreferenceKeys.pauseDuration
        launchFragmentInHiltContainer<DetailFragment>(Bundle(), R.style.Theme_AppCompat)
        onView(withId(R.id.delay_signal)).check(matches(withText(
            "delay for ${preferences.getPreference()} min."
        )))
    }


    @Test
    fun detailFragment_Ok() {

        launchFragmentInHiltContainer<DetailFragment>(args, R.style.Theme_AppCompat) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.detailFragment, args)
        }

        onView(withId(R.id.item_ok)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_createIntentRingtoneService() {

        val expectedIntentAction = IntentKeys.stopAction
        var actualIntent: Intent? = null

        launchFragmentInHiltContainer<DetailFragment>(Bundle(), R.style.Theme_AppCompat) {
            this as DetailFragment
            actualIntent = createIntentRingtoneService(IntentKeys.stopAction)
        }
        assertEquals(expectedIntentAction, actualIntent?.action)

    }

    @Test
    fun check_intentPauseAlarm() {

        val expectedIntentAction = IntentKeys.pauseAlarm
        val expectedExtraString = "1"
        var actualIntent: Intent? = null

        launchFragmentInHiltContainer<DetailFragment>(Bundle(), R.style.Theme_AppCompat) {
            this as DetailFragment
            actualIntent = createIntentAlarmReceiver(IntentKeys.pauseAlarm, 1L)
        }
        assertEquals(expectedIntentAction, actualIntent?.action)
        assertEquals(expectedExtraString, actualIntent?.getStringExtra(IntentKeys.timeId))
    }

    @Test
    fun check_intentOffAlarm() {

        val expectedIntentAction = IntentKeys.offAlarm
        val expectedExtraString = "1"
        var actualIntent: Intent? = null
        launchFragmentInHiltContainer<DetailFragment>(Bundle(), R.style.Theme_AppCompat) {
            this as DetailFragment
            actualIntent = this.createIntentAlarmReceiver(IntentKeys.offAlarm, 1L)
        }
        assertEquals(expectedIntentAction, actualIntent?.action)
        assertEquals(expectedExtraString, actualIntent?.getStringExtra(IntentKeys.timeId))
    }
}
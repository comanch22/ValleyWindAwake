package com.comanch.valley_wind_awake.detail

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.comanch.valley_wind_awake.IntentKeys
import com.comanch.valley_wind_awake.R
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DetailFragmentAndroidTest {

    private lateinit var detailFragmentScenario: FragmentScenario<DetailFragment>
    private val navController by lazy { TestNavHostController(
        ApplicationProvider.getApplicationContext()
    ) }
    private val args by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
        }
    }

    @Before
    fun init() {

        detailFragmentScenario =
            launchFragmentInContainer(args, R.style.Theme_MyAlarmClock)
    }

    @After
    fun end() {

        detailFragmentScenario.close()
    }

    @Test
    fun detailFragment_check_delay_signal_text() {

        onView(ViewMatchers.withId(R.id.delay_signal)).check(matches(withText("delay for 5 min.")))
    }

    @Test
    fun detailFragment_Ok() {

        detailFragmentScenario.onFragment { fragment ->

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
            navController.setCurrentDestination(R.id.detailFragment, args)
        }

        onView(ViewMatchers.withId(R.id.item_ok)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_intentPauseAlarm(){

        val expectedIntentAction = IntentKeys.pauseAlarm
        val expectedExtraString = "1"
        var actualIntent: Intent? = null
        detailFragmentScenario.onFragment { fragment ->
            actualIntent = fragment.createIntent(IntentKeys.pauseAlarm, 1L)
        }
        assertEquals(expectedIntentAction, actualIntent?.action)
        assertEquals(expectedExtraString, actualIntent?.getStringExtra(IntentKeys.timeId))
    }

    @Test
    fun check_intentOffAlarm(){

        val expectedIntentAction = IntentKeys.offAlarm
        val expectedExtraString = "1"
        var actualIntent: Intent? = null
        detailFragmentScenario.onFragment { fragment ->
            actualIntent = fragment.createIntent(IntentKeys.offAlarm, 1L)
        }
        assertEquals(expectedIntentAction, actualIntent?.action)
        assertEquals(expectedExtraString, actualIntent?.getStringExtra(IntentKeys.timeId))
    }
}
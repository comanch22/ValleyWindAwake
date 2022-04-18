package com.comanch.valley_wind_awake.ringtonePickerCustomFragment

import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.aboutFragment.AboutAppFragment
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RingtoneCustomPickerFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }
    private val bundle: Bundle by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
            putString("ringtoneTitle", "ringtoneTitleTest")
        }
    }
    private val language: String? by lazy { setLanguage() }

    @Before
    fun init() {

    }

    @Test
    fun check_pressBack() {

        launchFragmentInHiltContainer<RingtoneCustomPickerFragment>(
            bundle,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtoneCustomPickerFragment, bundle)
        }
        Espresso.pressBack()
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    @Test
    fun check_arrowBack() {

        launchFragmentInHiltContainer<RingtoneCustomPickerFragment>(
            bundle,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtoneCustomPickerFragment, bundle)
        }
        onView(withId(R.id.arrow_back)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    @Test
    fun check_Cancel() {

        launchFragmentInHiltContainer<RingtoneCustomPickerFragment>(
            bundle,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtoneCustomPickerFragment, bundle)
        }
        onView(withId(R.id.Cancel)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    // need ringtone
    @Test
    fun check_Ok() {

        launchFragmentInHiltContainer<RingtoneCustomPickerFragment>(
            bundle,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtoneCustomPickerFragment, bundle)
        }
        Thread.sleep(2000)
        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Thread.sleep(2000)
        onView(withId(R.id.Ok)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    // need ringtone
    @Test
    fun check_currentRingTone() {

        var customRingtoneFragment: Fragment? = null

        launchFragmentInHiltContainer<RingtoneCustomPickerFragment>(
            bundle,
            R.style.Theme_AppCompat
        ) {
            customRingtoneFragment = this
        }

        Thread.sleep(2000)
        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Thread.sleep(2000)

        if (language == "ru_RU") {
            onView(withId(R.id.RingtoneList))
                .check(matches(hasDescendant(withContentDescription(" эта мелодия выбрана и играет "))))
        } else {
            onView(withId(R.id.RingtoneList))
                .check(matches(hasDescendant(withContentDescription(" this melody is selected and is playing "))))
        }
        val musicPlay = (customRingtoneFragment as RingtoneCustomPickerFragment)
            .mService?.isPlaying()

        assertEquals(true, musicPlay)

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Thread.sleep(1000)

        if (language == "ru_RU") {
            onView(withId(R.id.RingtoneList))
                .check(matches(not(hasDescendant(withContentDescription(" эта мелодия выбрана и играет ")))))
        } else {
            onView(withId(R.id.RingtoneList))
                .check(matches(not(hasDescendant(withContentDescription(" this melody is selected and is playing ")))))
        }
        val musicNoPlay = (customRingtoneFragment as RingtoneCustomPickerFragment)
            .mService?.isPlaying()

        assertEquals(false, musicNoPlay)
    }

    private fun setLanguage(): String? {

        val localeList = Resources.getSystem().configuration.locales
        return if (localeList.size() > 0) {
            localeList[0].toString()
        } else {
            null
        }
    }

}
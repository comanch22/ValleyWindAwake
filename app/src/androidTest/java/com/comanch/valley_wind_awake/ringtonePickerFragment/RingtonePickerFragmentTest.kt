package com.comanch.valley_wind_awake.ringtonePickerFragment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.aboutFragment.AboutAppFragment
import com.comanch.valley_wind_awake.alarmManagement.RingtoneService
import com.comanch.valley_wind_awake.keyboardFragment.Correspondent
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class RingtonePickerFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    private val bundleKeyboardFragment: Bundle by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
            putString("ringtoneTitle", "ringtoneTitleTest")
            putSerializable("correspondent", Correspondent.KeyboardFragment)
        }
    }

    private val bundleSettingsFragment: Bundle by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
            putString("ringtoneTitle", "ringtoneTitleTest")
            putSerializable("correspondent", Correspondent.SettingsFragment)
        }
    }

    private val language: String? by lazy { setLanguage() }

    @Test
    fun check_pressBack() {

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleKeyboardFragment)
        }

        Espresso.pressBack()
        assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)
    }

    @Test
    fun check_arrowBack() {

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleKeyboardFragment)
        }
        onView(withId(R.id.arrow_back)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleSettingsFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleSettingsFragment)
        }
        onView(withId(R.id.arrow_back)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.settingsFragment)
    }

    @Test
    fun check_Cancel() {

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleKeyboardFragment)
        }
        onView(withId(R.id.Cancel)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleSettingsFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleSettingsFragment)
        }
        onView(withId(R.id.Cancel)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.settingsFragment)
    }

    @Test
    fun check_Ok_button() {

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleKeyboardFragment)
        }

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )

        onView(withId(R.id.Ok)).perform(ViewActions.click())
        assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleSettingsFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleSettingsFragment)
        }

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )

        onView(withId(R.id.Ok)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.settingsFragment)
    }

    @Test
    fun check_tapToRingtone() {

        var ringtoneFragment: Fragment? = null

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            ringtoneFragment = this
        }

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
                .check(matches(hasDescendant(withContentDescription(" эта мелодия выбрана и играет "))))
        } else {
            onView(withId(R.id.RingtoneList))
                .check(matches(hasDescendant(withContentDescription(" this melody is selected and is playing "))))
        }
        val musicPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()

        assertEquals(true, musicPlay)

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
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
        val musicNoPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()

        assertEquals(false, musicNoPlay)
    }

    @Test
    fun check_chooseRingtone() {

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.ringtonePickerFragment, bundleKeyboardFragment)
        }

        onView(withId(R.id.fabAdd)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.ringtoneCustomPickerFragment)
    }

    // need custom ringtone
    @Test
    fun check_deleteRingtone() {

        var ringtoneFragment: Fragment? = null
        var listSizeBefore: Int? = null
        var listSizeAfter: Int? = null
        var cannotDelete = ""

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            ringtoneFragment = this
            cannotDelete = resources.getString(R.string.cannot_deleted)
        }
        Thread.sleep(1000)

        val itemCountBefore: Int = (ringtoneFragment as RingtonePickerFragment).adapter.itemCount
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {

            listSizeBefore = (ringtoneFragment as RingtonePickerFragment)
                .ringtonePickerViewModel
                .database
                .getListItems()
                ?.size
        }
        Thread.sleep(2000)

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
                .check(matches(hasDescendant(withContentDescription(" эта мелодия выбрана и играет "))))
        } else {
            onView(withId(R.id.RingtoneList))
                .check(matches(hasDescendant(withContentDescription(" this melody is selected and is playing "))))
        }
        val musicPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()
        assertEquals(true, musicPlay)

        onView(withId(R.id.fabDelete)).perform(click())
        Thread.sleep(1000)

        val musicNoPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()
        assertEquals(false, musicNoPlay)

        mCoroutineScope.launch {

            listSizeAfter = (ringtoneFragment as RingtonePickerFragment)
                .ringtonePickerViewModel
                .database
                .getListItems()
                ?.size
        }
        Thread.sleep(2000)

        if (listSizeAfter == null
            || listSizeBefore?.minus(1) ?: -1 != listSizeAfter
        ) {
            onView(withText(cannotDelete))
                .inRoot(RootMatchers.withDecorView(not(ringtoneFragment?.activity?.window?.decorView)))
                .check(matches(isDisplayed()))
        } else {
            val itemCountAfter: Int = (ringtoneFragment as RingtonePickerFragment).adapter.itemCount
            assertEquals(itemCountBefore - 1, itemCountAfter)
        }
    }

    @Test
    fun check_toast() {

        var ringtoneFragment: Fragment? = null
        var cannotDelete = ""
        var chooseRingtone = ""

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            ringtoneFragment = this as RingtonePickerFragment
            cannotDelete = resources.getString(R.string.cannot_deleted)
            chooseRingtone = resources.getString(R.string.choose_a_ringtone)
        }
        Thread.sleep(1000)

        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {

            (ringtoneFragment as RingtonePickerFragment)
                .ringtonePickerViewModel
                .database
                .clear()
        }
        Thread.sleep(2000)

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.click()
                )
            )
        Thread.sleep(1000)

        onView(withId(R.id.fabDelete)).perform(click())
        onView(withText(cannotDelete))
            .inRoot(RootMatchers.withDecorView(not(ringtoneFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))

        Thread.sleep(3000)

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        Thread.sleep(1000)

        onView(withId(R.id.Ok)).perform(click())
        onView(withText(chooseRingtone))
            .inRoot(RootMatchers.withDecorView(not(ringtoneFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))
        Thread.sleep(5000)

        onView(withId(R.id.fabDelete)).perform(click())
        onView(withText(chooseRingtone))
            .inRoot(RootMatchers.withDecorView(not(ringtoneFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun check_seekBar() {

        var ringtoneFragment: Fragment? = null

        launchFragmentInHiltContainer<RingtonePickerFragment>(
            bundleKeyboardFragment,
            R.style.Theme_AppCompat
        ) {
            ringtoneFragment = this
        }
        onView(withId(R.id.seekbar)).perform(click())
        Thread.sleep(1000)

        val musicPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()
        assertEquals(true, musicPlay)

        onView(withId(R.id.seekbar)).perform(setSeekBarProgress(5))
        Thread.sleep(2000)

        onView(withText("5")).check(matches(isDisplayed()))
        assertEquals(
            5, (ringtoneFragment as RingtonePickerFragment)
                .mService?.getVolume()
        )

        onView(withId(R.id.seekbar)).perform(setSeekBarProgress(2))
        Thread.sleep(2000)

        onView(withText("2")).check(matches(isDisplayed()))
        assertEquals(
            2, (ringtoneFragment as RingtonePickerFragment)
                .mService?.getVolume()
        )

        onView(withId(R.id.seekbar)).perform(swipeRight())
        Thread.sleep(1000)

        onView(withId(R.id.RingtoneList))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        Thread.sleep(2000)

        val musicNoPlay = (ringtoneFragment as RingtonePickerFragment)
            .mService?.isPlaying()
        assertEquals(false, musicNoPlay)

        assertEquals(
            7, (ringtoneFragment as RingtonePickerFragment)
                .mService?.getVolume()
        )

    }

    private fun setSeekBarProgress(progress: Int) = object : ViewAction {

        override fun getConstraints(): Matcher<View?>? {
            return isAssignableFrom(SeekBar::class.java)
        }

        override fun getDescription() = ""

        override fun perform(uiController: UiController?, view: View) {
            val seekBar = view as SeekBar
            seekBar.progress = progress
        }
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
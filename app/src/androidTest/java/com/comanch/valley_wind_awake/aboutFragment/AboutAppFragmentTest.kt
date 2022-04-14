package com.comanch.valley_wind_awake.aboutFragment

import android.os.Build
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AboutAppFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    @Test
    fun aboutAppFragment_arrow_back() {

        launchFragmentInHiltContainer<AboutAppFragment>(Bundle(), R.style.Theme_AppCompat) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.aboutAppFragment)
        }

        onView(withId(R.id.arrow_back_about_app)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun aboutAppFragment_pressBack() {

        launchFragmentInHiltContainer<AboutAppFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.aboutAppFragment)
        }

        pressBack()
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun aboutAppFragment_oss_license_click() {

        if (Build.VERSION.SDK_INT >= 29) {
            launchFragmentInHiltContainer<AboutAppFragment>(Bundle(), R.style.Theme_MyAlarmClock)
            onView(withId(R.id.oss_license)).perform(click())
            onView(withText("Open source licenses")).check(matches(isDisplayed()))
        }
    }
}
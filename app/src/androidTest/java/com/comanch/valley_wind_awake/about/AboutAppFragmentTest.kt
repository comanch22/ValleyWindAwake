package com.comanch.valley_wind_awake.about

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
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

    @Before
    fun init() {

        Intents.init()
    }

    @After
    fun end() {

        Intents.release()
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

        launchFragmentInHiltContainer<AboutAppFragment>(Bundle(), R.style.Theme_MyAlarmClock) {

            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.aboutAppFragment)
        }

        onView(withId(R.id.oss_license)).perform(click())
        intended(hasComponent(OssLicensesMenuActivity::class.java.name))
    }
}
package com.comanch.valley_wind_awake.frontListFragment

import android.os.Bundle
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.DefaultPreference
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.alarmManagement.AlarmControl
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ListFragmentAndroidTest {

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

    @Test
    fun checkNearestDate() {

        var actualNearestDateStr = ""
        var actualToolbarTitle = ""
        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {

            (this as ListFragment).listViewModel.setNearestDate(true)
            val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
            mCoroutineScope.launch {
                val list =
                    (this@launchFragmentInHiltContainer).listViewModel.database.getListItems()
                if (list != null && list.isNotEmpty()) {
                    list.filter { it.active }.let { listF ->
                        if (listF.isNotEmpty()) {
                            listF.sortedBy {
                                it.nearestDate
                            }.let {
                                actualNearestDateStr = it[0].nearestDateStr
                                actualToolbarTitle = resources.getString(R.string.the_nearest_signal)
                            }
                        }
                    }
                }
            }
        }
        onView(withId(R.id.toolbar_title)).check(matches(withText(actualToolbarTitle)))
        if (actualToolbarTitle.isEmpty()) {
            onView(withId(R.id.toolbar_title)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        } else {
            onView(withId(R.id.toolbar_title)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        }
        onView(withId(R.id.toolbar_nearestDate)).check(matches(withText(actualNearestDateStr)))
    }

    @Test
    fun check_navigateToKeyboardFragment(){

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.listFragment)
        }

        onView(withId(R.id.itemLayout)).perform(click())
        Assert.assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)
    }

    @Test
    fun check_deleteAllItems(){

        var listFragment: Fragment? = null
        var actualItemsCount: Int? = null
        var actionDone = ""
        var ok = ""
            launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat){
                listFragment = this
                actionDone = resources.getString(R.string.delete_all)
                ok = resources.getString(R.string.delete_ok)
            }

        openActionBarOverflowOrOptionsMenu(ApplicationProvider.getApplicationContext())
        onView(withText(actionDone)).perform(click())
        onView(withText(ok))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click());
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {
            actualItemsCount =
                (listFragment as ListFragment).listViewModel.database.getListItems()?.size ?: 0
            Assert.assertEquals(0, actualItemsCount)
        }
    }


}
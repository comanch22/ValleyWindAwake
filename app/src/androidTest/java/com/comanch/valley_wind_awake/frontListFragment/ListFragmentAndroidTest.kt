package com.comanch.valley_wind_awake.frontListFragment

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.comanch.valley_wind_awake.DateDifference
import com.comanch.valley_wind_awake.R
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.*
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import javax.inject.Inject

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class ListFragmentAndroidTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: TimeDataDao

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    private val language: String? by lazy { setLanguage() }

    @Before
    fun init() {

        hiltRule.inject()
    }

    @After
    fun end() {
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {
            database.clear()
        }
    }

    @Test
    fun check_navigateToKeyboardFragment() {

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.listFragment)
        }

        onView(withId(R.id.ButtonPlus)).perform(click())
        Thread.sleep(1000)

        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    click()
                )
            )
        assertEquals(navController.currentDestination?.id, R.id.keyboardFragment)
    }

    @Test
    fun check_toAboutAppNavigation() {

        var aboutApp = ""

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            aboutApp = resources.getString(R.string.about_app)
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.listFragment)
        }
        openContextualActionModeOverflowMenu()

        onView(withText(aboutApp)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.aboutAppFragment)
    }

    @Test
    fun check_toSettingsNavigation() {

        var settings = ""

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            settings = resources.getString(R.string.settings)
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.listFragment)
        }
        openContextualActionModeOverflowMenu()

        onView(withText(settings)).perform(click())

        assertEquals(navController.currentDestination?.id, R.id.settingsFragment)
    }

    @Test
    fun check_deleteAllItems() {

        var actualItemsCount: Int = -1
        var actionDone = ""
        var ok = ""
        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            actionDone = resources.getString(R.string.delete_all)
            ok = resources.getString(R.string.delete_ok)
        }

        onView(withId(R.id.ButtonPlus)).perform(click())
        onView(withId(R.id.ButtonPlus)).perform(click())

        openContextualActionModeOverflowMenu()

        onView(withText(actionDone)).perform(click())
        onView(withText(ok))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {
            actualItemsCount = database.getListItems()?.size ?: 0
        }
        Thread.sleep(2000)
        onView(withId(R.id.toolbar_title)).check(matches(withEffectiveVisibility(Visibility.INVISIBLE)))
        onView(withId(R.id.toolbar_nearestDate)).check(matches(withText("")))
        assertEquals(0, actualItemsCount)
    }

    @Test
    fun check_insertItem() {

        var itemsCount: Int = -1
        var itemsCountActual: Int = -1
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat)

        mCoroutineScope.launch {
            itemsCount = database.getListItems()?.size ?: 0
        }
        Thread.sleep(2000)

        onView(withId(R.id.ButtonPlus)).perform(click())
        onView(withId(R.id.ButtonPlus)).perform(click())
        onView(withId(R.id.ButtonPlus)).perform(click())

        mCoroutineScope.launch {
            itemsCountActual = database.getListItems()?.size ?: 0
        }
        Thread.sleep(2000)
        assertEquals(itemsCount + 3, itemsCountActual)
    }

    @Test
    fun check_deleteOneItem() {

        var itemsCount: Int = -1
        var itemsCountActual: Int = -1
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat)

        onView(withId(R.id.ButtonPlus)).perform(click())
        if (language == "ru_RU") {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" будильник выключен. "))))
        } else {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" the alarm is off. "))))
        }

        mCoroutineScope.launch {
            itemsCount = database.getListItems()?.size ?: 0
        }
        Thread.sleep(2000)
        onView(withId(R.id.ButtonDelete)).perform(click())
        onView(withId(R.id.ButtonDone)).check(matches(isDisplayed()))

        if (language == "ru_RU") {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" удалить будильник из списка. "))))
        } else {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" remove the alarm from the list. "))))
        }
        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickElementOnView(R.id.deleteItem)
                )
            )
        mCoroutineScope.launch {
            itemsCountActual = database.getListItems()?.size ?: 0
        }
        Thread.sleep(2000)

        onView(withId(R.id.ButtonDone)).perform(click())
        onView(withId(R.id.ButtonDone)).check(matches(not(isDisplayed())))
        assertEquals(itemsCount - 1, itemsCountActual)
    }

    @Test
    fun check_switchActiveItem() {

        var listFragment: Fragment? = null
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        var item: TimeData?
        var active: Boolean? = true
        var activeActual: Boolean? = false
        var toastOff = ""
        var toastOn: String
        var days = ""
        var hours = ""
        var min = ""
        var nearestDate: Long?

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            listFragment = this
            toastOff = resources.getString(R.string.alarm_is_off)
            days = resources.getString(R.string.days)
            hours = resources.getString(R.string.hours)
            min = resources.getString(R.string.min)
        }
        Thread.sleep(2000)
        onView(withId(R.id.ButtonPlus)).perform(click())
        Thread.sleep(1000)
        if (language == "ru_RU") {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" будильник выключен. "))))
        } else {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" the alarm is off. "))))
        }
        mCoroutineScope.launch {
            item = database.getItem()
            active = item?.active
        }
        Thread.sleep(2000)
        assertEquals(false, active)

        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickElementOnView(R.id.switch_active)
                )
            )

        if (language == "ru_RU") {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" будильник включен. "))))
        } else {
            onView(withId(R.id.list)).check(matches(hasDescendant(withContentDescription(" the alarm is on. "))))
        }
        mCoroutineScope.launch {
            item = database.getItem()
            activeActual = item?.active
            nearestDate = item?.nearestDate

            toastOn = DateDifference().getResultString(
                nearestDate?.minus(Calendar.getInstance().timeInMillis) ?: 0,
                days,
                hours,
                min
            )

            onView(withText(toastOn))
                .inRoot(withDecorView(not(listFragment?.activity?.window?.decorView)))
                .check(matches(isDisplayed()))
        }
        Thread.sleep(2000)

        assertEquals(true, activeActual)
        onView(withId(R.id.toolbar_title)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.toolbar_nearestDate)).check(matches(not(withText(""))))

        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickElementOnView(R.id.switch_active)
                )
            )

        onView(withText(toastOff))
            .inRoot(withDecorView(not(listFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun check_NearestDate() {

        var actualNearestDateStr = ""
        var actualToolbarTitle = ""
        var listFragment: ListFragment? = null

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            listFragment = this as ListFragment
            actualToolbarTitle = resources.getString(R.string.the_nearest_signal)
        }

        onView(withId(R.id.ButtonPlus)).perform(click())
        Thread.sleep(1000)
        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickElementOnView(R.id.switch_active)
                )
            )
        Thread.sleep(1000)
        listFragment?.listViewModel?.setNearestDate(true)
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        mCoroutineScope.launch {
            val list = database.getListItems()
            if (list != null && list.isNotEmpty()) {
                list.filter { it.active }.let { listF ->
                    if (listF.isNotEmpty()) {
                        listF.sortedBy {
                            it.nearestDate
                        }.let {
                            actualNearestDateStr = it[0].nearestDateStr
                        }
                    }
                }
            }
        }
        Thread.sleep(2000)

        onView(withId(R.id.toolbar_title)).check(matches(withText(actualToolbarTitle)))
        onView(withId(R.id.toolbar_title)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
        onView(withId(R.id.toolbar_nearestDate)).check(matches(withText(actualNearestDateStr)))
    }

    @Test
    fun check_SpecialDate() {

        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat)

        onView(withId(R.id.ButtonPlus)).perform(click())

        mCoroutineScope.launch {
            val item = database.getItem()
            item?.specialDateStr = "22.22.2222"
            database.update(item!!)
        }

        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickElementOnView(R.id.switch_active)
                )
            )
        Thread.sleep(1000)
        onView(withText("22.22.2222")).check(matches(isDisplayed()))

    }

    @Test
    fun check_arrowBack() {

        var listFragment: Fragment? = null

        launchFragmentInHiltContainer<ListFragment>(Bundle(), R.style.Theme_AppCompat) {
            listFragment = this
        }
        Thread.sleep(1000)
        onView(withId(R.id.arrow_back)).perform(click())
        assertEquals(true, listFragment?.activity?.isFinishing)
    }


    private fun clickElementOnView(viewId: Int) = object : ViewAction {

        override fun getConstraints() = null

        override fun getDescription() = ""

        override fun perform(uiController: UiController, view: View) =
            click().perform(uiController, view.findViewById(viewId))
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
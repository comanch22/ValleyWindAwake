package com.comanch.valley_wind_awake.keyboardFragment

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.comanch.valley_wind_awake.*
import com.comanch.valley_wind_awake.alarmManagement.AlarmControl
import com.comanch.valley_wind_awake.dataBase.TimeData
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import com.comanch.valley_wind_awake.viewTags.ViewTags
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@LargeTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class KeyboardFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var activityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    private val navController by lazy {
        TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
    }

    private val bundleListFragment: Bundle by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
            putSerializable("correspondent", Correspondent.ListFragment)
            putString("ringtoneUri", "")
            putString("ringtoneTitle", "")
        }
    }

    private val bundleRingtoneFragment: Bundle by lazy {
        Bundle().apply {
            putLong("itemId", 1L)
            putSerializable("correspondent", Correspondent.RingtoneFragment)
            putString(
                "ringtoneUri",
                "content://media/internal/audio/media/70?title=Beat%20Plucker&canonical=1"
            )
            putString("ringtoneTitle", "ringtoneTitleTest")
        }
    }


    @Inject
    lateinit var alarmControl: AlarmControl

    @Inject
    lateinit var preferences: DefaultPreference

    @Inject
    lateinit var database: TimeDataDao

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
    fun check_calendar() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        )

        val actualDate = SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time)

        onView(withText("2")).perform(click())
        onView(withText("3")).perform(click())
        onView(withText("5")).perform(click())
        onView(withText("9")).perform(click())
        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withText(actualDate)).check(matches(isDisplayed()))

    }

    @Test
    fun check_selectTrack() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }
        onView(withId(R.id.textViewSelectTrack)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.ringtonePickerFragment)
    }

    @Test
    fun check_arrowBack() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }
        onView(withId(R.id.arrow_back)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_pressBack() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }
        pressBack()
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_cancel() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }
        onView(withId(R.id.textViewKeyCancel)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)
    }

    @Test
    fun check_save() {

        var keyFragment: Fragment? = null
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        var itemId = 1L

        var days = ""
        var hours = ""
        var min = ""

        mCoroutineScope.launch {

            database.insert(TimeData())
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            keyFragment = this
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
            days = resources.getString(R.string.days)
            hours = resources.getString(R.string.hours)
            min = resources.getString(R.string.min)
        }

        mCoroutineScope.launch {
            val item = (keyFragment as KeyboardFragment).keyboardViewModel.database.getItem()
            (keyFragment as KeyboardFragment).keyboardViewModel.localItemId = item!!.timeId
        }
        Thread.sleep(2000)
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeyEight)).perform(click())
        onView(withId(R.id.textViewKeySave)).perform(click())
        assertEquals(navController.currentDestination?.id, R.id.listFragment)

        val s1 = "2"
        val s2 = "3"
        val s3 = "5"
        val s4 = "8"
        var s1actual = "0"
        var s2actual = "0"
        var s3actual = "0"
        var s4actual = "0"

        var nearestDate = 0L

        mCoroutineScope.launch {
            val item = database.getItem()
            s1actual = item!!.s1
            s2actual = item.s2
            s3actual = item.s3
            s4actual = item.s4
            nearestDate = item.nearestDate
        }
        Thread.sleep(1000)

        val toastOn = DateDifference().getResultString(
            nearestDate.minus(Calendar.getInstance().timeInMillis),
            days,
            hours,
            min
        )

        onView(withText(toastOn))
            .inRoot(RootMatchers.withDecorView(not(keyFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))

        Thread.sleep(2000)
        assertEquals(s1, s1actual)
        assertEquals(s2, s2actual)
        assertEquals(s3, s3actual)
        assertEquals(s4, s4actual)
        Thread.sleep(2000)

        mCoroutineScope.launch {

            database.insert(TimeData())
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)

        var timeIsOver = ""
        var timeSpecialDateisOver = ""

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            keyFragment = this
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
            timeIsOver = resources.getString(R.string.time_is_over)
            timeSpecialDateisOver = resources.getString(R.string.incorrect_special_date)
        }

        mCoroutineScope.launch {
            val item = database.getItem()
            (keyFragment as KeyboardFragment).keyboardViewModel.localItemId = item!!.timeId
        }
        Thread.sleep(2000)

        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())

        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.textViewKeySave)).perform(click())
        Thread.sleep(1000)

        onView(withText(timeIsOver))
            .inRoot(RootMatchers.withDecorView(not(keyFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))

        Thread.sleep(4000)

        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.textViewKeySave)).perform(click())
        Thread.sleep(4000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            keyFragment = this
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }
        Thread.sleep(1000)

        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeySave)).perform(click())
        Thread.sleep(1000)

        onView(withText(timeSpecialDateisOver))
            .inRoot(RootMatchers.withDecorView(not(keyFragment?.activity?.window?.decorView)))
            .check(matches(isDisplayed()))

    }

    @Test
    fun check_ringtoneTitle() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleRingtoneFragment,
            R.style.Theme_AppCompat
        )
        Thread.sleep(2000)
        onView(withText("ringtoneTitleTest")).check(matches(isDisplayed()))

    }

    @Test
    fun check_testNumbers24Hours() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        )

        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeyOne)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())

        onView(withId(R.id.textViewNumberOne)).check(matches(withText("0")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))

        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFour)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeySix)).perform(click())
        onView(withId(R.id.textViewKeySeven)).perform(click())
        onView(withId(R.id.textViewKeyEight)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.textViewNumberOne)).check(matches(withText("0")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))

        onView(withId(R.id.textViewKeyOne)).perform(click())
        onView(withId(R.id.textViewKeyFour)).perform(click())

        onView(withId(R.id.textViewKeySix)).perform(click())
        onView(withId(R.id.textViewKeySeven)).perform(click())
        onView(withId(R.id.textViewKeyEight)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("4")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))

        onView(withId(R.id.textViewNumberThree)).perform(click())

        onView(withId(R.id.textViewKeySix)).perform(click())
        onView(withId(R.id.textViewKeySeven)).perform(click())
        onView(withId(R.id.textViewKeyEight)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("4")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))

        onView(withId(R.id.textViewNumberThree)).perform(click())

        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeySix)).perform(click())

        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("4")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("5")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("6")))
    }

    @Test
    fun check_daysOfWeek() {

        launchFragmentInHiltContainer<KeyboardFragment>(
            bundleListFragment,
            R.style.Theme_AppCompat
        )

        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewMonday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewMonday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewTuesday)).perform(click())
        onView(withId(R.id.textViewTuesday)).perform(click())
        onView(withId(R.id.textViewTuesday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewTuesday)).perform(click())
        onView(withId(R.id.textViewTuesday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewWednesday)).perform(click())
        onView(withId(R.id.textViewWednesday)).perform(click())
        onView(withId(R.id.textViewWednesday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewWednesday)).perform(click())
        onView(withId(R.id.textViewWednesday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewThursday)).perform(click())
        onView(withId(R.id.textViewThursday)).perform(click())
        onView(withId(R.id.textViewThursday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewThursday)).perform(click())
        onView(withId(R.id.textViewThursday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewFriday)).perform(click())
        onView(withId(R.id.textViewFriday)).perform(click())
        onView(withId(R.id.textViewFriday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewFriday)).perform(click())
        onView(withId(R.id.textViewFriday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewSaturday)).perform(click())
        onView(withId(R.id.textViewSaturday)).perform(click())
        onView(withId(R.id.textViewSaturday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewSaturday)).perform(click())
        onView(withId(R.id.textViewSaturday)).check(matches(withTag(ViewTags.dayOff)))

        onView(withId(R.id.textViewSunday)).perform(click())
        onView(withId(R.id.textViewSunday)).perform(click())
        onView(withId(R.id.textViewSunday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewSunday)).perform(click())
        onView(withId(R.id.textViewSunday)).check(matches(withTag(ViewTags.dayOff)))
    }

    @Test
    fun check_addItem() {

        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        var itemId = 1L

        val s1 = "0"
        val s2 = "7"
        val s3 = "3"
        val s4 = "4"
        var s1actual = "0"
        var s2actual = "0"
        var s3actual = "0"
        var s4actual = "0"
        var monday = false
        var tuesday = false
        var wednesday = false
        var thursday = false
        var friday = false
        var saturday = false
        var sunday = false

        var hhmm12 = "1200"
        var hhmm24 = "0000"
        var ampm = "AM"

        var specialDate = 0L
        var specialDateStr = ""
        var nearestDate = 0L
        var delayTime = 0L
        var nearestDateStr = ""
        var nearestDateStr12 = ""
        var active = false
        var oneInstance = false
        var ringtoneUri = ""
        var contentDescriptionRu12 = ""
        var contentDescriptionEn12 = ""
        var contentDescriptionRu24 = ""
        var contentDescriptionEn24 = ""
        var ringtoneTitle = ""

        mCoroutineScope.launch {

            database.insert(TimeData())
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.RingtoneFragment)
                putString(
                    "ringtoneUri",
                    "content://media/internal/audio/media/70?title=Beat%20Plucker&canonical=1"
                )
                putString("ringtoneTitle", "ringtoneTitleTest")
            },
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
        }

        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())

        onView(withId(R.id.textViewKeySave)).perform(click())

        mCoroutineScope.launch {
            val item = database.get(itemId)
            oneInstance = item!!.oneInstance
            nearestDate = item.nearestDate
            nearestDateStr = item.nearestDateStr
            nearestDateStr12 = item.nearestDateStr12
            contentDescriptionRu12 = item.contentDescriptionRu12
            contentDescriptionEn12 = item.contentDescriptionEn12

        }
        Thread.sleep(2000)

        val actualCalendar = Calendar.getInstance()
        actualCalendar.set(Calendar.HOUR_OF_DAY, 22)
        actualCalendar.set(Calendar.MINUTE, 22)
        actualCalendar.set(Calendar.SECOND, 0)
        actualCalendar.clear(Calendar.MILLISECOND)

        val simpleDate12 = SimpleDateFormat(
            "dd-MM-yyyy hh:mm a", Locale.US
        ).format(actualCalendar.time)

        assertEquals(actualCalendar.timeInMillis, nearestDate)
        assertEquals(
            SimpleDateFormat(
                "dd-MM-yyyy HH:mm", Locale.US
            ).format(actualCalendar.time), nearestDateStr
        )
        assertEquals(
            simpleDate12, nearestDateStr12
        )
        assertEquals(
            "будильник из списка. время будильника 10 часов 22 минут двенадцатичасовой формат P. M. .  отмечены дни недели. . ближайщий сигнал этого будильника. $simpleDate12",
            contentDescriptionRu12
        )
        assertEquals(
            "alarm clock from the list. alarm clock time 10 hours 22 minutes twelve - hour format P. M. .  the days of the week are marked. . the nearest of this alarm clock. $simpleDate12",
            contentDescriptionEn12
        )
        assertEquals(true, oneInstance)

        onView(withId(R.id.textViewKeyZero)).perform(click())
        onView(withId(R.id.textViewKeySeven)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFour)).perform(click())

        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewWednesday)).perform(click())
        onView(withId(R.id.textViewWednesday)).perform(click())
        onView(withId(R.id.textViewSunday)).perform(click())
        onView(withId(R.id.textViewSunday)).perform(click())

        onView(withId(R.id.textViewKeySave)).perform(click())

        mCoroutineScope.launch {
            val item = database.get(itemId)
            s1actual = item!!.s1
            s2actual = item.s2
            s3actual = item.s3
            s4actual = item.s4
            monday = item.mondayOn
            tuesday = item.tuesdayOn
            wednesday = item.wednesdayOn
            thursday = item.thursdayOn
            friday = item.fridayOn
            saturday = item.saturdayOn
            sunday = item.sundayOn
            hhmm12 = item.hhmm12
            hhmm24 = item.hhmm24
            ampm = item.ampm
            active = item.active
            oneInstance = item.oneInstance
            nearestDateStr = item.nearestDateStr
            contentDescriptionRu24 = item.contentDescriptionRu24
            contentDescriptionEn24 = item.contentDescriptionEn24
        }

        Thread.sleep(2000)
        assertEquals(s1, s1actual)
        assertEquals(s2, s2actual)
        assertEquals(s3, s3actual)
        assertEquals(s4, s4actual)
        assertEquals(true, monday)
        assertEquals(false, tuesday)
        assertEquals(true, wednesday)
        assertEquals(false, thursday)
        assertEquals(false, friday)
        assertEquals(false, saturday)
        assertEquals(true, sunday)
        assertEquals("0734", hhmm12)
        assertEquals("0734", hhmm24)
        assertEquals("AM", ampm)
        assertEquals(true, active)
        assertEquals(false, oneInstance)
        assertEquals(
            "будильник из списка. время будильника 07 часов 34 минут .  отмечены дни недели.  понедельник.  среда.  воскресенье. . ближайщий сигнал этого будильника. $nearestDateStr",
            contentDescriptionRu24
        )
        assertEquals(
            "alarm clock from the list. alarm clock time 07 hours 34 minutes .  the days of the week are marked.  monday.  wednesday.  sunday. . the nearest of this alarm clock. $nearestDateStr",
            contentDescriptionEn24
        )

        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        actualCalendar.set(Calendar.HOUR_OF_DAY, 23)
        actualCalendar.set(Calendar.MINUTE, 59)
        actualCalendar.set(Calendar.SECOND, 0)
        actualCalendar.clear(Calendar.MILLISECOND)

        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())
        val actualDate = SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time)

        onView(withId(R.id.textViewKeySave)).perform(click())

        mCoroutineScope.launch {
            val item = database.get(itemId)
            hhmm12 = item!!.hhmm12
            hhmm24 = item.hhmm24
            ampm = item.ampm
            specialDate = item.specialDate
            specialDateStr = item.specialDateStr
            alarmControl.delaySignal(item)
            delayTime = item.delayTime
            ringtoneUri = item.ringtoneUri
            ringtoneTitle = item.ringtoneTitle
        }
        Thread.sleep(2000)

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.clear(Calendar.MILLISECOND)
        val delayTimeExpected =
            calendar.timeInMillis + preferences.getString("pauseDuration").toLong() * 60000

        assertEquals("1159", hhmm12)
        assertEquals("2359", hhmm24)
        assertEquals("PM", ampm)
        assertEquals(actualDate, specialDateStr)
        assertEquals(actualCalendar.timeInMillis, specialDate)
        assertEquals(
            "content://media/internal/audio/media/70?title=Beat%20Plucker&canonical=1",
            ringtoneUri
        )
        assertEquals("ringtoneTitleTest", ringtoneTitle)
        assertEquals(delayTimeExpected, delayTime)
    }

    @Test
    fun check_changeHourFormat() {

        var keyFragment: Fragment?
        var itemId = 1L
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        mCoroutineScope.launch {

            database.insert(TimeData())
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)


        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
            keyFragment = this
            (keyFragment as KeyboardFragment).keyboardViewModel.setIs24HourFormat(true)
        }
        Thread.sleep(2000)

        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())

        onView(withId(R.id.ampm)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ampmKey)).check(matches(not(isDisplayed())))

        onView(withId(R.id.textViewKeySave)).perform(click())
        Thread.sleep(4000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
            keyFragment = this
            (keyFragment as KeyboardFragment).keyboardViewModel.setIs24HourFormat(false)
        }
        Thread.sleep(4000)

        onView(withId(R.id.ampm)).check(matches(isDisplayed()))
        onView(withId(R.id.ampmKey)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("0")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))
        onView(withId(R.id.ampm)).check(matches(withText("PM")))

        onView(withId(R.id.ampmKey)).perform(click())
        onView(withId(R.id.textViewKeySave)).perform(click())
        Thread.sleep(2000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(this.requireView(), navController)
            navController.setCurrentDestination(R.id.keyboardFragment, bundleListFragment)
            keyFragment = this
            (keyFragment as KeyboardFragment).keyboardViewModel.setIs24HourFormat(true)
        }
        Thread.sleep(2000)

        onView(withId(R.id.ampm)).check(matches(not(isDisplayed())))
        onView(withId(R.id.ampmKey)).check(matches(not(isDisplayed())))
        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("0")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("3")))

    }

    @Test
    fun check_stateAfterRotation24() {

        var keyFragment: Fragment? = null
        var itemId = 1L
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        mCoroutineScope.launch {

            val item = TimeData()
            item.ringtoneTitle = "ringtoneTitleTest"
            database.insert(item)
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            keyFragment = this
            (keyFragment as KeyboardFragment).keyboardViewModel.setIs24HourFormat(true)
        }

        val actualDate = SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time)

        onView(withId(R.id.textViewKeyTwo)).perform(click())
        onView(withId(R.id.textViewKeyThree)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewWednesday)).perform(click())

        (keyFragment as KeyboardFragment).activity?.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Thread.sleep(2000)

        onView(withText("ringtoneTitleTest")).check(matches(isDisplayed()))
        onView(withText(actualDate)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewNumberOne)).check(matches(withText("2")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("3")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("5")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("9")))
        onView(withId(R.id.textViewMonday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewWednesday)).check(matches(withTag(ViewTags.dayOn)))

    }

    @Test
    fun check_stateAfterRotation12() {

        var keyFragment: Fragment? = null
        var itemId = 1L
        val mCoroutineScope = CoroutineScope(Job() + Dispatchers.IO)

        mCoroutineScope.launch {

            val item = TimeData()
            item.ringtoneTitle = "ringtoneTitleTest"
            database.insert(item)
            itemId = database.getItem()!!.timeId
        }
        Thread.sleep(2000)

        launchFragmentInHiltContainer<KeyboardFragment>(
            Bundle().apply {
                putLong("itemId", itemId)
                putSerializable("correspondent", Correspondent.ListFragment)
                putString("ringtoneUri", "")
                putString("ringtoneTitle", "")
            },
            R.style.Theme_AppCompat
        ) {
            keyFragment = this
            (keyFragment as KeyboardFragment).keyboardViewModel.setIs24HourFormat(false)
        }

        val actualDate = SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().time)

        onView(withId(R.id.textViewKeyOne)).perform(click())
        onView(withId(R.id.textViewKeyOne)).perform(click())
        onView(withId(R.id.textViewKeyFive)).perform(click())
        onView(withId(R.id.textViewKeyNine)).perform(click())

        onView(withId(R.id.ampmKey)).perform(click())

        onView(withId(R.id.calendarMonth)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.textViewMonday)).perform(click())
        onView(withId(R.id.textViewWednesday)).perform(click())

        (keyFragment as KeyboardFragment).activity?.requestedOrientation =
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Thread.sleep(2000)

        onView(withText("ringtoneTitleTest")).check(matches(isDisplayed()))
        onView(withText(actualDate)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewNumberOne)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberTwo)).check(matches(withText("1")))
        onView(withId(R.id.textViewNumberThree)).check(matches(withText("5")))
        onView(withId(R.id.textViewNumberFour)).check(matches(withText("9")))
        onView(withId(R.id.textViewMonday)).check(matches(withTag(ViewTags.dayOn)))
        onView(withId(R.id.textViewWednesday)).check(matches(withTag(ViewTags.dayOn)))
    }

    private fun withTag(tag: String): Matcher<View?> {

        return object : TypeSafeMatcher<View>() {
            public override fun matchesSafely(view: View): Boolean {

                return view.tag == tag
            }

            override fun describeTo(description: Description) {
                description.appendText("check tag $tag")
            }
        }
    }


}
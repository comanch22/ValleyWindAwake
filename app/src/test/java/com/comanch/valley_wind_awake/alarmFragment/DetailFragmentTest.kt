package com.comanch.valley_wind_awake.alarmFragment

import org.junit.Assert.*
import org.junit.Test

class DetailFragmentTest {

    @Test
    fun check_getPauseDurationPreference(){

        val expected = "5"
        val actual = DetailFragment().getPauseDurationPreference()
        assertEquals(expected, actual)
    }

    @Test
    fun check_getSignalDurationPreference(){

        val expected = "2"
        val actual = DetailFragment().getSignalDurationPreference()
        assertEquals(expected, actual)
    }


}
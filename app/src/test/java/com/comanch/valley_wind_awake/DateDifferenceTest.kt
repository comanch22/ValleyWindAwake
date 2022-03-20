package com.comanch.valley_wind_awake

import org.junit.Assert.*
import org.junit.Test

class DateDifferenceTest {

    @Test
    fun check_the_calculation_of_the_resulting_string(){

        val dateDifference = DateDifference()
        val expected = "1 days 1 hours 1 min."
        val actual = dateDifference.getResultString(
            90060000,
            "days",
            "hours",
            "min."
        )
        assertEquals(expected, actual)
    }
}
package com.comanch.valley_wind_awake

import org.junit.Assert.*
import org.junit.Test

class DateFormatterTest{

    @Test
    fun check_format12from24(){

        val dateFormatter = DateFormatter("1212", "AM")
        val expected = "1212"
        val actual = dateFormatter.format12from24()
        assertEquals(expected, actual)
    }

    @Test
    fun check_format24from12(){

        val dateFormatter = DateFormatter("1212", "AM")
        val expected = "0012"
        val actual = dateFormatter.format24from12()
        assertEquals(expected, actual)
    }

    @Test
    fun check_getAmPm24(){

        val dateFormatter = DateFormatter("1212", "AM")
        val expected = "PM"
        val actual = dateFormatter.getAmPm24()
        assertEquals(expected, actual)
    }

    @Test
    fun check_getAmPm12(){

        val dateFormatter = DateFormatter("1212", "AM")
        val expected = "AM"
        val actual = dateFormatter.getAmPm12()
        assertEquals(expected, actual)
    }
}
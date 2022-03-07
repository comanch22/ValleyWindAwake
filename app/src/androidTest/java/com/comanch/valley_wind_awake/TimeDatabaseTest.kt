/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.comanch.valley_wind_awake

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.comanch.valley_wind_awake.dataBase.DataControl
import com.comanch.valley_wind_awake.dataBase.TimeDataDao
import org.junit.runner.RunWith

//
///**
// * This is not meant to be a full set of tests. For simplicity, most of your samples do not
// * include tests. However, when building the Room, it is helpful to make sure it works before
// * adding the UI.
// */
//
@RunWith(AndroidJUnit4::class)
class TimeDatabaseTest {

    private lateinit var timeDao: TimeDataDao
    private lateinit var db: DataControl

  /*  @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, ItemDataBase::class.java)
                // Allowing main thread queries, just for testing.
                .allowMainThreadQueries()
                .build()
        itemDao = db.sleepDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun insertAndGetNight() {
        val item = ItemData()
        item.itemId = 11
        item.text = "TestBase lol"
        itemDao.insert(item)
        val tonight = itemDao.getItem()
        assertEquals(tonight?.text, "TestRoom")
    }*/
}
package com.donntu.teachjournal

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.donntu.teachjournal.db.DBJournalHelper
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val db = Room.inMemoryDatabaseBuilder(context, DBJournalHelper::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        val sleepDao = db.subjectDAO()
    }
}
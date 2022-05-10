package com.donntu.teachjournal

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.donntu.teachjournal.db.DBJournalHelper

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    val db by lazy { DBJournalHelper.getDatabase(InstrumentationRegistry.getInstrumentation().targetContext) }
    @Test
    fun useAppContext() {
        val all = db.studyGroupDAO().getAllGroupsWithStudents()
        Log.d("STUD", all.toString() )
    }
}
package com.ivy

import com.ivy.common.androidtest.IvyAndroidTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

// this was a test to see if everything was working.
// of course my setup does not because nothing in this project is working correctly
// due to config/version changes since he created the course.
@HiltAndroidTest
class DummyTest : IvyAndroidTest() {

    @Test
    fun dummyTest() {
        println(db.accountCacheDao())

        assert(true)
    }
}
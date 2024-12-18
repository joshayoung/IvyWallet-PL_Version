package com.ivy.common.androidtest

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication

// we need to activate this in gradle:
class HiltTestRunner: AndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        // uses the HiltTestApplication:
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}
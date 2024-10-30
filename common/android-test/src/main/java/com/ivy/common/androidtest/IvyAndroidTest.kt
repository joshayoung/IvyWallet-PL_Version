package com.ivy.common.androidtest

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.test.core.app.ApplicationProvider
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.datastore.dataStore
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.time.LocalDate
import javax.inject.Inject

// shared logic:
abstract class IvyAndroidTest {

    // this is similar to an extension in junit5
    // with the rule we can automatically inject dependencies in a test case.
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    // Inject the database:
    @Inject
    lateinit var db: IvyWalletCoreDb

    // hilt can inject this for our test cases
    // our time provider fake is injected right here:
    @Inject
    lateinit var timeProvider: TimeProvider

    // we need the context:
    protected lateinit var context: Context

    @Before
    open fun setUp() {
        // set the context:
        context = ApplicationProvider.getApplicationContext()
        hiltRule.inject()

        // before every test case, clear all tables:
        db.clearAllTables()
        clearDataStore()
    }

    // runs after every single test case
    // make this 'open' so we can override in sub classes
    @After
    open fun tearDown() {
        // close our db connection:
        db.close()
    }

    // expose a function where we can manually set the date:
    protected fun setDate(date: LocalDate) {
        (timeProvider as TimeProviderFake).apply {
            timeNow = date.atTime(12, 0)
            dateNow = date
        }
    }

    // needs to be in run blocking since this is a suspend function:
    private fun clearDataStore() = runBlocking {
        context.dataStore.edit {
            it.clear()
        }
    }
}
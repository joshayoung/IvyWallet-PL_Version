package com.ivy.core.ui.time.picker.date

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.MainCoroutineRule
import com.ivy.core.ui.time.picker.date.data.PickerDay
import com.ivy.core.ui.time.picker.date.data.PickerMonth
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class DatePickerViewModelTest: IvyAndroidTest() {

    // equivalent of an extension in junit5:
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: DatePickerViewModel

    override fun setUp() {
        super.setUp()
        // inject the dependencies:
        viewModel = DatePickerViewModel(
            appContext = context,
            timeProvider = timeProvider
        )
    }

    @Test
    fun testSelectingDate() = runTest {
        // Making sure, the test runs with a month != February, so
        // February can be selected
        // we set the date here:
        setDate(LocalDate.of(2023, 1, 1))
        viewModel.uiState.test {
            // skip initial emission
            awaitItem() // Skip initial emission

            // change the date:
            viewModel.onEvent(DatePickerEvent.DayChange(PickerDay("30", 30)))

            // skip emission
            awaitItem() // Skip day emission

            viewModel.onEvent(DatePickerEvent.MonthChange(PickerMonth("Feb", 2)))

            val finalEmission = awaitItem()

            // comes from our fake:
            val timeProviderDate = timeProvider.dateNow()

            // date is correct:
            assertThat(finalEmission.selected).isEqualTo(
                LocalDate.of(timeProviderDate.year, 2, 28)
            )
        }
    }
}
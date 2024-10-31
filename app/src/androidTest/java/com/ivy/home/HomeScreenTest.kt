package com.ivy.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.ivy.common.androidtest.IvyAndroidTest
import com.ivy.common.androidtest.test_data.saveAccountWithTransactions
import com.ivy.common.androidtest.test_data.transactionWithTime
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.transaction.TransactionType
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.main.Home
import com.ivy.wallet.ui.RootActivity
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

// we are dealing with hilt, because this is an integrated test, not an isolated test.
@HiltAndroidTest
class HomeScreenTest: IvyAndroidTest() {

    @get:Rule
    // we are dealing with our real application so we use createAndroidComposeRule here:
    // automatically creates the composable content:
    val composeRule = createAndroidComposeRule<RootActivity>()

    // we need to be able to navigate to the right page:
    @Inject
    lateinit var navigator: Navigator

    @Test
    fun testSelectingDateRange() = runBlocking<Unit> {

        // set the date so it does not depend on the current date:
        val date = LocalDate.of(2023, 7, 23)
        setDate(date)

        // add transactions for our UI:
        val transaction1 = transactionWithTime(Instant.parse("2023-07-24T09:00:00Z")).copy(
            // change the name:
            // we need to be able to find the transaction in the UI with the title:
            title = "Transaction1"
        )
        val transaction2 = transactionWithTime(Instant.parse("2023-08-01T09:00:00Z")).copy(
            // change the name:
            title = "Transaction2"
        )
        val transaction3 = transactionWithTime(Instant.parse("2023-08-31T09:00:00Z")).copy(
            // change the name:
            title = "Transaction3"
        )
        db.saveAccountWithTransactions(
            transactions = listOf(transaction1, transaction2, transaction3)
        )

//        // wait until compose rule finishes showing our screen:
//        composeRule.awaitIdle()
//
//        // has to be on the UI thread:
//        composeRule.runOnUiThread {
//            navigator.navigate(Home.route)
//        }
//
//        // July in this case:
//        composeRule.onNodeWithText(date.month.name, ignoreCase = true).performClick()
//
//        composeRule.onNodeWithText("August")
//            .assertIsDisplayed()
//            .performClick()
//
//        composeRule.onNodeWithText("Aug. 01").assertIsDisplayed()
//        composeRule.onNodeWithText("Aug. 31").assertIsDisplayed()
//
//        composeRule.onNodeWithText("Done").performClick()
//
//        composeRule.onNodeWithText("Upcoming").performClick()
//
//        composeRule.onNodeWithText("Transaction").assertDoesNotExist()
//        composeRule.onNodeWithText("Transaction2").assertIsDisplayed()
//        composeRule.onNodeWithText("Transaction3").assertIsDisplayed()

        // much more readable and re-usable now:
        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .openDateRangeSheet(timeProvider)
            .selectMonth("August")
            .assertDateIsDisplayed(1, "August")
            .assertDateIsDisplayed(31, "August")
            .apply {
                // compose testing framework will try to merge different
                // UI components that can be merged.This prevents that:
                composeRule.onRoot(useUnmergedTree = true)

                // also search within substrings:
                composeRule.onNodeWithText("Hello", substring = true)
                // or
                composeRule.onNodeWithText("Hello", useUnmergedTree = true)
            }

            .clickDone()
            .clickUpcoming()
            .assertTransactionDoesNotExist("Transaction1")
            .assertTransactionIsDisplayed("Transaction2")
            .assertTransactionIsDisplayed("Transaction3")
    }

    @Test
    fun testGetOverdueTransaction_turnsIntoNormalTransaction() = runBlocking<Unit> {
        val date = LocalDate.of(2023, 7, 15)
        setDate(date)
        val dueTransaction = transactionWithTime(
            time = date
                .minusDays(1) // Make due
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant()
        ).copy(
            type = TransactionType.Income,
            timeType = TrnTimeType.Due,
            amount = 5.5
        )
        db.saveAccountWithTransactions(transactions = listOf(dueTransaction))

        HomeScreenRobot(composeRule)
            .navigateTo(navigator)
            .openOverdue()
            .clickGet()
            .assertTransactionIsDisplayed(dueTransaction.title!!)
            .assertBalanceIsDisplayed(dueTransaction.amount, dueTransaction.currency)
    }

}
package com.ivy.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.IvyComposeRule
import com.ivy.common.time.provider.TimeProvider
import com.ivy.data.CurrencyCode
import com.ivy.navigation.Navigator
import com.ivy.navigation.destinations.main.Home
import com.ivy.wallet.ui.RootActivity
import kotlinx.coroutines.runBlocking

class HomeScreenRobot(
    // needs our compose rule:
    private val composeRule: IvyComposeRule
) {
    fun navigateTo(navigator: Navigator): HomeScreenRobot {
        runBlocking {
            // await idle:
            composeRule.awaitIdle()


            composeRule.runOnUiThread {
                navigator.navigate(Home.route) {

                    // pop to back stack to start with fresh screen:
                    popUpTo(Home.route) {
                        // do not pop the home screen too:
                        inclusive = false
                    }
                }
            }
        }
        return this
    }

    // add function or date range:
    fun openDateRangeSheet(timeProvider: TimeProvider): HomeScreenRobot {
        // move what we wrote before to here:
        composeRule
            // use our time provider:
            .onNodeWithText(timeProvider.dateNow().month.name, ignoreCase = true)
            .performClick()

        return this
    }

    // select month:
    fun selectMonth(monthName: String): HomeScreenRobot {
        composeRule
            .onNodeWithText(monthName)
            .performClick()

        return this
    }

    // assert date:
    fun assertDateIsDisplayed(day: Int, month: String): HomeScreenRobot {
        // prepend a zero if single digit:
        val paddedDay = day.toString().padStart(2, '0')

        composeRule
            .onNodeWithText("${month.take(3)}. $paddedDay")
            .assertIsDisplayed()

        return this
    }

    // click done:
    fun clickDone(): HomeScreenRobot {
        composeRule.onNodeWithText("Done").performClick()

        return this
    }

    fun clickUpcoming(): HomeScreenRobot {
        // To help in debugging, we can use sleep:
        Thread.sleep(10000)

        composeRule.onNodeWithText("Upcoming").performClick()
        return this
    }

    fun assertTransactionDoesNotExist(transactionTitle: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionTitle).assertDoesNotExist()

        return this
    }

    fun assertTransactionIsDisplayed(transactionTitle: String): HomeScreenRobot {
        composeRule.onNodeWithText(transactionTitle).assertIsDisplayed()

        return this
    }

    // overload:
    // is displayed:
    fun assertTransactionIsDisplayed(
        transactionTitle: String,
        accountName: String,
        categoryName: String
    ): HomeScreenRobot {
        composeRule.onNodeWithText(transactionTitle).assertIsDisplayed()
        composeRule.onNodeWithText(accountName).assertIsDisplayed()
        composeRule.onNodeWithText(categoryName).assertIsDisplayed()

        return this
    }

    fun openOverdue(): HomeScreenRobot {
        composeRule
            .onNodeWithText("Overdue")
            .performClick()
        return this
    }

    fun assertBalanceIsDisplayed(amount: Double, currency: CurrencyCode): HomeScreenRobot {
        val formattedAmount = if(amount % 1.0 == 0.0) {
            amount.toInt().toString()
        } else amount.toString()

        composeRule
            .onAllNodes(
                // find node and sibling:
                hasText(formattedAmount) and hasAnySibling(hasText(currency)),
                // use the unmerged tree, we do not want to find them together as one node:
                useUnmergedTree = true
            )
            // find the first one:
            .onFirst()
            .assertIsDisplayed()

        return this
    }

    // add click get method
    fun clickGet(): HomeScreenRobot {
        composeRule.onNodeWithText("Get").performClick()
        return this
    }

    // click our transaction:
    fun clickNewTransaction(): HomeScreenRobot {
        composeRule.onNodeWithContentDescription("Add new transaction").performClick()
        return this
    }

    // add method to click expense:
    fun clickExpense(): HomeScreenRobot {
        composeRule.onNodeWithContentDescription("Create new expense").performClick()
        return this
    }

    fun assertTotalExpensesIs(amount: Int): HomeScreenRobot {
        composeRule
            // use the unmerged tree
            .onAllNodesWithTag("amount", useUnmergedTree = true)
            // get last one
            .onLast()
            .assertTextEquals(amount.toString())

        return this
    }

}
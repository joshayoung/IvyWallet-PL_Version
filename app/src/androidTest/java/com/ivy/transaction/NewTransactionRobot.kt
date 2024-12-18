package com.ivy.transaction

import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ivy.IvyComposeRule
import com.ivy.core.data.CategoryType

// new robot for the new screen:
class NewTransactionRobot(
    private val composeRule: IvyComposeRule
) {
    // return a NewTransactionRobot:
    fun addAccount(name: String): NewTransactionRobot {
        composeRule.onNodeWithText("Add account").performClick()

        // enter text:
        composeRule.onNodeWithContentDescription("New account").performTextInput(name)

        // there are multiple 'add account' buttons on the screen:
        composeRule.onAllNodesWithText("Add account").onLast().performClick()

        return this
    }

    fun selectAccount(name: String): NewTransactionRobot {
        composeRule.onNodeWithText(name).performClick()

        return this
    }

    fun enterTransactionAmount(amount: Int): NewTransactionRobot {
        val digits = amount.toString().map { it.digitToInt() }
        // we need to enter every digit individually:
        digits.forEach { digit ->
            composeRule.onNode(
                // hasClickAction:
                hasText(digit.toString()) and hasClickAction()
            ).performClick()
        }
        // one enter button on screen
        composeRule.onNodeWithText("Enter").performClick()

        // return the robot:
        return this
    }

    fun addCategory(name: String, type: CategoryType, parentName: String?): NewTransactionRobot {
        clickAddCategoryOnNewCategoryModal()
            .enterCategoryName(name)
            .selectCategoryType(type)
            .apply {
                if(parentName != null) {
                    chooseParent(parentName)
                }
            }
            .clickAddCategoryOnNewCategoryModal()

        return this
    }

    private fun clickAddCategoryOnNewCategoryModal(): NewTransactionRobot {
        // multiple add cateogry buttons:
        composeRule.onAllNodesWithText("Add category").onLast().performClick()

        return this
    }

    private fun enterCategoryName(name: String): NewTransactionRobot {
        // enter text:
        composeRule.onNodeWithContentDescription("New Category").performTextInput(name)

        return this
    }

    private fun selectCategoryType(type: CategoryType): NewTransactionRobot {
        composeRule.onNode(
                // has text and test tag:
            hasText(type.toString()) and hasTestTag("category_type_button")
        ).performClick()

        return this
    }

    private fun chooseParent(parentName: String): NewTransactionRobot {
        // only one text with 'Choose parent':
        composeRule.onNodeWithText("Choose parent").performClick()

        // Multiple so we click the last one.
        // Lhe last one in this case refers to the
        // most visible one on our screen (the one at the very
        // bottom will be the most visible because it is drawn
        // on top of everything else).
        composeRule.onAllNodesWithText(parentName).onLast().performClick()

        return this
    }

    fun chooseSubCategory(parentName: String, subName: String): NewTransactionRobot {
        composeRule.onNodeWithText(parentName).performClick()
        composeRule.onNodeWithText(subName).performClick()

        return this
    }

    fun enterTransactionTitle(title: String): NewTransactionRobot {
        // find with placeholder text:
        composeRule.onNodeWithContentDescription("Title").performTextInput(title)

        return this
    }

    fun enterTransactionDescription(description: String): NewTransactionRobot {
        composeRule.onNodeWithText("Add description").performClick()
        composeRule
            .onNodeWithContentDescription("Enter any details here")
            .performTextInput(description)

        // find the last one, which should be visible on the screen:
        composeRule.onAllNodesWithText("Add").onLast().performClick()

        return this
    }

    fun clickAddTransaction(): NewTransactionRobot {
        composeRule.onNodeWithText("Add").performClick()

        return this
    }
}
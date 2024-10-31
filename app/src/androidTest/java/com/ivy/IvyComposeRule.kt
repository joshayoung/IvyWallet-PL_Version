package com.ivy

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.ivy.wallet.ui.RootActivity

// add a type alias for this:
typealias IvyComposeRule = AndroidComposeTestRule<ActivityScenarioRule<RootActivity>, RootActivity>
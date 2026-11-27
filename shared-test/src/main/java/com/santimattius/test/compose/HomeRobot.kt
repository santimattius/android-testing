package com.santimattius.test.compose

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick

/**
 * Drives and verifies the Compose home screen through its semantics only.
 *
 * It does not know how the screen is hosted: the test supplies the content (or launches the
 * activity), so this module stays independent from the app module.
 */
class HomeRobot(private val composeTestRule: ComposeTestRule) {
    fun tapFavorite(title: String) {
        composeTestRule.onNodeWithTag("favorite_$title").performClick()
    }

    fun assertLoadingDisplayed() {
        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    fun assertMessageDisplayed(message: String) {
        composeTestRule.onNodeWithText(message).assertIsDisplayed()
    }
}

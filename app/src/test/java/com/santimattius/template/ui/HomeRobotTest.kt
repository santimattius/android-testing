package com.santimattius.template.ui

import android.app.Application
import android.os.Build
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.santimattius.test.compose.HomeRobot
import com.santimattius.test.compose.scenario
import com.santimattius.template.ui.compose.HomeScreenContent
import com.santimattius.template.ui.compose.models.HomeUiState
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/** Guards the test infrastructure: a wrong expectation must fail the scenario. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R], application = Application::class)
class HomeRobotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun assertFailsOnEmptyScreen(expectation: HomeRobot.() -> Unit) {
        composeTestRule.setContent {
            HomeScreenContent(state = HomeUiState(), onMovieClicked = {})
        }

        assertThrows(AssertionError::class.java) {
            scenario(HomeRobot(composeTestRule)) { then(expectation) }
        }
    }

    @Test
    fun failsWhenTheExpectedMessageIsNotDisplayed() =
        assertFailsOnEmptyScreen { assertMessageDisplayed("Not on screen") }

    @Test
    fun failsWhenLoadingIsExpectedButNotDisplayed() =
        assertFailsOnEmptyScreen { assertLoadingDisplayed() }
}

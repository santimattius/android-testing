package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.ForcedSize
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.StateRestorationTester
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.models.MovieUiModel
import org.junit.Assert.assertEquals
import com.santimattius.test.rules.FakeImageLoaderRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Behavior of [HomeScreenContent] across window sizes, font scales and state restoration, as
 * described in the official Compose testing patterns.
 * https://developer.android.com/develop/ui/compose/testing/common-patterns
 */
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.R], application = Application::class)
class HomeScreenAdaptabilityTest {

    @get:Rule
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule
    val composeTestRule = createComposeRule()

    private val movies = (0 until MOVIE_COUNT).map { index ->
        MovieUiModel(id = index.toLong(), title = "Movie $index", imageUrl = "")
    }

    // The grid uses GridCells.Adaptive(180.dp) with 2.dp content padding.
    @Test
    fun gridHasTwoColumnsInACompactWindow() = assertColumns(width = COMPACT_WIDTH, expected = 2)

    @Test
    fun gridHasThreeColumnsInAMediumWindow() = assertColumns(width = MEDIUM_WIDTH, expected = 3)

    @Test
    fun gridHasFourColumnsInAnExpandedWindow() = assertColumns(width = EXPANDED_WIDTH, expected = 4)

    private fun assertColumns(width: Int, expected: Int) {
        composeTestRule.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(DpSize(width.dp, TALL_HEIGHT.dp))
            ) {
                HomeScreenContent(state = HomeUiState(movies = movies), onMovieClicked = {})
            }
        }

        assertEquals(expected, columnsInFirstRow())
    }

    @Test
    fun messagesStayVisibleWithLargeFontScale() {
        composeTestRule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.FontScale(LARGE_FONT_SCALE)) {
                HomeScreenContent(state = HomeUiState(), onMovieClicked = {})
            }
        }

        composeTestRule.onNodeWithText("No hay resultados").assertIsDisplayed()
    }

    @Test
    fun scrollPositionSurvivesRecreation() {
        val lastMovie = "card_Movie ${MOVIE_COUNT - 1}"
        val restorationTester = StateRestorationTester(composeTestRule)
        restorationTester.setContent {
            DeviceConfigurationOverride(
                DeviceConfigurationOverride.ForcedSize(DpSize(COMPACT_WIDTH.dp, SHORT_HEIGHT.dp))
            ) {
                HomeScreenContent(state = HomeUiState(movies = movies), onMovieClicked = {})
            }
        }
        composeTestRule.onNode(hasScrollAction()).performScrollToNode(hasTestTag(lastMovie))
        composeTestRule.onNodeWithTag(lastMovie).assertIsDisplayed()

        restorationTester.emulateSavedInstanceStateRestore()

        composeTestRule.onNodeWithTag(lastMovie).assertIsDisplayed()
    }

    private fun columnsInFirstRow(): Int {
        val firstTop = cardTop(0)
        return (0 until MOVIE_COUNT).count { cardTopOrNull(it) == firstTop }
    }

    private fun cardTop(index: Int) = cardTopOrNull(index)!!

    private fun cardTopOrNull(index: Int): Float? = composeTestRule
        .onAllNodesWithCard(index)
        .firstOrNull()
        ?.boundsInRoot
        ?.top

    private fun androidx.compose.ui.test.junit4.ComposeContentTestRule.onAllNodesWithCard(index: Int) =
        onAllNodes(hasTestTag("card_Movie $index")).fetchSemanticsNodes()

    private companion object {
        const val MOVIE_COUNT = 40
        const val COMPACT_WIDTH = 400
        const val MEDIUM_WIDTH = 610
        const val EXPANDED_WIDTH = 840
        const val TALL_HEIGHT = 1000
        const val SHORT_HEIGHT = 500
        const val LARGE_FONT_SCALE = 1.5f
    }
}

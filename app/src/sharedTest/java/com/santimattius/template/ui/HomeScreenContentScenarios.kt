package com.santimattius.template.ui

import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.santimattius.test.compose.HomeRobot
import com.santimattius.test.compose.scenario
import com.santimattius.test.rules.FakeImageLoaderRule
import com.santimattius.template.ui.compose.HomeScreenContent
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.compose.ui.components.MoviesGridAction
import com.santimattius.template.ui.models.MovieUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Behavior of [HomeScreenContent] from injected state. The same cases run on the JVM and on a
 * device; each source set only picks the runner. Described in docs/features/home_screen.feature.
 */
abstract class HomeScreenContentScenarios {

    @get:Rule(order = 0)
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val movie = MovieUiModel(id = 1, title = "Movie", imageUrl = "https://image/1.jpg")
    private val actions = mutableListOf<MoviesGridAction>()
    private val robot by lazy { HomeRobot(composeTestRule) }

    @Test
    fun showsLoadingIndicatorWhileLoading() = scenario(robot) {
        given {
            // The indeterminate progress animation never lets Compose go idle.
            composeTestRule.mainClock.autoAdvance = false
            show(HomeUiState(isLoading = true))
        }
        then { assertLoadingDisplayed() }
    }

    @Test
    fun showsErrorMessageWhenLoadingFails() = scenario(robot) {
        given { show(HomeUiState(loadingError = true)) }
        then { assertMessageDisplayed("Ha ocurrido un error al cargar las peliculas") }
    }

    @Test
    fun showsEmptyMessageWhenThereAreNoMovies() = scenario(robot) {
        given { show(HomeUiState()) }
        then { assertMessageDisplayed("No hay resultados") }
    }

    @Test
    fun reportsFavoriteActionForTheClickedMovie() = scenario(robot) {
        given { show(HomeUiState(movies = listOf(movie))) }
        whenever { tapFavorite(movie.title) }
        then {
            composeTestRule.runOnIdle {
                assertEquals(listOf<MoviesGridAction>(MoviesGridAction.OnFavorite(movie)), actions)
            }
        }
    }

    private fun show(state: HomeUiState) = composeTestRule.setContent {
        HomeScreenContent(state = state, onMovieClicked = { actions += it })
    }
}

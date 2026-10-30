package com.santimattius.template.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.santimattius.test.rules.FakeImageLoaderRule
import com.santimattius.template.ui.compose.HomeScreenContent
import com.santimattius.template.ui.compose.models.HomeUiState
import com.santimattius.template.ui.compose.ui.components.MoviesGridAction
import com.santimattius.template.ui.models.MovieUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
class HomeScreenContentTest {

    @get:Rule(order = 0)
    val fakeImageLoaderRule = FakeImageLoaderRule()

    @get:Rule(order = 1)
    val composeTestRule = createComposeRule()

    private val movie = MovieUiModel(id = 1, title = "Movie", imageUrl = "https://image/1.jpg")

    @Test
    fun showsLoadingIndicatorWhileLoading() {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            HomeScreenContent(state = HomeUiState(isLoading = true), onMovieClicked = {})
        }

        composeTestRule.onNodeWithTag("loading").assertIsDisplayed()
    }

    @Test
    fun showsErrorMessageWhenLoadingFails() {
        composeTestRule.setContent {
            HomeScreenContent(state = HomeUiState(loadingError = true), onMovieClicked = {})
        }

        composeTestRule.onNodeWithText("Ha ocurrido un error al cargar las peliculas")
            .assertIsDisplayed()
    }

    @Test
    fun showsEmptyMessageWhenThereAreNoMovies() {
        composeTestRule.setContent {
            HomeScreenContent(state = HomeUiState(), onMovieClicked = {})
        }

        composeTestRule.onNodeWithText("No hay resultados").assertIsDisplayed()
    }

    @Test
    fun reportsFavoriteActionForTheClickedMovie() {
        val actions = mutableListOf<MoviesGridAction>()
        composeTestRule.setContent {
            HomeScreenContent(state = HomeUiState(movies = listOf(movie)), onMovieClicked = { actions += it })
        }

        composeTestRule.onNodeWithTag("favorite_Movie").performClick()

        composeTestRule.runOnIdle {
            assertEquals(listOf<MoviesGridAction>(MoviesGridAction.OnFavorite(movie)), actions)
        }
    }
}

package com.santimattius.template.ui.compose

import androidx.lifecycle.viewmodel.testing.viewModelScenario
import app.cash.turbine.test
import com.santimattius.core.domain.entities.Movie
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.test.data.FakeMovieRepository
import com.santimattius.test.data.MovieMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeComposeViewModelTest {

    @get:Rule
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Test
    fun `adding a favorite updates movies and publishes success`() =
        verifyFavoriteChange(
            initialFavorite = false,
            failure = null,
            expectedMessage = Messages.Success("Add Movie  to favorite"),
            expectedFavorite = true,
        )

    @Test
    fun `removing a favorite updates movies and publishes success`() =
        verifyFavoriteChange(
            initialFavorite = true,
            failure = null,
            expectedMessage = Messages.Success("Remove Movie from favorite"),
            expectedFavorite = false,
        )

    @Test
    fun `failed favorite addition keeps movies and publishes error`() =
        verifyFavoriteChange(
            initialFavorite = false,
            failure = IllegalStateException("favorite failed"),
            expectedMessage = Messages.Error("Failed to add Movie to favorite"),
            expectedFavorite = false,
        )

    @Test
    fun `failed favorite removal keeps movies and publishes error`() =
        verifyFavoriteChange(
            initialFavorite = true,
            failure = IllegalStateException("favorite failed"),
            expectedMessage = Messages.Error("Failed to remove Movie from favorite"),
            expectedFavorite = true,
        )

    private fun verifyFavoriteChange(
        initialFavorite: Boolean,
        failure: Throwable?,
        expectedMessage: Messages,
        expectedFavorite: Boolean,
    ) = runTest(mainCoroutinesTestRule.testDispatcher) {
        val movie = MovieMother.createMovie(
            id = 1,
            title = "Movie",
            overview = "Overview",
            poster = "/poster.jpg",
        ).copy(favorite = initialFavorite)
        val repository = FakeMovieRepository(listOf(movie)).apply {
            favoriteFailure = failure
        }
        val uiMovie = movie.asUiModel()

        viewModelScenario { HomeComposeViewModel(repository) }.use { scenario ->
            scenario.viewModel.state.test {
                var initialState = awaitItem()
                while (initialState.isLoading) initialState = awaitItem()
                assertEquals(listOf(uiMovie), initialState.movies)

                scenario.viewModel.onFavorite(uiMovie)

                var messageState = awaitItem()
                while ((messageState.successMessages == Messages.None &&
                        messageState.errorMessages == Messages.None) ||
                    messageState.movies != listOf(uiMovie.copy(favorite = expectedFavorite))
                ) {
                    messageState = awaitItem()
                }
                assertEquals(
                    listOf(uiMovie.copy(favorite = expectedFavorite)),
                    messageState.movies,
                )
                when (expectedMessage) {
                    is Messages.Success -> assertEquals(
                        expectedMessage,
                        messageState.successMessages,
                    )

                    is Messages.Error -> assertEquals(
                        expectedMessage,
                        messageState.errorMessages,
                    )

                    Messages.None -> error("A favorite operation must publish a message")
                }

                scenario.viewModel.onMessageConsumed()
                val consumedState = awaitItem()
                assertEquals(Messages.None, consumedState.successMessages)
                assertEquals(Messages.None, consumedState.errorMessages)
                cancelAndIgnoreRemainingEvents()
            }
        }

        val storedMovie = repository.getAll().single()
        if (expectedFavorite) {
            assertTrue(storedMovie.favorite)
        } else {
            assertFalse(storedMovie.favorite)
        }
    }

    private fun Movie.asUiModel() = MovieUiModel(
        id = id,
        title = title,
        imageUrl = poster,
        favorite = favorite,
    )
}

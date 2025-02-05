package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.lifecycle.viewmodel.testing.viewModelScenario
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.implementation.RetrofitMovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
import com.santimattius.core.data.repositories.TMDbRepository
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.models.MovieUiModel
import com.santimattius.template.ui.rules.RoomTestRule
import com.santimattius.template.ui.rules.loadModule
import com.santimattius.test.data.MovieMother
import com.santimattius.test.data.TheMovieDBServiceMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.test.rules.MockWebServerRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ksp.generated.com_santimattius_template_di_AppModule
import org.koin.ksp.generated.com_santimattius_template_di_DataModule
import org.koin.ksp.generated.defaultModule
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.R],
    instrumentedPackages = ["androidx.loader.content"],
    application = Application::class
)
class HomeComposeViewModelTest {

    private lateinit var movieRepository: TMDbRepository
    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule {
        TheMovieDBServiceMother.createPopularMovieResponse()
    }

    @get:Rule(order = 1)
    val roomTestRule = RoomTestRule(appContext, TheMovieDataBase::class.java)


    @get:Rule(order = 2)
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Before
    fun setUp() {
        val movieLocalDataSource = RoomMovieLocalDataSource(roomTestRule.db)
        val serviceCreator = RetrofitServiceCreator(mockWebServerRule.baseUrl, "test")
        val movieNetworkDataSource = RetrofitMovieNetworkDataSource(
            serviceCreator.createService(
                TheMovieDBService::class.java
            )
        )
        movieRepository = TMDbRepository(movieNetworkDataSource, movieLocalDataSource)
    }

    @Test
    fun `onFavorite should update state with success message when adding to favorite succeeds`() {
        viewModelScenario { HomeComposeViewModel(movieRepository = movieRepository) }.use {
            val dto = MovieMother.createMovie()
            val movie = MovieUiModel(
                id = dto.id,
                title = dto.title,
                imageUrl = dto.poster,
                favorite = false
            )
            val viewModel = it.viewModel

            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.onFavorite(movie)
                viewModel.state.test {
                    awaitItem()
                    assertEquals(
                        Messages.Success("Add ${movie.title}  to favorite"),
                        awaitItem().successMessages
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

    @Test
    @Ignore("ignore this test for now")
    fun `onFavorite should update state with success message when removing from favorite succeeds`() {
        viewModelScenario { HomeComposeViewModel(movieRepository = movieRepository) }.use {
            val dto = MovieMother.createMovie()
            val movie = MovieUiModel(
                id = dto.id,
                title = dto.title,
                imageUrl = dto.poster,
                favorite = true
            )
            val viewModel = it.viewModel
            runTest(mainCoroutinesTestRule.testDispatcher) {
                viewModel.onFavorite(movie)
                viewModel.state.test {
                    awaitItem()
                    assertEquals(
                        Messages.Success("Remove ${movie.title} from favorite"),
                        awaitItem().successMessages
                    )
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }

}
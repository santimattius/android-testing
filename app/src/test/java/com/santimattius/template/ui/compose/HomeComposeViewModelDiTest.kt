package com.santimattius.template.ui.compose

import android.app.Application
import android.os.Build
import androidx.lifecycle.viewmodel.testing.viewModelScenario
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.domain.repositories.MovieRepository
import com.santimattius.template.di.DataModule
import com.santimattius.template.ui.compose.models.Messages
import com.santimattius.template.ui.rules.RoomTestRule
import com.santimattius.template.ui.rules.loadModule
import com.santimattius.test.data.TheMovieDBServiceMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.test.rules.MockWebServerRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.plugin.module.dsl.modules
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
class HomeComposeViewModelDiTest : KoinTest {

    private val appContext = ApplicationProvider.getApplicationContext<Application>()

    @get:Rule(order = 0)
    val mockWebServerRule = MockWebServerRule {
        TheMovieDBServiceMother.createPopularMovieResponse()
    }

    @get:Rule(order = 1)
    val roomTestRule = RoomTestRule(appContext, TheMovieDataBase::class.java)

    @get:Rule(order = 2)
    var koinTestRule = KoinTestRule.create {
        modules(DataModule::class)
    }

    @get:Rule(order = 3)
    val mainCoroutinesTestRule = MainCoroutinesTestRule()

    @Before
    fun setUp() {
        koinTestRule.loadModule(
            module {
                factory(named("base_url")) { mockWebServerRule.baseUrl }
                factory<TheMovieDataBase> { roomTestRule.db }
            }
        )
    }

    @Test
    fun `onFavorite should update state with success message when adding to favorite succeeds`() =
        verifyFavoriteChange(remove = false)

    @Test
    fun `onFavorite should update state with success message when removing from favorite succeeds`() =
        verifyFavoriteChange(remove = true)

    private fun verifyFavoriteChange(remove: Boolean) = runTest(mainCoroutinesTestRule.testDispatcher) {
        val repository = get<MovieRepository>()
        viewModelScenario { HomeComposeViewModel(movieRepository = repository) }.use { scenario ->
            val viewModel = scenario.viewModel
            viewModel.state.test {
                // Room and MockWebServer complete independently of the test scheduler.
                var state = awaitItem()
                while (state.isLoading || state.movies.isEmpty()) state = awaitItem()
                var movie = state.movies.first()
                assertEquals(false, movie.favorite)

                if (remove) {
                    viewModel.onFavorite(movie)
                    val addMessage = Messages.Success("Add ${movie.title}  to favorite")
                    do {
                        state = awaitItem()
                    } while (state.successMessages != addMessage ||
                        state.movies.none { it.id == movie.id && it.favorite })
                    movie = state.movies.first { it.id == movie.id }
                }

                viewModel.onFavorite(movie)
                val expectedMessage = if (remove) {
                    Messages.Success("Remove ${movie.title} from favorite")
                } else {
                    Messages.Success("Add ${movie.title}  to favorite")
                }
                do {
                    state = awaitItem()
                } while (state.successMessages != expectedMessage ||
                    state.movies.none { it.id == movie.id && it.favorite == !remove })

                assertEquals(expectedMessage, state.successMessages)
                assertEquals(!remove, state.movies.first { it.id == movie.id }.favorite)
                assertEquals(!remove, repository.getAll().first { it.id == movie.id }.favorite)
                cancelAndIgnoreRemainingEvents()
            }
        }
    }
}

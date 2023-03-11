package com.santimattius.template.data.repositories

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.template.MainApplication
import com.santimattius.template.data.datasources.implementation.MovieDataSource
import com.santimattius.template.data.datasources.implementation.RoomDataSource
import com.santimattius.template.data.dtoToEntity
import com.santimattius.template.utils.MainCoroutinesTestRule
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.R])
class TMDbRepositoryTest {

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private val app = ApplicationProvider.getApplicationContext<MainApplication>()
    private lateinit var repository: TMDbRepository

    @Before
    fun setUp() {
        mockkConstructor(MovieDataSource::class)
        mockkConstructor(RoomDataSource::class)
        repository = TMDbRepository(app)
    }

    @After
    fun tearDown() {
        unmockkConstructor(MovieDataSource::class)
        unmockkConstructor(RoomDataSource::class)
    }

    @Test
    @Ignore("Is bad test")
    fun `first test example`() {
        val movies = MovieMother.getMovies()
        //Given
        coEvery {
            anyConstructed<MovieDataSource>().getPopularMovies()
        } returns Result.success(movies)
        coEvery {
            constructedWith<RoomDataSource>(OfTypeMatcher<Context>(Context::class)).save(any())
        } returns Result.success(true)
        coEvery {
            constructedWith<RoomDataSource>(OfTypeMatcher<Context>(Context::class)).getAll()
        } returns movies.dtoToEntity()

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            repository.fetchPopular()
        }

        //Then
        coVerify {
            anyConstructed<MovieDataSource>().getPopularMovies()
        }
        coVerify {
            constructedWith<RoomDataSource>(OfTypeMatcher<Context>(Context::class)).save(any())
        }
        coVerify {
            constructedWith<RoomDataSource>(OfTypeMatcher<Context>(Context::class)).getAll()
        }
    }
}
package com.santimattius.core.data.repositories

import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.implementation.RetrofitMovieNetworkDataSource
import com.santimattius.core.data.datasources.implementation.RoomMovieLocalDataSource
import com.santimattius.test.data.TheMovieDBServiceMother
import com.santimattius.test.rules.MainCoroutinesTestRule
import com.santimattius.test.rules.MockWebServerRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE, sdk = [Build.VERSION_CODES.R])
class TMDbRepositoryIntegrationTest {
    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    @get:Rule
    val coroutinesTestRule = MainCoroutinesTestRule()

    private lateinit var db: TheMovieDataBase
    private lateinit var localDataSource: RoomMovieLocalDataSource

    private lateinit var networkDataSource: RetrofitMovieNetworkDataSource

    private lateinit var repository: TMDbRepository

    @Before
    fun setUp() {
        val baseUrl = mockWebServerRule.baseUrl
        val service = RetrofitServiceCreator(baseUrl = baseUrl, apiKey = "")
            .createService(TheMovieDBService::class.java)
        networkDataSource = RetrofitMovieNetworkDataSource(service)

        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TheMovieDataBase::class.java)
            .allowMainThreadQueries()
            .build()
        localDataSource = RoomMovieLocalDataSource(db)
        repository = TMDbRepository(networkDataSource, localDataSource)
    }

    @Test
    fun `Given there are movies when make the request the result is not empty`() {
        //Given
        val response = TheMovieDBServiceMother.createPopularMovieResponse()
        mockWebServerRule.enqueue(response)

        //When
        runTest(coroutinesTestRule.testDispatcher) {
            val result = repository.refresh()
            //Then
            assertThat(result.getOrNull(), IsNot(Matchers.empty()))
        }
    }
}
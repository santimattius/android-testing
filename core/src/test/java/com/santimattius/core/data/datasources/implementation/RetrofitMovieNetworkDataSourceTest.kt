package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.models.NetworkMovie
import com.santimattius.core.data.models.Response
import com.santimattius.test.data.TheMovieDBServiceMother
import com.santimattius.test.rules.MockWebServerRule
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RetrofitMovieNetworkDataSourceTest {

    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    private lateinit var dataSource: RetrofitMovieNetworkDataSource

    @Before
    fun setUp() {
        val baseUrl = mockWebServerRule.baseUrl
        val service = RetrofitServiceCreator(baseUrl = baseUrl, apiKey = "")
            .createService(TheMovieDBService::class.java)
        dataSource = RetrofitMovieNetworkDataSource(service)
    }

    @Test
    fun validateSuccessResponse() = runTest {
        val response = TheMovieDBServiceMother.createPopularMovieResponse()
        mockWebServerRule.enqueue(response)

        val result = dataSource.getPopularMovies()

        assertThat(result.isSuccess, equalTo(true))
    }

    @Test
    fun validateFailResponse() = runTest {
        val response = TheMovieDBServiceMother.createErrorResponse()
        mockWebServerRule.enqueue(response)

        val result = dataSource.getPopularMovies()

        assertThat(result.isSuccess, equalTo(false))
    }

    @Test
    fun cancellationIsPropagatedToTheCaller() = runTest {
        val expected = CancellationException("cancelled")
        val cancellingService = object : TheMovieDBService {
            override suspend fun getMoviePopular(
                version: Int,
                page: Int,
            ): Response<NetworkMovie> = throw expected
        }
        val cancellingDataSource = RetrofitMovieNetworkDataSource(cancellingService)

        try {
            cancellingDataSource.getPopularMovies()
            fail("Expected cancellation to propagate")
        } catch (actual: CancellationException) {
            assertSame(expected, actual)
        }
    }
}
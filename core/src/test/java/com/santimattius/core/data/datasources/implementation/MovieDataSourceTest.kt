package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.shared_test.data.TheMovieDBServiceMother
import com.santimattius.shared_test.rules.MockWebServerRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MovieDataSourceTest {

    @get:Rule
    val mockWebServerRule = MockWebServerRule()

    private lateinit var dataSource: MovieDataSource

    @Before
    fun setUp() {
        val baseUrl = mockWebServerRule.baseUrl
        val service = RetrofitServiceCreator(baseUrl = baseUrl, apiKey = "")
            .createService(TheMovieDBService::class.java)
        dataSource = MovieDataSource(service)
    }

    @Test
    fun validateSuccessResponse() {
        //Give
        val response = TheMovieDBServiceMother.createPopularMovieResponse()
        mockWebServerRule.enqueue(response)
        runBlocking {
            //When
            val result = dataSource.getPopularMovies()
            //Then
            assertThat(result.isSuccess, equalTo(true))
        }
    }

    @Test
    fun validateFailResponse() {
        val response = TheMovieDBServiceMother.createErrorResponse()

        mockWebServerRule.enqueue(response)

        runBlocking {
            val result = dataSource.getPopularMovies()
            assertThat(result.isSuccess, equalTo(false))
        }
    }
}
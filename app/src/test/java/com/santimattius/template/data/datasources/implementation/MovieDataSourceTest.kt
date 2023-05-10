package com.santimattius.template.data.datasources.implementation

import com.santimattius.shared_test.data.TheMovieDBServiceMother
import com.santimattius.template.data.client.network.TheMovieDBService
import com.santimattius.template.data.client.network.createRetrofitService
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.create

class MovieDataSourceTest {

    @get:Rule
    val mockWebServerRule = com.santimattius.shared_test.rules.MockWebServerRule()
    private lateinit var dataSource: MovieDataSource

    @Before
    fun setUp() {
        val baseUrl = mockWebServerRule.baseUrl
        val service =
            createRetrofitService(baseUrl = baseUrl, apiKey = "").create<TheMovieDBService>()
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
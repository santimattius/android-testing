package com.santimattius.shared_test.data

import okhttp3.mockwebserver.MockResponse
import java.net.HttpURLConnection

object TheMovieDBServiceMother {
    private val jsonLoader = com.santimattius.shared_test.JsonLoader()

    fun createPopularMovieResponse(): MockResponse {
        return MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(jsonLoader.load("movie_popular_response_success.json"))
    }

    fun createErrorResponse(code: Int = HttpURLConnection.HTTP_BAD_REQUEST): MockResponse {
        return MockResponse()
            .setResponseCode(code)
    }
}
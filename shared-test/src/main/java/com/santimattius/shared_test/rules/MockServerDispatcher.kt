package com.santimattius.shared_test.rules

import com.santimattius.shared_test.JsonLoader
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class MockServerDispatcher {

    private val jsonLoader = JsonLoader()

    private val mocks = mapOf(
        "/3/movie/popular" to "movie_popular_response_success.json"
    )

    internal inner class RequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                "/3/movie/popular" -> MockResponse().setResponseCode(200)
                    .setBody(jsonLoader.load(mocks["/3/movie/popular"].orEmpty()))

                else -> MockResponse().setResponseCode(400)
            }
        }
    }

}
package com.santimattius.test.rules

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerRule(
    private val server: MockWebServer = MockWebServer(),
    private val port: Int = 0,
    private val initialResponse: (() -> MockResponse)? = null
) : TestWatcher() {

    val baseUrl: String
        get() = server.url("/").toUri().toString()


    override fun starting(description: Description) {
        super.starting(description)
        server.start(port)
        initialResponse?.let { server.enqueue(it.invoke()) }
    }

    override fun finished(description: Description) {
        super.finished(description)
        server.shutdown()
    }

    fun enqueue(response: MockResponse) {
        server.enqueue(response)
    }
}
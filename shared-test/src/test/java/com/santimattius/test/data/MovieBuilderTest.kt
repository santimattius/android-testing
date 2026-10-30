package com.santimattius.test.data

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieBuilderTest {

    @Test
    fun buildMovieMapsTitleAndPosterIndependently() {
        val movie = MovieMother.buildMovie(
            title = "Expected title",
            poster = "/expected-poster.jpg",
        )

        assertEquals("Expected title", movie.title)
        assertEquals("/expected-poster.jpg", movie.poster)
    }
}

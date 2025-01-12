package com.santimattius.test.data.strategies

import com.santimattius.core.domain.entities.Movie

class MovieBuilder(
    private var id: Int = -1,
    private var overview: String = "",
    private var title: String = "",
    private var poster: String = "",
) {

    fun withId(value: Int) = apply {
        this.id = value
    }

    fun withOverview(value: String) = apply {
        this.overview = value
    }

    fun withTitle(value: String) = apply {
        this.title = value
    }

    fun withPoster(value: String) = apply {
        this.title = value
    }

    fun build() = Movie(id, overview, title, poster)
}
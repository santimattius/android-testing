package com.santimattius.core.domain.repositories

import com.santimattius.core.domain.entities.Movie

interface MovieRepository {
    suspend fun getPopular(): List<Movie>
    suspend fun fetchPopular(): Result<List<Movie>>
}
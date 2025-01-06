package com.santimattius.core.domain.repositories

import com.santimattius.core.domain.entities.Movie

interface MovieRepository {
    suspend fun getAll(): List<Movie>
    suspend fun refresh(): Result<List<Movie>>
}
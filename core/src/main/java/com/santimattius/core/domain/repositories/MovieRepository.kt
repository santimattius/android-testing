package com.santimattius.core.domain.repositories

import com.santimattius.core.domain.entities.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    val all: Flow<List<Movie>>
    suspend fun getAll(): List<Movie>
    suspend fun refresh(): Result<List<Movie>>
}
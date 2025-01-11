package com.santimattius.core.data.datasources

import com.santimattius.core.data.models.MovieEntity
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {

    val all: Flow<List<MovieEntity>>

    suspend fun getAll(): List<MovieEntity>

    suspend fun isEmpty(): Boolean

    suspend fun save(movies: List<MovieEntity>): Result<Boolean>

    suspend fun find(id: Int): Result<MovieEntity>

    suspend fun delete(movie: MovieEntity): Result<Boolean>

    suspend fun update(movie: MovieEntity): Result<Boolean>
}
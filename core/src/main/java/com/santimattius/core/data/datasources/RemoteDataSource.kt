package com.santimattius.core.data.datasources

import com.santimattius.core.data.entities.MovieDto

interface RemoteDataSource {
    suspend fun getPopularMovies(): Result<List<MovieDto>>
}
package com.santimattius.shared_test.data

import com.santimattius.core.data.datasources.RemoteDataSource
import com.santimattius.core.data.entities.MovieDto

class FakeRemoteDataSource(
    var onMovies: () -> List<MovieDto> = {
        MovieMother.createMovies()
    },
) : RemoteDataSource {

    override suspend fun getPopularMovies(): Result<List<MovieDto>> {
        return runCatching { onMovies() }
    }
}
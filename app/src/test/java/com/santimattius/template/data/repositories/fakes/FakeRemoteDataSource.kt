package com.santimattius.template.data.repositories.fakes

import com.santimattius.template.data.datasources.RemoteDataSource
import com.santimattius.template.data.entities.MovieDto
import com.santimattius.template.data.repositories.MovieMother

class FakeRemoteDataSource(
    var onMovies: () -> List<MovieDto> = {
        MovieMother.createMovies()
    },
) : RemoteDataSource {

    override suspend fun getPopularMovies(): Result<List<MovieDto>> {
        return runCatching { onMovies() }
    }
}
package com.santimattius.shared_test.data

import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.models.NetworkMovie

class FakeMovieNetworkDataSource(
    var onMovies: () -> List<NetworkMovie> = {
        MovieMother.createMovies()
    },
) : MovieNetworkDataSource {

    override suspend fun getPopularMovies(): Result<List<NetworkMovie>> {
        return runCatching { onMovies() }
    }
}
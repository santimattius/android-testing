package com.santimattius.core.data.datasources.implementation

import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.core.data.datasources.MovieNetworkDataSource
import com.santimattius.core.data.models.NetworkMovie as TheMovieDbMovie

class MovieDataSource(
    private val service: TheMovieDBService,
) : MovieNetworkDataSource {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun getPopularMovies(): Result<List<TheMovieDbMovie>> {
        return try {
            val response = service.getMoviePopular(version = DEFAULT_VERSION, page = SINGLE_PAGE)
            Result.success(response.results)
        } catch (ex: Throwable) {
            Result.failure(ex)
        }
    }

    companion object {
        private const val SINGLE_PAGE = 1
        const val DEFAULT_VERSION = 3
    }
}

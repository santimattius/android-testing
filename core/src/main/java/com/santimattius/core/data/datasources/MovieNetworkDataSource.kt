package com.santimattius.core.data.datasources

import com.santimattius.core.data.models.NetworkMovie

interface MovieNetworkDataSource {
    suspend fun getPopularMovies(): Result<List<NetworkMovie>>
}
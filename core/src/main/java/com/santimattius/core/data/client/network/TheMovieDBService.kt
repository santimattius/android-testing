package com.santimattius.core.data.client.network

import com.santimattius.core.data.models.NetworkMovie
import com.santimattius.core.data.models.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBService {

    @GET("/{version}/movie/popular")
    suspend fun getMoviePopular(
        @Path("version") version: Int,
        @Query("page") page: Int,
    ): Response<NetworkMovie>

}

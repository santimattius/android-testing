package com.santimattius.core.data.client.network

import com.santimattius.core.data.entities.MovieDto
import com.santimattius.core.data.entities.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBService {

    @GET("/{version}/movie/popular")
    suspend fun getMoviePopular(
        @Path("version") version: Int,
        @Query("page") page: Int,
    ): Response<MovieDto>

}

package com.santimattius.template.data.repositories

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.santimattius.template.data.entities.MovieDto
import com.santimattius.template.data.entities.Response
import com.santimattius.template.utils.JsonLoader

object MovieMother {
    private val jsonLoader = JsonLoader()
    private val gson = GsonBuilder().create()

    fun getMovies(): List<MovieDto> {
        val json = jsonLoader.load("movie_popular_response_success")
        return gson.fromJson<Response<MovieDto>>(
            json,
            object : TypeToken<Response<MovieDto>>() {}.type
        ).results
    }
}
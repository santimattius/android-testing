package com.santimattius.template.data.client.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object RetrofitServiceCreator {

    private const val BASE_URL = "https://api.themoviedb.org"
    internal inline fun <reified S> create(key: String): S {
        require(key.isNotBlank()) { "API Key required" }

        val client = OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(key))
            .build()

        return create(baseUrl = BASE_URL, client = client)
    }

    private inline fun <reified S> create(
        baseUrl: String,
        client: OkHttpClient = OkHttpClient(),
    ): S {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create()
    }
}

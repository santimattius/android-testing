package com.santimattius.core.data.client.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal fun createRetrofitService(baseUrl: String, apiKey: String): Retrofit {
    val client = OkHttpClient().newBuilder()
        .addInterceptor(RequestInterceptor(apiKey))
        .build()

    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

class RetrofitServiceCreator(baseUrl: String, apiKey: String) {
    private val retrofit: Retrofit = createRetrofitService(baseUrl, apiKey)

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
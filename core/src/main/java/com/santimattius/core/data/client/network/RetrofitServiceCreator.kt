package com.santimattius.core.data.client.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//TODO:only for testing
class RetrofitServiceCreator(baseUrl: String, apiKey: String) {

    // Use 'by lazy' for client
    val client: OkHttpClient by lazy { // <--- Made lazy
        OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(apiKey))
            .build()
    }

    // Use 'by lazy' for retrofit instance
    private val retrofit: Retrofit by lazy { // <--- Made lazy
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client) // 'client' will be initialized on first access here
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }
}
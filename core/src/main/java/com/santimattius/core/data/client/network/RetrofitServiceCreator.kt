package com.santimattius.core.data.client.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    companion object {
        private val services: TheMovieDBService? = null
        private var instance: RetrofitServiceCreator? = null

        fun getInstance(baseUrl: String, apiKey: String): RetrofitServiceCreator {
            if (instance == null) {
                instance = RetrofitServiceCreator(baseUrl, apiKey)
            }
            return instance!!
        }


        fun createService(baseUrl: String, apiKey: String): TheMovieDBService {
            if (services == null) {
                return getInstance(
                    baseUrl,
                    apiKey,
                ).createService(TheMovieDBService::class.java)
            }
            return services
        }
    }
}
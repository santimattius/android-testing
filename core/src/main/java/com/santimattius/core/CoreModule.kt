package com.santimattius.core

import android.content.Context
import com.santimattius.core.data.client.database.TheMovieDataBase
import com.santimattius.core.data.client.network.RequestInterceptor
import com.santimattius.core.data.client.network.TheMovieDBService
import okhttp3.OkHttpClient
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class CoreModule {

    @Single(createdAtStart = true)
    fun provideOkHttpClient(@Named("apiKey") apiKey: String): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(apiKey))
            .build()
    }

    @Single(createdAtStart = true)
    fun provideRetrofit(
        @Named("baseUrl") baseUrl: String,
        client: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Single(createdAtStart = true)
    fun provideService(retrofit: Retrofit): TheMovieDBService {
        return retrofit.create(TheMovieDBService::class.java)
    }

    @Single(createdAtStart = true)
    fun provideAppDatabase(context: Context): TheMovieDataBase =
        TheMovieDataBase.get(context)
}
package com.santimattius.template.startup

import android.content.Context
import androidx.startup.Initializer
import com.santimattius.core.data.client.network.RetrofitServiceCreator
import com.santimattius.core.data.client.network.TheMovieDBService
import com.santimattius.template.BuildConfig
import org.koin.androix.startup.KoinInitializer
import org.koin.core.annotation.KoinExperimentalAPI

class RetrofitServiceInitializer : Initializer<TheMovieDBService> {

    override fun create(context: Context): TheMovieDBService {
        return RetrofitServiceCreator.createService(
            baseUrl = "https://api.themoviedb.org",
            BuildConfig.apiKey)
    }

    @OptIn(KoinExperimentalAPI::class)
    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(KoinInitializer::class.java)
    }
}
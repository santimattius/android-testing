package com.santimattius.core.data.client.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.santimattius.core.data.models.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class TheMovieDataBase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "tmdb_database"

        fun get(context: Context) =
            Room.databaseBuilder(context, TheMovieDataBase::class.java, DATABASE_NAME)
                .build()
    }

    abstract fun dao(): MovieDao
}
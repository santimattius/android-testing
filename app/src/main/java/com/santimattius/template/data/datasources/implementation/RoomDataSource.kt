package com.santimattius.template.data.datasources.implementation

import android.content.Context
import com.santimattius.template.data.client.database.AppDataBase
import com.santimattius.template.data.client.database.MovieDao
import com.santimattius.template.data.datasources.LocalDataSource
import com.santimattius.template.data.entities.MovieEntity

class RoomDataSource(
    context: Context,
) : LocalDataSource {

    private val dao: MovieDao = AppDataBase.get(context).dao()
    override suspend fun getAll(): List<MovieEntity> = dao.getAll()

    override suspend fun isEmpty() = runSafe { count() == 0 }
        .getOrDefault(defaultValue = true)

    override suspend fun save(movies: List<MovieEntity>): Result<Boolean> =
        runSafe {
            deleteAndInsert(*movies.toTypedArray()); true
        }

    override suspend fun find(id: Int) =
        runSafe { findById(id) }.fold(onSuccess = {
            if (it == null) Result.failure(MovieNoExists())
            else Result.success(it)
        }, onFailure = { Result.failure(MovieNoExists()) })

    override suspend fun delete(movie: MovieEntity) = runSafe { delete(movie); true }

    override suspend fun update(movie: MovieEntity) = runSafe { update(movie); true }

    private suspend fun <R> runSafe(block: suspend MovieDao.() -> R) =
        dao.runCatching { block() }
}
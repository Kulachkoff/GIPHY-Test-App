package com.chililabs.giphytest.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chililabs.giphytest.data.local.dao.FavoriteGifDao
import com.chililabs.giphytest.data.local.entity.FavoriteGifEntity

@Database(
    entities = [FavoriteGifEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteGifDao(): FavoriteGifDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(appContext: Context): AppDatabase {
            return instance ?: Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "core-pos-database"
            )
                .fallbackToDestructiveMigration(false)
                .build()
                .also { instance = it }
        }
    }
}
package com.threes.scenespotinseoul.data

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.threes.scenespotinseoul.data.dao.DataInfoDao
import com.threes.scenespotinseoul.data.dao.LocationDao
import com.threes.scenespotinseoul.data.dao.LocationTagDao
import com.threes.scenespotinseoul.data.dao.MediaDao
import com.threes.scenespotinseoul.data.dao.MediaTagDao
import com.threes.scenespotinseoul.data.dao.SceneDao
import com.threes.scenespotinseoul.data.dao.SceneTagDao
import com.threes.scenespotinseoul.data.dao.TagDao
import com.threes.scenespotinseoul.data.model.DataInfo
import com.threes.scenespotinseoul.data.model.Location
import com.threes.scenespotinseoul.data.model.LocationTag
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.data.model.MediaTag
import com.threes.scenespotinseoul.data.model.Scene
import com.threes.scenespotinseoul.data.model.SceneTag
import com.threes.scenespotinseoul.data.model.Tag
import com.threes.scenespotinseoul.utilities.DATABASE_NAME

@Database(
    entities = [
        Location::class,
        LocationTag::class,
        Media::class,
        MediaTag::class,
        Scene::class,
        SceneTag::class,
        Tag::class,
        DataInfo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun locationTagDao(): LocationTagDao

    abstract fun mediaDao(): MediaDao

    abstract fun mediaTagDao(): MediaTagDao

    abstract fun sceneDao(): SceneDao

    abstract fun sceneTagDao(): SceneTagDao

    abstract fun tagDao(): TagDao

    abstract fun dataInfoDao(): DataInfoDao

    companion object {

        @Volatile
        private var instance: AppDatabase? = null

        @JvmStatic
        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
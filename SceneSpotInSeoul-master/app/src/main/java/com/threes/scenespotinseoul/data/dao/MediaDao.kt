package com.threes.scenespotinseoul.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.threes.scenespotinseoul.data.model.Media

@Dao
interface MediaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(media: Media): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(media: List<Media>): List<Long>

    @Query("SELECT * FROM media WHERE ROWID = :rowId")
    fun loadByRowId(rowId: Long): Media

    @Query("SELECT * FROM media WHERE uuid = :mediaId")
    fun loadById(mediaId: String): Media?

    @Query("SELECT * FROM media WHERE name = :name")
    fun loadByName(name: String): Media?

    @Query("SELECT * FROM media")
    fun loadAll(): List<Media>

    @Query("SELECT COUNT(*) FROM media")
    fun getNumberOfRows(): LiveData<Int>

    @Update
    fun update(media: Media)

    @Delete
    fun delete(media: Media)
}
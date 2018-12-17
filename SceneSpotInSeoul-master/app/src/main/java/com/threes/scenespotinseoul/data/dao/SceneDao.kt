package com.threes.scenespotinseoul.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.threes.scenespotinseoul.data.model.Scene

@Dao
interface SceneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(scene: Scene): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(scenes: List<Scene>): List<Long>

    @Query("SELECT * FROM scenes WHERE ROWID = :rowId")
    fun loadByRowId(rowId: Long): Scene

    @Query("SELECT * FROM scenes WHERE ROWID = :rowId")
    fun loadByRowIdWithLive(rowId: Long): LiveData<Scene>

    @Query("SELECT * FROM scenes WHERE uuid = :sceneId")
    fun loadById(sceneId: String): Scene?

    @Query("SELECT * FROM scenes WHERE uuid = :sceneId")
    fun loadByIdWithLive(sceneId: String): LiveData<Scene>

    @Query("SELECT * FROM scenes WHERE mediaId = :mediaId")
    fun loadByMediaId(mediaId: String): List<Scene>

    @Query("SELECT * FROM scenes WHERE locationId = :locationId")
    fun loadByLocationId(locationId: String): List<Scene>

    @Query("SELECT * FROM scenes WHERE locationId = :locationId AND mediaId = :mediaId")
    fun loadByLocationAndMediaId(locationId: String, mediaId: String): List<Scene>

    @Query("SELECT * FROM scenes WHERE isUploaded = 1 ORDER BY uploadedDate DESC")
    fun loadAllAreUploaded(): List<Scene>

    @Query("SELECT * FROM scenes WHERE isUploaded = 1 ORDER BY uploadedDate DESC")
    fun loadAllAreUploadedWithLive(): LiveData<List<Scene>>

    @Query("SELECT * FROM scenes")
    fun loadAll(): List<Scene>

    @Update
    fun update(scene: Scene)

    @Delete
    fun delete(scene: Scene)
}
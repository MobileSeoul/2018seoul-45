package com.threes.scenespotinseoul.data.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.threes.scenespotinseoul.data.model.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(locations: List<Location>): List<Long>

    @Query("SELECT * FROM locations WHERE ROWID = :rowId")
    fun loadByRowId(rowId: Long): Location

    @Query("SELECT * FROM locations WHERE uuid = :locationId")
    fun loadById(locationId: String): Location?

    @Query("SELECT * FROM locations WHERE name = :name")
    fun loadByName(name: String): Location?

    @Query("SELECT * FROM locations WHERE isVisited = 1")
    fun loadAllAreVisited(): List<Location>

    @Query("SELECT * FROM locations")
    fun loadAll(): List<Location>

    @Query("SELECT * FROM locations")
    fun loadAllWithLive(): LiveData<List<Location>>

    @Update
    fun update(location: Location)

    @Delete
    fun delete(location: Location)
}
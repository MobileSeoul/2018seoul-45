package com.threes.scenespotinseoul.data.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.threes.scenespotinseoul.data.model.DataInfo

@Dao
interface DataInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(dataInfo: DataInfo): Long

    @Query("SELECT * FROM data_info WHERE name = :tableName")
    fun load(tableName: String): DataInfo?

    @Update
    fun update(dataInfo: DataInfo)
}
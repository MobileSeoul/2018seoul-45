package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "data_info")
data class DataInfo(
    @PrimaryKey val name: String,
    var updatedDate: Long
)
package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.threes.scenespotinseoul.utilities.TAG_TABLE

@Entity(
    tableName = TAG_TABLE,
    indices = [Index("name", unique = true)]
)
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
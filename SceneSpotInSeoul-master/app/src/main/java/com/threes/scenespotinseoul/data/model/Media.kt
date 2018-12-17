package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.threes.scenespotinseoul.utilities.MEDIA_TABLE

@Entity(tableName = MEDIA_TABLE)
data class Media(
    @PrimaryKey val uuid: String,
    val name: String,
    val desc: String,
    val image: String
)
package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.threes.scenespotinseoul.utilities.LOCATION_TABLE

@Entity(tableName = LOCATION_TABLE)
data class Location(
    @PrimaryKey val uuid: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val desc: String,
    val address: String,
    val image: String
) {
    var isVisited: Boolean = false
}
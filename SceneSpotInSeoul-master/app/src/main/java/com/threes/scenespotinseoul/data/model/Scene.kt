package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import com.threes.scenespotinseoul.utilities.SCENE_TABLE
import java.util.UUID

@Entity(
    tableName = SCENE_TABLE,
    indices = [Index("mediaId"), Index("locationId")],
    foreignKeys = [
        ForeignKey(
            entity = Media::class,
            parentColumns = arrayOf("uuid"),
            childColumns = arrayOf("mediaId"),
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Location::class,
            parentColumns = arrayOf("uuid"),
            childColumns = arrayOf("locationId"),
            onDelete = CASCADE
        )
    ]
)
data class Scene(
    @PrimaryKey val uuid: String = UUID.randomUUID().toString(),
    val mediaId: String,
    val locationId: String,
    val desc: String,
    val image: String
) {
    var isUploaded: Boolean = false
    var uploadedImage: String? = null
    var uploadedDate: Long = 0L
}
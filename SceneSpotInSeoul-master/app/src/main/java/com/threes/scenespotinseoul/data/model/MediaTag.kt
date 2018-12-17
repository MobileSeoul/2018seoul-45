package com.threes.scenespotinseoul.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import com.threes.scenespotinseoul.utilities.MEDIA_TAG_TABLE

@Entity(
    tableName = MEDIA_TAG_TABLE,
    primaryKeys = ["tagId", "mediaId"],
    indices = [Index("tagId"), Index("mediaId")],
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tagId"),
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Media::class,
            parentColumns = arrayOf("uuid"),
            childColumns = arrayOf("mediaId"),
            onDelete = CASCADE
        )
    ]
)
data class MediaTag(
    val tagId: Long,
    val mediaId: String
)
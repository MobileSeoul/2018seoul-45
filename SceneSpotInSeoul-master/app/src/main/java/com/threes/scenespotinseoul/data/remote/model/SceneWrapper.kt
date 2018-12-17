package com.threes.scenespotinseoul.data.remote.model

import com.google.gson.annotations.SerializedName

data class SceneWrapper(
    @SerializedName("data") val sceneContent: SceneContent,
    @SerializedName("related_location") val locationName: String,
    @SerializedName("related_media") val mediaName: String,
    val tags: List<String>
)
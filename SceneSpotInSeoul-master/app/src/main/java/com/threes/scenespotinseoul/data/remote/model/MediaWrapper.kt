package com.threes.scenespotinseoul.data.remote.model

import com.google.gson.annotations.SerializedName
import com.threes.scenespotinseoul.data.model.Media

data class MediaWrapper(@SerializedName("data") val media: Media, val tags: List<String>)
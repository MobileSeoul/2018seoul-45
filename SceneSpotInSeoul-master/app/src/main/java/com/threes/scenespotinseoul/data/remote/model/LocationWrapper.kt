package com.threes.scenespotinseoul.data.remote.model

import com.google.gson.annotations.SerializedName
import com.threes.scenespotinseoul.data.model.Location

data class LocationWrapper(@SerializedName("data") val location: Location, val tags: List<String>)
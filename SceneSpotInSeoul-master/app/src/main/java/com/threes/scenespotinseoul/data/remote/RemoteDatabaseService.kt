package com.threes.scenespotinseoul.data.remote

import com.threes.scenespotinseoul.data.model.DataInfo
import com.threes.scenespotinseoul.data.remote.model.LocationWrapper
import com.threes.scenespotinseoul.data.remote.model.MediaWrapper
import com.threes.scenespotinseoul.data.remote.model.SceneWrapper
import retrofit2.Call
import retrofit2.http.GET

interface RemoteDatabaseService {

    @GET("/dev/info")
    fun loadInfo(): Call<List<DataInfo>>

    @GET("/dev/locations")
    fun loadLocations(): Call<List<LocationWrapper>>

    @GET("/dev/media")
    fun loadMedia(): Call<List<MediaWrapper>>

    @GET("/dev/scenes")
    fun loadScenes(): Call<List<SceneWrapper>>
}
package com.threes.scenespotinseoul.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.threes.scenespotinseoul.data.AppDatabase
import com.threes.scenespotinseoul.ui.main.adapter.MediaCategory
import com.threes.scenespotinseoul.utilities.runOnDiskIO
import com.threes.scenespotinseoul.utilities.runOnMain

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var db: AppDatabase = AppDatabase.getInstance(application)

    private val _mediaCategoryData = MutableLiveData<List<MediaCategory>>()

    val mediaCategory: LiveData<List<MediaCategory>>
        get() = _mediaCategoryData

    val mediaCount: LiveData<Int> = db.mediaDao().getNumberOfRows()

    fun loadMediaCategory() {
        runOnDiskIO {
            val categories = arrayOf("예능", "드라마", "영화")
            val mediaCategories = mutableListOf<MediaCategory>()
            categories.forEach { it ->
                val tag = db.tagDao().loadByName(it)
                val mediaTags = db.mediaTagDao().loadByTagId(tag?.id!!)
                val media = mediaTags.map { db.mediaDao().loadById(it.mediaId)!! }
                mediaCategories.add(MediaCategory(it, media))
            }
            runOnMain {
                _mediaCategoryData.value = mediaCategories
            }
        }
    }
}
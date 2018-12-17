package com.threes.scenespotinseoul.ui.search

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.StringDef
import com.threes.scenespotinseoul.data.AppDatabase
import com.threes.scenespotinseoul.data.model.Location
import com.threes.scenespotinseoul.data.model.Media
import com.threes.scenespotinseoul.data.model.Scene
import com.threes.scenespotinseoul.utilities.AppExecutors
import com.threes.scenespotinseoul.utilities.runOnDiskIO
import com.threes.scenespotinseoul.utilities.runOnMain

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private var db: AppDatabase = AppDatabase.getInstance(application)
    private var executors: AppExecutors = AppExecutors()

    private val _tagAutoCompleteData = MutableLiveData<List<String>>()
    private val _showSearchResult = MutableLiveData<String>()

    val tagAutoCompleteData: LiveData<List<String>>
        get() = _tagAutoCompleteData

    var searchResultMediaData: List<Media> = listOf()
    var searchResultLocationData: List<Location> = listOf()
    var searchResultSceneData: List<Scene> = listOf()

    val showSearchResult: LiveData<String>
        get() = _showSearchResult

    fun loadTagAutoComplete(keyword: String) {
        if (keyword.isEmpty()) {
            _tagAutoCompleteData.value = listOf()
        } else {
            executors.diskIO().execute {
                val tags = db.tagDao().loadBySimilarName("%$keyword%", 5)
                executors.mainThread().execute {
                    _tagAutoCompleteData.value = tags.map { it.name }
                }
            }
        }
    }

    fun requestSearch(@RequestType requestType: String, keyword: String) {
        when (requestType) {
            TYPE_EXACTLY -> {
                runOnDiskIO {
                    val tag = db.tagDao().loadByName(keyword.trim())
                    val tagId = tag?.id!!

                    val mediaTags = db.mediaTagDao().loadByTagId(tagId)
                    val media = mediaTags.map {
                        db.mediaDao().loadById(it.mediaId)!!
                    }

                    val locationTags = db.locationTagDao().loadByTagId(tagId)
                    val locations = locationTags.map {
                        db.locationDao().loadById(it.locationId)!!
                    }

                    val sceneTags = db.sceneTagDao().loadByTagId(tagId)
                    val scenes = sceneTags.map {
                        db.sceneDao().loadById(it.sceneId)!!
                    }

                    runOnMain {
                        searchResultMediaData = media
                        searchResultLocationData = locations
                        searchResultSceneData = scenes
                        _showSearchResult.value = keyword
                    }
                }
            }
            else -> {
                runOnDiskIO {
                    val tags = db.tagDao().loadBySimilarName("%${keyword.trim()}%")

                    val media = tags
                            .flatMap { db.mediaTagDao().loadByTagId(it.id) }
                            .map { db.mediaDao().loadById(it.mediaId)!! }
                            .distinctBy { it.uuid }

                    val locations = tags
                            .flatMap { db.locationTagDao().loadByTagId(it.id) }
                            .map { db.locationDao().loadById(it.locationId)!! }
                            .distinctBy { it.uuid }

                    val scenes = tags
                            .flatMap { db.sceneTagDao().loadByTagId(it.id) }
                            .map { db.sceneDao().loadById(it.sceneId)!! }
                            .distinctBy { it.uuid }

                    runOnMain {
                        searchResultMediaData = media
                        searchResultLocationData = locations
                        searchResultSceneData = scenes
                        _showSearchResult.value = keyword
                    }
                }
            }
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
            TYPE_SIMILAR,
            TYPE_EXACTLY
    )
    annotation class RequestType

    companion object {
        const val TYPE_SIMILAR = "type_similar"
        const val TYPE_EXACTLY = "type_exactly"
    }
}
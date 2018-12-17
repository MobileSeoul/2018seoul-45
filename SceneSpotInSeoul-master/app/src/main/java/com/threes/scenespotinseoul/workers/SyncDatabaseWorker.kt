package com.threes.scenespotinseoul.workers

import android.util.Log
import androidx.work.Worker
import com.threes.scenespotinseoul.data.AppDatabase
import com.threes.scenespotinseoul.data.model.DataInfo
import com.threes.scenespotinseoul.data.model.LocationTag
import com.threes.scenespotinseoul.data.model.MediaTag
import com.threes.scenespotinseoul.data.model.Scene
import com.threes.scenespotinseoul.data.model.SceneTag
import com.threes.scenespotinseoul.data.model.Tag
import com.threes.scenespotinseoul.data.remote.RemoteDatabaseService
import com.threes.scenespotinseoul.data.remote.model.LocationWrapper
import com.threes.scenespotinseoul.data.remote.model.MediaWrapper
import com.threes.scenespotinseoul.data.remote.model.SceneContent
import com.threes.scenespotinseoul.data.remote.model.SceneWrapper
import com.threes.scenespotinseoul.utilities.LOCATION_TABLE
import com.threes.scenespotinseoul.utilities.MEDIA_TABLE
import com.threes.scenespotinseoul.utilities.REMOTE_ENDPOINT_URL
import com.threes.scenespotinseoul.utilities.SCENE_TABLE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SyncDatabaseWorker : Worker() {

    companion object {
        private val TAG = SyncDatabaseWorker::class.java.simpleName
    }

    override fun doWork(): Result {
        val db = AppDatabase.getInstance(applicationContext)
        val retrofit = Retrofit.Builder()
            .baseUrl(REMOTE_ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RemoteDatabaseService::class.java)
        val infoResponse = service.loadInfo().execute()
        try {
            var updatedTables = 0
            infoResponse.body()?.forEach {
                val localInfo = db.dataInfoDao().load(it.name)
                if (localInfo == null || isOutOfDate(localInfo, it)) {
                    Log.d(TAG, "${it.name} is out-of-date. Proceed Sync.")
                    when (it.name) {
                        LOCATION_TABLE -> {
                            val locationsResponse = service.loadLocations().execute()
                            val locationWrappers = locationsResponse.body()
                            if (locationWrappers != null && locationWrappers.isNotEmpty()) {
                                writeLocations(db, locationWrappers)
                                writeDataInfo(db, it)
                            }
                        }
                        MEDIA_TABLE -> {
                            val mediaResponse = service.loadMedia().execute()
                            val mediaWrappers = mediaResponse.body()
                            if (mediaWrappers != null && mediaWrappers.isNotEmpty()) {
                                writeMedia(db, mediaWrappers)
                                writeDataInfo(db, it)
                            }
                        }
                        SCENE_TABLE -> {
                            val scenesResponse = service.loadScenes().execute()
                            val sceneWrappers = scenesResponse.body()
                            if (sceneWrappers != null && sceneWrappers.isNotEmpty()) {
                                writeScenes(db, sceneWrappers)
                                writeDataInfo(db, it)
                            }
                        }
                    }
                    updatedTables++
                } else {
                    Log.d(TAG, "${it.name} is up-to-date. Passed Sync.")
                }
            }
            if (updatedTables > 0) {
                pruneTags(db)
            }
            return Worker.Result.SUCCESS
        } catch (ex: IOException) {
            return Worker.Result.FAILURE
        }
    }

    private fun isOutOfDate(localInfo: DataInfo, remoteInfo: DataInfo): Boolean =
        localInfo.updatedDate < remoteInfo.updatedDate

    private fun writeLocations(db: AppDatabase, locationWrappers: List<LocationWrapper>) {
        val updatedIds = mutableListOf<String>()
        locationWrappers.forEach { it ->
            val locationId = it.location.uuid
            with(it.location) {
                val oldLocation = db.locationDao().loadById(locationId)
                if (oldLocation == null) {
                    db.locationDao().insert(this)
                } else {
                    if (oldLocation != this) {
                        // 사용자 데이터를 새로 작성할 데이터에 저장
                        this.isVisited = oldLocation.isVisited
                        db.locationDao().update(this)
                    }
                }
                updatedIds.add(locationId)
            }
            with(it.tags) {
                // 서버에서 사라진 태그 정보가 있는 것을 대비하여 모든 관계를 삭제 후 재생성
                val oldLocationTags = db.locationTagDao().loadByLocationId(locationId)
                db.locationTagDao().deleteAll(oldLocationTags)
                forEach { tagName ->
                    val tagId = writeTag(db, tagName)
                    db.locationTagDao().insert(LocationTag(tagId, locationId))
                }
            }
        }
        // 서버에서 받은 데이터에 포함되지 않던 장소 삭제
        val oldLocations = db.locationDao().loadAll()
        oldLocations.forEach {
            if (updatedIds.indexOf(it.uuid) == -1) {
                db.locationDao().delete(it)
            }
        }
    }

    private fun writeMedia(db: AppDatabase, mediaWrappers: List<MediaWrapper>) {
        val updatedIds = mutableListOf<String>()
        mediaWrappers.forEach { it ->
            val mediaId = it.media.uuid
            with(it.media) {
                val oldMedia = db.mediaDao().loadById(mediaId)
                if (oldMedia == null) {
                    db.mediaDao().insert(this)
                } else {
                    if (oldMedia != this) {
                        db.mediaDao().update(this)
                    }
                }
                updatedIds.add(mediaId)
            }
            with(it.tags) {
                // 서버에서 사라진 태그 정보가 있는 것을 대비하여 모든 관계를 삭제 후 재생성
                val oldMediaTags = db.mediaTagDao().loadByMediaId(mediaId)
                db.mediaTagDao().deleteAll(oldMediaTags)
                forEach { tagName ->
                    val tagId = writeTag(db, tagName)
                    db.mediaTagDao().insert(MediaTag(tagId, mediaId))
                }
            }
        }
        // 서버에서 받은 데이터에 포함되지 않던 미디어 삭제
        val oldMedia = db.mediaDao().loadAll()
        oldMedia.forEach {
            if (updatedIds.indexOf(it.uuid) == -1) {
                db.mediaDao().delete(it)
            }
        }
    }

    private fun writeScenes(db: AppDatabase, sceneWrappers: List<SceneWrapper>) {
        val updatedIds = mutableListOf<String>()
        sceneWrappers.forEach { it ->
            val sceneId = it.sceneContent.uuid
            with(it.sceneContent) {
                val oldScene = db.sceneDao().loadById(sceneId)
                if (oldScene == null) {
                    val mediaId = db.mediaDao().loadByName(it.mediaName)?.uuid
                    val locationId = db.locationDao().loadByName(it.locationName)?.uuid

                    if (mediaId == null || locationId == null) {
                        return@forEach
                    }

                    db.sceneDao().insert(
                        Scene(
                            uuid = sceneId,
                            desc = this.desc,
                            image = this.image,
                            mediaId = mediaId,
                            locationId = locationId
                        )
                    )
                } else {
                    val newMediaId = db.mediaDao().loadByName(it.mediaName)?.uuid
                    val newLocationId = db.locationDao().loadByName(it.locationName)?.uuid

                    if (newMediaId == null || newLocationId == null) {
                        return@forEach
                    }

                    if (oldScene.mediaId != newMediaId ||
                        oldScene.locationId != newLocationId ||
                        SceneContent(oldScene.uuid, oldScene.desc, oldScene.image) != this
                    ) {
                        val newScene = Scene(
                            uuid = sceneId,
                            desc = this.desc,
                            image = this.image,
                            mediaId = newMediaId,
                            locationId = newLocationId
                        )
                        newScene.isUploaded = oldScene.isUploaded
                        newScene.uploadedImage = oldScene.uploadedImage
                        newScene.uploadedDate = oldScene.uploadedDate
                        db.sceneDao().update(newScene)
                    }
                }
                updatedIds.add(sceneId)
            }
            with(it.tags) {
                // 서버에서 사라진 태그 정보가 있는 것을 대비하여 모든 관계를 삭제 후 재생성
                val oldSceneTags = db.sceneTagDao().loadBySceneId(sceneId)
                db.sceneTagDao().deleteAll(oldSceneTags)
                forEach { tagName ->
                    val tagId = writeTag(db, tagName)
                    db.sceneTagDao().insert(SceneTag(tagId, sceneId))
                }
            }
        }
        // 서버에서 받은 데이터에 포함되지 않던 장면 삭제
        val oldScenes = db.sceneDao().loadAll()
        oldScenes.forEach {
            if (updatedIds.indexOf(it.uuid) == -1) {
                db.sceneDao().delete(it)
            }
        }
    }

    private fun writeTag(db: AppDatabase, tagName: String): Long {
        val tag = db.tagDao().loadByName(tagName)
        return if (tag != null) tag.id
        else {
            val tagRowId = db.tagDao().insert(Tag(0, tagName))
            db.tagDao().loadByRowId(tagRowId).id
        }
    }

    private fun pruneTags(db: AppDatabase) {
        Log.d(TAG, "Proceed prune tags")
        val tags = db.tagDao().loadAll()
        tags.forEach {
            val locationTags = db.locationTagDao().loadByTagId(it.id)
            val mediaTags = db.mediaTagDao().loadByTagId(it.id)
            val sceneTags = db.sceneTagDao().loadByTagId(it.id)
            if (locationTags.isEmpty() && mediaTags.isEmpty() && sceneTags.isEmpty()) {
                db.tagDao().delete(it)
            }
        }
    }

    private fun writeDataInfo(db: AppDatabase, newInfo: DataInfo) {
        val oldInfo = db.dataInfoDao().load(newInfo.name)
        if (oldInfo == null) {
            db.dataInfoDao().insert(newInfo)
        } else {
            db.dataInfoDao().update(newInfo)
        }
    }
}
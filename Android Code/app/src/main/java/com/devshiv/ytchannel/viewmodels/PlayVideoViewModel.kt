package com.devshiv.ytchannel.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.devshiv.ytchannel.db.dao.FavouritesDao
import com.devshiv.ytchannel.db.entity.FavouritesEntity
import com.devshiv.ytchannel.model.VideosModel
import com.devshiv.ytchannel.repository.DataRepository
import com.devshiv.ytchannel.utils.Constants.TAG
import javax.inject.Inject

@HiltViewModel
class PlayVideoViewModel @Inject constructor(
    private var repository: DataRepository,
    private var favouritesDao: FavouritesDao,
) : ViewModel() {

    suspend fun addToFav(video: VideosModel): Boolean {
        val record: FavouritesEntity? = favouritesDao.findDataByLink(video.Link)
        return if (record != null) {
            favouritesDao.deleteByLink(video.Link)
            false
        } else {
            val favouritesEntity = FavouritesEntity(title = video.Title, link = video.Link)
            favouritesDao.addData(favouritesEntity)
            true
        }
    }

    suspend fun removeFromFav(video: VideosModel): Boolean {
        val record: FavouritesEntity? = favouritesDao.findDataByLink(video.Link)
        Log.d(TAG, "removeFromFav: $record")
        return if (record == null) {
            true
        } else {
            favouritesDao.deleteByLink(video.Link)
            true
        }
    }

    suspend fun getFavVideos(): List<FavouritesEntity> {
        val record = favouritesDao.getAllData()
        return record
    }

    suspend fun checkInFav(video: VideosModel): Boolean {
        val record: FavouritesEntity = favouritesDao.findDataByLink(video.Link)
        return if (record == null) {
            false
        } else {
            true
        }
    }

}
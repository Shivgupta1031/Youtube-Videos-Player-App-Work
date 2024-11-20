package com.devshiv.ytchannel.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.devshiv.ytchannel.db.entity.SettingsEntity
import com.devshiv.ytchannel.model.BannersModel
import com.devshiv.ytchannel.model.VidCatModel
import com.devshiv.ytchannel.model.VideosModel
import com.devshiv.ytchannel.utils.Constants
import com.devshiv.ytchannel.utils.Constants.TAG
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class DataRepository {

    suspend fun getSettings(): Flow<SettingsEntity> = callbackFlow {
        val docRef =
            Firebase.firestore.collection(Constants.SETTINGS_COLLECTION).document(Constants.APP_DOC)

        val eventListener = docRef.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting settings", exception)
                close(exception) // Close the flow with an error
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val data = snapshot.toObject(SettingsEntity::class.java)
                if (data != null) {
                    trySend(data) // Emit the data to the flow
                } else {
                    Log.e(TAG, "Error converting snapshot to SettingsEntity")
                    close(IllegalStateException("Error converting snapshot to SettingsEntity"))
                }
            } else {
                Log.e(TAG, "No settings document")
                close(NoSuchElementException("No settings document"))
            }
        }

        awaitClose { eventListener.remove() }
    }

    suspend fun getBanners(): Flow<List<VideosModel>> = callbackFlow {
        val collection = Firebase.firestore
        val docRef = collection.collection(Constants.BANNERS_COLLECTION)
        val dataList = ArrayList<VideosModel>()

        val eventListener = docRef.addSnapshotListener { documents, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting Banners", exception)
                close(exception) // Close the flow with an error
                return@addSnapshotListener
            }
            if (documents != null && !documents.isEmpty) {
                for (doc: DocumentSnapshot in documents.documents) {
                    val banner = doc.toObject(BannersModel::class.java)
                    if (banner != null) {
                        collection.collection(Constants.VIDEOS_COLLECTION).document(banner.VideoRef)
                            .get()
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val video = it.result.toObject(VideosModel::class.java)
                                    if (video != null) {
                                        dataList.add(video)
                                        trySend(dataList)
                                    }
                                }
                            }
                    }
                }
            } else {
                Log.d(TAG, "No Banner Found")
                close(NoSuchElementException("No Banner Found"))
            }
        }
        awaitClose { eventListener.remove() }
    }

    suspend fun getVideoCategories(): Flow<List<VidCatModel>> = getVideos().map { videosList ->
        val sortedVidCatList = videosList
            .groupBy { it.Category }
            .values
            .map { videos ->
                VidCatModel(
                    videos[0].Category,
                    videos.sortedByDescending { it.Created_At }
                        .distinctBy { it.Link },
                    videos[0].Created_At
                )
            }
            .distinctBy { it.category }
            .sortedByDescending { it.Created_At }
        sortedVidCatList
    }

    private fun getVideos(): Flow<List<VideosModel>> = callbackFlow {
        val collection = Firebase.firestore
        val docRef = collection.collection(Constants.VIDEOS_COLLECTION)
        val dataList = ArrayList<VideosModel>()

        val eventListener = docRef.addSnapshotListener { documents, exception ->
            if (exception != null) {
                Log.e(TAG, "Error getting Videos", exception)
                close(exception) // Close the flow with an error
                return@addSnapshotListener
            }
            if (documents != null && !documents.isEmpty) {
                for (doc: DocumentSnapshot in documents.documents) {
                    val video = doc.toObject(VideosModel::class.java)
                    if (video != null) {
                        dataList.add(video)
                    }
                }
                trySend(dataList)
            } else {
                Log.d(TAG, "No Videos Found")
                close(NoSuchElementException("No Videos Found"))
            }
        }
        awaitClose { eventListener.remove() }
    }
}
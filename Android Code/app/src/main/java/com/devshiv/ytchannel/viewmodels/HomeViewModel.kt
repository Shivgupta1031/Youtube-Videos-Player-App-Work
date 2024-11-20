package com.devshiv.ytchannel.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.devshiv.ytchannel.db.dao.FavouritesDao
import com.devshiv.ytchannel.db.entity.FavouritesEntity
import com.devshiv.ytchannel.repository.DataRepository
import com.devshiv.ytchannel.utils.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var repository: DataRepository,
    private var favouritesDao: FavouritesDao,
) : ViewModel() {

    private val _banners = MutableStateFlow<ApiState>(ApiState.Empty)
    val banners: StateFlow<ApiState> = _banners.asStateFlow()

    private val _vidCategories = MutableStateFlow<ApiState>(ApiState.Empty)
    val vidCategories: StateFlow<ApiState> = _vidCategories.asStateFlow()

    init {
        viewModelScope.launch {
            _banners.value = ApiState.Loading
            repository.getBanners()
                .catch { e ->
                    _banners.value = ApiState.Failure(e)
                }.collect {
                    _banners.value = ApiState.Success(it)
                }
        }
        viewModelScope.launch {
            _vidCategories.value = ApiState.Loading
            repository.getVideoCategories()
                .catch { e ->
                    _vidCategories.value = ApiState.Failure(e)
                }.collect {
                    _vidCategories.value = ApiState.Success(it)
                }
        }
    }

    suspend fun getFavVideos(): List<FavouritesEntity> {
        val record = favouritesDao.getAllData()
        return record
    }

}
package com.devshiv.ytchannel.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.devshiv.ytchannel.App
import com.devshiv.ytchannel.db.dao.SettingsDao
import com.devshiv.ytchannel.db.entity.SettingsEntity
import com.devshiv.ytchannel.repository.DataRepository
import com.devshiv.ytchannel.utils.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private var repository: DataRepository,
    private var settingsDao: SettingsDao,
) : ViewModel() {

    private val _settings = MutableStateFlow<ApiState>(ApiState.Empty)
    val settings: StateFlow<ApiState> = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            _settings.value = ApiState.Loading
            repository.getSettings()
                .catch { e ->
                    _settings.value = ApiState.Failure(e)
                }.collect {
                    _settings.value = ApiState.Success(it)
                }
        }
    }

    fun saveSettings(data: SettingsEntity) {
        App.settings = data

        viewModelScope.launch {
            settingsDao.addData(data)
        }
    }
}
package com.devshiv.ytchannel

import android.app.Application
import com.devshiv.ytchannel.db.entity.SettingsEntity
import com.devshiv.ytchannel.utils.getAppVersion
import com.devshiv.ytchannel.utils.getUserDeviceId
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app_version = getAppVersion()
        device_id = getUserDeviceId()
    }

    companion object {
        var device_id = ""
        var app_version = ""
        var settings: SettingsEntity = SettingsEntity()
        var curUser = ""
    }
}
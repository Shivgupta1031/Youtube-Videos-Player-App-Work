package com.devshiv.ytchannel.utils

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.provider.Settings

fun Context.getAppVersion(): String {
    try {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return "Unknown"
}

fun Context.getUserDeviceId(): String {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}
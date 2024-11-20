package com.devshiv.ytchannel.db.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings_db")
data class SettingsEntity(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int = 1,
    @ColumnInfo(name = "banner_id")
    var banner_id: String = "",
    @ColumnInfo(name = "rewarded_id")
    var rewarded_id: String = "",
    @ColumnInfo(name = "interstitial_id")
    var interstitial_id: String = "",
    @ColumnInfo(name = "interstitial_1")
    var interstitial_1: String = "",
    @ColumnInfo(name = "interstitial_2")
    var interstitial_2: String = "",
    @ColumnInfo(name = "interstitial_3")
    var interstitial_3: String = "",
    @ColumnInfo(name = "interstitial_4")
    var interstitial_4: String = "",
    @ColumnInfo(name = "interstitial_5")
    var interstitial_5: String = "",
    @ColumnInfo(name = "interstitial_6")
    var interstitial_6: String = "",
    @ColumnInfo(name = "interstitial_7")
    var interstitial_7: String = "",
    @ColumnInfo(name = "applovin_sdk_key")
    var applovin_sdk_key: String = "",
    @ColumnInfo(name = "admob_app_id")
    var admob_app_id: String = "",
    @ColumnInfo(name = "one_signal_id")
    var one_signal_id: String = "",
    @ColumnInfo(name = "privacy_policy")
    var privacy_policy: String = "",
    @ColumnInfo(name = "show_ads")
    var show_ads: Boolean = false,
)

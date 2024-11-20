package com.devshiv.ytchannel.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.devshiv.ytchannel.db.dao.FavouritesDao
import com.devshiv.ytchannel.db.dao.SettingsDao
import com.devshiv.ytchannel.db.entity.FavouritesEntity
import com.devshiv.ytchannel.db.entity.SettingsEntity

@Database(entities = [SettingsEntity::class,FavouritesEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun settingsDao(): SettingsDao
    abstract fun favouritesDao(): FavouritesDao

}
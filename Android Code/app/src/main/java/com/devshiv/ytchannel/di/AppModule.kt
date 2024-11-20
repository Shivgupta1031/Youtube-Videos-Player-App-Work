package com.devshiv.ytchannel.di

import android.content.Context
import androidx.room.Room
import com.devshiv.ytchannel.R
import com.devshiv.ytchannel.db.AppDatabase
import com.devshiv.ytchannel.db.dao.SettingsDao
import com.devshiv.ytchannel.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.devshiv.ytchannel.db.dao.FavouritesDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun provideDataRepository(): DataRepository {
        return DataRepository()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            appContext.getString(R.string.app_name).lowercase() + "_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideSettingsDao(appDatabase: AppDatabase): SettingsDao {
        return appDatabase.settingsDao()
    }

    @Provides
    fun provideFavouritesDao(appDatabase: AppDatabase): FavouritesDao {
        return appDatabase.favouritesDao()
    }

}
package com.devshiv.ytchannel.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devshiv.ytchannel.db.entity.SettingsEntity

@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(data: SettingsEntity)

    @Query("SELECT * FROM settings_db WHERE id = :id")
    suspend fun findDataById(id: String): SettingsEntity

    @Query("SELECT * FROM settings_db")
    suspend fun getAllData(): List<SettingsEntity>

    @Update
    suspend fun updateData(data: SettingsEntity)

    @Delete
    suspend fun deleteData(data: SettingsEntity)
}
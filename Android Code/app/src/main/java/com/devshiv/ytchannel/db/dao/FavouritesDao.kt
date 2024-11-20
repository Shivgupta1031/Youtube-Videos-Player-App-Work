package com.devshiv.ytchannel.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.devshiv.ytchannel.db.entity.FavouritesEntity

@Dao
interface FavouritesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addData(data: FavouritesEntity)

    @Query("SELECT * FROM favourites_db WHERE id = :id")
    suspend fun findDataById(id: String): FavouritesEntity

    @Query("SELECT * FROM favourites_db WHERE link = :link")
    suspend fun findDataByLink(link: String): FavouritesEntity

    @Query("SELECT * FROM favourites_db")
    suspend fun getAllData(): List<FavouritesEntity>

    @Query("DELETE FROM favourites_db WHERE link = :link")
    suspend fun deleteByLink(link: String): Int

    @Update
    suspend fun updateData(data: FavouritesEntity)

    @Delete
    suspend fun deleteData(data: FavouritesEntity)
}
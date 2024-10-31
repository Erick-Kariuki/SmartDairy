package com.example.dairyapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cow: CowEntity)

    @Query("SELECT * FROM cows")
    suspend fun getAllCows(): List<CowEntity>

}
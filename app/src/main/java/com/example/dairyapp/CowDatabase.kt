package com.example.dairyapp

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CowEntity::class], version = 1)
abstract class CowDatabase : RoomDatabase() {

    abstract fun cowDao(): CowDao

    companion object{

        lateinit var database : CowDatabase

        private fun CreateDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                CowDatabase::class.java, "cow_db"
            ).build()
    }
}
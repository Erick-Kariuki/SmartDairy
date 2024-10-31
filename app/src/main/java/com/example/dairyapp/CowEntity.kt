package com.example.dairyapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cows")
data class CowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name : String,
    val breed : String,
    val dateOfBirth : String,
    val motherName : String,
    val status : String
)

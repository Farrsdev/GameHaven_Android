package com.example.gamehaven.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val username:String,
    val email:String,
    val password:String,
    val role:Boolean,
    val photo:String?
)

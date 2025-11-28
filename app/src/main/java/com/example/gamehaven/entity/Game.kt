package com.example.gamehaven.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val title:String,
    val description:String,
    val developer:String,
    val category:String,
    val price: Double,
    val releaseDate: Date?,
    val stock:Int,
    val fileUrl:String,
    val imageUrl:String
)

package com.example.gamehaven.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    foreignKeys = [
        ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["userId"]),
        ForeignKey(entity = Game::class, parentColumns = ["id"], childColumns = ["gameId"])
    ]
)
data class DownloadHistory(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val userId:Int,
    val gameId:Int,
    val downloadDate: Date,
)

package com.example.gamehaven.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "PurchasedGame",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PurchasedGame(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val gameId: Int,
    val purchaseDate: Date,
    val transactionId: Int, // Reference to the transaction
    val downloadStatus: DownloadStatus = DownloadStatus.NOT_DOWNLOADED
)

enum class DownloadStatus {
    NOT_DOWNLOADED,
    DOWNLOADING,
    DOWNLOADED,
    INSTALLED
}
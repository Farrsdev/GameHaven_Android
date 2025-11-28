package com.example.gamehaven

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.gamehaven.dao.DownloadHistoryDao
import com.example.gamehaven.dao.GameDao
import com.example.gamehaven.dao.PurchasedGameDao
import com.example.gamehaven.dao.TransactionDao
import com.example.gamehaven.dao.TransactionDetailDao
import com.example.gamehaven.dao.UserDao
import com.example.gamehaven.entity.DateConverter
import com.example.gamehaven.entity.DownloadHistory
import com.example.gamehaven.entity.Game
import com.example.gamehaven.entity.PurchasedGame
import com.example.gamehaven.entity.Transaction
import com.example.gamehaven.entity.TransactionDetail
import com.example.gamehaven.entity.User

@Database(
    entities = [
        User::class,
        Game::class,
        Transaction::class,
        TransactionDetail::class,
        DownloadHistory::class,
        PurchasedGame::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDb: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun gameDao(): GameDao
    abstract fun transactionDao(): TransactionDao
    abstract fun transactionDetailDao(): TransactionDetailDao
    abstract fun downloadHistoryDao(): DownloadHistoryDao
    abstract fun purchasedGameDao(): PurchasedGameDao
    companion object{
        @Volatile
        private var INSTANCE : AppDb? = null

        fun getDb(context: Context) : AppDb{
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "GameHavenDb"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = inst
                inst
            }
        }
    }
}
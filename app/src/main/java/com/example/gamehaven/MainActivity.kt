package com.example.gamehaven

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.gamehaven.entity.User
import com.example.gamehaven.ui.theme.GameHavenTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gamehaven.entity.Game
import com.example.gamehaven.other.CurrentUserManager
import com.example.gamehaven.viewmodel.UserViewModel
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDb.getDb(this)
        insertDefaultUserIfNeed(this, db)
        CurrentUserManager.initialize(this)
        enableEdgeToEdge()
        setContent {
            GameHavenTheme {
                val nc = rememberNavController()

                AppNavGraph(
                    nc = nc,
                )
            }
        }
    }
}
fun insertDefaultUserIfNeed(context: Context, db: AppDb) {
    val prefs = context.getSharedPreferences("init_db", Context.MODE_PRIVATE)
    val isFirstRun = prefs.getBoolean("fifth", true)

    if (isFirstRun) {
        CoroutineScope(Dispatchers.IO).launch {

            db.userDao().insert(
                User(
                    username = "Farr",
                    email = "farr@gmail.com",
                    password = "123",
                    role = true,
                    photo = null
                )
            )

            db.userDao().insert(
                User(
                    username = "shir",
                    email = "shir@gmail.com",
                    password = "123",
                    role = false,
                    photo = null
                )
            )

            // ✅ INSERT GAME DEFAULT
            db.gameDao().insert(
                Game(
                    title = "Cyber Jump",
                    description = "Fast-paced cyber platformer game",
                    developer = "Farr Studio",
                    category = "Action",
                    price = 25000.0,
                    releaseDate = Date(),
                    stock = 100,
                    fileUrl = "",
                    imageUrl = "https://www.cyberjump.eu/wp-content/uploads/2024/07/cjpozsonyw.jpg"
                )
            )

            db.gameDao().insert(
                Game(
                    title = "Zombie Arena",
                    description = "Survival zombie shooter",
                    developer = "Shir Corp",
                    category = "Shooter",
                    price = 40000.0,
                    releaseDate = Date(),
                    stock = 50,
                    fileUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS88N3tYJySO8ZbBFqpQEqNINB1EFcs8qYYdQ&s",
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS88N3tYJySO8ZbBFqpQEqNINB1EFcs8qYYdQ&s"
                )
            )

            db.gameDao().insert(
                Game(
                    title = "Puzzle Quest",
                    description = "Relaxing brain puzzle game",
                    developer = "Indie Dev",
                    category = "Puzzle",
                    price = 15000.0,
                    releaseDate = Date(),
                    stock = 200,
                    fileUrl = "https://example.com/puzzle.apk",
                    imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSAUVY6-RFPBR3ae0ZUa56Oz1HsqgW6m6w0VQ&s"
                )
            )

            prefs.edit { putBoolean("fifth", false) }
        }
    }
}

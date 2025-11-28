package com.example.gamehaven.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.InstallDesktop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamehaven.entity.DownloadStatus
import com.example.gamehaven.entity.Game
import com.example.gamehaven.entity.PurchasedGame
import com.example.gamehaven.other.rememberCurrentUser
import com.example.gamehaven.viewmodel.GameViewModel
import com.example.gamehaven.viewmodel.PurchasedGameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyGamesScreen(
    navController: NavHostController
) {
    val purchasedGameViewModel: PurchasedGameViewModel = viewModel()
    val gameViewModel: GameViewModel = viewModel()

    val currentUserManager = rememberCurrentUser()
    val currentUserId = currentUserManager.getUserId()

    val purchasedGames by purchasedGameViewModel.getPurchasedGamesByUser(currentUserId).observeAsState(initial = emptyList())
    val allGames by gameViewModel.allGames.observeAsState(initial = emptyList())

    // Combine purchased games with game details
    val gamesWithDetails = remember(purchasedGames, allGames) {
        purchasedGames.map { purchased ->
            val game = allGames.find { it.id == purchased.gameId }
            PurchasedGameWithDetails(purchased, game)
        }.filter { it.game != null }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "My Games",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (gamesWithDetails.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.Games,
                    contentDescription = "No Games",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No games purchased yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Your purchased games will appear here",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(gamesWithDetails, key = { it.purchasedGame.id }) { item ->
                    PurchasedGameCard(
                        purchasedGame = item.purchasedGame,
                        game = item.game!!,
                        onDownloadClick = {
                            // Handle download
                            purchasedGameViewModel.updateDownloadStatus(
                                item.purchasedGame.id,
                                DownloadStatus.DOWNLOADING
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                delay(3000)
                                DownloadStatus.DOWNLOADED
                            }
                             // Simulate download completion after 3 seconds
                            // In real app, you'd use a proper download manager
                        },
                        onInstallClick = {
                            purchasedGameViewModel.updateDownloadStatus(
                                item.purchasedGame.id,
                                DownloadStatus.INSTALLED
                            )
                        }
                    )
                }
            }
        }
    }
}

data class PurchasedGameWithDetails(
    val purchasedGame: PurchasedGame,
    val game: Game?
)

@Composable
private fun PurchasedGameCard(
    purchasedGame: PurchasedGame,
    game: Game,
    onDownloadClick: () -> Unit,
    onInstallClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Game Image and Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Game Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    if (!game.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = game.imageUrl,
                            contentDescription = game.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Games,
                                contentDescription = "Game Image",
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Game Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = game.developer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Purchased: ${dateFormat.format(purchasedGame.purchaseDate)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Download/Install Button
            Surface(
                color = when (purchasedGame.downloadStatus) {
                    DownloadStatus.NOT_DOWNLOADED -> MaterialTheme.colorScheme.primaryContainer
                    DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.tertiaryContainer
                    DownloadStatus.DOWNLOADED -> MaterialTheme.colorScheme.secondaryContainer
                    DownloadStatus.INSTALLED -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = when (purchasedGame.downloadStatus) {
                                DownloadStatus.NOT_DOWNLOADED -> "Ready to download"
                                DownloadStatus.DOWNLOADING -> "Downloading..."
                                DownloadStatus.DOWNLOADED -> "Download complete"
                                DownloadStatus.INSTALLED -> "Installed"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = when (purchasedGame.downloadStatus) {
                                DownloadStatus.NOT_DOWNLOADED -> MaterialTheme.colorScheme.onPrimaryContainer
                                DownloadStatus.DOWNLOADING -> MaterialTheme.colorScheme.onTertiaryContainer
                                DownloadStatus.DOWNLOADED -> MaterialTheme.colorScheme.onSecondaryContainer
                                DownloadStatus.INSTALLED -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        if (purchasedGame.downloadStatus == DownloadStatus.DOWNLOADING) {
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    when (purchasedGame.downloadStatus) {
                        DownloadStatus.NOT_DOWNLOADED -> {
                            Button(
                                onClick = onDownloadClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    Icons.Default.Download,
                                    contentDescription = "Download",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download")
                            }
                        }
                        DownloadStatus.DOWNLOADING -> {
                            Text(
                                "45%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        DownloadStatus.DOWNLOADED -> {
                            Button(
                                onClick = onInstallClick,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Icon(
                                    Icons.Default.InstallDesktop,
                                    contentDescription = "Install",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Install")
                            }
                        }
                        DownloadStatus.INSTALLED -> {
                            Text(
                                "Ready to play",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
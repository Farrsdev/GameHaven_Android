package com.example.gamehaven.ui.screen.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gamehaven.entity.Game
import com.example.gamehaven.viewmodel.GameViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AdminGameManagementScreen(
    modifier: Modifier = Modifier
) {
    val gameViewModel: GameViewModel = viewModel()
    val allGames by gameViewModel.allGames.observeAsState(initial = emptyList())
    val searchResults by gameViewModel.searchResults.observeAsState(initial = emptyList())

    // Predefined categories list
    val predefinedCategories = listOf(
        "Action",
        "Adventure",
        "RPG",
        "Strategy",
        "Simulation",
        "Sports",
        "Racing",
        "Puzzle",
        "Horror",
        "FPS",
        "MMO",
        "Indie",
        "Casual",
        "Arcade",
        "Platformer"
    )

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedStockFilter by remember { mutableStateOf<StockFilter>(StockFilter.ALL) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showGameDialog by remember { mutableStateOf(false) }
    var gameToDelete by remember { mutableStateOf<Game?>(null) }
    var gameToEdit by remember { mutableStateOf<Game?>(null) }
    var showStats by remember { mutableStateOf(true) }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            gameViewModel.searchGames(searchQuery)
        }
    }

    val displayedGames = when {
        searchQuery.isNotEmpty() -> searchResults
        selectedCategory != null -> allGames.filter { it.category == selectedCategory }
        selectedStockFilter != StockFilter.ALL -> allGames.filter { game ->
            when (selectedStockFilter) {
                StockFilter.IN_STOCK -> game.stock > 0
                StockFilter.OUT_OF_STOCK -> game.stock == 0
                StockFilter.LOW_STOCK -> game.stock in 1..5
                else -> true
            }
        }
        else -> allGames
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Game Management",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { showStats = !showStats }
            ) {
                Icon(
                    imageVector = if (showStats) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (showStats) "Hide Stats" else "Show Stats"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Statistics Cards
        if (showStats) {
            GameStatsSection(gameViewModel = gameViewModel)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Search and Filter Section
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search games by title, developer, category...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                keyboardController?.hide()
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Text
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                    }
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Filter Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Category Filter
                var categoryExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedButton(
                        onClick = { categoryExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Category, contentDescription = "Category", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedCategory ?: "All Categories", maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Categories") },
                            onClick = {
                                selectedCategory = null
                                categoryExpanded = false
                            }
                        )
                        predefinedCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // Stock Filter
                var stockExpanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedButton(
                        onClick = { stockExpanded = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = "Stock", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedStockFilter.displayName)
                    }

                    DropdownMenu(
                        expanded = stockExpanded,
                        onDismissRequest = { stockExpanded = false }
                    ) {
                        StockFilter.entries.forEach { filter ->
                            DropdownMenuItem(
                                text = { Text(filter.displayName) },
                                onClick = {
                                    selectedStockFilter = filter
                                    stockExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Filters
        if (selectedCategory != null || selectedStockFilter != StockFilter.ALL || searchQuery.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildString {
                        append("Showing ${displayedGames.size} games")
                        if (selectedCategory != null) {
                            append(" • ${selectedCategory}")
                        }
                        if (selectedStockFilter != StockFilter.ALL) {
                            append(" • ${selectedStockFilter.displayName}")
                        }
                        if (searchQuery.isNotEmpty()) {
                            append(" • \"$searchQuery\"")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = {
                        selectedCategory = null
                        selectedStockFilter = StockFilter.ALL
                        searchQuery = ""
                    }
                ) {
                    Text("Clear All")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Games List
        if (displayedGames.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(displayedGames, key = { it.id }) { game ->
                    GameCard(
                        game = game,
                        onEdit = {
                            gameToEdit = game
                            showGameDialog = true
                        },
                        onDelete = {
                            gameToDelete = game
                            showDeleteDialog = true
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Games,
                    contentDescription = "No Games",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (searchQuery.isNotEmpty() || selectedCategory != null || selectedStockFilter != StockFilter.ALL)
                        "No games found"
                    else
                        "No games available",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Add Game Button
        FloatingActionButton(
            onClick = {
                gameToEdit = null
                showGameDialog = true
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Game")
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && gameToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                gameToDelete = null
            },
            title = { Text("Delete Game") },
            text = {
                Text("Are you sure you want to delete \"${gameToDelete?.title}\"? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        gameToDelete?.let { gameViewModel.delete(it) }
                        showDeleteDialog = false
                        gameToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        gameToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add/Edit Game Dialog
    if (showGameDialog) {
        GameFormDialog(
            game = gameToEdit,
            predefinedCategories = predefinedCategories,
            onDismiss = {
                showGameDialog = false
                gameToEdit = null
            },
            onSave = { game ->
                if (game.id == 0) {
                    // Add new game
                    gameViewModel.insert(game)
                } else {
                    // Update existing game
                    gameViewModel.update(game)
                }
                showGameDialog = false
                gameToEdit = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameFormDialog(
    game: Game?,
    predefinedCategories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Game) -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(game?.title ?: "") }
    var description by remember { mutableStateOf(game?.description ?: "") }
    var developer by remember { mutableStateOf(game?.developer ?: "") }
    var category by remember { mutableStateOf(game?.category ?: "") }
    var price by remember { mutableStateOf(game?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(game?.stock?.toString() ?: "") }
    var fileUrl by remember { mutableStateOf(game?.fileUrl ?: "") }
    var imageUrl by remember { mutableStateOf(game?.imageUrl ?: "") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }

    // Image Picker
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
        imageUrl = uri?.toString() ?: ""
    }

    // File Picker (untuk game file)
    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedFileUri = uri
        fileUrl = uri?.toString() ?: ""
    }

    val isEditMode = game != null
    val isFormValid = title.isNotBlank() &&
            description.isNotBlank() &&
            developer.isNotBlank() &&
            category.isNotBlank() &&
            price.isNotBlank() &&
            stock.isNotBlank() &&
            fileUrl.isNotBlank() &&
            imageUrl.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (isEditMode) "Edit Game" else "Add New Game")
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.heightIn(max = 600.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Game Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }

                item {
                    OutlinedTextField(
                        value = developer,
                        onValueChange = { developer = it },
                        label = { Text("Developer *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            predefinedCategories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = {
                                if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    price = it
                                }
                            },
                            label = { Text("Price *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            prefix = { Text("$") }
                        )

                        OutlinedTextField(
                            value = stock,
                            onValueChange = {
                                if (it.matches(Regex("^\\d*$"))) {
                                    stock = it
                                }
                            },
                            label = { Text("Stock *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                }

                item {
                    // Image Picker Section
                    Column {
                        Text(
                            text = "Game Image *",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clickable {
                                    imagePicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            if (selectedImageUri != null || !imageUrl.isNullOrEmpty()) {
                                val imageUri = selectedImageUri ?: Uri.parse(imageUrl)
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Selected Image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Add Image",
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "Tap to select image",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    // File Picker Section
                    Column {
                        Text(
                            text = "Game File *",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    filePicker.launch("*/*")
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
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
                                        "Select Game File",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (selectedFileUri != null || !fileUrl.isNullOrEmpty()) {
                                        val fileName = selectedFileUri?.lastPathSegment ?: fileUrl.substringAfterLast("/")
                                        Text(
                                            fileName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.width(200.dp)
                                        )
                                    }
                                }
                                Icon(
                                    Icons.Default.AttachFile,
                                    contentDescription = "Select File",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newGame = Game(
                        id = game?.id ?: 0,
                        title = title,
                        description = description,
                        developer = developer,
                        category = category,
                        price = price.toDoubleOrNull() ?: 0.0,
                        releaseDate = game?.releaseDate,
                        stock = stock.toIntOrNull() ?: 0,
                        fileUrl = fileUrl,
                        imageUrl = imageUrl
                    )
                    onSave(newGame)
                },
                enabled = isFormValid
            ) {
                Text(if (isEditMode) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// Composable lainnya tetap sama...
@Composable
private fun GameStatsSection(gameViewModel: GameViewModel) {
    var totalGames by remember { mutableStateOf(0) }
    var availableGames by remember { mutableStateOf(0) }
    var outOfStockGames by remember { mutableStateOf(0) }
    var totalValue by remember { mutableStateOf(0.0) }

    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    LaunchedEffect(Unit) {
        totalGames = gameViewModel.getTotalGames()
        availableGames = gameViewModel.getAvailableGames()
        outOfStockGames = gameViewModel.getOutOfStockGames()
        totalValue = gameViewModel.getTotalInventoryValue()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Total Games",
                value = totalGames.toString(),
                icon = Icons.Default.Games,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Available",
                value = availableGames.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Out of Stock",
                value = outOfStockGames.toString(),
                icon = Icons.Default.Cancel,
                modifier = Modifier.weight(1f)
            )
        }

        // Total Value Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Total Inventory Value",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        currencyFormat.format(totalValue),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Icon(
                    Icons.Default.AttachMoney,
                    contentDescription = "Total Value",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun GameCard(
    game: Game,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = game.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = game.developer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Price
                Text(
                    text = currencyFormat.format(game.price),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Category Badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = game.category,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stock Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = when {
                            game.stock == 0 -> Icons.Default.Cancel
                            game.stock <= 5 -> Icons.Default.Warning
                            else -> Icons.Default.CheckCircle
                        },
                        contentDescription = "Stock Status",
                        modifier = Modifier.size(16.dp),
                        tint = when {
                            game.stock == 0 -> MaterialTheme.colorScheme.error
                            game.stock <= 5 -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.primary
                        }
                    )
                    Text(
                        text = if (game.stock == 0) "Out of Stock" else "${game.stock} in stock",
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            game.stock == 0 -> MaterialTheme.colorScheme.error
                            game.stock <= 5 -> MaterialTheme.colorScheme.onSurfaceVariant
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Description (truncated)
            if (game.description.isNotBlank()) {
                Text(
                    text = game.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }
            }
        }
    }
}

enum class StockFilter(val displayName: String) {
    ALL("All Stock"),
    IN_STOCK("In Stock"),
    OUT_OF_STOCK("Out of Stock"),
    LOW_STOCK("Low Stock")
}
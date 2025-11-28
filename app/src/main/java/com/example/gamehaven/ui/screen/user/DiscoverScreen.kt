package com.example.gamehaven.ui.screen.user

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gamehaven.entity.Game
import com.example.gamehaven.ui.theme.GameHavenTheme
import com.example.gamehaven.viewmodel.GameViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverUi(navController: NavHostController? = null) {
    val gameViewModel: GameViewModel = viewModel()
    val allGames by gameViewModel.allGames.observeAsState(initial = emptyList())
    val categories by gameViewModel.categories.observeAsState(initial = emptyList())

    var searchQuery by remember { mutableStateOf("") }
    var selectedPriceRange by remember { mutableStateOf<ClosedFloatingPointRange<Float>?>(null) }
    var selectedCategories by remember { mutableStateOf<Set<String>>(emptySet()) }
    var sortOption by remember { mutableStateOf(SortOption.POPULAR) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Discover Games",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Advanced Search & Filters
            AdvancedSearchSection(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedPriceRange = selectedPriceRange,
                onPriceRangeChange = { selectedPriceRange = it },
                selectedCategories = selectedCategories,
                onCategoriesChange = { selectedCategories = it },
                sortOption = sortOption,
                onSortOptionChange = { sortOption = it },
                categories = categories
            )

            // Filtered Games Grid
            DiscoverGamesGrid(
                games = allGames,
                searchQuery = searchQuery,
                selectedPriceRange = selectedPriceRange,
                selectedCategories = selectedCategories,
                sortOption = sortOption,
                onGameClick = { game ->
                    navController?.navigate("store/${game.id}")}
            )
        }
    }
}

@Composable
private fun AdvancedSearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedPriceRange: ClosedFloatingPointRange<Float>?,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>?) -> Unit,
    selectedCategories: Set<String>,
    onCategoriesChange: (Set<String>) -> Unit,
    sortOption: SortOption,
    onSortOptionChange: (SortOption) -> Unit,
    categories: List<String>
) {
    var showFilters by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search games, developers...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Filter & Sort Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Filter Button
            FilterChip(
                selected = showFilters,
                onClick = { showFilters = !showFilters },
                label = { Text("Filters") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filters",
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            // Sort Dropdown
            var sortExpanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(
                    selected = false,
                    onClick = { sortExpanded = true },
                    label = { Text(sortOption.displayName) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                )

                DropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                onSortOptionChange(option)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }

            // Active filters count
            if (selectedPriceRange != null || selectedCategories.isNotEmpty()) {
                FilterChip(
                    selected = true,
                    onClick = {
                        onPriceRangeChange(null)
                        onCategoriesChange(emptySet())
                    },
                    label = {
                        Text("Clear (${(if (selectedPriceRange != null) 1 else 0) + selectedCategories.size})")
                    }
                )
            }
        }

        // Expanded Filters
        if (showFilters) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Price Range Filter
                PriceRangeFilter(
                    selectedRange = selectedPriceRange,
                    onRangeSelected = onPriceRangeChange
                )

                // Categories Filter
                CategoriesFilter(
                    selectedCategories = selectedCategories,
                    onCategoriesChange = onCategoriesChange,
                    categories = categories
                )
            }
        }
    }
}

@Composable
private fun PriceRangeFilter(
    selectedRange: ClosedFloatingPointRange<Float>?,
    onRangeSelected: (ClosedFloatingPointRange<Float>?) -> Unit
) {
    var sliderPosition by remember { mutableStateOf(0f..100f) }

    Column {
        Text(
            "Price Range",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$${sliderPosition.start.toInt()}")
            Text("$${sliderPosition.endInclusive.toInt()}")
        }

        RangeSlider(
            value = sliderPosition,
            onValueChange = { sliderPosition = it },
            valueRange = 0f..50000f,
            steps = 99,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    onRangeSelected(sliderPosition)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Apply")
            }

            OutlinedButton(
                onClick = {
                    onRangeSelected(null)
                    sliderPosition = 0f..100f
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }
    }
}

@Composable
private fun CategoriesFilter(
    selectedCategories: Set<String>,
    onCategoriesChange: (Set<String>) -> Unit,
    categories: List<String>
) {
    Column {
        Text(
            "Categories",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategories.contains(category),
                    onClick = {
                        val newSelection = selectedCategories.toMutableSet()
                        if (newSelection.contains(category)) {
                            newSelection.remove(category)
                        } else {
                            newSelection.add(category)
                        }
                        onCategoriesChange(newSelection)
                    },
                    label = { Text(category) }
                )
            }
        }
    }
}

@Composable
private fun DiscoverGamesGrid(
    games: List<Game>,
    searchQuery: String,
    selectedPriceRange: ClosedFloatingPointRange<Float>?,
    selectedCategories: Set<String>,
    sortOption: SortOption,
    onGameClick: (Game) -> Unit
) {
    val filteredGames = remember(games, searchQuery, selectedPriceRange, selectedCategories, sortOption) {
        games.filter { game ->
            // Filter by search query
            val matchesSearch = searchQuery.isEmpty() ||
                    game.title.contains(searchQuery, ignoreCase = true) ||
                    game.developer.contains(searchQuery, ignoreCase = true) ||
                    game.description.contains(searchQuery, ignoreCase = true)

            // Filter by price range
            val matchesPrice = selectedPriceRange == null ||
                    (game.price >= selectedPriceRange.start && game.price <= selectedPriceRange.endInclusive)

            // Filter by categories
            val matchesCategories = selectedCategories.isEmpty() ||
                    selectedCategories.contains(game.category)

            matchesSearch && matchesPrice && matchesCategories && game.stock > 0
        }.sortedWith(
            when (sortOption) {
                SortOption.POPULAR -> compareByDescending { it.price } // Using price as popularity proxy
                SortOption.NEWEST -> compareByDescending { it.releaseDate }
                SortOption.PRICE_LOW -> compareBy { it.price }
                SortOption.PRICE_HIGH -> compareByDescending { it.price }
                SortOption.NAME -> compareBy { it.title }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (filteredGames.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.SearchOff,
                        contentDescription = "No results",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No games found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(
                items = filteredGames.chunked(2),
                key = { it.hashCode() }
            ) { rowGames ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowGames.forEach { game ->
                        DiscoverGameCard(
                            game = game,
                            onClick = { onGameClick(game) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Add empty space if odd number
                    if (rowGames.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverGameCard(
    game: Game,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("en", "US")) }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column {
            // Game Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = game.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
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
                    text = currencyFormat.format(game.price),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Category badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = game.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

enum class SortOption(val displayName: String) {
    POPULAR("Popular"),
    NEWEST("Newest"),
    PRICE_LOW("Price: Low to High"),
    PRICE_HIGH("Price: High to Low"),
    NAME("Name")
}
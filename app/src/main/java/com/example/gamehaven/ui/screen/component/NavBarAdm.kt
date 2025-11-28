package com.example.gamehaven.ui.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.gamehaven.ui.theme.GameHavenTheme

data class AdminNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun NavBarAdmin(
    modifier: Modifier = Modifier,
    currentScreen: String = "dashboard",
    onNavigationSelected: (String) -> Unit = {}
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    val navItems = listOf(
        AdminNavItem("Dashboard", Icons.Filled.Dashboard, "dashboard"),
        AdminNavItem("Users", Icons.Filled.People, "users"),
        AdminNavItem("Games", Icons.Filled.Storage, "games"),
        AdminNavItem("Analytics", Icons.Filled.Analytics, "analytics")
    )

    // Update selected item based on current screen
    val initialSelectedIndex = navItems.indexOfFirst { it.route == currentScreen }
    if (initialSelectedIndex != -1 && selectedItem != initialSelectedIndex) {
        selectedItem = initialSelectedIndex
    }

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    onNavigationSelected(item.route)
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NavBarAdminPreview() {
    GameHavenTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            NavBarAdmin(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NavBarAdminDarkPreview() {
    GameHavenTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavBarAdmin(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            )
        }
    }
}
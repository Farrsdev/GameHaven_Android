package com.example.gamehaven

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamehaven.other.rememberCurrentUser
import com.example.gamehaven.ui.screen.admin.AdminAnalyticScreen
import com.example.gamehaven.ui.screen.admin.AdminGameManagementScreen
import com.example.gamehaven.ui.screen.admin.AdminUserManagementScreen
import com.example.gamehaven.ui.screen.admin.HomeUiAdmin
import com.example.gamehaven.ui.screen.auth.LoginScreenContent
import com.example.gamehaven.ui.screen.auth.RegisterScreenContent
import com.example.gamehaven.ui.screen.auth.RegisterUi
import com.example.gamehaven.ui.screen.component.NavBarAdmin
import com.example.gamehaven.ui.screen.component.NavbarUser
import com.example.gamehaven.ui.screen.user.DiscoverUi
import com.example.gamehaven.ui.screen.user.DownloadsScreen
import com.example.gamehaven.ui.screen.user.HomeUiUser
import com.example.gamehaven.ui.screen.user.MyGamesScreen
import com.example.gamehaven.ui.screen.user.PaymentScreen
import com.example.gamehaven.ui.screen.user.ProfileScreen
import com.example.gamehaven.ui.screen.user.StoreScreen
import com.example.gamehaven.ui.theme.GameHavenTheme
import com.example.gamehaven.viewmodel.UserViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object User : Screen("user")
    object Admin : Screen("admin")
    object Store : Screen("store/{gameId}") {
        fun createRoute(gameId: Int) = "store/$gameId"
    }
    object Payment : Screen("payment/{gameId}") {
        fun createRoute(gameId: Int) = "payment/$gameId"
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    nc: NavHostController,
    startDestination: String = Screen.Login.route
) {
    val currentUserManager = rememberCurrentUser()

    val actualStartDestination = if (currentUserManager.isLoggedIn()) {
        if (currentUserManager.getRole()) Screen.Admin.route else Screen.User.route
    } else {
        Screen.Login.route
    }
    AnimatedNavHost(
        navController = nc,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) },
        exitTransition = { fadeOut(animationSpec = tween(300)) }
    ) {

        composable(
            route = Screen.Login.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(350)
                ) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeOut()
            }
        ) {
            val vm: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            LoginScreenContent(
                vm = vm,
                onGoToRegist = {
                    nc.navigate(Screen.Register.route)
                },
                onLoginUser = {
                    nc.navigate(Screen.User.route) {
                        popUpTo(Screen.Login.route){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onLoginAdmin = {
                    nc.navigate(Screen.Admin.route){
                        popUpTo(Screen.Login.route){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.Register.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(350)
                ) + fadeIn()
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(350)
                ) + fadeOut()
            }
        ) {
            val vm: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

            RegisterScreenContent(
                vm = vm,
                onGoToLogin = {
                    nc.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.User.route){
           UserRoot()
        }

        composable(Screen.Admin.route) {
           AdminRoot()
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserRoot(modifier: Modifier = Modifier) {
    val userNavController = rememberNavController()
    val navBackStackEntry by userNavController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            NavbarUser(
                currentScreen = currentRoute,
                onNavigationSelected = {r ->
                    userNavController.navigate(r){
                        launchSingleTop = true
                        popUpTo("home"){
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    ) { p->
        NavHost(
            navController = userNavController,
            modifier = modifier.padding(p),
            startDestination = "home"
        ){
            composable("home") { HomeUiUser(navController = userNavController) }
            composable("discover") { DiscoverUi(userNavController) }
            composable("store") {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = "No Game",
                        modifier = Modifier.size(64.dp),
                        tint = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No game selected",
                        style = typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Select a game to view details",
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
            composable(
                route = "store/{gameId}",
                arguments = listOf(
                    navArgument("gameId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getInt("gameId")
                if (gameId != null) {
                    StoreScreen(
                        gameId = gameId,
                        navController = userNavController
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Game not found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            composable(
                route = "payment/{gameId}",
                arguments = listOf(
                    navArgument("gameId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getInt("gameId")

                PaymentScreen(
                    gameId = gameId ?: 0,
                    navController = userNavController
                )
            }

            composable("mygames") { MyGamesScreen(navController = userNavController) }
            composable("downloads") { DownloadsScreen(navController = userNavController) }
            composable("profile") { ProfileScreen(navController = userNavController) }

        }
    }
}
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AdminRoot() {
    val adminNavController = rememberNavController()
    val navBackStackEntry by adminNavController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        bottomBar = {
            NavBarAdmin(
                currentScreen = currentRoute,
                onNavigationSelected = { route ->
                    adminNavController.navigate(route) {
                        launchSingleTop = true
                        popUpTo("dashboard") { saveState = true }
                        restoreState = true
                    }
                }
            )
        }
    ) { padding ->

        NavHost(
            navController = adminNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(padding)
        ) {
            composable("dashboard") { HomeUiAdmin() }
            composable("users") { AdminUserManagementScreen() }
            composable("games") { AdminGameManagementScreen() }
            composable("analytics") { AdminAnalyticScreen() }
        }
    }
}

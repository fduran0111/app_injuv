package com.injuv.voluntariado.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.injuv.voluntariado.components.BottomNavItem
import com.injuv.voluntariado.components.bottomNavIcon
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.components.StatusAlertSheet
import com.injuv.voluntariado.screens.AcademyScreen
import com.injuv.voluntariado.screens.AdminScreen
import com.injuv.voluntariado.screens.ApplicationDetailScreen
import com.injuv.voluntariado.screens.ApplicationsScreen
import com.injuv.voluntariado.screens.DocumentsScreen
import com.injuv.voluntariado.screens.HomeScreen
import com.injuv.voluntariado.screens.LibraryScreen
import com.injuv.voluntariado.screens.NewsScreen
import com.injuv.voluntariado.screens.ProfileScreen
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import androidx.compose.material3.NavigationBarItem
// Pantallas donde se muestra la barra de navegación inferior (igual que showBottomNav en App.tsx).
private val screensWithBottomNav = setOf("home", "applications", "news", "academy", "profile")

@Composable
fun InjuvApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"
    val currentBaseRoute = currentRoute.substringBefore("/")

    var showStatusAlert by remember { mutableStateOf(false) }
    val T = LocalAppColors.current

    Scaffold(
        containerColor = T.bgBase,
        bottomBar = {
            if (currentBaseRoute in screensWithBottomNav) {
                BottomBar(
                    currentRoute = currentBaseRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = T.bgBase,
        ) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    HomeScreen(
                        onNavigate = { route -> navController.navigate(route) },
                        onOpenDetail = { id -> navController.navigate("application_detail/$id") },
                        onShowAlert = { showStatusAlert = true },
                    )
                }
                composable("applications") {
                    ApplicationsScreen(
                        onOpenDetail = { id -> navController.navigate("application_detail/$id") },
                    )
                }
                composable(
                    "application_detail/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType }),
                ) { entry ->
                    val id = entry.arguments?.getInt("id") ?: 1
                    val opportunity = SampleData.opportunities.find { it.id == id } ?: SampleData.opportunities.first()
                    ApplicationDetailScreen(
                        opportunity = opportunity,
                        onBack = { navController.popBackStack() },
                    )
                }
                composable("profile") { ProfileScreen() }
                composable("admin") {
                    AdminScreen(onBack = { navController.popBackStack() })
                }
                composable("news") { NewsScreen() }
                composable("documents") { DocumentsScreen() }
                composable("academy") { AcademyScreen() }
                composable("library") { LibraryScreen() }
            }
        }
    }

    if (showStatusAlert) {
        StatusAlertSheet(onClose = { showStatusAlert = false })
    }
}

@Composable
private fun BottomBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val T = LocalAppColors.current
    androidx.compose.material3.NavigationBar(
        containerColor = T.navBg,
        contentColor = T.navInactive,
    ) {
        BottomNavItem.values().forEach { item ->
            val isActive = item.route == currentRoute
            NavigationBarItem(
                selected = isActive,
                onClick = { onNavigate(item.route) },
                icon = { bottomNavIcon(item = item, active = isActive) },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = SpaceGrotesk,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    )
                },
                colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                    selectedIconColor = T.coral,
                    selectedTextColor = T.coral,
                    unselectedIconColor = T.navInactive,
                    unselectedTextColor = T.navInactive,
                    indicatorColor = T.bgBase,
                ),
            )
        }
    }
}
package com.injuv.voluntariado.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.injuv.voluntariado.components.BottomNavItem
import com.injuv.voluntariado.components.ResourcesMenuCard
import com.injuv.voluntariado.components.StatusAlertSheet
import com.injuv.voluntariado.components.bottomNavIcon
import com.injuv.voluntariado.data.ProfilePreferences
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.data.SessionPreferences
import com.injuv.voluntariado.screens.AcademyScreen
import com.injuv.voluntariado.screens.ApplicationDetailScreen
import com.injuv.voluntariado.screens.ApplicationsScreen
import com.injuv.voluntariado.screens.AuthScreen
import com.injuv.voluntariado.screens.DocumentsScreen
import com.injuv.voluntariado.screens.EditProfileScreen
import com.injuv.voluntariado.screens.HomeScreen
import com.injuv.voluntariado.screens.LibraryScreen
import com.injuv.voluntariado.screens.NewsScreen
import com.injuv.voluntariado.screens.ProfileScreen
import com.injuv.voluntariado.screens.SettingsScreen
import com.injuv.voluntariado.screens.computeAgeYears
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.SpaceGrotesk

private val screensWithBottomNav = setOf(
    "home",
    "applications",
    "news",
    "profile",
    "documents",
    "academy",
    "library",
)

private fun NavHostController.goTo(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            inclusive = false
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }
}

@Composable
fun InjuvApp(
    isDarkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val session = remember { SessionPreferences(context) }
    val profilePrefs = remember { ProfilePreferences(context) }
    var loggedIn by remember { mutableStateOf(session.isLoggedIn()) }
    var userProfile by remember { mutableStateOf(profilePrefs.load(session.displayName(), session.email())) }

    if (!loggedIn) {
        AuthScreen(
            onAuthenticated = { name, email, birthMillis ->
                val resolvedName = if (birthMillis != null) {
                    session.registerAccount(name, email)
                    name
                } else {
                    session.registeredName(email) ?: name.also { session.registerAccount(it, email) }
                }
                session.login(resolvedName, email)
                val loaded = profilePrefs.load(resolvedName, email).copy(name = resolvedName)
                userProfile = if (birthMillis != null) {
                    loaded.copy(age = computeAgeYears(birthMillis).toString())
                } else {
                    loaded
                }
                profilePrefs.save(userProfile)
                loggedIn = true
            },
        )
        return
    }

    fun logout() {
        session.logout()
        profilePrefs.clear()
        userProfile = profilePrefs.load("", "")
        loggedIn = false
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"
    val currentBaseRoute = currentRoute.substringBefore("/")

    var showStatusAlert by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    val T = LocalAppColors.current
    val onShowAlert = { showStatusAlert = true }
    val onToggleMenu = { menuOpen = !menuOpen }

    Scaffold(
        containerColor = T.bgBase,
        bottomBar = {
            if (currentBaseRoute in screensWithBottomNav) {
                BottomBar(
                    currentRoute = currentBaseRoute,
                    onNavigate = { route ->
                        menuOpen = false
                        navController.goTo(route)
                    },
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = T.bgBase,
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onOpenDetail = { id ->
                                menuOpen = false
                                navController.navigate("application_detail/$id")
                            },
                            onShowAlert = onShowAlert,
                            onToggleMenu = onToggleMenu,
                            userName = userProfile.name,
                        )
                    }
                    composable("applications") {
                        ApplicationsScreen(
                            onOpenDetail = { id ->
                                menuOpen = false
                                navController.navigate("application_detail/$id")
                            },
                            onShowAlert = onShowAlert,
                            onToggleMenu = onToggleMenu,
                        )
                    }
                    composable(
                        "application_detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType }),
                    ) { entry ->
                        val id = entry.arguments?.getInt("id") ?: 1
                        val opportunity = SampleData.opportunities.find { it.id == id }
                            ?: SampleData.opportunities.first()
                        ApplicationDetailScreen(
                            opportunity = opportunity,
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("profile") {
                        ProfileScreen(
                            onShowAlert = onShowAlert,
                            onOpenSettings = {
                                menuOpen = false
                                navController.navigate("settings")
                            },
                            onToggleMenu = onToggleMenu,
                            onEditProfile = {
                                menuOpen = false
                                navController.navigate("edit_profile")
                            },
                            profile = userProfile,
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = onDarkThemeChange,
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) },
                            onLogout = { logout() },
                        )
                    }
                    composable("edit_profile") {
                        EditProfileScreen(
                            profile = userProfile,
                            onSave = { updated ->
                                userProfile = updated
                                profilePrefs.save(updated)
                                if (updated.name.isNotBlank() && updated.email.isNotBlank()) {
                                    session.login(updated.name, updated.email)
                                }
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("news") {
                        NewsScreen(onShowAlert = onShowAlert, onToggleMenu = onToggleMenu)
                    }
                    composable("documents") {
                        DocumentsScreen(onShowAlert = onShowAlert, onToggleMenu = onToggleMenu)
                    }
                    composable("academy") {
                        AcademyScreen(onShowAlert = onShowAlert, onToggleMenu = onToggleMenu)
                    }
                    composable("library") {
                        LibraryScreen(onShowAlert = onShowAlert, onToggleMenu = onToggleMenu)
                    }
                }
            }

            AnimatedVisibility(
                visible = menuOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.38f))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) { menuOpen = false },
                )
            }

            AnimatedVisibility(
                visible = menuOpen,
                enter = fadeIn() + slideInVertically { -it / 3 },
                exit = fadeOut() + slideOutVertically { -it / 3 },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 12.dp, top = 58.dp),
            ) {
                ResourcesMenuCard(
                    selectedRoute = currentBaseRoute,
                    onSelect = { route ->
                        menuOpen = false
                        navController.goTo(route)
                    },
                )
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

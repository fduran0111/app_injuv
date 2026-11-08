package com.injuv.voluntariado.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.injuv.voluntariado.R
import com.injuv.voluntariado.components.BottomNavItem
import com.injuv.voluntariado.components.ResourceNavItem
import com.injuv.voluntariado.components.StatusAlertSheet
import com.injuv.voluntariado.components.bottomNavIcon
import com.injuv.voluntariado.components.swipeToGoBack
import com.injuv.voluntariado.data.ProfilePreferences
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.data.SessionPreferences
import com.injuv.voluntariado.screens.AcademyScreen
import com.injuv.voluntariado.screens.ApplicationDetailScreen
import com.injuv.voluntariado.screens.ApplicationsScreen
import com.injuv.voluntariado.screens.AuthScreen
import com.injuv.voluntariado.screens.computeAgeYears
import com.injuv.voluntariado.screens.DocumentsScreen
import com.injuv.voluntariado.screens.EditProfileScreen
import com.injuv.voluntariado.screens.HomeScreen
import com.injuv.voluntariado.screens.LibraryScreen
import com.injuv.voluntariado.screens.NewsScreen
import com.injuv.voluntariado.screens.ProfileScreen
import com.injuv.voluntariado.screens.SettingsScreen
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.Outfit
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import kotlinx.coroutines.launch

private val screensWithBottomNav = setOf(
    "home",
    "applications",
    "news",
    "academy",
    "profile",
    "documents",
    "library",
    "settings",
    "application_detail",
    "edit_profile",
)

@Composable
fun InjuvApp() {
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
    val canGoBack = navController.previousBackStackEntry != null

    var showStatusAlert by remember { mutableStateOf(false) }
    val T = LocalAppColors.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    fun goToTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun goToResource(route: String) {
        scope.launch { drawerState.close() }
        if (route == "academy") {
            goToTab("academy")
        } else {
            navController.navigate(route) { launchSingleTop = true }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = T.navBg,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.linearGradient(T.bgHeaderGradient))
                            .padding(20.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_injuv_logo),
                            contentDescription = "INJUV",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp),
                            contentScale = ContentScale.Fit,
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "INJUV Voluntariado",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.White,
                        )
                        Text(
                            "Documentación, academia y biblioteca",
                            fontFamily = Outfit,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                        )
                    }
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        ResourceNavItem(
                            title = "Documentos",
                            subtitle = "Reglamentos y formularios",
                            icon = Icons.Filled.Description,
                            accent = T.yellow,
                            onClick = { goToResource("documents") },
                        )
                        ResourceNavItem(
                            title = "Academia",
                            subtitle = "Cursos de voluntariado",
                            icon = Icons.Filled.School,
                            accent = T.green,
                            onClick = { goToResource("academy") },
                        )
                        ResourceNavItem(
                            title = "Biblioteca",
                            subtitle = "Lecturas recomendadas",
                            icon = Icons.Filled.MenuBook,
                            accent = T.coral,
                            onClick = { goToResource("library") },
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {
                            scope.launch { drawerState.close() }
                            logout()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = T.coral,
                            contentColor = Color.White,
                        ),
                    ) {
                        Icon(Icons.Filled.Logout, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cerrar sesión",
                            fontFamily = SpaceGrotesk,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            containerColor = T.bgBase,
            bottomBar = {
                if (currentBaseRoute in screensWithBottomNav) {
                    BottomBar(
                        currentRoute = currentBaseRoute,
                        onNavigate = { goToTab(it) },
                    )
                }
            },
        ) { innerPadding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .swipeToGoBack(canGoBack) { navController.popBackStack() },
                color = T.bgBase,
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            onNavigate = { route ->
                                if (route == "academy" || route == "news") goToTab(route)
                                else navController.navigate(route) { launchSingleTop = true }
                            },
                            onOpenDetail = { id -> navController.navigate("application_detail/$id") },
                            onShowAlert = { showStatusAlert = true },
                            onOpenMenu = { scope.launch { drawerState.open() } },
                            userName = session.displayName(),
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
                    composable("profile") {
                        ProfileScreen(
                            onNavigate = { route -> navController.navigate(route) },
                            profile = userProfile,
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
                    composable("settings") {
                        SettingsScreen(
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) },
                            onLogout = { logout() },
                        )
                    }
                    composable("news") { NewsScreen() }
                    composable("documents") {
                        DocumentsScreen(onBack = { navController.popBackStack() })
                    }
                    composable("academy") { AcademyScreen() }
                    composable("library") {
                        LibraryScreen(onBack = { navController.popBackStack() })
                    }
                }
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

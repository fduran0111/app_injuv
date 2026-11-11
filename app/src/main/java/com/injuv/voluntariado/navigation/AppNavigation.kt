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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.AttendancePreferences
import com.injuv.voluntariado.data.ForumChannel
import com.injuv.voluntariado.data.ForumMessage
import com.injuv.voluntariado.data.ForumPreferences
import com.injuv.voluntariado.data.ProfilePreferences
import com.injuv.voluntariado.data.SampleData
import com.injuv.voluntariado.data.SessionPreferences
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.data.api.Usuario
import com.injuv.voluntariado.screens.AcademyScreen
import com.injuv.voluntariado.screens.ApplicationDetailScreen
import com.injuv.voluntariado.screens.ApplicationsScreen
import com.injuv.voluntariado.screens.AttendanceScreen
import com.injuv.voluntariado.screens.AuthScreen
import com.injuv.voluntariado.screens.DocumentsScreen
import com.injuv.voluntariado.screens.EditProfileScreen
import com.injuv.voluntariado.screens.ForumScreen
import com.injuv.voluntariado.screens.ForumsScreen
import com.injuv.voluntariado.screens.HomeScreen
import com.injuv.voluntariado.screens.LibraryScreen
import com.injuv.voluntariado.screens.NewsDetailScreen
import com.injuv.voluntariado.screens.NewsScreen
import com.injuv.voluntariado.screens.ProfileScreen
import com.injuv.voluntariado.screens.SettingsScreen
import com.injuv.voluntariado.screens.TutorDashboardScreen
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
    "tutor_home",
    "forums",
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
    val attendancePrefs = remember { AttendancePreferences(context) }
    val forumPrefs = remember { ForumPreferences(context) }
    var isTutorMode by remember { mutableStateOf(session.isTutor()) }
    var tutorProgramId by remember { mutableStateOf(session.tutorProgramId()) }
    var presentTodayCount by remember {
        mutableStateOf(
            attendancePrefs.presentNames(
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
            ).size
        )
    }

    // Sin sesión se muestra el login; al iniciar sesión (o si el token expira) cambia solo.
    val usuario by SessionManager.user.collectAsState()
    var userProfile by remember { mutableStateOf(profilePrefs.load("", "")) }
    LaunchedEffect(usuario) {
        usuario?.let { u -> userProfile = profilePrefs.load(u.nombreCompleto, u.email).completarCon(u) }
    }
    if (usuario == null) {
        AuthScreen(
            onAuthenticated = { u, birthMillis ->
                val loaded = profilePrefs.load(u.nombreCompleto, u.email).completarCon(u)
                userProfile = if (birthMillis != null) loaded.copy(age = computeAgeYears(birthMillis).toString()) else loaded
                profilePrefs.save(userProfile)
            },
        )
        return
    }
    LaunchedEffect(usuario?.id) { SessionManager.refrescar() }

    // Aviso de cambios de estado: compara las postulaciones actuales con las últimas vistas.
    var cambios by remember { mutableStateOf<List<PostulacionUsuario>>(emptyList()) }
    var postulaciones by remember { mutableStateOf<List<PostulacionUsuario>>(emptyList()) }
    LaunchedEffect(usuario?.id) {
        runCatching { ApiClient.call { misPostulaciones() } }.onSuccess { lista ->
            val conocidos = SessionManager.estadosConocidos()
            postulaciones = lista
            cambios = if (conocidos.isEmpty()) emptyList() else lista.filter { conocidos[it.id] != null && conocidos[it.id] != it.estado }
            if (conocidos.isEmpty()) SessionManager.guardarEstados(lista)
        }
    }

    fun logout() {
        SessionManager.cerrarSesion()
        session.logout()
        profilePrefs.clear()
        userProfile = profilePrefs.load("", "")
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
                    isTutor = isTutorMode,
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
                            onOpenNews = {
                                menuOpen = false
                                navController.goTo("news")
                            },
                            onOpenNewsDetail = { id ->
                                menuOpen = false
                                navController.navigate("news_detail/$id")
                            },
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
                        val id = entry.arguments?.getInt("id") ?: 0
                        ApplicationDetailScreen(
                            oportunidadId = id,
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
                            onOpenOportunidad = { id -> navController.navigate("application_detail/$id") },
                            onEditProfile = {
                                menuOpen = false
                                navController.navigate("edit_profile")
                            },
                            profile = userProfile,
                            onOpenForum = { id ->
                                menuOpen = false
                                navController.navigate("forum/$id")
                            },
                        )
                    }
                    composable("settings") {
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = onDarkThemeChange,
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) },
                            onLogout = { logout() },
                            isTutor = isTutorMode,
                            onTutorChange = { enabled ->
                                session.setTutor(enabled)
                                isTutorMode = enabled
                            },
                            tutorProgramId = tutorProgramId,
                            onTutorProgramChange = { id ->
                                session.setTutorProgramId(id)
                                tutorProgramId = id
                            },
                        )
                    }
                    composable("tutor_home") {
                        val program = SampleData.opportunities.find { it.id == tutorProgramId }
                            ?: SampleData.opportunities.first()
                        TutorDashboardScreen(
                            program = program,
                            enrolledCount = SampleData.tutorRoster.size,
                            presentToday = presentTodayCount,
                            onAttendance = { navController.navigate("attendance") },
                            onOpenForum = { navController.navigate("forum/$tutorProgramId") },
                            onBack = { navController.popBackStack() },
                            onOpenSettings = { navController.navigate("settings") },
                        )
                    }
                    composable("attendance") {
                        val program = SampleData.opportunities.find { it.id == tutorProgramId }
                            ?: SampleData.opportunities.first()
                        val todayKey = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
                        AttendanceScreen(
                            programTitle = program.title,
                            roster = SampleData.tutorRoster,
                            initialDateKey = todayKey,
                            loadPresent = { key -> attendancePrefs.presentNames(key) },
                            savePresent = { key, names ->
                                attendancePrefs.savePresent(key, names)
                                if (key == todayKey) presentTodayCount = names.size
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() },
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
                    composable(
                        "forum/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        val programTitle = postulaciones.find { it.oportunidad_id == programId }?.oportunidad_titulo
                            ?: SampleData.opportunities.find { it.id == programId }?.title
                            ?: "Voluntariado"
                        var messages by remember {
                            mutableStateOf(
                                forumPrefs.seedIfEmpty(
                                    programId,
                                    ForumMessage(
                                        id = 1,
                                        author = "Tutor",
                                        text = "Bienvenidos al foro de anuncios de $programTitle. Aquí el tutor compartirá novedades y recordatorios.",
                                        date = java.text.SimpleDateFormat("d MMM, HH:mm", java.util.Locale("es", "CL")).format(java.util.Date()),
                                    ),
                                ),
                            )
                        }
                        ForumScreen(
                            programTitle = programTitle,
                            messages = messages,
                            isTutor = isTutorMode,
                            onSend = { text ->
                                val message = ForumMessage(
                                    id = (messages.maxOfOrNull { it.id } ?: 0) + 1,
                                    author = usuario?.nombreCompleto ?: userProfile.name,
                                    text = text,
                                    date = java.text.SimpleDateFormat("d MMM, HH:mm", java.util.Locale("es", "CL")).format(java.util.Date()),
                                )
                                forumPrefs.append(programId, message)
                                messages = messages + message
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("forums") {
                        val channels = postulaciones
                            .filter { it.estado == "Seleccionado" && !it.voluntariado_terminado && !it.rechazo_participacion }
                            .map { p ->
                                ForumChannel(
                                    p.oportunidad_id,
                                    p.oportunidad_titulo,
                                    p.organizacion_nombre,
                                    forumPrefs.messages(p.oportunidad_id).lastOrNull()?.text,
                                )
                            }
                        ForumsScreen(
                            forums = channels,
                            onOpenForum = { id ->
                                menuOpen = false
                                navController.navigate("forum/$id")
                            },
                            onShowAlert = onShowAlert,
                            onToggleMenu = onToggleMenu,
                        )
                    }
                    composable("news") {
                        NewsScreen(
                            onShowAlert = onShowAlert,
                            onToggleMenu = onToggleMenu,
                            onOpenDetail = { id ->
                                menuOpen = false
                                navController.navigate("news_detail/$id")
                            },
                        )
                    }
                    composable(
                        "news_detail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType }),
                    ) { entry ->
                        val id = entry.arguments?.getInt("id") ?: 0
                        NewsDetailScreen(
                            newsId = id,
                            onBack = { navController.popBackStack() },
                        )
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
        StatusAlertSheet(
            cambios = cambios,
            postulaciones = postulaciones,
            onClose = {
                showStatusAlert = false
                // Al ver el aviso, los estados actuales pasan a ser los "conocidos".
                SessionManager.guardarEstados(postulaciones)
                cambios = emptyList()
            },
        )
    }
}

@Composable
private fun BottomBar(currentRoute: String, isTutor: Boolean, onNavigate: (String) -> Unit) {
    val T = LocalAppColors.current
    androidx.compose.material3.NavigationBar(
        containerColor = T.navBg,
        contentColor = T.navInactive,
    ) {
        BottomNavItem.values()
            .filter { it != BottomNavItem.Tutor || isTutor }
            .forEach { item ->
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

/** Rellena los campos vacíos del perfil local con los datos de la cuenta en el backend. */
private fun UserProfile.completarCon(u: Usuario): UserProfile = copy(
    name = u.nombreCompleto,
    email = u.email,
    phone = phone.ifBlank { u.telefono },
    region = region.ifBlank { u.region },
    city = city.ifBlank { u.ciudad },
    commune = commune.ifBlank { u.comuna },
)

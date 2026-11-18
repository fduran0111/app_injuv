package com.injuv.voluntariado.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import android.widget.Toast
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.injuv.voluntariado.components.EstadoCarga
import com.injuv.voluntariado.components.rememberCarga
import com.injuv.voluntariado.data.AttendancePreferences
import com.injuv.voluntariado.data.NotificationPreferences
import com.injuv.voluntariado.data.OrgProfile
import com.injuv.voluntariado.data.api.ForoCanal
import com.injuv.voluntariado.data.api.ForoMensaje
import com.injuv.voluntariado.data.api.ForoMensajeBody
import com.injuv.voluntariado.data.api.Integrante
import com.injuv.voluntariado.data.api.OrganizacionDetalle
import com.injuv.voluntariado.data.api.aForumMessage
import com.injuv.voluntariado.data.api.aOrgProfile
import com.injuv.voluntariado.data.api.conCambios
import com.injuv.voluntariado.data.api.asistenciaDelDia
import com.injuv.voluntariado.data.api.guardarAsistenciaDia
import com.injuv.voluntariado.data.api.guardarPerfil
import com.injuv.voluntariado.data.api.presentesIniciales
import com.injuv.voluntariado.data.api.ApiClient
import com.injuv.voluntariado.data.api.Encargado
import com.injuv.voluntariado.data.api.EncargadosBody
import com.injuv.voluntariado.data.api.Oportunidad
import com.injuv.voluntariado.data.api.PostulacionUsuario
import com.injuv.voluntariado.data.api.pendienteCalificarOrg
import com.injuv.voluntariado.data.api.SessionManager
import com.injuv.voluntariado.data.ForumChannel
import com.injuv.voluntariado.data.Opportunity
import com.injuv.voluntariado.data.ProfilePreferences
import com.injuv.voluntariado.data.SessionPreferences
import com.injuv.voluntariado.data.UserProfile
import com.injuv.voluntariado.data.api.Usuario
import com.injuv.voluntariado.screens.AcademyScreen
import com.injuv.voluntariado.screens.ApplicationDetailScreen
import com.injuv.voluntariado.screens.ApplicationsScreen
import com.injuv.voluntariado.screens.AssignTutorScreen
import com.injuv.voluntariado.screens.AttendanceScreen
import com.injuv.voluntariado.screens.RosterEntry
import com.injuv.voluntariado.screens.AuthScreen
import com.injuv.voluntariado.data.AvisoItem
import com.injuv.voluntariado.screens.CertificadosScreen
import com.injuv.voluntariado.screens.DocumentsScreen
import com.injuv.voluntariado.screens.EditOrgScreen
import com.injuv.voluntariado.screens.EditProfileScreen
import com.injuv.voluntariado.screens.ForumScreen
import com.injuv.voluntariado.screens.ForumsScreen
import com.injuv.voluntariado.screens.HomeScreen
import com.injuv.voluntariado.screens.LibraryScreen
import com.injuv.voluntariado.screens.NewsDetailScreen
import com.injuv.voluntariado.screens.NewsScreen
import com.injuv.voluntariado.screens.NotificationSettingsScreen
import com.injuv.voluntariado.screens.NotificationsScreen
import com.injuv.voluntariado.screens.OrganizationScreen
import com.injuv.voluntariado.screens.ProfileScreen
import com.injuv.voluntariado.screens.SettingsScreen
import com.injuv.voluntariado.screens.TutorDashboardScreen
import com.injuv.voluntariado.screens.TutorListScreen
import com.injuv.voluntariado.screens.computeAgeYears
import com.injuv.voluntariado.ui.theme.LocalAppColors
import com.injuv.voluntariado.ui.theme.SpaceGrotesk
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val screensWithBottomNav = setOf(
    "home",
    "applications",
    "news",
    "profile",
    "documents",
    "academy",
    "library",
    "tutor_list",
    "tutor_home",
    "forums",
    "org",
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
    // Roles según la API (los mismos que en la web):
    //  - super tutor: administrador de la organización, gestiona todo;
    //  - tutor: integrante asignado como encargado de uno o más voluntariados;
    //  - voluntario: cualquier otra cuenta.
    var tutorias by remember { mutableStateOf<List<Oportunidad>>(emptyList()) }
    var tutorProgramId by remember { mutableStateOf(session.tutorProgramId()) }
    var orgOportunidades by remember { mutableStateOf<List<Oportunidad>>(emptyList()) }
    var orgEncargados by remember { mutableStateOf<Map<Int, List<Encargado>>>(emptyMap()) }
    var orgDetalle by remember { mutableStateOf<OrganizacionDetalle?>(null) }
    var integrantes by remember { mutableStateOf<List<Integrante>>(emptyList()) }
    var orgError by remember { mutableStateOf<String?>(null) }
    var orgVersion by remember { mutableStateOf(0) }
    var attendanceVersion by remember { mutableStateOf(0) }
    var foros by remember { mutableStateOf<List<ForoCanal>>(emptyList()) }
    val scope = rememberCoroutineScope()
    fun avisar(e: Throwable) {
        Toast.makeText(context, e.message ?: "Ocurrió un error inesperado.", Toast.LENGTH_LONG).show()
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
    val orgRef = usuario?.organizacion
    val esSuperTutor = orgRef?.rol == "admin"
    val isOrgMode = orgRef != null
    // El super tutor puede pasar lista en todos los voluntariados de su organización.
    val tutorPrograms = if (esSuperTutor) orgOportunidades else tutorias
    val isTutorMode = tutorPrograms.isNotEmpty()
    val puedePasarLista = { id: Int -> esSuperTutor || tutorias.any { it.id == id } }
    val orgProfile = orgDetalle?.aOrgProfile() ?: OrgProfile(name = orgRef?.nombre.orEmpty())
    LaunchedEffect(usuario?.id, orgVersion) {
        runCatching { ApiClient.call { tutorias() } }.onSuccess { tutorias = it }
    }
    LaunchedEffect(usuario?.id, orgRef?.id, orgVersion) {
        if (orgRef == null) {
            orgDetalle = null
            orgOportunidades = emptyList()
            orgEncargados = emptyMap()
            integrantes = emptyList()
            return@LaunchedEffect
        }
        runCatching { ApiClient.call { miOrganizacion() } }.onSuccess { orgDetalle = it }
        runCatching { ApiClient.call { integrantes() } }.onSuccess { integrantes = it }
        try {
            val lista = ApiClient.call { oportunidadesMias() }
            orgOportunidades = lista
            orgEncargados = lista.associate { op ->
                op.id to runCatching { ApiClient.call { encargados(op.id) } }.getOrDefault(emptyList())
            }
            orgError = null
        } catch (e: Exception) {
            orgError = e.message ?: "No se pudieron cargar los voluntariados de tu organización"
        }
    }

    // Aviso de cambios de estado y anuncios: compara lo que llega con lo último visto.
    var cambios by remember { mutableStateOf<List<PostulacionUsuario>>(emptyList()) }
    var cambiosPrevios by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }
    var postulaciones by remember { mutableStateOf<List<PostulacionUsuario>>(emptyList()) }
    var avisosNoLeidos by remember { mutableStateOf<List<AvisoItem>>(emptyList()) }
    var calificarPendientes by remember { mutableStateOf<List<PostulacionUsuario>>(emptyList()) }
    var showStatusAlert by remember { mutableStateOf(false) }
    val notifPrefs = remember { NotificationPreferences(context) }

    // Cada 30 s refresca postulaciones y anuncios de foros; si llega algo nuevo, el aviso se abre solo.
    LaunchedEffect(usuario?.id) {
        if (usuario == null) return@LaunchedEffect
        while (true) {
            runCatching { ApiClient.call { misPostulaciones() } }.onSuccess { lista ->
                val conocidos = SessionManager.estadosConocidos()
                val nuevos = if (conocidos.isEmpty()) emptyList()
                    else lista.filter { conocidos[it.id] != null && conocidos[it.id] != it.estado }
                val llegaron = nuevos.any { n -> cambios.none { it.id == n.id } }
                postulaciones = lista
                cambios = nuevos
                cambiosPrevios = nuevos.associate { it.id to (conocidos[it.id] ?: "") }
                if (conocidos.isEmpty()) SessionManager.guardarEstados(lista)
                if (llegaron && notifPrefs.permiteEstados()) showStatusAlert = true
                // Voluntariados terminados pendientes de tu reseña a la organización.
                val recordadas = SessionManager.calificarVistos()
                val nuevasCalificar = lista.filter { it.pendienteCalificarOrg() && it.id !in recordadas }
                if (nuevasCalificar.any { n -> calificarPendientes.none { it.id == n.id } } && notifPrefs.permiteCalificar()) {
                    showStatusAlert = true
                }
                calificarPendientes = nuevasCalificar
            }
            val vistos = SessionManager.avisosVistos()
            val canales = runCatching { ApiClient.call { foros() } }.getOrDefault(emptyList())
            foros = canales
            val nuevosAvisos = canales.mapNotNull { f ->
                runCatching { ApiClient.call { foroMensajes(f.oportunidad_id, "anuncios") } }
                    .getOrDefault(emptyList())
                    .maxByOrNull { it.id }
                    ?.takeIf { it.id > (vistos[f.oportunidad_id] ?: 0) }
                    ?.let { m ->
                        AvisoItem(
                            oportunidadId = f.oportunidad_id,
                            titulo = f.titulo,
                            organizacion = f.organizacion_nombre,
                            autor = m.autor_nombre,
                            texto = m.texto,
                            fecha = m.created_at.orEmpty().take(10),
                            mensajeId = m.id,
                            esNuevo = true,
                        )
                    }
            }
            val llegaronAvisos = nuevosAvisos.any { n -> avisosNoLeidos.none { it.mensajeId == n.mensajeId } }
            avisosNoLeidos = nuevosAvisos
            if (llegaronAvisos && notifPrefs.permiteAnuncios()) showStatusAlert = true
            delay(30_000)
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

    // Al entrar a estas pantallas se refrescan las tutorías: si la ONG te acaba de designar,
    // la pestaña Tutoría aparece sin tener que cerrar sesión.
    LaunchedEffect(currentBaseRoute) {
        if (currentBaseRoute in setOf("home", "tutor_home", "forums")) {
            runCatching { ApiClient.call { tutorias() } }.onSuccess { tutorias = it }
        }
    }

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
                    isOrg = isOrgMode,
                    isAdmin = usuario?.rol == "admin",
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
                        val notifPrefs = remember { NotificationPreferences(context) }
                        SettingsScreen(
                            isDarkTheme = isDarkTheme,
                            onDarkThemeChange = onDarkThemeChange,
                            onBack = { navController.popBackStack() },
                            onNavigate = { route -> navController.navigate(route) },
                            onLogout = { logout() },
                            certificadosCount = postulaciones.count { it.tiene_certificado } + (usuario?.certificados_personales?.size ?: 0),
                            notificacionesSubtitle = if (!notifPrefs.activas) "Desactivadas"
                                else "Activadas · ${notifPrefs.tiposActivos()} ${if (notifPrefs.tiposActivos() == 1) "tipo" else "tipos"}",
                        )
                    }
                    composable("notification_settings") {
                        NotificationSettingsScreen(
                            onBack = { navController.popBackStack() },
                            onVerNotificaciones = { navController.navigate("notifications") },
                        )
                    }
                    composable("certificates") {
                        // Refresca por si la organización emitió un certificado con la app abierta.
                        LaunchedEffect(Unit) {
                            runCatching { ApiClient.call { misPostulaciones() } }.onSuccess { postulaciones = it }
                            SessionManager.refrescar()
                        }
                        CertificadosScreen(
                            usuario = usuario,
                            postulaciones = postulaciones,
                            onBack = { navController.popBackStack() },
                            onUsuarioActualizado = { SessionManager.actualizarUsuario(it) },
                        )
                    }
                    composable("notifications") {
                        // Último anuncio del foro de cada voluntariado del usuario.
                        var avisos by remember { mutableStateOf<List<AvisoItem>>(emptyList()) }
                        LaunchedEffect(Unit) {
                            runCatching { ApiClient.call { misPostulaciones() } }.onSuccess { lista ->
                                val conocidos = SessionManager.estadosConocidos()
                                postulaciones = lista
                                cambios = lista.filter { conocidos[it.id] != null && conocidos[it.id] != it.estado }
                                cambiosPrevios = cambios.associate { it.id to (conocidos[it.id] ?: "") }
                            }
                            val vistos = SessionManager.avisosVistos()
                            val canales = runCatching { ApiClient.call { foros() } }.getOrDefault(emptyList())
                            avisos = canales.mapNotNull { f ->
                                runCatching { ApiClient.call { foroMensajes(f.oportunidad_id, "anuncios") } }
                                    .getOrDefault(emptyList())
                                    .maxByOrNull { it.id }
                                    ?.let { m ->
                                        AvisoItem(
                                            oportunidadId = f.oportunidad_id,
                                            titulo = f.titulo,
                                            organizacion = f.organizacion_nombre,
                                            autor = m.autor_nombre,
                                            texto = m.texto,
                                            fecha = m.created_at.orEmpty().take(10),
                                            mensajeId = m.id,
                                            esNuevo = m.id > (vistos[f.oportunidad_id] ?: 0),
                                        )
                                    }
                            }
                        }
                        // Al salir, todo lo visto queda marcado como leído.
                        DisposableEffect(Unit) {
                            onDispose {
                                SessionManager.guardarEstados(postulaciones)
                                cambios = emptyList()
                                avisos.forEach { SessionManager.guardarAvisoVisto(it.oportunidadId, it.mensajeId) }
                            }
                        }
                        val notifPrefs = remember { NotificationPreferences(context) }
                        NotificationsScreen(
                            cambios = if (notifPrefs.permiteEstados()) cambios else emptyList(),
                            previos = cambiosPrevios,
                            postulaciones = postulaciones,
                            avisos = if (notifPrefs.permiteAnuncios()) avisos else emptyList(),
                            onBack = { navController.popBackStack() },
                            onOpenAviso = { id -> navController.navigate("forum/$id") },
                            pendientesCalificar = if (notifPrefs.permiteCalificar()) postulaciones.filter { it.pendienteCalificarOrg() } else emptyList(),
                            onCalificar = { navController.goTo("profile") },
                            mostrarEstado = notifPrefs.permiteResumen(),
                            desactivadas = !notifPrefs.activas,
                        )
                    }
                    composable("org") {
                        OrganizationScreen(
                            org = orgProfile,
                            onEdit = { navController.navigate("edit_org") },
                            canManage = esSuperTutor,
                            memberCount = integrantes.size,
                            tutorDe = tutorias.map { it.id }.toSet(),
                            onShowAlert = onShowAlert,
                            onToggleMenu = onToggleMenu,
                            oportunidades = orgOportunidades,
                            encargados = orgEncargados,
                            loadError = orgError,
                            onAssignTutor = { id ->
                                menuOpen = false
                                navController.navigate("assign_tutor/$id")
                            },
                            onViewAttendance = { id ->
                                menuOpen = false
                                navController.navigate("org_attendance/$id")
                            },
                        )
                    }
                    composable(
                        "org_attendance/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        AttendanceRoute(
                            programTitle = orgOportunidades.find { it.id == programId }?.titulo ?: "Voluntariado",
                            programId = programId,
                            readOnly = !puedePasarLista(programId),
                            excludeUserIds = orgOportunidades.find { it.id == programId }?.encargados_ids.orEmpty().toSet() + listOfNotNull(usuario?.id),
                            onSaved = { attendanceVersion++ },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(
                        "assign_tutor/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        val program = orgOportunidades.find { it.id == programId }
                        val integrantesIds = integrantes.map { it.id }.toSet()
                        AssignTutorScreen(
                            programTitle = program?.titulo ?: "Voluntariado",
                            loadAssigned = { ApiClient.call { encargados(programId) } },
                            findAccount = { email -> ApiClient.call { buscarUsuario(email) } },
                            isMember = { cuenta -> cuenta.id in integrantesIds },
                            onSave = { ids ->
                                val guardados = ApiClient.call { guardarEncargados(programId, EncargadosBody(ids)) }
                                orgEncargados = orgEncargados + (programId to guardados)
                                orgVersion++
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("edit_org") {
                        EditOrgScreen(
                            org = orgProfile,
                            onSave = { updated ->
                                scope.launch {
                                    runCatching {
                                        val actual = orgDetalle ?: ApiClient.call { miOrganizacion() }
                                        ApiClient.call { actualizarOrganizacion(actual.conCambios(updated)) }
                                    }
                                        .onSuccess {
                                            orgDetalle = it
                                            SessionManager.refrescar()
                                            navController.popBackStack()
                                        }
                                        .onFailure(::avisar)
                                }
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("tutor_list") {
                        TutorListScreen(
                            tutorias = tutorPrograms,
                            onOpen = { id ->
                                tutorProgramId = id
                                session.setTutorProgramId(id)
                                navController.navigate("tutor_home/$id")
                            },
                            onOpenSettings = { navController.navigate("settings") },
                        )
                    }
                    composable(
                        "tutor_home/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        val program = tutorPrograms.find { it.id == programId } ?: run {
                            Text("Ya no eres tutor de este voluntariado.", modifier = Modifier.padding(24.dp))
                            return@composable
                        }
                        val roster = rememberCarga(program.id, attendanceVersion, usuario?.id) {
                            val excluidos = program.encargados_ids.toSet() + listOfNotNull(usuario?.id)
                            ApiClient.call { postulantes(program.id) }.filter { it.participa && it.usuario_id !in excluidos }
                        }
                        val inscritos = roster.datos.orEmpty()
                        TutorDashboardScreen(
                            program = program.comoPrograma(),
                            enrolledCount = inscritos.size,
                            presentToday = inscritos.count { it.asistencia_actividad == "SI" },
                            onAttendance = { navController.navigate("attendance/${program.id}") },
                            onOpenForum = { navController.navigate("forum/${program.id}") },
                            onBack = { navController.popBackStack() },
                            onOpenSettings = { navController.navigate("settings") },
                        )
                    }
                    composable(
                        "attendance/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        AttendanceRoute(
                            programTitle = tutorPrograms.find { it.id == programId }?.titulo ?: "Voluntariado",
                            programId = programId,
                            excludeUserIds = tutorPrograms.find { it.id == programId }?.encargados_ids.orEmpty().toSet() + listOfNotNull(usuario?.id),
                            onSaved = { attendanceVersion++ },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("edit_profile") {
                        EditProfileScreen(
                            profile = userProfile,
                            onSave = { updated ->
                                val antes = userProfile
                                val cuenta = usuario ?: return@EditProfileScreen
                                scope.launch {
                                    runCatching { guardarPerfil(context, cuenta, antes, updated) }
                                        .onSuccess { u ->
                                            SessionManager.actualizarUsuario(u)
                                            userProfile = updated
                                            profilePrefs.save(updated)
                                            session.login(u.nombreCompleto, u.email)
                                            navController.popBackStack()
                                        }
                                        .onFailure(::avisar)
                                }
                            },
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable(
                        "forum/{programId}",
                        arguments = listOf(navArgument("programId") { type = NavType.IntType }),
                    ) { entry ->
                        val programId = entry.arguments?.getInt("programId") ?: 0
                        ForoRoute(
                            programId = programId,
                            programTitle = tituloForo(programId, postulaciones, tutorPrograms, foros),
                            canal = "anuncios",
                            canWrite = puedePasarLista(programId),
                            onBack = { navController.popBackStack() },
                        )
                    }
                    composable("forums") {
                        LaunchedEffect(Unit) {
                            runCatching { ApiClient.call { foros() } }.onSuccess { foros = it }
                        }
                        ForumsScreen(
                            forums = foros.filter { !it.es_tutor }.map { f ->
                                ForumChannel(
                                    id = f.oportunidad_id,
                                    title = f.titulo,
                                    org = f.organizacion_nombre,
                                    lastMessage = f.ultimo_mensaje,
                                )
                            },
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
            cambios = if (notifPrefs.permiteEstados()) cambios else emptyList(),
            previos = cambiosPrevios,
            postulaciones = postulaciones,
            avisos = if (notifPrefs.permiteAnuncios()) avisosNoLeidos else emptyList(),
            mostrarResumen = notifPrefs.permiteResumen(),
            porCalificar = if (notifPrefs.permiteCalificar()) calificarPendientes else emptyList(),
            onCalificar = {
                calificarPendientes.forEach { SessionManager.guardarCalificarVisto(it.id) }
                calificarPendientes = emptyList()
                showStatusAlert = false
                navController.goTo("profile")
            },
            onClose = {
                showStatusAlert = false
                // Al ver el aviso, los estados, anuncios y recordatorios pasan a ser los "conocidos".
                SessionManager.guardarEstados(postulaciones)
                cambios = emptyList()
                cambiosPrevios = emptyMap()
                avisosNoLeidos.forEach { SessionManager.guardarAvisoVisto(it.oportunidadId, it.mensajeId) }
                avisosNoLeidos = emptyList()
                calificarPendientes.forEach { SessionManager.guardarCalificarVisto(it.id) }
                calificarPendientes = emptyList()
            },
        )
    }
}

@Composable
private fun BottomBar(
    currentRoute: String,
    isTutor: Boolean,
    isOrg: Boolean,
    isAdmin: Boolean = false,
    onNavigate: (String) -> Unit,
) {
    val T = LocalAppColors.current
    androidx.compose.material3.NavigationBar(
        containerColor = T.navBg,
        contentColor = T.navInactive,
    ) {
        BottomNavItem.values()
            .filter { it != BottomNavItem.Tutor || isTutor }
            .filter { it != BottomNavItem.Org || isOrg }
            .filter { it != BottomNavItem.Applications || (!isOrg && !isAdmin) }
            .forEach { item ->
            val isActive = item.route == currentRoute || (item == BottomNavItem.Tutor && currentRoute == "tutor_home")
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

/** Adapta un voluntariado de la API al modelo que usa el panel de tutor. */
private fun Oportunidad.comoPrograma() = Opportunity(
    id = id,
    title = titulo,
    org = organizacion_nombre,
    location = ubicacion,
    dateRange = listOfNotNull(fecha_inicio_voluntariado, fecha_fin_voluntariado).joinToString(" – "),
    hours = horas_voluntariado ?: 0,
    spots = cupo_maximo ?: 0,
    deadline = fecha_limite_postulacion.orEmpty(),
    tags = listOf(tipo_de_voluntariado).filter { it.isNotBlank() },
    description = descripcion,
    imageUrl = if (tiene_imagen) ApiClient.imagenOportunidad(id) else "",
)

private fun tituloForo(
    programId: Int,
    postulaciones: List<PostulacionUsuario>,
    tutorPrograms: List<Oportunidad>,
    foros: List<ForoCanal>,
): String = postulaciones.find { it.oportunidad_id == programId }?.oportunidad_titulo
    ?: tutorPrograms.find { it.id == programId }?.titulo
    ?: foros.find { it.oportunidad_id == programId }?.titulo
    ?: "Voluntariado"

/** Pasar lista (tutor / super tutor) o verla, con los voluntarios seleccionados del voluntariado. */
@Composable
private fun AttendanceRoute(
    programTitle: String,
    programId: Int,
    onBack: () -> Unit,
    readOnly: Boolean = false,
    excludeUserIds: Set<Int> = emptySet(),
    onSaved: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val asistenciaPrefs = remember { AttendancePreferences(context) }
    var saving by remember { mutableStateOf(false) }
    val hoy = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val carga = rememberCarga(programId, excludeUserIds) {
        val lista = ApiClient.call { postulantes(programId) }.filter { it.participa && it.usuario_id !in excludeUserIds }
        // La lista de hoy: la del historial del backend; si no hay registro, la última del flag;
        // y si nunca se pasó lista, todos presentes.
        val presentesHoy = runCatching { asistenciaDelDia(programId, hoy, lista) }.getOrNull()
            ?: asistenciaPrefs.presentIds(programId, hoy).takeIf { it.isNotEmpty() }
            ?: presentesIniciales(lista)
        lista to presentesHoy
    }
    EstadoCarga(carga, modifier = Modifier.fillMaxSize()) { (lista, presentesHoy) ->
        val todos = lista.map { it.id }.toSet()
        // La lista del día elegido: la del historial del backend; si ese día no tiene registro,
        // la caché local; y si tampoco, todos presentes (o la última lista si es hoy).
        suspend fun listaDe(dateKey: String): Set<Int> =
            runCatching { asistenciaDelDia(programId, dateKey, lista) }.getOrNull()
                ?: asistenciaPrefs.presentIds(programId, dateKey).takeIf { it.isNotEmpty() }
                ?: if (dateKey == hoy) presentesHoy else todos
        AttendanceScreen(
            programTitle = programTitle,
            roster = lista.map { RosterEntry(it.id, it.usuario_nombre.ifBlank { it.usuario_email }) },
            initialDateKey = hoy,
            loadPresent = { listaDe(it) },
            initialPresent = presentesHoy,
            saving = saving,
            readOnly = readOnly,
            onSave = { dateKey, presentes ->
                scope.launch {
                    saving = true
                    asistenciaPrefs.savePresent(programId, dateKey, presentes)
                    runCatching { guardarAsistenciaDia(programId, dateKey, lista, presentes) }
                        .onSuccess {
                            onSaved()
                            onBack()
                        }
                        .onFailure {
                            Toast.makeText(context, it.message ?: "No se pudo guardar la lista.", Toast.LENGTH_LONG).show()
                            carga.recargar()
                        }
                    saving = false
                }
            },
            onBack = onBack,
        )
    }
}

/**
 * Foro de un voluntariado guardado en la plataforma: "anuncios" (publica el tutor) o "chat"
 * (tutores y voluntarios seleccionados). Se refresca cada 10 s mientras está abierto.
 */
@Composable
private fun ForoRoute(
    programId: Int,
    programTitle: String,
    canal: String,
    canWrite: Boolean,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var mensajes by remember(programId, canal) { mutableStateOf<List<ForoMensaje>>(emptyList()) }
    LaunchedEffect(programId, canal) {
        while (true) {
            runCatching { ApiClient.call { foroMensajes(programId, canal) } }.onSuccess { mensajes = it }
            delay(10_000)
        }
    }
    val esChat = canal == "chat"
    ForumScreen(
        programTitle = programTitle,
        messages = mensajes.map { it.aForumMessage() },
        canWrite = canWrite,
        headerTitle = if (esChat) "Chat del voluntariado" else "Foro de anuncios",
        badge = if (esChat) null else "TUTOR",
        placeholder = if (esChat) "Escribe un mensaje..." else "Escribe un anuncio...",
        onSend = { text ->
            scope.launch {
                runCatching { ApiClient.call { publicarEnForo(programId, ForoMensajeBody(canal, text)) } }
                    .onSuccess { nuevo -> mensajes = mensajes + nuevo }
                    .onFailure {
                        Toast.makeText(context, it.message ?: "No se pudo enviar el mensaje.", Toast.LENGTH_LONG).show()
                    }
            }
        },
        onBack = onBack,
    )
}

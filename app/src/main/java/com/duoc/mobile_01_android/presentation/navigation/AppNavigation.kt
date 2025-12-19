package com.duoc.mobile_01_android.presentation.navigation

import android.app.Application
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.mobile_01_android.di.ServiceLocator
import com.duoc.mobile_01_android.domain.model.SessionState
import com.duoc.mobile_01_android.presentation.auth.LoginScreen
import com.duoc.mobile_01_android.presentation.auth.RecuperarPasswordScreen
import com.duoc.mobile_01_android.presentation.citas.CitasScreen
import com.duoc.mobile_01_android.presentation.clientes.ClientesScreen
import com.duoc.mobile_01_android.presentation.consultas.ConsultasScreen
import com.duoc.mobile_01_android.presentation.home.HomeScreen
import com.duoc.mobile_01_android.presentation.mascotas.MascotasScreen
import com.duoc.mobile_01_android.presentation.resumen.ResumenScreen
import com.duoc.mobile_01_android.presentation.vacunas.VacunasScreen
import kotlinx.coroutines.launch

/**
 * Sealed class que representa las diferentes rutas de navegación.
 * Patrón type-safe para evitar errores con strings mágicos.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object RecuperarPassword : Screen("recuperar_password")
    object Home : Screen("home")
    object Mascotas : Screen("mascotas")
    object Clientes : Screen("clientes")
    object Consultas : Screen("consultas")
    object Resumen : Screen("resumen")
    object Citas : Screen("citas")
    object Vacunas : Screen("vacunas")
}

/**
 * Configuración de navegación de la aplicación.
 * Usa ServiceLocator para inyectar ViewModels en cada pantalla.
 *
 * Ventajas de este enfoque:
 * - Cada pantalla obtiene su propio ViewModel
 * - Los ViewModels son proporcionados por ServiceLocator
 * - Navegación con animaciones fade in/out suaves
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    // Observar estado de sesión para determinar la pantalla inicial
    val authRepository = ServiceLocator.provideAuthRepository()
    val sessionState by authRepository.getCurrentSession().collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val startDestination = when (sessionState) {
        is SessionState.Authenticated -> Screen.Home.route
        is SessionState.NotAuthenticated -> Screen.Login.route
    }

    // Obtener usuario actual si está autenticado
    val currentUser = (sessionState as? SessionState.Authenticated)?.usuario
    val userRole = currentUser?.rol ?: com.duoc.mobile_01_android.domain.model.Rol.DUENO
    val clienteIdFiltro = if (userRole == com.duoc.mobile_01_android.domain.model.Rol.DUENO) {
        currentUser?.clienteId
    } else {
        null
    }

    // Función para cerrar sesión
    fun handleLogout() {
        coroutineScope.launch {
            authRepository.logout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(durationMillis = 500))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(durationMillis = 300))
        }
    ) {
        // ==================== Pantallas de Autenticación ====================

        composable(
            route = Screen.Login.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            LoginScreen(
                viewModel = ServiceLocator.provideLoginViewModel(),
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRecuperarPassword = {
                    navController.navigate(Screen.RecuperarPassword.route)
                }
            )
        }

        composable(
            route = Screen.RecuperarPassword.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            RecuperarPasswordScreen(
                viewModel = ServiceLocator.provideRecuperarPasswordViewModel(),
                onBack = { navController.popBackStack() }
            )
        }

        // ==================== Pantallas Principales ====================

        composable(
            route = Screen.Home.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            HomeScreen(
                viewModel = ServiceLocator.provideHomeViewModel(),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Mascotas.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            MascotasScreen(
                viewModel = ServiceLocator.provideMascotasViewModel(clienteIdFiltro),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Clientes.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            ClientesScreen(
                viewModel = ServiceLocator.provideClientesViewModel(),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Consultas.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            ConsultasScreen(
                viewModel = ServiceLocator.provideConsultasViewModel(),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Resumen.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            ResumenScreen(
                viewModel = ServiceLocator.provideResumenViewModel(),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Citas.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            CitasScreen(
                viewModel = ServiceLocator.provideCitasViewModel(application, clienteIdFiltro),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }

        composable(
            route = Screen.Vacunas.route,
            enterTransition = {
                fadeIn(animationSpec = tween(durationMillis = 500))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = 300))
            }
        ) {
            VacunasScreen(
                viewModel = ServiceLocator.provideVacunasViewModel(application, clienteIdFiltro),
                userRole = userRole,
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() },
                onLogout = { handleLogout() }
            )
        }
    }
}

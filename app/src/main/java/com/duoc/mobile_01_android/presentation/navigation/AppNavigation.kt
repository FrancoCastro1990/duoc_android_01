package com.duoc.mobile_01_android.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.duoc.mobile_01_android.di.ServiceLocator
import com.duoc.mobile_01_android.presentation.clientes.ClientesScreen
import com.duoc.mobile_01_android.presentation.consultas.ConsultasScreen
import com.duoc.mobile_01_android.presentation.home.HomeScreen
import com.duoc.mobile_01_android.presentation.mascotas.MascotasScreen
import com.duoc.mobile_01_android.presentation.resumen.ResumenScreen

/**
 * Sealed class que representa las diferentes rutas de navegación.
 * Patrón type-safe para evitar errores con strings mágicos.
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Mascotas : Screen("mascotas")
    object Clientes : Screen("clientes")
    object Consultas : Screen("consultas")
    object Resumen : Screen("resumen")
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
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
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
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                }
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
                viewModel = ServiceLocator.provideMascotasViewModel(),
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
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
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
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
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
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
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

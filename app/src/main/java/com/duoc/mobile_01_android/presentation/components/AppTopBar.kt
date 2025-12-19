package com.duoc.mobile_01_android.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.duoc.mobile_01_android.domain.model.Rol

data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    showBackButton: Boolean = false,
    userRole: Rol = Rol.ADMIN,
    onBackClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    // Definir items del menú según el rol del usuario
    val menuItems = when (userRole) {
        Rol.ADMIN -> listOf(
            MenuItem("Inicio", Icons.Default.Home, "home"),
            MenuItem("Mascotas", Icons.Default.Pets, "mascotas"),
            MenuItem("Clientes", Icons.Default.Person, "clientes"),
            MenuItem("Consultas", Icons.Default.MedicalServices, "consultas"),
            MenuItem("Citas", Icons.Default.CalendarMonth, "citas"),
            MenuItem("Vacunas", Icons.Default.Vaccines, "vacunas"),
            MenuItem("Resumen", Icons.Default.Assessment, "resumen")
        )
        Rol.DUENO -> listOf(
            MenuItem("Inicio", Icons.Default.Home, "home"),
            MenuItem("Mis Mascotas", Icons.Default.Pets, "mascotas"),
            MenuItem("Mis Citas", Icons.Default.CalendarMonth, "citas"),
            MenuItem("Mis Vacunas", Icons.Default.Vaccines, "vacunas")
        )
    }

    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Volver"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                menuItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.title) },
                        onClick = {
                            onNavigate(item.route)
                            expanded = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title
                            )
                        }
                    )
                }

                HorizontalDivider()

                DropdownMenuItem(
                    text = { Text("Cerrar Sesion") },
                    onClick = {
                        onLogout()
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesion"
                        )
                    }
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

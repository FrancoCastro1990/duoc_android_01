package com.duoc.mobile_01_android

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource
import com.duoc.mobile_01_android.domain.model.Consulta
import com.duoc.mobile_01_android.presentation.theme.Mobile_01_androidTheme

/**
 * Activity que muestra los detalles completos de una consulta veterinaria.
 *
 * Esta Activity recibe un ID de consulta mediante Intent extras y muestra:
 * - Logo de la veterinaria
 * - Información del cliente
 * - Datos de la mascota
 * - Detalles de la consulta (descripción, fecha)
 * - Lista de medicamentos aplicados con precios
 * - Total de la consulta
 *
 * Incluye funcionalidad para:
 * - Volver a la pantalla anterior (botón back)
 * - Compartir los detalles de la consulta vía Intent.ACTION_SEND
 *
 * @see ComponentActivity
 */
class DetalleConsultaActivity : ComponentActivity() {

    companion object {
        /**
         * Key para pasar el ID de la consulta como extra en el Intent.
         */
        private const val EXTRA_CONSULTA_ID = "consulta_id"

        /**
         * Crea un Intent para iniciar DetalleConsultaActivity con un ID de consulta específico.
         *
         * @param context Contexto desde el cual se lanza la Activity
         * @param consultaId ID de la consulta a mostrar
         * @return Intent configurado con el consultaId como extra
         */
        fun createIntent(context: Context, consultaId: Int): Intent {
            return Intent(context, DetalleConsultaActivity::class.java).apply {
                putExtra(EXTRA_CONSULTA_ID, consultaId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener ID de consulta del Intent
        val consultaId = intent.getIntExtra(EXTRA_CONSULTA_ID, -1)

        setContent {
            Mobile_01_androidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetalleConsultaScreen(
                        consultaId = consultaId,
                        onBackClick = { finish() },
                        onShareClick = { consulta -> shareConsulta(consulta) }
                    )
                }
            }
        }
    }

    /**
     * Comparte los detalles de la consulta usando Intent.ACTION_SEND.
     *
     * Crea un Intent chooser para permitir al usuario seleccionar la aplicación
     * con la que desea compartir la información (WhatsApp, Email, etc.).
     *
     * @param consulta Consulta cuyos detalles se compartirán
     */
    private fun shareConsulta(consulta: Consulta) {
        val shareText = buildString {
            appendLine("DETALLE DE CONSULTA VETERINARIA")
            appendLine("=" * 40)
            appendLine()
            appendLine("CLIENTE: ${consulta.cliente.nombre}")
            appendLine("Email: ${consulta.cliente.email}")
            appendLine("Teléfono: ${consulta.cliente.telefono}")
            appendLine()
            appendLine("MASCOTA: ${consulta.mascota.nombre}")
            appendLine("Especie: ${consulta.mascota.especie}")
            appendLine("Edad: ${consulta.mascota.edad} años")
            appendLine("Peso: ${consulta.mascota.peso} kg")
            appendLine()
            appendLine("CONSULTA")
            appendLine("Fecha: ${consulta.fecha}")
            appendLine("Descripción: ${consulta.descripcion}")
            appendLine()
            appendLine("MEDICAMENTOS:")
            consulta.medicamentos.forEach { med ->
                val precioFinal = med.precioConDescuento()
                appendLine("- ${med.nombre} (${med.dosificacion})")
                if (med.descuento > 0) {
                    appendLine("  Precio: $${med.precio} (Desc: ${(med.descuento * 100).toInt()}%) = $${String.format("%.2f", precioFinal)}")
                } else {
                    appendLine("  Precio: $${String.format("%.2f", precioFinal)}")
                }
            }
            appendLine()
            appendLine("TOTAL: $${String.format("%.2f", consulta.total)}")
            appendLine("=" * 40)
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Compartir consulta mediante")
        startActivity(shareIntent)
    }

    /**
     * Operador de extensión para repetir un String n veces.
     * Utilizado para crear líneas decorativas en el texto compartido.
     */
    private operator fun String.times(n: Int): String = this.repeat(n)
}

/**
 * Composable principal que muestra la pantalla de detalle de consulta.
 *
 * @param consultaId ID de la consulta a mostrar
 * @param onBackClick Callback invocado cuando se presiona el botón de retroceso
 * @param onShareClick Callback invocado cuando se presiona el botón de compartir
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleConsultaScreen(
    consultaId: Int,
    onBackClick: () -> Unit,
    onShareClick: (Consulta) -> Unit
) {
    // Observar la consulta desde InMemoryDataSource
    val consulta by remember {
        derivedStateOf {
            InMemoryDataSource.consultas.value.find { it.id == consultaId }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Consulta") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    consulta?.let {
                        IconButton(onClick = { onShareClick(it) }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Compartir"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (consulta == null) {
            // Mostrar mensaje si no se encuentra la consulta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Consulta no encontrada",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            // Mostrar contenido de la consulta
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo de veterinaria
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = "Logo Veterinaria",
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterHorizontally),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Veterinaria App",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                HorizontalDivider()

                // Sección Cliente
                DetailSection(title = "CLIENTE") {
                    DetailRow(label = "Nombre", value = consulta!!.cliente.nombre)
                    DetailRow(label = "Email", value = consulta!!.cliente.email)
                    DetailRow(label = "Teléfono", value = consulta!!.cliente.telefono)
                }

                // Sección Mascota
                DetailSection(title = "MASCOTA") {
                    DetailRow(label = "Nombre", value = consulta!!.mascota.nombre)
                    DetailRow(label = "Especie", value = consulta!!.mascota.especie)
                    DetailRow(label = "Edad", value = "${consulta!!.mascota.edad} años")
                    DetailRow(label = "Peso", value = "${consulta!!.mascota.peso} kg")
                }

                // Sección Consulta
                DetailSection(title = "CONSULTA") {
                    DetailRow(label = "Fecha", value = consulta!!.fecha)
                    DetailRow(label = "Descripción", value = consulta!!.descripcion)
                }

                // Sección Medicamentos
                DetailSection(title = "MEDICAMENTOS") {
                    if (consulta!!.medicamentos.isEmpty()) {
                        Text(
                            text = "No se aplicaron medicamentos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        consulta!!.medicamentos.forEach { medicamento ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = medicamento.nombre,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Dosificación: ${medicamento.dosificacion}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (medicamento.descuento > 0) {
                                        Text(
                                            text = "Precio: $${medicamento.precio} (Desc: ${(medicamento.descuento * 100).toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                    Text(
                                        text = "Precio final: $${String.format("%.2f", medicamento.precioConDescuento())}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Total
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "$${String.format("%.2f", consulta!!.total)}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composable reutilizable para mostrar una sección con título y contenido.
 *
 * @param title Título de la sección (ej: "CLIENTE", "MASCOTA")
 * @param content Contenido de la sección (composable lambda)
 */
@Composable
fun DetailSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            HorizontalDivider()
            content()
        }
    }
}

/**
 * Composable reutilizable para mostrar una fila de detalle (etiqueta: valor).
 *
 * @param label Etiqueta descriptiva (ej: "Nombre", "Email")
 * @param value Valor a mostrar
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

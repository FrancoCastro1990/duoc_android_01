package com.duoc.mobile_01_android.util

import android.content.Intent
import android.net.Uri
import com.duoc.mobile_01_android.domain.model.Consulta

/**
 * Utilidades para trabajar con Intents de Android.
 *
 * Proporciona métodos helper para crear Intents comunes como:
 * - Compartir texto
 * - Enviar emails
 * - Realizar llamadas telefónicas
 * - Formatear información de consultas
 *
 * Sigue principio DRY: Centraliza la creación de Intents para evitar duplicación.
 */
object IntentUtils {

    /**
     * Crea un Intent para compartir contenido de texto mediante ACTION_SEND.
     *
     * El Intent resultante puede usarse con createChooser para mostrar
     * una lista de aplicaciones compatibles (WhatsApp, Email, SMS, etc.).
     *
     * @param title Título que aparecerá en el Intent chooser
     * @param content Contenido de texto a compartir
     * @return Intent configurado con ACTION_SEND y tipo "text/plain"
     *
     * Ejemplo de uso:
     * ```
     * val intent = IntentUtils.createShareIntent("Compartir consulta", consultaText)
     * context.startActivity(Intent.createChooser(intent, "Compartir mediante"))
     * ```
     */
    fun createShareIntent(title: String, content: String): Intent {
        return Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
    }

    /**
     * Crea un Intent para enviar un email mediante ACTION_SENDTO.
     *
     * Utiliza el esquema "mailto:" para asegurar que solo se muestren
     * aplicaciones de email (no apps de mensajería).
     *
     * @param to Dirección de email del destinatario
     * @param subject Asunto del email
     * @param body Cuerpo del mensaje
     * @return Intent configurado con mailto: scheme
     *
     * Ejemplo de uso:
     * ```
     * val intent = IntentUtils.createEmailIntent(
     *     to = "cliente@example.com",
     *     subject = "Recordatorio de consulta",
     *     body = "Estimado cliente..."
     * )
     * context.startActivity(intent)
     * ```
     */
    fun createEmailIntent(to: String, subject: String, body: String): Intent {
        return Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(to))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
    }

    /**
     * Crea un Intent para realizar una llamada telefónica mediante ACTION_DIAL.
     *
     * Usa ACTION_DIAL en lugar de ACTION_CALL para no requerir permisos.
     * ACTION_DIAL abre el marcador con el número precargado, pero el usuario
     * debe presionar el botón de llamada manualmente.
     *
     * @param phoneNumber Número telefónico a marcar (puede incluir formato con +, espacios)
     * @return Intent configurado con tel: scheme
     *
     * Ejemplo de uso:
     * ```
     * val intent = IntentUtils.createCallIntent("+56 9 1234 5678")
     * context.startActivity(intent)
     * ```
     */
    fun createCallIntent(phoneNumber: String): Intent {
        return Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
    }

    /**
     * Formatea los datos de una consulta veterinaria en texto legible para compartir.
     *
     * Genera un resumen estructurado con:
     * - Encabezado decorativo
     * - Información del cliente
     * - Datos de la mascota
     * - Detalles de la consulta
     * - Lista de medicamentos con precios y descuentos
     * - Total de la consulta
     *
     * @param consulta Objeto Consulta a formatear
     * @return String formateado con todos los detalles de la consulta
     *
     * Ejemplo de uso:
     * ```
     * val consultaText = IntentUtils.formatConsultaForShare(consulta)
     * val shareIntent = createShareIntent("Detalle de consulta", consultaText)
     * ```
     */
    fun formatConsultaForShare(consulta: Consulta): String {
        return buildString {
            appendLine("=".repeat(40))
            appendLine("DETALLE DE CONSULTA VETERINARIA")
            appendLine("=".repeat(40))
            appendLine()

            // Información del cliente
            appendLine("CLIENTE:")
            appendLine("  Nombre: ${consulta.cliente.nombre}")
            appendLine("  Email: ${consulta.cliente.email}")
            appendLine("  Teléfono: ${consulta.cliente.telefono}")
            appendLine()

            // Información de la mascota
            appendLine("MASCOTA:")
            appendLine("  Nombre: ${consulta.mascota.nombre}")
            appendLine("  Especie: ${consulta.mascota.especie}")
            appendLine("  Edad: ${consulta.mascota.edad} años")
            appendLine("  Peso: ${consulta.mascota.peso} kg")
            appendLine()

            // Detalles de la consulta
            appendLine("CONSULTA:")
            appendLine("  Fecha: ${consulta.fecha}")
            appendLine("  Descripción: ${consulta.descripcion}")
            appendLine()

            // Medicamentos aplicados
            if (consulta.medicamentos.isNotEmpty()) {
                appendLine("MEDICAMENTOS APLICADOS:")
                consulta.medicamentos.forEach { medicamento ->
                    val precioFinal = medicamento.precioConDescuento()
                    appendLine("  - ${medicamento.nombre} (${medicamento.dosificacion})")

                    if (medicamento.descuento > 0) {
                        val descuentoPorcentaje = (medicamento.descuento * 100).toInt()
                        appendLine("    Precio: $${String.format("%,.0f", medicamento.precio)}")
                        appendLine("    Descuento: $descuentoPorcentaje%")
                        appendLine("    Precio final: $${String.format("%,.0f", precioFinal)}")
                    } else {
                        appendLine("    Precio: $${String.format("%,.0f", precioFinal)}")
                    }
                }
                appendLine()
            }

            // Total
            appendLine("=".repeat(40))
            appendLine("TOTAL: $${String.format("%,.0f", consulta.total)}")
            appendLine("=".repeat(40))
        }
    }
}

package com.duoc.mobile_01_android.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import com.duoc.mobile_01_android.data.datasource.InMemoryDataSource

/**
 * Content Provider de solo lectura para exponer datos de la veterinaria a otras aplicaciones.
 *
 * Este proveedor permite que aplicaciones externas consulten información sobre mascotas y consultas
 * registradas en la veterinaria, sin permitir modificaciones (read-only).
 *
 * URIs soportadas:
 * - content://com.duoc.mobile_01_android.provider/mascotas (todas las mascotas)
 * - content://com.duoc.mobile_01_android.provider/mascotas/# (mascota por ID)
 * - content://com.duoc.mobile_01_android.provider/consultas (todas las consultas)
 * - content://com.duoc.mobile_01_android.provider/consultas/# (consulta por ID)
 *
 * Los datos se obtienen desde InMemoryDataSource, garantizando consistencia con el resto de la app.
 *
 * @see ContentProvider
 */
class VeterinariaProvider : ContentProvider() {

    companion object {
        /**
         * Autoridad del Content Provider. Debe ser única en el sistema.
         */
        private const val AUTHORITY = "com.duoc.mobile_01_android.provider"

        /**
         * Path para acceder a todas las mascotas.
         */
        private const val PATH_MASCOTAS = "mascotas"

        /**
         * Path para acceder a todas las consultas.
         */
        private const val PATH_CONSULTAS = "consultas"

        /**
         * Código para identificar consultas de todas las mascotas.
         */
        private const val MASCOTAS = 1

        /**
         * Código para identificar consultas de una mascota específica por ID.
         */
        private const val MASCOTA_ID = 2

        /**
         * Código para identificar consultas de todas las consultas.
         */
        private const val CONSULTAS = 3

        /**
         * Código para identificar consultas de una consulta específica por ID.
         */
        private const val CONSULTA_ID = 4

        /**
         * MIME type para una lista de mascotas.
         */
        private const val MIME_TYPE_MASCOTAS = "vnd.android.cursor.dir/vnd.$AUTHORITY.mascotas"

        /**
         * MIME type para una mascota individual.
         */
        private const val MIME_TYPE_MASCOTA = "vnd.android.cursor.item/vnd.$AUTHORITY.mascota"

        /**
         * MIME type para una lista de consultas.
         */
        private const val MIME_TYPE_CONSULTAS = "vnd.android.cursor.dir/vnd.$AUTHORITY.consultas"

        /**
         * MIME type para una consulta individual.
         */
        private const val MIME_TYPE_CONSULTA = "vnd.android.cursor.item/vnd.$AUTHORITY.consulta"

        /**
         * URI Matcher para identificar el tipo de consulta según la URI.
         */
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, PATH_MASCOTAS, MASCOTAS)
            addURI(AUTHORITY, "$PATH_MASCOTAS/#", MASCOTA_ID)
            addURI(AUTHORITY, PATH_CONSULTAS, CONSULTAS)
            addURI(AUTHORITY, "$PATH_CONSULTAS/#", CONSULTA_ID)
        }
    }

    override fun onCreate(): Boolean {
        // Inicialización del provider
        // Retorna true si la inicialización fue exitosa
        return true
    }

    /**
     * Consulta datos según la URI proporcionada.
     *
     * @param uri URI que identifica los datos a consultar
     * @param projection Columnas a incluir en el resultado (null = todas)
     * @param selection Cláusula WHERE (no soportada en este provider)
     * @param selectionArgs Argumentos para la cláusula WHERE
     * @param sortOrder Orden de los resultados (no soportado en este provider)
     * @return Cursor con los datos solicitados, o null si la URI no es válida
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            MASCOTAS -> queryAllMascotas()
            MASCOTA_ID -> queryMascotaById(uri.lastPathSegment?.toIntOrNull())
            CONSULTAS -> queryAllConsultas()
            CONSULTA_ID -> queryConsultaById(uri.lastPathSegment?.toIntOrNull())
            else -> null
        }
    }

    /**
     * Retorna todas las mascotas registradas en el sistema.
     *
     * @return Cursor con las columnas: id, nombre, especie, edad, peso, clienteId
     */
    private fun queryAllMascotas(): Cursor {
        val cursor = MatrixCursor(arrayOf("id", "nombre", "especie", "edad", "peso", "clienteId"))
        val mascotas = InMemoryDataSource.mascotas.value

        mascotas.forEach { mascota ->
            cursor.addRow(
                arrayOf(
                    mascota.id,
                    mascota.nombre,
                    mascota.especie,
                    mascota.edad,
                    mascota.peso,
                    mascota.clienteId
                )
            )
        }

        return cursor
    }

    /**
     * Retorna una mascota específica por su ID.
     *
     * @param id ID de la mascota a consultar
     * @return Cursor con la mascota encontrada (vacío si no existe)
     */
    private fun queryMascotaById(id: Int?): Cursor {
        val cursor = MatrixCursor(arrayOf("id", "nombre", "especie", "edad", "peso", "clienteId"))

        if (id != null) {
            val mascota = InMemoryDataSource.mascotas.value.find { it.id == id }
            mascota?.let {
                cursor.addRow(
                    arrayOf(
                        it.id,
                        it.nombre,
                        it.especie,
                        it.edad,
                        it.peso,
                        it.clienteId
                    )
                )
            }
        }

        return cursor
    }

    /**
     * Retorna todas las consultas registradas en el sistema.
     *
     * @return Cursor con las columnas: id, clienteNombre, mascotaNombre, descripcion, fecha, total
     */
    private fun queryAllConsultas(): Cursor {
        val cursor = MatrixCursor(
            arrayOf("id", "clienteNombre", "mascotaNombre", "descripcion", "fecha", "total")
        )
        val consultas = InMemoryDataSource.consultas.value

        consultas.forEach { consulta ->
            cursor.addRow(
                arrayOf(
                    consulta.id,
                    consulta.cliente.nombre,
                    consulta.mascota.nombre,
                    consulta.descripcion,
                    consulta.fecha,
                    consulta.total
                )
            )
        }

        return cursor
    }

    /**
     * Retorna una consulta específica por su ID.
     *
     * @param id ID de la consulta a consultar
     * @return Cursor con la consulta encontrada (vacío si no existe)
     */
    private fun queryConsultaById(id: Int?): Cursor {
        val cursor = MatrixCursor(
            arrayOf("id", "clienteNombre", "mascotaNombre", "descripcion", "fecha", "total")
        )

        if (id != null) {
            val consulta = InMemoryDataSource.consultas.value.find { it.id == id }
            consulta?.let {
                cursor.addRow(
                    arrayOf(
                        it.id,
                        it.cliente.nombre,
                        it.mascota.nombre,
                        it.descripcion,
                        it.fecha,
                        it.total
                    )
                )
            }
        }

        return cursor
    }

    /**
     * Retorna el MIME type apropiado para una URI dada.
     *
     * @param uri URI para la cual obtener el MIME type
     * @return MIME type en formato vnd.android.cursor (dir o item)
     */
    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            MASCOTAS -> MIME_TYPE_MASCOTAS
            MASCOTA_ID -> MIME_TYPE_MASCOTA
            CONSULTAS -> MIME_TYPE_CONSULTAS
            CONSULTA_ID -> MIME_TYPE_CONSULTA
            else -> null
        }
    }

    /**
     * Inserciones no soportadas (provider de solo lectura).
     *
     * @return null (operación no soportada)
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // Provider de solo lectura - no permite inserciones
        return null
    }

    /**
     * Eliminaciones no soportadas (provider de solo lectura).
     *
     * @return 0 (ninguna fila eliminada)
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        // Provider de solo lectura - no permite eliminaciones
        return 0
    }

    /**
     * Actualizaciones no soportadas (provider de solo lectura).
     *
     * @return 0 (ninguna fila actualizada)
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        // Provider de solo lectura - no permite actualizaciones
        return 0
    }
}

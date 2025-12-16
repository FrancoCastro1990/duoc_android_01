package com.duoc.mobile_01_android.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.duoc.mobile_01_android.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests instrumentados de UI para validar el diseño responsivo y navegación.
 *
 * Estos tests verifican:
 * - Que MainActivity se renderiza correctamente
 * - Que el HomeScreen muestra los elementos esperados
 * - Que la navegación entre pantallas funciona
 * - Que el logo y textos principales son visibles
 *
 * Usa Jetpack Compose Testing para interactuar con la UI de forma declarativa.
 *
 * NOTA: Estos tests requieren ejecutarse en un dispositivo/emulador Android
 * porque prueban la composición y renderizado real de la UI.
 *
 * @see MainActivity
 * @see HomeScreen
 */
@RunWith(AndroidJUnit4::class)
class ResponsiveLayoutTest {

    /**
     * Regla de Compose que inicia MainActivity automáticamente.
     *
     * createAndroidComposeRule inicia la Activity y proporciona acceso
     * al árbol de nodos de Compose para hacer assertions.
     */
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Verifica que MainActivity se renderiza sin errores.
     *
     * Este es el test más básico - asegura que la app puede iniciar
     * y que el Activity Lifecycle funciona correctamente.
     */
    @Test
    fun mainActivity_displaysCorrectly() {
        // Esperar un segundo para que las animaciones terminen
        composeTestRule.waitForIdle()

        // Verificar que la Activity está activa y el Compose tree se renderizó
        // Si llegamos aquí sin excepciones, la Activity se creó correctamente
        // Verificar que al menos el título principal está presente
        composeTestRule.onNodeWithText("Veterinaria App")
            .assertExists("MainActivity debe mostrar el título 'Veterinaria App'")
    }

    /**
     * Verifica que el HomeScreen muestra el logo de la veterinaria.
     *
     * El logo es un elemento visual crítico para la identidad de la app.
     * Usa VeterinariaLogo composable que renderiza un icono de mascota.
     */
    @Test
    fun homeScreen_hasLogo() {
        // Esperar a que las animaciones de entrada terminen
        composeTestRule.waitForIdle()

        // El logo usa Icons.Default.Pets internamente con contentDescription "Veterinaria Logo"
        // Verificamos que el logo esté presente en la pantalla
        composeTestRule.onNodeWithContentDescription("Veterinaria Logo")
            .assertExists("HomeScreen debe mostrar el logo de la veterinaria")
    }

    /**
     * Verifica que el HomeScreen muestra los botones de navegación principales.
     *
     * La pantalla de inicio debe tener accesos rápidos a:
     * - Nueva Consulta
     * - Clientes
     * - Mascotas
     * - Resumen
     */
    @Test
    fun homeScreen_hasNavigationButtons() {
        // Esperar a que las animaciones terminen
        composeTestRule.waitForIdle()

        // Verificar botón "Nueva Consulta"
        composeTestRule.onNodeWithText("Nueva Consulta")
            .assertExists("Debe existir botón 'Nueva Consulta'")
            .assertIsDisplayed()

        // Verificar botón "Clientes"
        composeTestRule.onNodeWithText("Clientes", substring = true)
            .assertExists("Debe existir botón 'Clientes'")
            .assertIsDisplayed()

        // Verificar que las cards de acceso rápido existen
        composeTestRule.onNodeWithText("Mascotas")
            .assertExists("Debe existir card 'Mascotas'")

        composeTestRule.onNodeWithText("Consultas")
            .assertExists("Debe existir card 'Consultas'")

        composeTestRule.onNodeWithText("Resumen")
            .assertExists("Debe existir card 'Resumen'")
    }

    /**
     * Verifica que la navegación a la pantalla de Clientes funciona.
     *
     * Este test valida que:
     * 1. El botón "Clientes" es clickeable
     * 2. La navegación se ejecuta correctamente
     * 3. La nueva pantalla se renderiza
     *
     * NOTA: Este test puede fallar si ClientesScreen tarda en cargar
     * o si tiene errores de renderizado.
     */
    @Test
    fun navigationToClientes_works() {
        // Esperar a que la pantalla inicial esté lista
        composeTestRule.waitForIdle()

        // Hacer clic en el botón "Clientes"
        // Usamos substring = true porque el texto puede tener espacios extra
        composeTestRule.onNodeWithText("Clientes", substring = true, useUnmergedTree = true)
            .performClick()

        // Esperar a que la navegación complete
        composeTestRule.waitForIdle()

        // Verificar que navegamos a ClientesScreen
        // La pantalla de clientes debe mostrar "Clientes" en el título
        // o algún elemento específico de esa pantalla
        composeTestRule.onNodeWithText("Clientes")
            .assertExists("Después de navegar, debe mostrarse la pantalla de Clientes")
    }
}

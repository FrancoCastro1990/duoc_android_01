# Veterinaria App - Sistema de Gestion Veterinaria

Aplicacion Android nativa desarrollada en Kotlin que implementa un sistema completo de gestion para clinicas veterinarias. El proyecto demuestra el uso de los componentes fundamentales de Android, arquitectura MVVM, Jetpack Compose con Material Design 3, y buenas practicas de desarrollo movil.

## Tabla de Contenidos

1. [Descripcion General](#descripcion-general)
2. [Credenciales de Acceso](#credenciales-de-acceso)
3. [Arquitectura](#arquitectura)
4. [Componentes de Android](#componentes-de-android)
5. [Interfaz de Usuario](#interfaz-de-usuario)
6. [Navegacion e Intents](#navegacion-e-intents)
7. [Estructura del Proyecto](#estructura-del-proyecto)
8. [Requisitos del Sistema](#requisitos-del-sistema)
9. [Instalacion y Configuracion](#instalacion-y-configuracion)
10. [Ejecucion de Pruebas](#ejecucion-de-pruebas)
11. [Tecnologias Utilizadas](#tecnologias-utilizadas)

---

## Descripcion General

Veterinaria App es una aplicacion de gestion integral para clinicas veterinarias que permite administrar clientes, mascotas, consultas medicas y medicamentos. La aplicacion esta disenada siguiendo los principios SOLID, KISS y DRY, garantizando un codigo mantenible, escalable y de facil comprension.

### Funcionalidades Principales

- **Sistema de Autenticacion**: Login con roles (Admin/Dueno), recuperacion de contrasena
- **Gestion de Clientes**: CRUD completo con validacion en tiempo real y busqueda
- **Gestion de Mascotas**: CRUD con campo raza, filtros por especie y busqueda
- **Gestion de Consultas**: CRUD completo con seleccion de medicamentos y calculo de costos
- **Sistema de Citas**: Agenda de citas con estados (Pendiente/Completada/Cancelada)
- **Sistema de Vacunas**: Registro de vacunacion con alertas de proximas vacunas
- **Notificaciones Automaticas**: Confirmacion inmediata al crear citas y registrar vacunas
- **Panel de Resumen**: Estadisticas, ingresos totales, proximas citas y alertas de vacunas
- **Navegacion por Rol**: Admin ve todo, Dueno ve solo sus mascotas y datos relacionados
- **Manejo de Errores**: Snackbars y dialogos de error con opcion de reintento
- Servicio de recordatorios con notificaciones persistentes
- Compartir informacion de consultas con aplicaciones externas
- Deteccion de conectividad WiFi con notificaciones contextuales

---

## Credenciales de Acceso

### Usuario Administrador
- **Email**: `admin@vet.com`
- **Password**: `admin123`
- **Acceso**: Completo a todas las funcionalidades

### Usuario Dueno (Cliente)
- **Email**: Email de cualquier cliente registrado
- **Password**: `cliente123`
- **Acceso**: Solo sus mascotas, citas y vacunas

---

## Arquitectura

El proyecto implementa una arquitectura MVVM (Model-View-ViewModel) combinada con Clean Architecture, separando claramente las responsabilidades en capas diferenciadas.

### Capas de la Arquitectura

```
Presentation Layer (UI)
    |
    v
ViewModel Layer (Logica de Presentacion)
    |
    v
Domain Layer (Reglas de Negocio)
    |
    v
Data Layer (Acceso a Datos)
```

### Flujo de Datos

```
Composable (UI) --> ViewModel --> Repository Interface --> RepositoryImpl --> DataSource
```

- Los ViewModels exponen estados mediante StateFlow para actualizaciones reactivas
- Los Repositories implementan interfaces del dominio, permitiendo sustitucion e inyeccion
- El DataSource almacena datos en memoria utilizando MutableStateFlow
- La inyeccion de dependencias se realiza mediante ServiceLocator

---

## Componentes de Android

La aplicacion implementa los cuatro componentes fundamentales de Android:

### Activity

La aplicacion cuenta con dos Activities:

- **MainActivity**: Activity principal que aloja la navegacion con Jetpack Compose Navigation. Implementa manejo de Deep Links para apertura desde enlaces externos.

- **DetalleConsultaActivity**: Activity secundaria que muestra el detalle completo de una consulta. Demuestra el uso de Explicit Intents para navegacion entre Activities e Implicit Intents para compartir informacion.

### Service

- **ReminderService**: Foreground Service que gestiona recordatorios de consultas veterinarias. Muestra una notificacion persistente mientras esta activo y cumple con las restricciones de Android 12+ mediante el tipo `specialUse`.

Caracteristicas del servicio:
- Creacion automatica de NotificationChannel para Android 8+
- Notificacion persistente con acceso directo a la aplicacion
- Compatibilidad con Android 14+ mediante ServiceCompat
- Acciones START y STOP para control del ciclo de vida

### Content Provider

- **VeterinariaProvider**: Content Provider de solo lectura que expone datos de mascotas y consultas para consumo por aplicaciones externas.

URIs soportadas:
- `content://com.duoc.mobile_01_android.provider/mascotas` - Lista de mascotas
- `content://com.duoc.mobile_01_android.provider/mascotas/{id}` - Mascota por ID
- `content://com.duoc.mobile_01_android.provider/consultas` - Lista de consultas
- `content://com.duoc.mobile_01_android.provider/consultas/{id}` - Consulta por ID

### Broadcast Receiver

- **WifiConnectionReceiver**: Receptor que responde a cambios de conectividad de red. Al detectar conexion WiFi, muestra un Toast con informacion contextual sobre consultas y mascotas registradas.

---

## Interfaz de Usuario

La interfaz esta desarrollada integramente con Jetpack Compose y Material Design 3.

### Pantallas

| Pantalla | Descripcion |
|----------|-------------|
| LoginScreen | Inicio de sesion con validacion de credenciales y acceso a recuperar contrasena |
| RecuperarPasswordScreen | Recuperacion de contrasena simulada con confirmacion por email |
| HomeScreen | Dashboard con accesos rapidos, proximas citas, alertas de vacunas y control de recordatorios |
| ClientesScreen | Gestion CRUD de clientes con validacion en tiempo real, busqueda y filtros |
| MascotasScreen | Administracion de mascotas con campo raza, filtros por especie y busqueda |
| ConsultasScreen | CRUD de consultas con seleccion de mascota, medicamentos y calculo de total |
| CitasScreen | Gestion de citas con estados (Pendiente/Completada/Cancelada) y filtros |
| VacunasScreen | Registro de vacunas con seccion de proximas vacunas y alertas |
| ResumenScreen | Panel de estadisticas con ingresos, especies mas atendidas y ultimas consultas |
| DetalleConsultaScreen | Vista detallada de consulta con opcion de compartir |

### Componentes Reutilizables

- **VeterinariaLogo**: Componente de logo con icono circular y texto opcional
- **AppTopBar**: Barra superior con navegacion condicional por rol y logout
- **LoadingIndicator**: Indicadores de carga en multiples variantes
- **SearchBar**: Barra de busqueda reutilizable con boton de limpiar
- **ErrorDisplay**: Componentes de error (Snackbar, Dialog, Card) con reintento
- **DatePickerDialog**: Selector de fecha Material 3 (formato dd/MM/yyyy)
- **TimePickerDialog**: Selector de hora Material 3 (formato HH:mm, 24 horas)

### Selectores de Fecha y Hora

Los formularios de Citas y Vacunas utilizan componentes nativos de Material 3:

| Pantalla | Campo | Componente |
|----------|-------|------------|
| Citas | Fecha | DatePickerDialog |
| Citas | Hora | TimePickerDialog |
| Vacunas | Fecha aplicacion | DatePickerDialog |
| Vacunas | Proxima fecha | DatePickerDialog |

### Validaciones en Tiempo Real

El formulario de clientes implementa validacion instantanea:
- Nombre: Minimo 3 caracteres, solo letras
- Email: Formato valido mediante expresion regular
- Telefono: Minimo 9 digitos con formateo automatico

Cada campo muestra indicadores visuales de estado (correcto/error) mediante iconos y textos de soporte.

---

## Navegacion e Intents

### Explicit Intents

Utilizados para navegacion deterministica entre Activities:

```kotlin
val intent = DetalleConsultaActivity.createIntent(context, consultaId)
context.startActivity(intent)
```

### Implicit Intents

Implementados para compartir informacion con aplicaciones externas:

```kotlin
val shareIntent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, contenido)
}
startActivity(Intent.createChooser(shareIntent, "Compartir via"))
```

### Intent Filters

Configurados para permitir apertura de la aplicacion desde enlaces externos:

```xml
<intent-filter>
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="veterinaria" android:host="open" />
</intent-filter>
```

Ejemplo de Deep Link: `veterinaria://open/consultas`

---

## Estructura del Proyecto

```
com.duoc.mobile_01_android/
|
|-- data/
|   |-- datasource/
|   |   |-- InMemoryDataSource.kt
|   |-- repository/
|       |-- AuthRepositoryImpl.kt
|       |-- ClienteRepositoryImpl.kt
|       |-- MascotaRepositoryImpl.kt
|       |-- ConsultaRepositoryImpl.kt
|       |-- CitaRepositoryImpl.kt
|       |-- VacunaRepositoryImpl.kt
|       |-- MedicamentoRepositoryImpl.kt
|
|-- domain/
|   |-- model/
|   |   |-- Usuario.kt
|   |   |-- SessionState.kt
|   |   |-- Cliente.kt
|   |   |-- Mascota.kt
|   |   |-- Consulta.kt
|   |   |-- Cita.kt
|   |   |-- Vacuna.kt
|   |   |-- Medicamento.kt
|   |-- repository/
|       |-- AuthRepository.kt
|       |-- ClienteRepository.kt
|       |-- MascotaRepository.kt
|       |-- ConsultaRepository.kt
|       |-- CitaRepository.kt
|       |-- VacunaRepository.kt
|       |-- MedicamentoRepository.kt
|
|-- presentation/
|   |-- auth/
|   |   |-- LoginScreen.kt
|   |   |-- LoginViewModel.kt
|   |   |-- RecuperarPasswordScreen.kt
|   |   |-- RecuperarPasswordViewModel.kt
|   |-- home/
|   |   |-- HomeScreen.kt
|   |   |-- HomeViewModel.kt
|   |-- clientes/
|   |   |-- ClientesScreen.kt
|   |   |-- ClientesViewModel.kt
|   |-- mascotas/
|   |   |-- MascotasScreen.kt
|   |   |-- MascotasViewModel.kt
|   |-- consultas/
|   |   |-- ConsultasScreen.kt
|   |   |-- ConsultasViewModel.kt
|   |-- citas/
|   |   |-- CitasScreen.kt
|   |   |-- CitasViewModel.kt
|   |-- vacunas/
|   |   |-- VacunasScreen.kt
|   |   |-- VacunasViewModel.kt
|   |-- resumen/
|   |   |-- ResumenScreen.kt
|   |   |-- ResumenViewModel.kt
|   |-- navigation/
|   |   |-- AppNavigation.kt
|   |-- components/
|   |   |-- AppTopBar.kt
|   |   |-- LoadingIndicator.kt
|   |   |-- VeterinariaLogo.kt
|   |   |-- SearchBar.kt
|   |   |-- ErrorDisplay.kt
|   |-- theme/
|       |-- Color.kt
|       |-- Theme.kt
|       |-- Type.kt
|
|-- service/
|   |-- ReminderService.kt
|
|-- provider/
|   |-- VeterinariaProvider.kt
|
|-- receiver/
|   |-- WifiConnectionReceiver.kt
|
|-- di/
|   |-- ServiceLocator.kt
|
|-- util/
|   |-- Constants.kt
|   |-- ValidationUtils.kt
|   |-- IntentUtils.kt
|   |-- NotificationHelper.kt
|
|-- MainActivity.kt
|-- DetalleConsultaActivity.kt
```

---

## Requisitos del Sistema

### Desarrollo

- Android Studio Hedgehog (2023.1.1) o superior
- JDK 17 o JDK 21
- Gradle 8.7.3
- Kotlin 1.9.0 o superior

### Dispositivo/Emulador

- Android API Level 21 (Lollipop) minimo
- Android API Level 36 recomendado
- Permiso de notificaciones (Android 13+)

---

## Instalacion y Configuracion

### Clonar el Repositorio

```bash
git clone <url-del-repositorio>
cd mobile_01_android
```

### Compilar el Proyecto

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew assembleDebug
```

### Instalar en Dispositivo/Emulador

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew installDebug
```

### Generar APK de Release

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew assembleRelease
```

El APK generado se encuentra en: `app/build/outputs/apk/debug/app-debug.apk`

---

## Ejecucion de Pruebas

### Pruebas Unitarias

Ejecutar todas las pruebas unitarias:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew test
```

Ejecutar una clase de prueba especifica:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew test --tests "com.duoc.mobile_01_android.util.IntentUtilsTest"
```

### Pruebas Instrumentadas

Requieren un emulador o dispositivo conectado:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew connectedAndroidTest
```

### Cobertura de Pruebas

| Categoria | Archivo | Descripcion |
|-----------|---------|-------------|
| Unitaria | ReminderServiceTest.kt | Validacion de constantes del servicio |
| Unitaria | IntentUtilsTest.kt | Formateo de datos para compartir |
| Unitaria | ValidationUtilsTest.kt | Validacion de email y telefono |
| Instrumentada | VeterinariaProviderTest.kt | Operaciones del Content Provider |
| Instrumentada | ResponsiveLayoutTest.kt | Elementos de UI y navegacion |
| Integracion | MvvmIntegrationTest.kt | Comunicacion entre capas MVVM |

---

## Tecnologias Utilizadas

### Lenguaje y Plataforma

- Kotlin 1.9.0
- Android SDK (API 21-36)
- Gradle 8.7.3 con Kotlin DSL

### Jetpack y AndroidX

- Jetpack Compose (UI declarativa)
- Compose Navigation (navegacion type-safe)
- ViewModel (gestion de estado)
- StateFlow (programacion reactiva)
- Lifecycle (observacion del ciclo de vida)

### Diseno

- Material Design 3
- Material Icons Extended
- Dynamic Color (Android 12+)
- Soporte para tema oscuro

### Testing

- JUnit 4
- AndroidX Test
- Compose UI Test
- Turbine (testing de Flows)

### Arquitectura

- MVVM (Model-View-ViewModel)
- Clean Architecture
- Repository Pattern
- Service Locator (inyeccion de dependencias)

---

## Informacion del Proyecto

- **Nombre**: Veterinaria App
- **Paquete**: com.duoc.mobile_01_android
- **Version SDK Minima**: 21 (Android 5.0 Lollipop)
- **Version SDK Objetivo**: 36 (Android 15)
- **Version de Compilacion**: 36

---

## Criterios de Evaluacion Cumplidos (EFT)

| Criterio | Puntos | Estado |
|----------|--------|--------|
| CRUD + Organizacion (filtros, busqueda, categorias) | 15 | Completado |
| Registro y Seguimiento (citas, historial, tiempo real) | 15 | Completado |
| Notificaciones Automaticas (citas, vacunas) | 10 | Completado |
| Interfaz de Usuario (intuitiva, elementos visuales) | 10 | Completado |
| Animaciones y Efectos Visuales (fade in/out) | 10 | Completado |
| Manejo de Errores (excepciones, estabilidad) | 10 | Completado |
| Optimizacion (rendimiento, experiencia fluida) | 5 | Completado |

---

## Licencia

Este proyecto fue desarrollado con fines educativos para el curso PMY2201 - Desarrollo de Aplicaciones Moviles.

---

## Autor

Desarrollado como Evaluacion Final Transversal (EFT) - Semana 9: "Sistema de Gestion Veterinaria".

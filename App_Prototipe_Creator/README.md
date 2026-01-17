#  App Prototype Creator

> Una aplicación multiplataforma que transforma ideas en prototipos detallados de aplicaciones mediante IA, con soporte para chat interactivo y visualización de prototipos.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green)
![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase)

##  Descripción

App Prototype Creator es una herramienta que utiliza inteligencia artificial para convertir ideas conceptuales en prototipos funcionales y detallados de aplicaciones. Construida con Kotlin Multiplatform y Compose Multiplatform, ofrece una experiencia fluida tanto en Android como en escritorio, permitiendo a desarrolladores y diseñadores visualizar y exportar sus conceptos de manera eficiente.

##  Características Principales

-  **Chat con IA**: Interfaz conversacional para describir tu idea de aplicación
-  **Galería de Prototipos**: Visualiza todos tus prototipos creados en una galería organizada
-  **Vista Detallada**: Explora cada prototipo con detalles completos y exportación
-  **Multiplataforma**: Funciona en Android y Desktop (JVM) con código compartido
-  **Tema Personalizable**: Soporte para tema claro y oscuro
-  **Multiidioma**: Sistema de internacionalización integrado (i18n)
-  **Persistencia en la Nube**: Integración con Supabase para almacenamiento
-  **Exportación**: Capacidad de exportar prototipos generados

##  Arquitectura

### Stack Tecnológico

- **Kotlin Multiplatform**: Compartición de código entre plataformas
- **Compose Multiplatform**: Framework UI declarativo moderno
- **Supabase**: Backend as a Service para almacenamiento y autenticación
- **Koin**: Inyección de dependencias
- **Napier**: Logging multiplataforma
- **Material Design 3**: Sistema de diseño moderno

### Estructura de Pantallas

```
┌─────────────────┐
│  GalleryScreen  │ ◄─── Pantalla inicial
└────────┬────────┘
         │
         ├──► ChatScreen ────► onOpenPrototype()
         │                              │
         └──────────────────────────────┴──► PrototypeDetailScreen
```

##  Requisitos Previos

- **JDK 17** o superior
- **Android Studio** Arctic Fox o superior (para desarrollo Android)
- **IntelliJ IDEA** (recomendado para desarrollo Desktop)
- **Gradle 8.0+**
- Cuenta de **Supabase** (para funcionalidad completa)

##  Instalación y Configuración

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Configurar Variables de Entorno

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
SUPABASE_URL=tu_url_de_supabase
SUPABASE_KEY=tu_clave_de_supabase
N8N_WEBHOOK_PATH=n8n_webhook_path
...
```

> **Nota**: El archivo `.env` debe estar en la raíz del proyecto. La aplicación busca automáticamente en `.env`, `../.env` o `../../.env`

### 3. Compilar y Ejecutar

####  Android

**En macOS/Linux:**
```bash
./gradlew :composeApp:assembleDebug
```

**En Windows:**
```bash
.\gradlew.bat :composeApp:assembleDebug
```

Para instalar directamente en un dispositivo conectado:
```bash
./gradlew :composeApp:installDebug
```

####  Desktop (JVM)

**En macOS/Linux:**
```bash
./gradlew :composeApp:run
```

**En Windows:**
```bash
.\gradlew.bat :composeApp:run
```

##  Estructura del Proyecto

```
App-Prototipe-Creator/
├── composeApp/
│   └── src/
│       ├── commonMain/kotlin/
│       │   └── app/prototype/creator/
│       │       ├── App.kt                    # Punto de entrada principal
│       │       ├── screens/
│       │       │   ├── GalleryScreen.kt     # Galería de prototipos
│       │       │   ├── ChatScreen.kt        # Chat con IA
│       │       │   └── PrototypeDetail.kt   # Detalles del prototipo
│       │       ├── ui/
│       │       │   ├── theme/               # Temas y estilos
│       │       │   └── navigation/          # Navegación
│       │       ├── data/
│       │       │   ├── model/               # Modelos de datos
│       │       │   ├── repository/          # Repositorios
│       │       │   ├── service/             # Servicios (Supabase)
│       │       │   └── i18n/                # Internacionalización
│       │       └── di/                      # Inyección de dependencias
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── MainActivity.kt
│       └── jvmMain/
│           └── kotlin/
│               └── Main.kt                   # Punto de entrada Desktop
├── .env                                      # Variables de entorno (no subir a git)
├── build.gradle.kts
└── README.md
```

##  Guía de Uso

### Flujo de Trabajo

1. **Inicio en Galería**
  - Al abrir la app, verás la galería con todos tus prototipos guardados
  - Puedes navegar entre prototipos o crear uno nuevo

2. **Crear Nuevo Prototipo**
  - Accede al **ChatScreen** desde la galería
  - Describe tu idea de aplicación de forma conversacional
  - La IA procesará tu descripción y generará un prototipo detallado

3. **Visualizar y Exportar**
  - Selecciona cualquier prototipo de la galería
  - Visualiza todos los detalles en **PrototypeDetailScreen**
  - Exporta el prototipo en el formato deseado

### Características del Sistema

- **Gestión de Estado**: Utiliza `remember` y `mutableStateOf` para mantener el estado entre navegaciones
- **Caché de Prototipos**: La galería mantiene los prototipos en caché para mejor rendimiento
- **Sistema de Versiones**: Forzar recreación de vistas cuando sea necesario
- **Navegación Type-Safe**: Sistema de navegación con rutas tipadas

##  Componentes Clave

### AppSettings
Gestiona configuraciones globales de la aplicación:
- **Tema**: Modo claro/oscuro
- **Idioma**: Sistema multiidioma con soporte para español por defecto
- **Configuración persistente**: Las preferencias se mantienen entre sesiones

### Repositorios
- **PrototypeRepository**: Gestión de prototipos (CRUD)
- **ChatRepository**: Manejo de conversaciones con IA
- **LanguageRepository**: Gestión de idiomas y traducciones

### Servicios
- **SupabaseService**: Conexión con backend de Supabase para persistencia

##  Internacionalización

El proyecto incluye un sistema completo de i18n con soporte para múltiples idiomas:

```kotlin
// Uso de strings localizados
Text(Strings.initializingApp.localized(currentLanguage))
```

Idioma por defecto: **Español (es)**

##  Manejo de Errores

La aplicación incluye un sistema robusto de manejo de errores:

- **LoadingScreen**: Pantalla de carga durante inicialización
- **ErrorScreen**: Pantalla de error con opción de reintentar
- **Logging**: Sistema de logs con Napier para debugging
- **Validación de Servicios**: Verificación de que todos los servicios estén inicializados correctamente

##  Seguridad

- Las claves API se gestionan mediante variables de entorno
- No se incluyen credenciales en el código fuente
- El archivo `.env` está en `.gitignore`


##  Características Técnicas Avanzadas

- **Inyección de Dependencias**: Uso de Koin para un código limpio y testeable
- **Composición**: Uso de `CompositionLocalProvider` para estado global
- **Lazy Initialization**: Componentes se inicializan solo cuando son necesarios
- **State Management**: Gestión reactiva del estado con StateFlow y MutableState
- **Error Boundaries**: Manejo elegante de errores en toda la app

##  Licencia

Este proyecto está bajo la Licencia MIT.

##  Autor

**Slenderman1314**
- GitHub: [@Slenderman1314](https://github.com/Slenderman1314)

##  Recursos y Documentación

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Supabase Documentation](https://supabase.com/docs)
- [Koin Documentation](https://insert-koin.io/)
- [Material Design 3](https://m3.material.io/)

##  Troubleshooting

### La app no inicia en Desktop
- Verifica que el archivo `.env` esté en la ubicación correcta
- Revisa los logs en consola con `Napier`
- Asegúrate de tener Java 17 o superior

### Errores de inicialización de servicios
- Confirma que las variables de entorno estén configuradas correctamente
- Verifica la conectividad con Supabase
- Revisa los logs para identificar qué servicio falla

### Problemas de compilación
```bash
# Limpia el proyecto
./gradlew clean

# Reconstruye
./gradlew build
```

##  Reportar Problemas

Si encuentras algún bug o tienes sugerencias:
1. Abre un [issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
2. Describe el problema detalladamente
3. Incluye logs si es posible
4. Especifica la plataforma (Android/Desktop)

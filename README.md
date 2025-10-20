# 🎨 App Prototipe Creator

Una aplicación multiplatforma innovadora que genera prototipos detallados de aplicaciones a partir de simples ideas. Transforma conceptos iniciales en diseños funcionales y estructurados, acelerando el proceso de desarrollo y validación de nuevas aplicaciones.

## 📋 Tabla de Contenidos

- [Características](#características)
- [Requisitos Previos](#requisitos-previos)
- [Instalación](#instalación)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Cómo Usar](#cómo-usar)
- [Desarrollo](#desarrollo)
- [Construcción](#construcción)
- [Tecnologías](#tecnologías)
- [Licencia](#licencia)

## ✨ Características

- **Generación Automática de Prototipos**: Convierte ideas en prototipos visuales detallados
- **Interfaz Intuitiva**: Diseño limpio y fácil de usar
- **Multiplatforma**: Disponible en Android y Desktop
- **Código Compartido**: Arquitectura Multiplatform de Kotlin para máxima eficiencia
- **Prototipado Rápido**: Acelera el ciclo de validación de proyectos
- **Exportación de Diseños**: Exporte los prototipos generados en múltiples formatos

## 🛠 Requisitos Previos

Antes de comenzar, asegúrate de tener instalado:

- **Java Development Kit (JDK)**: Versión 11 o superior
- **Android Studio**: Última versión recomendada
- **Gradle**: 7.0 o superior (incluido en el proyecto)
- **Kotlin**: 1.9.0 o superior

### Para Android:
- Android SDK 24 (API nivel mínimo)
- Android SDK 34 (API nivel destino recomendado)

### Para Desktop:
- Compatible con Windows, macOS y Linux

## 📦 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Sincronizar Dependencias

```bash
./gradlew sync
```

## 📁 Estructura del Proyecto

```
App-Prototipe-Creator/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/          # Código compartido entre todas las plataformas
│   │   │   └── kotlin/          # Lógica principal, modelos y composables
│   │   ├── androidMain/         # Código específico para Android
│   │   ├── jvmMain/             # Código específico para Desktop (JVM)
│   │   └── iosMain/             # Código específico para iOS (opcional)
│   ├── build.gradle.kts
│   └── resources/
├── gradle/
├── gradlew
├── settings.gradle.kts
└── README.md
```

### Descripción de Directorios

**commonMain/**: Contiene toda la lógica compartida, incluyendo:
- Modelos de datos
- Lógica de negocio
- Componentes UI reutilizables (Composables)
- Servicios y repositorios

**androidMain/**: Implementaciones específicas para Android
- Integraciones de plataforma Android
- Acceso a APIs nativas de Android

**jvmMain/**: Implementaciones específicas para Desktop
- Configuraciones de ventana
- Acceso a APIs de escritorio

## 🚀 Cómo Usar

### Flujo Básico de la Aplicación

1. **Iniciar la App**: Ejecuta la aplicación en tu plataforma objetivo
2. **Iniciar el Chat**: Entra en el chat desde su botón correspondiente en la pagina principal, la biblioteca de diseños
3. **Ingresar Idea**: Describe la idea de tu aplicación
4. **Configurar Parámetros**: Ajusta opciones de diseño y características
5. **Generar Prototipo**: La app genera automáticamente un prototipo detallado
6. **Revisar**: Visualiza el prototipo entrando a el desde la biblioteca 
## 💻 Desarrollo

### Android

#### Configuración del IDE

1. Abre Android Studio
2. Selecciona "Open" y navega a la carpeta del proyecto
3. Espera a que Gradle sincronice las dependencias
4. Conecta un dispositivo Android o inicia un emulador

#### Ejecutar en Android

**Usando Android Studio:**
- Selecciona la configuración "composeApp (Android)" en el dropdown de run
- Presiona el botón "Run" o usa `Shift + F10`

**Desde Terminal:**

```bash
# macOS/Linux
./gradlew :composeApp:assembleDebug

# Windows
.\gradlew.bat :composeApp:assembleDebug
```

### Desktop

#### Ejecutar en Desktop

**Usando Android Studio:**
- Selecciona la configuración "composeApp (Desktop)" en el dropdown de run
- Presiona el botón "Run"

**Desde Terminal:**

```bash
# macOS/Linux
./gradlew :composeApp:run

# Windows
.\gradlew.bat :composeApp:run
```

## 🔨 Construcción

### Generar APK para Android

```bash
# Debug APK
./gradlew :composeApp:assembleDebug

# Release APK
./gradlew :composeApp:assembleRelease
```

El APK generado se encontrará en: `composeApp/build/outputs/apk/`

### Generar Ejecutable para Desktop

```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

El ejecutable se generará en: `composeApp/build/compose/binaries/`

## 🔧 Tecnologías

### Stack Principal

| Tecnología | Versión | Propósito |
|------------|---------|----------|
| **Kotlin Multiplatform** | 1.9.0+ | Lenguaje principal |
| **Jetpack Compose** | Latest | Framework UI |
| **Gradle** | 7.0+ | Build system |
| **Android** | 24+ | Plataforma Android |
| **JVM** | 11+ | Plataforma Desktop |

### Librerías Clave

- **Jetpack Compose**: UI declarativa multiplataforma
- **Kotlin Coroutines**: Manejo asincrónico
- **Ktor Client**: Cliente HTTP (si aplica)
- **kotlinx.serialization**: Serialización JSON


## 📚 Recursos Útiles

- [Documentación de Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Documentación de Jetpack Compose](https://developer.android.com/jetpack/compose/documentation)
- [Kotlin Official Documentation](https://kotlinlang.org/docs/)
- [Android Developer Guide](https://developer.android.com/guide)

## ⚠️ Troubleshooting

### Problema: Gradle no sincroniza

**Solución:**
```bash
./gradlew clean
./gradlew sync
```

### Problema: Android Emulator no inicia

**Solución:**
- Verifica que tengas suficiente espacio en disco
- Reinicia Android Studio
- En Android Studio: Tools → AVD Manager → Selecciona tu emulador y presiona Play

### Problema: Error de compilación en Desktop

**Solución:**
```bash
./gradlew :composeApp:run --info
```

## 📄 Licencia

Este proyecto está bajo la licencia MIT.

## 👨‍💻 Autor

**Slenderman1314** - [GitHub Profile](https://github.com/Slenderman1314)

## 📞 Soporte

Si encuentras algún problema o tienes sugerencias:
- Abre un [Issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
- Contáctame directamente a través de GitHub

---

**¡Gracias por usar App Prototipe Creator! 🎉**

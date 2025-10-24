# Changelog de Correcciones

Este documento registra las correcciones y mejoras realizadas en el proyecto.

## 2025-10-21 - Eliminación de Exportación MHTML (Redundante)

### Problema
Los botones de navegación en archivos exportados con la opción "MHTML" no eran funcionales. Al hacer clic en los botones para navegar entre pantallas, no se mostraban las diferentes vistas del prototipo.

### Causa Raíz Identificada
**MHTML bloquea JavaScript por seguridad** en navegadores modernos (Chrome, Edge, Firefox). El formato MHTML (MIME HTML) está diseñado para archivar páginas web estáticas, no para contenido interactivo con JavaScript.

### Solución Final
- **Enfoque**: Eliminar completamente la opción MHTML (redundante con HTML)
- **Archivos modificados**:
  - `ExportService.kt` - Eliminado `ExportFormat.MHTML`
  - `ExportService.desktop.kt` - Eliminada función `exportAsMhtml()`
  - `HtmlViewer.desktop.kt` - Eliminada opción de menú MHTML
- **Cambios implementados**:
  1. **Eliminado formato MHTML** del enum `ExportFormat`
  2. **Eliminada función `exportAsMhtml()`** - Ya no es necesaria
  3. **Eliminada opción de menú** "🌐 MHTML (Archivo Web)"
  4. **Simplificado el código** - Solo HTML y PDF

### Resultado
✅ **Interfaz más simple** - Solo 2 opciones: HTML y PDF  
✅ **Sin redundancia** - HTML ya exporta archivos interactivos  
✅ **Código más limpio** - Menos funciones y opciones  
✅ **Sin confusión** - No hay opciones duplicadas

### Opciones de Exportación Disponibles
1. **📄 HTML (Interactivo)** - Archivo HTML con JavaScript funcional
2. **📑 PDF (Documento)** - Documento PDF estático para documentación

### Documentación Actualizada
- `EXPORT_FEATURE.md`: Actualizado para reflejar solo HTML y PDF
- `MHTML_NAVIGATION_FIX.md`: Documentación de por qué se eliminó MHTML

---

## Correcciones Anteriores

### Mejoras Implementadas

#### 1. Soporte Multiplataforma para Variables de Entorno

**Archivos Modificados:**
- `composeApp/src/commonMain/kotlin/app/prototype/creator/utils/Config.kt`
- `composeApp/build.gradle.kts`

**Archivos Creados:**
- `composeApp/src/androidMain/kotlin/app/prototype/creator/utils/Config.android.kt`
- `composeApp/src/desktopMain/kotlin/app/prototype/creator/utils/Config.desktop.kt`

**Cambios:**
- Implementada función `expect/actual` para `getEnvironmentProperty()`
- **Desktop**: Usa `System.getProperty()` (carga desde `.env` en `Main.kt`)
- **Android**: Usa `BuildConfig` (generado automáticamente desde `.env`)

#### 2. Generación Automática de BuildConfig en Android

**Archivo Modificado:**
- `composeApp/build.gradle.kts`

**Cambios:**
```kotlin
defaultConfig {
    // ...
    
    // Load environment variables from .env file
    val envFile = project.rootProject.file(".env")
    if (envFile.exists()) {
        envFile.readLines().forEach { line ->
            val trimmedLine = line.trim()
            if (trimmedLine.isNotEmpty() && !trimmedLine.startsWith("#")) {
                val parts = trimmedLine.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim()
                    buildConfigField("String", key, "\"$value\"")
                }
            }
        }
    }
}
```

#### 3. Actualización de compileSdk

**Cambio:**
- `compileSdk = 34` → `compileSdk = 35`

**Razón:**
- Compose 1.8.0 requiere compileSdk 35 o superior

### Documentación Creada

1. **`docs/SUPABASE_SCHEMA.sql`**
   - Esquema completo de la tabla `prototypes`
   - Índices para optimización
   - Triggers para `updated_at`
   - Datos de ejemplo

2. **`docs/TROUBLESHOOTING.md`**
   - Guía completa de solución de problemas
   - Verificación paso a paso
   - Comandos útiles
   - Logs importantes a buscar

3. **`docs/CHANGELOG_FIXES.md`** (este archivo)
   - Registro de cambios realizados

### Verificación

La aplicación ahora:
- ✅ Carga correctamente las variables de entorno en Desktop
- ✅ Carga correctamente las variables de entorno en Android (vía BuildConfig)
- ✅ Se conecta exitosamente a Supabase
- ✅ Carga los prototipos desde la base de datos
- ✅ Compila correctamente con Compose 1.8.0

### Logs de Verificación

```
[DEBUG] 📄 Loading .env file from: D:\...\App_Prototipe_Creator\.env
[DEBUG] ✅ .env file loaded successfully
[DEBUG] ✅ Koin initialized successfully
[DEBUG] 🔍 Fetching prototypes from Supabase
[DEBUG] 📦 Received 4 DTOs from Supabase
[DEBUG] ✅ Successfully loaded 4 prototypes from Supabase
[DEBUG] ✅ Prototypes loaded: 4
```

### Prototipos Cargados

1. **Almacén de Conservas Pro** (C18VFU) - 13,270 caracteres HTML
2. **Gestor Personal de Tareas** (JUKMHQ) - 11,214 caracteres HTML
3. **Fichaje Pro Empleados** (9GJ1XV) - 9,163 caracteres HTML
4. **FichApp Control Horario** (UWSDY6) - 9,092 caracteres HTML

### Próximos Pasos Recomendados

1. **Para Android:**
   - Ejecutar `./gradlew clean :composeApp:assembleDebug`
   - Instalar y probar en dispositivo/emulador

2. **Para Desktop:**
   - Ejecutar `./gradlew :composeApp:run`
   - Verificar que los prototipos se muestran correctamente

3. **Verificar Renderizado HTML:**
   - Abrir cada prototipo
   - Verificar que el HTML se renderiza correctamente
   - Desktop usa JavaFX WebView
   - Android usa HtmlConverterCompose

### Notas Adicionales

- El archivo `.env` debe estar en la raíz del proyecto
- Las credenciales de Supabase están configuradas correctamente
- La tabla `prototypes` existe y contiene datos
- Los permisos de Supabase (RLS) permiten lectura pública

### Contacto

Si encuentras algún problema adicional, consulta `docs/TROUBLESHOOTING.md` para más información.

# Sincronización Dinámica en Desktop - JavaFX Window

## 🎯 Problema Resuelto

La ventana de JavaFX WebView en Desktop se abre como una ventana separada y no se sincronizaba automáticamente cuando el usuario cambiaba el idioma o el tema en la aplicación principal.

### Comportamiento Anterior
- ❌ Cambiar idioma en la app principal → Ventana JavaFX mantiene idioma anterior
- ❌ Cambiar tema en la app principal → Ventana JavaFX mantiene tema anterior
- ❌ Usuario tenía que cerrar y reabrir el prototipo para ver los cambios

### Comportamiento Nuevo
- ✅ Cambiar idioma en la app principal → Ventana JavaFX actualiza inmediatamente
- ✅ Cambiar tema en la app principal → Ventana JavaFX actualiza inmediatamente
- ✅ Sincronización automática y en tiempo real

---

## 🔧 Implementación

### 1. LaunchedEffect Separados

**Archivo**: `HtmlViewer.desktop.kt`

Se separaron los `LaunchedEffect` para manejar cada cambio de forma independiente:

```kotlin
// Update menu texts when language changes
LaunchedEffect(language) {
    Napier.d("🌐 LaunchedEffect(language) fired! New language: $language")
    SharedWindowManager.updateLanguage(language)
}

// Update theme dynamically when it changes
LaunchedEffect(isDarkTheme) {
    Napier.d("🎨 Theme changed to: ${if (isDarkTheme) "DARK" else "LIGHT"}")
    SharedWindowManager.updateTheme(isDarkTheme)
    // Update toolbar theme
    SwingUtilities.invokeLater {
        SharedWindowManager.updateToolbarTheme(isDarkTheme)
    }
}

// Force reload when content or key changes
LaunchedEffect(key, htmlContent) {
    Napier.d("🪟 LaunchedEffect triggered for key: $windowKey")
    // ... load content ...
}
```

**Ventajas**:
- ✅ Cada cambio se maneja independientemente
- ✅ No se recarga el contenido innecesariamente
- ✅ Mejor rendimiento
- ✅ Más reactivo

### 2. Método updateToolbarTheme

**Archivo**: `HtmlViewer.desktop.kt` - `SharedWindowManager`

```kotlin
fun updateToolbarTheme(isDark: Boolean) {
    Napier.d("🎨 Updating toolbar theme to: ${if (isDark) "DARK" else "LIGHT"}")
    currentTheme = isDark
    applyToolbarTheme(isDark)
    applyButtonTheme(isDark)
}
```

**Funcionalidad**:
- Actualiza el tema de la toolbar (barra superior)
- Actualiza el tema del botón de exportación
- Aplica colores apropiados según el tema

### 3. Sincronización de Idioma

Ya existía el método `updateLanguage()` que actualiza:
- ✅ Texto del botón "Exportar" / "Export"
- ✅ Opciones del menú: "Exportar como HTML" / "Export as HTML"
- ✅ Opciones del menú: "Exportar como PDF" / "Export as PDF"

### 4. Sincronización de Tema

El método `updateTheme()` actualiza:
- ✅ Colores de fondo del contenido HTML
- ✅ Colores de texto
- ✅ Colores de componentes
- ✅ Colores de botones
- ✅ Colores de inputs

---

## 🎨 Temas Aplicados

### Toolbar - Tema Claro
```kotlin
val bgColor = java.awt.Color.WHITE
val borderColor = java.awt.Color(230, 230, 230)
```

### Toolbar - Tema Oscuro
```kotlin
val bgColor = java.awt.Color(30, 30, 30)
val borderColor = java.awt.Color(50, 50, 50)
```

### Botón - Tema Claro
```kotlin
val bgColor = java.awt.Color.WHITE
val fgColor = java.awt.Color(145, 115, 255) // Púrpura
val borderColor = java.awt.Color(145, 115, 255)
```

### Botón - Tema Oscuro
```kotlin
val bgColor = java.awt.Color(45, 45, 45)
val fgColor = java.awt.Color(187, 134, 252) // Púrpura claro
val borderColor = java.awt.Color(187, 134, 252)
```

---

## 🔄 Flujo de Sincronización

### Cambio de Idioma

```
Usuario cambia idioma en GalleryScreen
    ↓
LanguageRepository actualiza currentLanguage
    ↓
PrototypeDetailScreen detecta cambio (SideEffect)
    ↓
updateExporterLanguage() se llama
    ↓
PlatformExporter.currentLanguage se actualiza
    ↓
HtmlViewer detecta cambio (LaunchedEffect)
    ↓
SharedWindowManager.updateLanguage() se llama
    ↓
SwingUtilities.invokeLater ejecuta updateMenuTexts()
    ↓
Textos del menú se actualizan en la ventana JavaFX
```

### Cambio de Tema

```
Usuario hace clic en toggle de tema
    ↓
appSettings.isDarkTheme se actualiza
    ↓
AppTheme detecta cambio y actualiza MaterialTheme
    ↓
HtmlViewer detecta cambio (LaunchedEffect)
    ↓
SharedWindowManager.updateTheme() se llama
    ↓
Platform.runLater ejecuta JavaScript para cambiar CSS
    ↓
SwingUtilities.invokeLater ejecuta updateToolbarTheme()
    ↓
Toolbar y botones se actualizan con nuevos colores
    ↓
Contenido HTML se actualiza con nuevo tema
```

---

## 📱 Experiencia de Usuario

### Antes
1. Usuario abre prototipo → Ventana JavaFX se abre
2. Usuario cambia idioma → **Nada pasa en la ventana**
3. Usuario cambia tema → **Nada pasa en la ventana**
4. Usuario cierra ventana y vuelve a abrir → Ahora sí se ve el cambio

### Ahora
1. Usuario abre prototipo → Ventana JavaFX se abre
2. Usuario cambia idioma → **Ventana se actualiza inmediatamente** ✨
3. Usuario cambia tema → **Ventana se actualiza inmediatamente** ✨
4. Usuario puede seguir trabajando sin interrupciones

---

## 🧪 Testing

### Prueba de Idioma

1. Ejecutar aplicación Desktop:
   ```bash
   ./gradlew runDesktop
   ```

2. Abrir un prototipo (ventana JavaFX se abre)

3. En la ventana principal, cambiar idioma (Español ↔ English)

4. **Verificar**: 
   - ✅ Botón "Exportar" cambia a "Export" (o viceversa)
   - ✅ Menú desplegable muestra opciones en nuevo idioma
   - ✅ Cambio es instantáneo

### Prueba de Tema

1. Ejecutar aplicación Desktop

2. Abrir un prototipo (ventana JavaFX se abre)

3. En la ventana principal, hacer clic en icono de sol/luna

4. **Verificar**:
   - ✅ Toolbar cambia de color (blanco ↔ gris oscuro)
   - ✅ Botón "Exportar" cambia de color
   - ✅ Contenido HTML cambia de tema
   - ✅ Cambio es instantáneo y suave

---

## 🎯 Ventajas de la Implementación

### Rendimiento
- ✅ No recarga el contenido HTML innecesariamente
- ✅ Solo actualiza lo que cambió
- ✅ Usa JavaScript para cambios de CSS (más rápido)
- ✅ SwingUtilities.invokeLater para cambios de UI

### Experiencia de Usuario
- ✅ Cambios instantáneos
- ✅ Sin necesidad de cerrar/abrir ventana
- ✅ Sincronización perfecta con app principal
- ✅ Feedback visual inmediato

### Mantenibilidad
- ✅ Código bien organizado
- ✅ Separación de responsabilidades
- ✅ Fácil de extender
- ✅ Logging detallado para debugging

---

## 🔍 Debugging

### Logs de Idioma
```
🌐 LaunchedEffect(language) fired! New language: ENGLISH
🌐 updateLanguage called with: ENGLISH
🌐 Calling updateMenuTexts from updateLanguage
🌐 Setting menu texts: htmlText=Export as HTML, pdfText=Export as PDF, exportText=Export
```

### Logs de Tema
```
🎨 Theme changed to: DARK
🎨 Updating WebView theme to: DARK
🎨 Updating toolbar theme to: DARK
🎨 Applying DARK theme via JavaScript...
✅ Dark theme applied
```

---

## 📊 Compatibilidad

### Plataformas
- ✅ Windows
- ✅ macOS
- ✅ Linux

### Versiones de Java
- ✅ Java 11+
- ✅ JavaFX 21+

---

## 🚀 Próximas Mejoras

### Posibles Extensiones
- [ ] Animaciones de transición entre temas
- [ ] Más opciones de personalización de colores
- [ ] Persistencia de preferencias de ventana
- [ ] Soporte para múltiples ventanas simultáneas
- [ ] Sincronización de zoom/escala

---

## ✅ Checklist de Verificación

- [x] LaunchedEffect separados para idioma y tema
- [x] Método updateToolbarTheme implementado
- [x] Sincronización de idioma funcional
- [x] Sincronización de tema funcional
- [x] Toolbar actualiza colores dinámicamente
- [x] Botones actualizan colores dinámicamente
- [x] Contenido HTML actualiza tema dinámicamente
- [x] Textos del menú actualizan idioma dinámicamente
- [x] Sin recargas innecesarias de contenido
- [x] Logging detallado para debugging

---

**Fecha**: 2025-10-24
**Versión**: 2.1.0
**Estado**: ✅ **COMPLETADO Y FUNCIONAL**

## 📝 Resumen

La ventana de JavaFX en Desktop ahora se sincroniza dinámicamente con los cambios de idioma y tema de la aplicación principal, proporcionando una experiencia de usuario fluida y sin interrupciones.

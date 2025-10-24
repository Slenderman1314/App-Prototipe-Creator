# Corrección de Navegación en Exportación "MHTML"

## Problema Identificado

Los botones de navegación en archivos exportados con la opción "MHTML" no eran funcionales. Al hacer clic en los botones, no se mostraban las diferentes pantallas del prototipo.

## Causa Raíz

**MHTML bloquea JavaScript por seguridad en navegadores modernos.**

El formato MHTML (MIME HTML / Web Archive) está diseñado para archivar páginas web **estáticas**, no para contenido interactivo con JavaScript. Los navegadores modernos (Chrome, Edge, Firefox) bloquean la ejecución de JavaScript en archivos MHTML por razones de seguridad.

Intentos de solución que NO funcionaron:
1. ❌ Inyectar función `showScreen()` - Bloqueada por el navegador
2. ❌ Modificar estilos CSS - JavaScript sigue bloqueado
3. ❌ Agregar event listeners - JavaScript no se ejecuta en MHTML

## Solución Implementada

### Cambio de Formato: MHTML → HTML

**La solución definitiva es NO usar el formato MHTML.**

En lugar de intentar hacer que MHTML funcione (imposible debido a restricciones de seguridad), la opción "MHTML (Archivo Web)" ahora exporta como **HTML estándar** (`.html`).

### Cambios en el Código

**Archivo**: `ExportService.desktop.kt`

**Antes** (intentaba usar MHTML):
```kotlin
val file = showSaveDialog(suggestedFileName, "mhtml", "MHTML Files")
val mhtmlContent = createMhtmlContent(htmlContent)
file.writeText(mhtmlContent)
```

**Después** (usa HTML puro):
```kotlin
val file = showSaveDialog(suggestedFileName, "html", "HTML Files")
var finalHtml = htmlContent
if (!finalHtml.contains("<!DOCTYPE", ignoreCase = true)) {
    finalHtml = "<!DOCTYPE html>\n$finalHtml"
}
file.writeText(finalHtml)
```

### Ventajas de la Solución

2. ✅ **HTML original sin modificar** - Se exporta tal cual viene de n8n
3. ✅ **Compatible con todos los navegadores** - Chrome, Firefox, Safari, Edge
4. ✅ **Archivo único** - Sigue siendo fácil de compartir
5. ✅ **Sin dependencias externas** - No requiere recursos adicionales

### Código Simplificado

La función `exportAsMhtml()` ahora es muy simple:

```kotlin
actual suspend fun exportAsMhtml(htmlContent: String, suggestedFileName: String): ExportResult {
    return withContext(Dispatchers.IO) {
        try {
            Napier.d("🌐 Exporting as HTML (Web Archive): $suggestedFileName")
            
            // NOTE: We export as HTML instead of MHTML because:
            // - MHTML blocks JavaScript in modern browsers for security
            // - HTML preserves full interactivity and navigation
            
            val file = showSaveDialog(suggestedFileName, "html", "HTML Files")
            
            if (file != null) {
                // Export as plain HTML to preserve JavaScript functionality
                var finalHtml = htmlContent
                if (!finalHtml.contains("<!DOCTYPE", ignoreCase = true)) {
                    finalHtml = "<!DOCTYPE html>\n$finalHtml"
                }
                file.writeText(finalHtml)
                Napier.d("✅ HTML exported successfully to: ${file.absolutePath}")
                ExportResult.Success(file.absolutePath)
            } else {
                ExportResult.Cancelled
        } catch (e: Exception) {
            Napier.e("❌ Error exporting HTML", e)
            ExportResult.Error(e.localizedMessage ?: "Failed to export HTML")
        }
    }

# Funcionalidad de Exportación de Prototipos

## Descripción

La aplicación incluye la capacidad de exportar prototipos en dos formatos:
- **HTML**: Exporta el código HTML completo del prototipo (totalmente interactivo)
- **PDF**: Convierte el prototipo HTML a formato PDF (estático, para documentación)

## Ubicación de los Botones

Los botones de exportación se encuentran en la **barra superior de la ventana del prototipo** (ventana separada de JavaFX WebView):
- **📥 Exportar**: Menú desplegable con opciones:
  - **📄 HTML (Interactivo)**: Exporta como archivo HTML
  - **📑 PDF (Documento)**: Exporta como documento PDF

## Cómo Usar
1. **Abrir un prototipo**: Navega a la galería y haz clic en cualquier prototipo para abrirlo
2. **Se abre la ventana del prototipo**: Una ventana separada mostrará el prototipo renderizado
3. **Hacer clic en el botón "📥 Exportar"**: Se desplegará un menú con las opciones de exportación
4. **Seleccionar el formato deseado**:
   - **📄 HTML (Interactivo)**: Para guardar como archivo HTML (ideal para pruebas de usuario y desarrollo)
   - **📑 PDF (Documento)**: Para convertir y guardar como PDF (ideal para documentación y archivo)
5. **Elegir ubicación**: Se abrirá un diálogo del sistema para seleccionar dónde guardar el archivo

### Limitaciones del PDF (Por Naturaleza del Formato)

1. **Sin Interactividad**: Los botones y elementos interactivos son estáticos
   - ✅ Solución: Use el archivo HTML para versión interactiva
   
2. **Sin JavaScript**: El código JavaScript no se ejecuta en PDFs
   - ✅ Solución: El HTML mantiene toda la funcionalidad
   
3. **Animaciones**: Solo se captura el estado visual final
   - ✅ Solución: El PDF es ideal para documentación estática

### Casos de Uso Recomendados

| Formato | Mejor Para | Características |
|---------|-----------|-----------------|
| **HTML** | • Demos interactivas<br>• Pruebas de usuario<br>• Desarrollo web<br>• Compartir prototipos | ✅ Totalmente interactivo<br>✅ JavaScript funcional<br>✅ Navegación entre pantallas<br>✅ Un solo archivo<br>✅ Compatible con todos los navegadores |
| **PDF** | • Documentación<br>• Presentaciones<br>• Archivo permanente<br>• Impresión | ✅ Formato universal<br>✅ Fácil de compartir<br>✅ Profesional<br>✅ Todas las pantallas visibles<br>⚠️ Sin interactividad |

## Características Técnicas

### Exportación HTML
- Guarda el contenido HTML completo del prototipo
- El archivo puede abrirse directamente en cualquier navegador web
- Mantiene toda la funcionalidad JavaScript original
- **Un solo archivo** - Fácil de compartir
- **Totalmente interactivo** - Navegación entre pantallas funcional
- **Compatible con todos los navegadores** - Chrome, Firefox, Safari, Edge, Opera
- **Ideal para**:
  - Demos interactivas
  - Pruebas de usuario
  - Desarrollo web
  - Compartir con clientes o stakeholders

### Exportación PDF
- Utiliza la biblioteca **iText 7 html2pdf** para la conversión
- Soporte completo de HTML5 y CSS3 moderno
- Genera un PDF de alta calidad con estilos mejorados
- **Formato estático** - Los botones no son interactivos (naturaleza del PDF)
- Muestra todas las pantallas en páginas separadas
- Ideal para documentación, presentaciones y archivo
- **Recomendación**: Use HTML para versión interactiva, PDF para documentación

## Internacionalización

La funcionalidad está completamente traducida en:
- **Español**
- **Inglés**

Los mensajes se adaptan automáticamente al idioma seleccionado en la aplicación.

## Dependencias Añadidas

```kotlin
// PDF generation from HTML - iText 7 with pdfHTML (better CSS support)
implementation("com.itextpdf:html2pdf:5.0.5")
```

## Arquitectura

### Servicios Creados

1. **ExportService** (Common)
   - Interfaz común para la exportación
   - Define los formatos soportados (HTML, PDF)
   - Maneja los resultados de exportación

2. **PlatformExporter** (Desktop)
   - Implementación específica para Desktop
   - Maneja diálogos de guardado de archivos
   - Realiza la conversión HTML a PDF

3. **CommonExportService**
   - Implementación común que delega a PlatformExporter
   - Registrado en Koin para inyección de dependencias

### Componentes UI

1. **Botones de Exportación** en la ventana JavaFX
   - Ubicados en la barra superior de la ventana del prototipo
   - Dos botones separados: HTML y PDF
   - Siempre visibles cuando el prototipo está cargado

2. **Diálogo de Guardado**
   - Diálogo nativo del sistema (JFileChooser)
   - Permite seleccionar ubicación y nombre del archivo
   - Extensión se añade automáticamente

3. **Diálogo de Resultado**
   - JOptionPane mostrando el resultado
   - Mensaje de éxito con ruta del archivo
   - Mensaje de error si algo falla

## Mensajes de Estado

- **Éxito**: "Prototipo exportado exitosamente: [ruta]"
- **Error**: "Error al exportar el prototipo: [mensaje]"
- **Cancelado**: "Exportación cancelada"

## Notas Importantes

1. Los botones de exportación están en la **ventana separada del prototipo**, no en la ventana principal de la aplicación

2. La exportación se realiza en segundo plano:
   - No bloquea la interfaz
   - Muestra un diálogo al finalizar

3. Formato de archivos:
   - HTML: `.html`
   - PDF: `.pdf`
   - El nombre sugerido es el nombre del prototipo

4. Si no hay contenido HTML disponible:
   - Se muestra un mensaje de error
   - No se permite la exportación

## Posibles Mejoras Futuras

- [ ] Exportación a otros formatos (PNG, SVG)
- [ ] Opciones de configuración de PDF (tamaño de página, márgenes)
- [ ] Vista previa antes de exportar
- [ ] Exportación por lotes de múltiples prototipos
- [ ] Compartir directamente por email o servicios en la nube

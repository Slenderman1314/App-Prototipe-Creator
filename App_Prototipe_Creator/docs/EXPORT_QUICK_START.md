# Guía Rápida - Exportación de Prototipos

## 🚀 Inicio Rápido

### En 3 Pasos

1. **Abre un prototipo** desde la galería
2. **Haz clic en el botón de exportación** en la ventana del prototipo
3. **Selecciona dónde guardar** y listo!

---

## 📸 Interfaz Visual

### Ventana del Prototipo con Botones de Exportación

```
┌──────────────────────────────────────────────────────────┐
│ Prototipo: TaskFlow Software (Light)                 ⊗ □ ✕│
│ ┌────────────────┐ ┌────────────────┐                    │
│ │📄 Exportar HTML│ │📑 Exportar PDF │                    │
│ └────────────────┘ └────────────────┘                    │
├──────────────────────────────────────────────────────────┤
│                                                          │
│                  TaskFlow Software                       │
│                                                          │
│   Aplicación para gestión eficiente de proyectos        │
│   y tareas con integración y reportes.                  │
│                                                          │
│   ┌─────────────────────────────────────────┐          │
│   │ [Pantalla Principal]                    │          │
│   │                                         │          │
│   │ • Dashboard                             │          │
│   │ • Proyectos                             │          │
│   │ • Tareas                                │          │
│   └─────────────────────────────────────────┘          │
│                                                          │
└──────────────────────────────────────────────────────────┘
```

### Flujo de Exportación

```
┌─────────────┐
│   Usuario   │
│ abre proto  │
└──────┬──────┘
       │
       ▼
┌─────────────────────────────┐
│  Ventana del Prototipo      │
│  [📄 HTML] [📑 PDF]         │
└──────┬──────────────────────┘
       │ Click en botón
       ▼
┌─────────────────────────────┐
│  Diálogo de Guardado        │
│  "¿Dónde guardar?"          │
│  Nombre: TaskFlow.html      │
│  [Guardar] [Cancelar]       │
└──────┬──────────────────────┘
       │ Confirma
       ▼
┌─────────────────────────────┐
│  Exportando...              │
│  ⏳ Procesando...           │
└──────┬──────────────────────┘
       │
       ▼
┌─────────────────────────────┐
│  ✅ Éxito!                  │
│  Archivo guardado en:       │
│  C:\Users\...\TaskFlow.html │
│  [OK]                       │
└─────────────────────────────┘
```

---

## 🎯 Casos de Uso

### Caso 1: Compartir con Cliente
```
Objetivo: Enviar prototipo al cliente para revisión
Acción: Exportar como HTML
Resultado: Archivo HTML que el cliente puede abrir en su navegador
```

### Caso 2: Documentación del Proyecto
```
Objetivo: Archivar el diseño en documentación
Acción: Exportar como PDF
Resultado: PDF profesional para incluir en documentos
```

### Caso 3: Backup Local
```
Objetivo: Guardar copia de seguridad del diseño
Acción: Exportar ambos formatos (HTML + PDF)
Resultado: Dos archivos de respaldo
```

---

## ⚡ Atajos y Tips

### Tips de Uso

1. **Nombre del archivo**: El nombre del prototipo se usa automáticamente
2. **Ubicación por defecto**: El diálogo abre en la carpeta del usuario
3. **Sobrescribir**: El sistema pregunta antes de sobrescribir archivos
4. **Cancelar**: Puedes cancelar en cualquier momento sin consecuencias

### Formatos Recomendados

| Formato | Mejor Para | Ventajas |
|---------|-----------|----------|
| **HTML** | • Compartir con desarrolladores<br>• Pruebas interactivas<br>• Edición posterior | • Mantiene interactividad<br>• Fácil de modificar<br>• Tamaño pequeño |
| **PDF** | • Presentaciones<br>• Documentación<br>• Archivo | • Formato universal<br>• No requiere navegador<br>• Profesional |

---

## 🔧 Solución de Problemas

### Problema: "No hay contenido HTML para exportar"
**Solución**: El prototipo no tiene contenido. Verifica que se haya generado correctamente.

### Problema: "Servicio de exportación no disponible"
**Solución**: Reinicia la aplicación. Si persiste, verifica la configuración de Koin.

### Problema: "Error al exportar PDF"
**Solución**: 
- Verifica que el HTML sea válido
- Asegúrate de tener permisos de escritura en la carpeta destino
- Intenta exportar como HTML primero para verificar el contenido

### Problema: El archivo no se guarda
**Solución**:
- Verifica permisos de la carpeta destino
- Asegúrate de tener espacio en disco
- Intenta guardar en otra ubicación

---

## 📊 Especificaciones Técnicas

### Formatos Soportados

#### HTML
- **Extensión**: `.html`
- **MIME Type**: `text/html`
- **Codificación**: UTF-8
- **Compatibilidad**: Todos los navegadores modernos

#### PDF
- **Extensión**: `.pdf`
- **MIME Type**: `application/pdf`
- **Motor**: OpenHTMLtoPDF 1.0.10
- **Compatibilidad**: PDF 1.4+

### Limitaciones Conocidas

1. **JavaScript en PDF**: El código JavaScript no se ejecuta en PDFs
2. **Fuentes personalizadas**: Pueden no renderizarse idénticamente
3. **Animaciones CSS**: Solo se captura el estado inicial
4. **Contenido dinámico**: Se exporta el estado actual, no la interactividad

### Requisitos del Sistema

- **Sistema Operativo**: Windows, macOS, Linux
- **Java**: JDK 21 o superior
- **Espacio en disco**: Mínimo 10MB para exportaciones
- **Permisos**: Escritura en carpeta de destino

---

## 📞 Soporte

### Logs de Depuración

Los logs de exportación se pueden encontrar en la consola de la aplicación:

```
📤 Exporting prototype as HTML: TaskFlow
📝 Exporting as HTML: TaskFlow
✅ HTML exported successfully to: C:\Users\...\TaskFlow.html
```

### Prefijos de Log

- `📤` - Inicio de exportación
- `📝` - Exportación HTML
- `📄` - Exportación PDF
- `✅` - Operación exitosa
- `❌` - Error
- `ℹ️` - Información

### Reportar Problemas

Si encuentras un problema:

1. Revisa los logs en la consola
2. Verifica que el prototipo se visualice correctamente
3. Intenta con un prototipo más simple
4. Reporta el error con:
   - Descripción del problema
   - Pasos para reproducir
   - Logs relevantes
   - Tamaño del HTML (si es muy grande)

---

## 🎓 Mejores Prácticas

### Para Exportar HTML

✅ **Hacer**:
- Verificar que el prototipo se vea bien antes de exportar
- Usar nombres descriptivos para los archivos
- Probar el HTML exportado en un navegador

❌ **Evitar**:
- Exportar prototipos incompletos
- Usar caracteres especiales en nombres de archivo
- Sobrescribir sin hacer backup

### Para Exportar PDF

✅ **Hacer**:
- Revisar que todos los estilos se vean correctamente
- Usar nombres que indiquen la versión (ej: "TaskFlow_v1.pdf")
- Verificar el PDF después de exportar

❌ **Evitar**:
- Exportar prototipos con JavaScript complejo
- Usar fuentes no estándar sin verificar
- Asumir que todo se verá idéntico al HTML

---

## 🚀 Próximos Pasos

Después de exportar tu prototipo:

1. **HTML**: 
   - Abre en navegador para verificar
   - Comparte con tu equipo
   - Usa para pruebas de usuario

2. **PDF**:
   - Revisa la calidad del renderizado
   - Incluye en documentación
   - Archiva para referencia futura

3. **Ambos**:
   - Mantén versiones organizadas
   - Documenta cambios entre versiones
   - Usa control de versiones si es necesario

---

## ✨ Características Destacadas

- 🚀 **Rápido**: Exportación en segundos
- 🎯 **Preciso**: Mantiene fidelidad visual
- 🔒 **Seguro**: Validación antes de sobrescribir
- 💪 **Robusto**: Manejo de errores completo
- 🌍 **Universal**: Formatos estándar compatibles
- 📱 **Intuitivo**: Interfaz simple y clara

---

**¡Disfruta exportando tus prototipos!** 🎉

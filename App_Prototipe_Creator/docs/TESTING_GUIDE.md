# Guía de Pruebas - Funcionalidad de Exportación

## 🧪 Cómo Probar la Nueva Funcionalidad

### Prerequisitos
1. Compilar el proyecto: `./gradlew desktopRun`
2. Tener al menos un prototipo creado en la aplicación

### Pasos de Prueba

#### 1. Verificar que el Botón Aparece
- [ ] Abrir la aplicación
- [ ] Navegar a la galería de prototipos
- [ ] Hacer clic en cualquier prototipo para abrirlo
- [ ] **Verificar**: El botón de descarga (📥) debe aparecer en la barra superior derecha

#### 2. Probar Exportación HTML
- [ ] Hacer clic en el botón de exportación
- [ ] Seleccionar "Exportar como HTML"
- [ ] Elegir una ubicación para guardar
- [ ] Dar un nombre al archivo (o usar el sugerido)
- [ ] Hacer clic en "Guardar"
- [ ] **Verificar**: Debe aparecer un mensaje de éxito con la ruta del archivo
- [ ] **Verificar**: El archivo .html debe existir en la ubicación seleccionada
- [ ] Abrir el archivo HTML en un navegador
- [ ] **Verificar**: El prototipo debe verse correctamente

#### 3. Probar Exportación PDF
- [ ] Hacer clic en el botón de exportación
- [ ] Seleccionar "Exportar como PDF"
- [ ] Elegir una ubicación para guardar
- [ ] Dar un nombre al archivo (o usar el sugerido)
- [ ] Hacer clic en "Guardar"
- [ ] **Verificar**: Debe aparecer un mensaje de éxito con la ruta del archivo
- [ ] **Verificar**: El archivo .pdf debe existir en la ubicación seleccionada
- [ ] Abrir el archivo PDF
- [ ] **Verificar**: El prototipo debe verse correctamente con estilos preservados

#### 4. Probar Cancelación
- [ ] Hacer clic en el botón de exportación
- [ ] Seleccionar cualquier formato
- [ ] En el diálogo de guardado, hacer clic en "Cancelar"
- [ ] **Verificar**: Debe aparecer mensaje "Exportación cancelada"
- [ ] **Verificar**: No se debe crear ningún archivo

#### 5. Probar Internacionalización
- [ ] Cambiar el idioma de la aplicación a Español
- [ ] Abrir un prototipo
- [ ] Hacer clic en exportar
- [ ] **Verificar**: Todos los textos deben estar en español
- [ ] Cambiar el idioma a Inglés
- [ ] **Verificar**: Todos los textos deben estar en inglés

#### 6. Probar Estados de Error
- [ ] Intentar exportar a una ubicación sin permisos de escritura
- [ ] **Verificar**: Debe mostrar mensaje de error apropiado
- [ ] Intentar sobrescribir un archivo existente
- [ ] **Verificar**: El sistema debe pedir confirmación

#### 7. Probar Múltiples Exportaciones
- [ ] Exportar el mismo prototipo como HTML
- [ ] Exportar el mismo prototipo como PDF
- [ ] **Verificar**: Ambos archivos deben existir y ser correctos
- [ ] Exportar diferentes prototipos
- [ ] **Verificar**: Cada uno debe tener su contenido correcto

### Casos de Prueba Específicos

#### Caso 1: Prototipo Simple
```html
<!DOCTYPE html>
<html>
<head><title>Test</title></head>
<body><h1>Hello World</h1></body>
</html>
```
**Resultado esperado**: Debe exportarse correctamente en ambos formatos

#### Caso 2: Prototipo con Estilos
```html
<!DOCTYPE html>
<html>
<head>
<style>
body { background: #f0f0f0; }
h1 { color: #667eea; }
</style>
</head>
<body><h1>Styled Content</h1></body>
</html>
```
**Resultado esperado**: Los estilos deben preservarse en PDF

#### Caso 3: Prototipo Complejo
- Prototipo con múltiples pantallas
- Formularios con inputs
- Botones y navegación
- Tablas y listas

**Resultado esperado**: Todo el contenido debe exportarse correctamente

### Checklist de Calidad

#### Funcionalidad
- [ ] Botón visible solo cuando hay contenido HTML
- [ ] Botón deshabilitado durante exportación
- [ ] Diálogo de formato se muestra correctamente
- [ ] Diálogo de guardado del sistema funciona
- [ ] HTML se guarda correctamente
- [ ] PDF se genera correctamente
- [ ] Mensajes de resultado se muestran
- [ ] Mensajes desaparecen automáticamente

#### UI/UX
- [ ] Icono de exportación es claro
- [ ] Diálogo es intuitivo
- [ ] Botones tienen tamaño adecuado
- [ ] Textos son legibles
- [ ] Colores siguen el tema de la app
- [ ] Animaciones son suaves
- [ ] No hay parpadeos o glitches

#### Internacionalización
- [ ] Todos los textos están traducidos
- [ ] Español funciona correctamente
- [ ] Inglés funciona correctamente
- [ ] Cambio de idioma actualiza textos

#### Rendimiento
- [ ] Exportación HTML es rápida (< 1 segundo)
- [ ] Exportación PDF es razonable (< 5 segundos)
- [ ] No hay bloqueos de UI
- [ ] Memoria se libera después de exportar

#### Manejo de Errores
- [ ] Errores de permisos se manejan
- [ ] Errores de disco lleno se manejan
- [ ] Errores de conversión PDF se manejan
- [ ] Mensajes de error son claros

### Problemas Conocidos

#### Limitaciones Actuales
1. **PDF con JavaScript**: El contenido JavaScript no se ejecuta en el PDF
2. **Fuentes personalizadas**: Pueden no renderizarse igual en PDF
3. **Animaciones CSS**: No se preservan en PDF (solo estado final)

#### Soluciones Alternativas
- Para JavaScript interactivo, usar exportación HTML
- Para fuentes, asegurar que estén embebidas en el HTML
- Para animaciones, exportar como HTML

### Reportar Problemas

Si encuentras algún problema:

1. **Descripción**: ¿Qué estabas intentando hacer?
2. **Pasos**: ¿Cómo reproducir el problema?
3. **Resultado esperado**: ¿Qué debería haber pasado?
4. **Resultado actual**: ¿Qué pasó realmente?
5. **Logs**: Revisar la consola para mensajes de error
6. **Archivos**: Si es posible, adjuntar el HTML que causó el problema

### Logs Útiles

Los logs de exportación usan estos prefijos:
- `📤` - Inicio de exportación
- `📝` - Exportación HTML
- `📄` - Exportación PDF
- `✅` - Éxito
- `❌` - Error
- `ℹ️` - Información

Ejemplo de log exitoso:
```
📤 Exporting prototype as HTML: MiPrototipo
📝 Exporting as HTML: MiPrototipo
✅ HTML exported successfully to: C:\Users\...\MiPrototipo.html
```

### Métricas de Éxito

La funcionalidad se considera exitosa si:
- ✅ 100% de exportaciones HTML funcionan
- ✅ 95%+ de exportaciones PDF funcionan
- ✅ Tiempo de exportación HTML < 1 segundo
- ✅ Tiempo de exportación PDF < 5 segundos
- ✅ 0 crashes durante exportación
- ✅ Mensajes de error claros en todos los casos

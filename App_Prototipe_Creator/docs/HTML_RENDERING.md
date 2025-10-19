# Renderizado HTML con HtmlConverterCompose

## Descripción

Este proyecto utiliza la biblioteca [HtmlConverterCompose](https://github.com/cbeyls/HtmlConverterCompose) de Christophe Beyls para renderizar contenido HTML de prototipos de aplicaciones en Compose Multiplatform.

## Características Implementadas

### ✅ Renderizado HTML Nativo
- Conversión de HTML a `AnnotatedString` nativo de Compose
- Soporte completo para Android y Desktop (JVM)
- Sin necesidad de WebView, mejorando el rendimiento

### ✅ Estilos Personalizados
- **Enlaces interactivos**: Color primario del tema con subrayado
- **Colores CSS**: Soporte completo para colores inline en HTML
- **Indentación**: 24sp para listas y blockquotes
- **Tipografía**: Integración con Material 3 Typography

### ✅ Manejo de Enlaces
- Clics en enlaces abren el navegador predeterminado del sistema
- Logging de eventos de navegación
- Manejo de errores robusto

### ✅ Tags HTML Soportados

#### Tags Inline con Estilos
- `<strong>`, `<b>` - Negrita
- `<em>`, `<cite>`, `<dfn>`, `<i>` - Cursiva
- `<big>` - Texto más grande
- `<small>` - Texto más pequeño
- `<tt>`, `<code>` - Fuente monoespaciada
- `<a>` - Enlaces (con soporte de clics)
- `<u>` - Subrayado
- `<del>`, `<s>`, `<strike>` - Tachado
- `<sup>` - Superíndice
- `<sub>` - Subíndice
- `<span>` - Colores CSS mediante atributo `style`

#### Tags de Bloque (Párrafos)
- `<p>`, `<div>` - Párrafos y contenedores
- `<blockquote>` - Citas en bloque
- `<pre>` - Texto preformateado (monoespaciado)
- `<header>`, `<footer>`, `<main>`, `<nav>`, `<aside>`, `<section>`, `<article>` - Elementos semánticos HTML5
- `<address>`, `<figure>`, `<figcaption>` - Elementos de contenido

#### Listas
- `<ul>`, `<ol>`, `<li>` - Listas ordenadas y no ordenadas
- `<dl>`, `<dt>`, `<dd>` - Listas de definición

#### Encabezados
- `<h1>`, `<h2>`, `<h3>`, `<h4>`, `<h5>`, `<h6>` - Títulos de sección

#### Otros
- `<br>` - Salto de línea
- `<hr>` - Separador horizontal (marca nuevo párrafo)

### ⚠️ Tags Ignorados
Los siguientes tags se omiten junto con su contenido:
- `<script>`, `<head>`, `<table>`, `<form>`, `<fieldset>`

## Implementación

### Dependencia
```kotlin
// En commonMain
implementation("be.digitalia.compose.htmlconverter:htmlconverter:1.1.0")
```

### Uso Básico
```kotlin
@Composable
fun MyScreen() {
    val htmlContent = "<h1>Título</h1><p>Contenido con <strong>negrita</strong></p>"
    
    HtmlViewer(
        htmlContent = htmlContent,
        modifier = Modifier.fillMaxSize()
    )
}
```

### Configuración Avanzada

El componente `HtmlViewer` está configurado con:

```kotlin
htmlToAnnotatedString(
    html = htmlContent,
    style = HtmlStyle(
        // Estilos para enlaces
        textLinkStyles = TextLinkStyles(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        ),
        // Habilitar colores CSS
        isTextColorEnabled = true,
        // Indentación para listas
        indentUnit = 24.sp
    ),
    // Separación normal entre párrafos
    compactMode = false,
    // Manejo de clics en enlaces
    linkInteractionListener = { link ->
        if (link is LinkAnnotation.Url) {
            uriHandler.openUri(link.url)
        }
    }
)
```

## Soporte de Colores CSS

### Propiedades CSS Soportadas
- `color` - Color de texto
- `background`, `background-color` - Color de fondo

### Formatos de Color Soportados
- ✅ Nombres CSS Level 4 (ej: `darkred`, `lightblue`)
- ✅ Hexadecimal: `#RRGGBB`, `#RRGGBBAA`, `#RGB`, `#RGBA`
- ❌ No soportado: `rgb()`, `rgba()`, `hsl()`, `hsla()`

### Ejemplo
```html
<span style="color: darkred; background-color: #FFFF0099">
    Texto rojo sobre amarillo translúcido
</span>
```

## Arquitectura

```
composeApp/src/
├── commonMain/kotlin/.../ui/components/
│   └── HtmlViewer.kt                    # Interfaz expect
├── desktopMain/kotlin/.../ui/components/
│   └── HtmlViewer.desktop.kt           # Implementación Desktop
└── androidMain/kotlin/.../ui/components/
    └── HtmlViewer.android.kt           # Implementación Android
```

## Rendimiento

### Optimizaciones
- ✅ Uso de `remember()` para cachear conversiones HTML
- ✅ Recomputación solo cuando cambia el contenido o el tema
- ✅ Scroll nativo de Compose (mejor que WebView)
- ✅ Sin overhead de JavaScript

### Comparación con WebView
| Característica | HtmlConverterCompose | WebView |
|----------------|---------------------|---------|
| Rendimiento | ⚡ Excelente | 🐌 Moderado |
| Integración con Compose | ✅ Nativa | ⚠️ Interop |
| Tamaño del bundle | ✅ Pequeño | ❌ Grande |
| Soporte offline | ✅ Completo | ⚠️ Limitado |
| Personalización | ✅ Total | ⚠️ Limitada |

## Limitaciones Conocidas

1. **Imágenes**: No soporta `<img>` tags (planificado para versiones futuras)
2. **Tablas**: Tags `<table>` son ignorados
3. **Formularios**: Tags `<form>` son ignorados
4. **JavaScript**: No se ejecuta código JavaScript
5. **CSS Externo**: Solo soporta estilos inline

## Casos de Uso

### ✅ Ideal Para
- Prototipos HTML estáticos
- Documentación formateada
- Contenido de blogs/artículos
- Descripciones ricas en aplicaciones
- Emails HTML simples

### ❌ No Recomendado Para
- Aplicaciones web completas
- Contenido con JavaScript
- Formularios interactivos complejos
- Visualización de sitios web externos

## Recursos

- [Repositorio GitHub](https://github.com/cbeyls/HtmlConverterCompose)
- [Documentación Oficial](https://github.com/cbeyls/HtmlConverterCompose#readme)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)

## Licencia

HtmlConverterCompose está licenciado bajo Apache License 2.0
Copyright (C) 2023-2025 Christophe Beyls

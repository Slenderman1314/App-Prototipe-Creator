package app.prototype.creator.ui.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.prototype.creator.LocalAppSettings
import io.github.aakira.napier.Napier

/**
 * Inject simple theme CSS for Android (no JavaFX workarounds needed)
 */
private fun injectAndroidThemeCSS(htmlContent: String, isDarkTheme: Boolean): String {
    val themeCSS = if (isDarkTheme) {
        """
        <style id="theme-override">
            /* DARK THEME - Android */
            :root {
                color-scheme: dark;
            }
            
            html, body {
                background-color: #121212 !important;
                color: #e0e0e0 !important;
            }
            
            /* Containers */
            div, section, article, main, aside, header, footer, nav {
                background-color: #1e1e1e !important;
                color: #e0e0e0 !important;
            }
            
            /* App header */
            .app-header {
                background-color: #1e1e1e !important;
                border-color: #333333 !important;
            }
            
            /* Title - solid color (no gradient) */
            .app-title {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: #bb86fc !important;
                background-clip: unset !important;
                color: #bb86fc !important;
            }
            
            /* Screens */
            .screen {
                background-color: #1e1e1e !important;
                border-color: #333333 !important;
                color: #e0e0e0 !important;
            }
            
            .screen-title {
                color: #bb86fc !important;
                border-bottom-color: #444444 !important;
            }
            
            /* Components */
            .component {
                background-color: #2a2a2a !important;
                border-left-color: #bb86fc !important;
                color: #e0e0e0 !important;
            }
            
            .component-label {
                color: #bb86fc !important;
            }
            
            /* Inputs */
            input, textarea, select {
                background-color: #2a2a2a !important;
                color: #e0e0e0 !important;
                border-color: #444444 !important;
            }
            
            input::placeholder, textarea::placeholder {
                color: #888888 !important;
            }
            
            input:focus, textarea:focus, select:focus {
                border-color: #bb86fc !important;
            }
            
            /* Buttons - gradient with correct font */
            button, button * {
                background: linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%) !important;
                color: #ffffff !important;
                border: none !important;
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            .nav-buttons button, .nav-buttons button * {
                background: linear-gradient(135deg, #bb86fc 0%, #9d6fd8 100%) !important;
                color: #ffffff !important;
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            /* Cards */
            .component-card {
                background-color: #2a2a2a !important;
                border-color: #444444 !important;
                color: #e0e0e0 !important;
            }
            
            /* Tables */
            table {
                background-color: #1e1e1e !important;
            }
            
            th, td {
                background-color: #2c2c2c !important;
                color: #e0e0e0 !important;
                border-color: #424242 !important;
            }
            
            th {
                background-color: #333333 !important;
            }
            
            /* Lists */
            .component-list li {
                border-bottom-color: #444444 !important;
            }
            
            /* Headings */
            h2, h3, h4, h5, h6 {
                color: #bb86fc !important;
            }
        </style>
        """.trimIndent()
    } else {
        """
        <style id="theme-override">
            /* LIGHT THEME - Android */
            :root {
                color-scheme: light;
            }
            
            /* Title - solid color (no gradient) */
            .app-title {
                background: none !important;
                -webkit-background-clip: unset !important;
                -webkit-text-fill-color: #667eea !important;
                background-clip: unset !important;
                color: #667eea !important;
            }
            
            /* Buttons - correct font */
            button, button * {
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
            
            .nav-buttons button, .nav-buttons button * {
                text-decoration: none !important;
                font-weight: 600 !important;
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif !important;
            }
        </style>
        """.trimIndent()
    }
    
    // Inject CSS right after <head> tag or at the beginning if no head tag
    return if (htmlContent.contains("<head>", ignoreCase = true)) {
        htmlContent.replaceFirst(
            Regex("<head>", RegexOption.IGNORE_CASE),
            "<head>\n$themeCSS"
        )
    } else {
        "$themeCSS\n$htmlContent"
    }
}

@Composable
actual fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier,
    key: Any?,
    prototypeName: String,
    exportService: app.prototype.creator.data.service.ExportService?,
    language: app.prototype.creator.data.model.Language
) {
    val appSettings = LocalAppSettings.current
    val isDarkTheme = appSettings.isDarkTheme
    
    Napier.d("🌐 HtmlViewer (Android) rendering HTML (length: ${htmlContent.length}), darkTheme: $isDarkTheme")
    
    // Inject theme CSS into HTML content
    val themedHtmlContent = remember(htmlContent, isDarkTheme) {
        injectAndroidThemeCSS(htmlContent, isDarkTheme)
    }
    
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Napier.d("🏭 Factory: Creating Android WebView for key: $key")
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = false
                    displayZoomControls = false
                }
            }
        },
        update = { webView ->
            Napier.d("🔄 Update: Loading HTML content for key: $key with theme: ${if (isDarkTheme) "dark" else "light"}")
            webView.loadDataWithBaseURL(
                null,
                themedHtmlContent,
                "text/html",
                "UTF-8",
                null
            )
            Napier.d("✅ HTML loaded in WebView")
        }
    )
}

/**
 * Update the language of the HtmlViewer dynamically
 * For Android, this is a no-op as language updates are handled through recomposition
 */
actual fun updateWebViewLanguage(language: app.prototype.creator.data.model.Language) {
    Napier.d("🌐 updateWebViewLanguage called with language: $language (Android)")
    // Language updates are handled through Compose recomposition
}

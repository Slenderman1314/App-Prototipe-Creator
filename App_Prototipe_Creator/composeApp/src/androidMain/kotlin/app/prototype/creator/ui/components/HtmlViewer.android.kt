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
            :root {
                color-scheme: dark;
            }
            
            html, body {
                background-color: #121212 !important;
                color: #e0e0e0 !important;
            }
            
            /* Force dark backgrounds on all containers */
            div, section, article, main, aside, header, footer, nav {
                background-color: #1e1e1e !important;
                color: #e0e0e0 !important;
            }
            
            /* Force dark backgrounds on light colored elements */
            *[style*="background-color: rgb(255"], 
            *[style*="background-color: #fff"],
            *[style*="background-color: white"],
            *[style*="background: rgb(255"],
            *[style*="background: #fff"],
            *[style*="background: white"] {
                background-color: #1e1e1e !important;
                background: #1e1e1e !important;
            }
            
            /* Text colors */
            * {
                color: #e0e0e0 !important;
            }
            
            /* Buttons keep dark text */
            button, [role="button"], .button, .btn {
                background-color: #bb86fc !important;
                color: #000000 !important;
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
        </style>
        """.trimIndent()
    } else {
        """
        <style id="theme-override">
            :root {
                color-scheme: light;
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
    key: Any?
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

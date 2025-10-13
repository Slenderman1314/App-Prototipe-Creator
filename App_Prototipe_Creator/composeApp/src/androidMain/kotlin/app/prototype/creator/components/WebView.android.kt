package app.prototype.creator.components

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun WebView(
    url: String,
    modifier: Modifier,
    onLoadingStateChanged: (Boolean) -> Unit,
    onError: (String) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoading = false
                        onLoadingStateChanged(false)
                    }
                    
                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        super.onReceivedError(view, errorCode, description, failingUrl)
                        onError("Error loading page: $description")
                        onLoadingStateChanged(false)
                    }
                }
                
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
            }
        },
        update = { webView ->
            if (webView.url != url) {
                isLoading = true
                onLoadingStateChanged(true)
                webView.loadUrl(url)
            }
        },
        modifier = modifier
    )
    
    // Show loading indicator if needed
    if (isLoading) {
        // You can add a loading indicator here
    }
}

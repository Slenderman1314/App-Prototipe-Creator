package app.prototype.creator.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific HTML viewer component
 * @param key Unique key to force recreation when changed
 */
@Composable
expect fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier = Modifier,
    key: Any? = null
)

package app.prototype.creator.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific HTML viewer component
 */
@Composable
expect fun HtmlViewer(
    htmlContent: String,
    modifier: Modifier = Modifier
)

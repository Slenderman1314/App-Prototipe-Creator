package app.prototype.creator.ui.navigation

import androidx.compose.runtime.Composable

/**
 * Platform-specific navigation wrapper
 * Routes to the appropriate PrototypeDetailScreen implementation
 */
@Composable
expect fun PrototypeDetailRoute(
    prototypeId: String,
    onBack: () -> Unit,
    version: Int = 0
)

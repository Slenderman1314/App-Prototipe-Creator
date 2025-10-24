package app.prototype.creator.ui.navigation

import androidx.compose.runtime.Composable
import app.prototype.creator.screens.PrototypeDetailScreen

/**
 * Desktop-specific navigation wrapper
 * Routes to the common PrototypeDetailScreen
 */
@Composable
actual fun PrototypeDetailRoute(
    prototypeId: String,
    onBack: () -> Unit,
    version: Int
) {
    PrototypeDetailScreen(
        prototypeId = prototypeId,
        onBack = onBack,
        version = version
    )
}

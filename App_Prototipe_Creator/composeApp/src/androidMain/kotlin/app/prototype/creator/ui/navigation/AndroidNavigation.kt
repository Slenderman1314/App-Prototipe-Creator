package app.prototype.creator.ui.navigation

import androidx.compose.runtime.Composable
import app.prototype.creator.screens.PrototypeDetailScreenAndroid

/**
 * Android-specific navigation wrapper
 * Routes to the Android version of PrototypeDetailScreen with export functionality
 */
@Composable
actual fun PrototypeDetailRoute(
    prototypeId: String,
    onBack: () -> Unit,
    version: Int
) {
    PrototypeDetailScreenAndroid(
        prototypeId = prototypeId,
        onBack = onBack,
        version = version
    )
}

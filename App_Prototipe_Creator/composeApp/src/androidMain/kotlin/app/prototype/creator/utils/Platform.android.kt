package app.prototype.creator.utils

import android.view.WindowManager
import app.prototype.creator.ActivityProvider

actual val isDesktop: Boolean = false

actual fun setSecureMode(enabled: Boolean) {
    SecureStateHolder.isSecure = enabled
    val activity = ActivityProvider.current
    android.util.Log.d("SECURE_FLAG", "🔒 setSecureMode($enabled) activity=${activity != null}")
    if (activity == null) return
    if (enabled) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        android.util.Log.d("SECURE_FLAG", "✅ FLAG_SECURE ADDED")
    } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        android.util.Log.d("SECURE_FLAG", "✅ FLAG_SECURE CLEARED")
    }
}

package app.prototype.creator.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object PermissionUtils {
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    fun hasRequiredPermissions(context: Context): Boolean {
        println("🔍 [PERMISSIONS] Checking required permissions...")
        return REQUIRED_PERMISSIONS.all { permission ->
            val granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            println("   - $permission: ${if (granted) "✅ GRANTED" else "❌ DENIED"}")
            granted
        }
    }

    @Composable
    fun RequestPermissions(onPermissionsGranted: () -> Unit) {
        val context = LocalContext.current
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            println("🔔 [PERMISSIONS] Permission request result:")
            permissions.forEach { (permission, isGranted) ->
                println("   - $permission: ${if (isGranted) "✅ GRANTED" else "❌ DENIED"}")
            }
            
            if (permissions.all { it.value }) {
                println("✅ [PERMISSIONS] All permissions granted")
                onPermissionsGranted()
            } else {
                println("❌ [PERMISSIONS] Some permissions were denied")
                // Proceed anyway for now, but show a warning
                onPermissionsGranted()
            }
        }

        SideEffect {
            val permissionsToRequest = REQUIRED_PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (permissionsToRequest.isNotEmpty()) {
                println("🔔 [PERMISSIONS] Requesting permissions: ${permissionsToRequest.joinToString()}")
                permissionLauncher.launch(permissionsToRequest)
            } else {
                println("✅ [PERMISSIONS] All permissions already granted")
                onPermissionsGranted()
            }
        }
    }
}

package app.prototype.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.prototype.creator.di.initKoin
import app.prototype.creator.ui.theme.AppTheme
import app.prototype.creator.utils.Config
import io.github.aakira.napier.Napier
import org.koin.compose.KoinApplication

/**
 * Sealed class representing all screens in the app
 */
sealed class Screen(val route: String) {
    object Gallery : Screen("gallery")
    object Chat : Screen("chat")
    object PrototypeDetail : Screen("prototype/{prototypeId}") {
        fun createRoute(prototypeId: String) = "prototype/$prototypeId"
    }
    
    companion object {
        const val ARG_PROTOTYPE_ID = "prototypeId"
    }
}

@Composable
fun App() {
    // App state
    var isAppReady by remember { mutableStateOf(false) }
    var initializationError by remember { mutableStateOf<String?>(null) }
    
    // App settings
    val appSettings = remember {
        AppSettings(
            isDarkTheme = Config.defaultTheme.equals("dark", ignoreCase = true),
            defaultLanguage = Config.defaultLanguage
        )
    }
    
    // Initialize Koin
    LaunchedEffect(Unit) {
        try {
            Napier.d("🚀 Starting app initialization...")
            if (!koinInitialized) {
                Napier.d("📦 Initializing Koin...")
                initKoin()
                koinInitialized = true
                Napier.d("✅ Koin initialized successfully")
            } else {
                Napier.d("ℹ️ Koin already initialized")
            }
            Napier.d("✅ App ready!")
            isAppReady = true
        } catch (e: Exception) {
            val errorMsg = "❌ Error initializing app: ${e.message}\n${e.stackTraceToString()}"
            initializationError = errorMsg
            Napier.e(errorMsg, e)
            e.printStackTrace()
        }
    }
    
    // Main app content
    KoinApplication(application = {
        // Koin modules are already loaded in initKoin()
    }) {
        AppContent(
            isAppReady = isAppReady,
            initializationError = initializationError,
            appSettings = appSettings,
            onRetry = { 
                initializationError = null
                isAppReady = false 
            }
        )
    }
}

@Composable
private fun AppContent(
    isAppReady: Boolean,
    initializationError: String?,
    appSettings: AppSettings,
    onRetry: () -> Unit
) {
    AppTheme(
        darkTheme = appSettings.isDarkTheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                initializationError != null -> {
                    ErrorScreen(
                        message = initializationError,
                        onRetry = onRetry
                    )
                }
                !isAppReady -> {
                    LoadingScreen()
                }
                else -> {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Inicializando la aplicación...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Koin initialization state
private var koinInitialized = false

@Composable
private fun MainAppContent() {
    // Simple navigation state
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Gallery) }
    var selectedPrototypeId by remember { mutableStateOf<String?>(null) }
    
    when (currentScreen) {
        is Screen.Gallery -> {
            app.prototype.creator.screens.GalleryScreen(
                onNavigateToChat = { currentScreen = Screen.Chat },
                onNavigateToPrototype = { prototypeId ->
                    selectedPrototypeId = prototypeId
                    currentScreen = Screen.PrototypeDetail
                }
            )
        }
        is Screen.Chat -> {
            app.prototype.creator.screens.ChatScreen(
                onBack = { currentScreen = Screen.Gallery }
            )
        }
        is Screen.PrototypeDetail -> {
            selectedPrototypeId?.let { id ->
                app.prototype.creator.screens.PrototypeDetailScreen(
                    prototypeId = id,
                    onBack = { currentScreen = Screen.Gallery }
                )
            }
        }
    }
}

/**
 * App-wide settings that control the app's appearance and behavior
 * @property isDarkTheme Whether dark theme is enabled
 * @property defaultLanguage The default language code (e.g., "en", "es")
 */
data class AppSettings(
    var isDarkTheme: Boolean = false,
    val defaultLanguage: String = Config.defaultLanguage
) {
    companion object {
        val Saver = androidx.compose.runtime.saveable.Saver<AppSettings, Any>(
            save = { listOf(it.isDarkTheme, it.defaultLanguage) },
            restore = { 
                AppSettings(
                    isDarkTheme = (it as List<*>)[0] as Boolean,
                    defaultLanguage = (it.getOrNull(1) as? String) ?: Config.defaultLanguage
                )
            }
        )
    }
}

// CompositionLocal for theme settings
val LocalAppSettings = androidx.compose.runtime.staticCompositionLocalOf { AppSettings() }

/**
 * Shows a full-screen error message with a retry button
 */
@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Retry")
            }
        }
    }
}

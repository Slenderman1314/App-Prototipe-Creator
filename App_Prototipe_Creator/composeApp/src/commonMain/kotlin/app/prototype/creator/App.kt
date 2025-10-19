package app.prototype.creator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.data.service.SupabaseService
import app.prototype.creator.ui.theme.AppTheme
import io.github.aakira.napier.Napier
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

// Koin is initialized in Main.kt

// Sealed class representing all screens in the app
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

/**
 * App-wide settings that control the app's appearance and behavior
 * Uses MutableState to make theme changes observable
 */
class AppSettings {
    var isDarkTheme by mutableStateOf(false)
    val defaultLanguage: String = "en"
}

// CompositionLocal for theme settings
val LocalAppSettings = androidx.compose.runtime.staticCompositionLocalOf { AppSettings() }

@Composable
fun App() {
    // Koin is already initialized in Main.kt
    // Wrap with KoinContext to provide Compose-specific Koin context
    KoinContext {
        // Initialize WebView window eagerly on desktop
        LaunchedEffect(Unit) {
            initializeHtmlViewer()
        }
        AppContent()
    }
}

// Expect function to initialize platform-specific components
expect fun initializeHtmlViewer()

@Composable
private fun AppContent() {
    val appSettings = remember { AppSettings() }
    var isAppReady by remember { mutableStateOf(false) }
    var initializationError by remember { mutableStateOf<String?>(null) }
    
    // Get services from Koin with null safety
    val supabaseService = koinInject<SupabaseService>()
    val prototypeRepository = koinInject<PrototypeRepository>()
    val chatRepository = koinInject<ChatRepository>()
    
    // Track service initialization state
    var servicesInitialized by remember { mutableStateOf(false) }
    var servicesError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            // Verify all required services are initialized
            val services = listOf(
                "SupabaseService" to supabaseService,
                "PrototypeRepository" to prototypeRepository,
                "ChatRepository" to chatRepository
            )
            
            val missingServices = services.filter { (_, service) -> service == null }
                .map { (name, _) -> name }
            
            if (missingServices.isEmpty()) {
                isAppReady = true
                initializationError = null
                servicesInitialized = true
                Napier.d("✅ All services initialized successfully")
            } else {
                val error = "❌ Missing services: ${missingServices.joinToString()}"
                Napier.e(error)
                servicesError = error
                isAppReady = false
            }
        }.onFailure { e ->
            val error = "❌ Error initializing services: ${e.message}"
            Napier.e(error, e)
            servicesError = error
            isAppReady = false
        }
    }

    AppTheme(
        darkTheme = appSettings.isDarkTheme
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            when {
                servicesError != null -> ErrorScreen(
                    message = servicesError ?: "Unknown error occurred",
                    onRetry = { 
                        servicesError = null
                        servicesInitialized = false
                        isAppReady = false
                    },
                    modifier = Modifier.fillMaxSize()
                )
                !servicesInitialized -> LoadingScreen(initializationError)
                else -> MainAppContent(
                    appSettings = appSettings,
                    prototypeRepository = prototypeRepository,
                    chatRepository = chatRepository
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(error: String?) {
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
            if (error != null) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Error",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Initializing application...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "Error",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Try Again")
            }
        }
    }
}

@Composable
private fun MainAppContent(
    appSettings: AppSettings,
    prototypeRepository: PrototypeRepository,
    chatRepository: ChatRepository
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Gallery) }
    var selectedPrototypeId by remember { mutableStateOf<String?>(null) }
    // Add a version counter to force recreation even when opening the same prototype
    var prototypeVersion by remember { mutableStateOf(0) }
    // Cache gallery state to survive screen changes
    var cachedPrototypes by remember { mutableStateOf<List<app.prototype.creator.data.model.Prototype>>(emptyList()) }
    
    CompositionLocalProvider(
        LocalAppSettings provides appSettings
    ) {
        println("🖥️ APP.KT: Current screen = $currentScreen")
        when (currentScreen) {
            is Screen.Gallery -> {
                println("🖥️ APP.KT: Rendering GalleryScreen")
                // Using fully qualified name to avoid any resolution issues
                @Suppress("ClassName")
                app.prototype.creator.screens.GalleryScreen(
                    initialPrototypes = cachedPrototypes,
                    onPrototypesLoaded = { prototypes ->
                        cachedPrototypes = prototypes
                    },
                    onNavigateToChat = { currentScreen = Screen.Chat },
                    onNavigateToPrototype = { prototypeId ->
                        println("🚀 APP.KT: onNavigateToPrototype called with ID: $prototypeId")
                        selectedPrototypeId = prototypeId
                        prototypeVersion++ // Increment version to force recreation
                        println("🚀 APP.KT: prototypeVersion incremented to: $prototypeVersion")
                        currentScreen = Screen.PrototypeDetail
                        println("🚀 APP.KT: currentScreen set to PrototypeDetail")
                    }
                )
            }
            is Screen.Chat -> {
                println("🖥️ APP.KT: Rendering ChatScreen")
                @Suppress("ClassName")
                app.prototype.creator.screens.ChatScreen(
                    onBack = { currentScreen = Screen.Gallery },
                    onOpenPrototype = { prototypeId ->
                        println("🚀 APP.KT: onOpenPrototype called from Chat with ID: $prototypeId")
                        selectedPrototypeId = prototypeId
                        prototypeVersion++ // Increment version to force recreation
                        currentScreen = Screen.PrototypeDetail
                    }
                )
            }
            is Screen.PrototypeDetail -> {
                println("🖥️ APP.KT: Rendering PrototypeDetailScreen")
                selectedPrototypeId?.let { id ->
                    // Use key with both id and version to force complete recreation
                    key("$id-$prototypeVersion") {
                        @Suppress("ClassName")
                        app.prototype.creator.screens.PrototypeDetailScreen(
                            prototypeId = id,
                            version = prototypeVersion,  // Pass version to force recreation
                            onBack = { 
                                selectedPrototypeId = null
                                // Don't increment galleryVersion - let GalleryScreen maintain its state
                                currentScreen = Screen.Gallery 
                            }
                        )
                    }
                } ?: run {
                    currentScreen = Screen.Gallery
                }
            }
        }
    }
}

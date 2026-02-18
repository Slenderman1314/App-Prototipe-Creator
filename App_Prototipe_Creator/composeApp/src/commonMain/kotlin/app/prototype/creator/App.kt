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
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.ui.components.StorageSelectionDialog
import app.prototype.creator.ui.theme.AppTheme
import app.prototype.creator.utils.StoragePreferences
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
    var language by mutableStateOf(Language.SPANISH)
    val defaultLanguage: String = "es"
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
    
    // Storage preferences
    val storagePreferences = koinInject<StoragePreferences>()
    
    // TEMPORAL: Descomentar la siguiente línea para resetear y ver el diálogo nuevamente
    // storagePreferences.resetStorageConfiguration()
    
    val isStorageConfigured = remember { storagePreferences.isStorageConfigured() }
    var showStorageDialog by remember { mutableStateOf(!isStorageConfigured) }
    var storageMode by remember { mutableStateOf(storagePreferences.getStorageMode()) }
    
    Napier.d("🔍 Storage configured: $isStorageConfigured, Show dialog: $showStorageDialog")
    
    // Get services from Koin with null safety
    val prototypeRepository = koinInject<PrototypeRepository>()
    val chatRepository = koinInject<ChatRepository>()
    
    // Track service initialization state
    var servicesInitialized by remember { mutableStateOf(false) }
    var servicesError by remember { mutableStateOf<String?>(null) }

    // Show storage selection dialog if not configured
    if (showStorageDialog) {
        StorageSelectionDialog(
            onModeSelected = { mode ->
                storageMode = mode
                storagePreferences.setStorageMode(mode)
                storagePreferences.setStorageConfigured()
                showStorageDialog = false
                Napier.d("💾 Storage mode selected: $mode")
            },
            onDismiss = {
                // Use LOCAL as default if dismissed
                storageMode = "LOCAL"
                storagePreferences.setStorageMode("LOCAL")
                storagePreferences.setStorageConfigured()
                showStorageDialog = false
                Napier.d("💾 Storage mode defaulted to: LOCAL")
            }
        )
    }
    
    LaunchedEffect(Unit) {
        runCatching {
            // Verify all required services are initialized
            val services = listOf(
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
                Napier.d("💾 Current storage mode: $storageMode")
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
    val languageRepository = koinInject<LanguageRepository>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
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
                    contentDescription = Strings.error.localized(currentLanguage),
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = Strings.error.localized(currentLanguage),
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
                    text = Strings.initializingApp.localized(currentLanguage),
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
    val languageRepository = koinInject<LanguageRepository>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
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
                contentDescription = Strings.error.localized(currentLanguage),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Strings.error.localized(currentLanguage),
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
                Text(Strings.tryAgain.localized(currentLanguage))
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
    
    // Storage settings dialog state
    val storagePreferences = koinInject<StoragePreferences>()
    var showStorageSettingsDialog by remember { mutableStateOf(false) }

    
    // Show storage settings dialog when requested
    if (showStorageSettingsDialog) {
        StorageSelectionDialog(
            onModeSelected = { mode ->
                storagePreferences.setStorageMode(mode)
                showStorageSettingsDialog = false
                Napier.d("💾 Storage mode changed to: $mode")
            },
            onDismiss = {
                showStorageSettingsDialog = false
                Napier.d("💾 Storage settings dialog dismissed")
            }
        )
    }
    
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
                    },
                    onStorageSettingsClick = {
                        showStorageSettingsDialog = true
                        Napier.d("💾 Storage settings button clicked")
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
                println("🖥️ APP.KT: Rendering PrototypeDetailScreen (Android with Export)")
                selectedPrototypeId?.let { id ->
                    // Use key with both id and version to force complete recreation
                    key("$id-$prototypeVersion") {
                        @Suppress("ClassName")
                        app.prototype.creator.ui.navigation.PrototypeDetailRoute(
                            prototypeId = id,
                            onBack = { 
                                selectedPrototypeId = null
                                // Don't increment galleryVersion - let GalleryScreen maintain its state
                                currentScreen = Screen.Gallery 
                            },
                            version = prototypeVersion  // Pass version to force recreation
                        )
                    }
                } ?: run {
                    currentScreen = Screen.Gallery
                }
            }
        }
    }
}

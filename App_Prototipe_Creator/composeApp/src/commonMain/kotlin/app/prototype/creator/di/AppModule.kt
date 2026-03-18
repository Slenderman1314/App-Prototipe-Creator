package app.prototype.creator.di

import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.data.repository.SQLDelightPrototypeRepository
import app.prototype.creator.data.repository.SQLDelightChatRepository
import app.prototype.creator.db.DatabaseDriverFactory
import app.prototype.creator.db.AppDatabase
import app.prototype.creator.data.service.AiService
import app.prototype.creator.data.service.N8nAIService
import app.prototype.creator.data.service.SpringBootAIService
import app.prototype.creator.data.service.ExportService
import app.prototype.creator.data.service.CommonExportService
import app.prototype.creator.data.service.PlatformExporter
import app.prototype.creator.data.local.AppSettings
import app.prototype.creator.data.local.FavoritesRepository
import app.prototype.creator.utils.Config
import app.prototype.creator.utils.StoragePreferences
import com.russhwolf.settings.Settings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.dsl.module

/**
 * Initialize Koin dependency injection - common implementation
 */
fun initKoin() {
    try {
        Napier.d("📦 initKoin() called")
        
        // Check if Koin is already started
        val koin = org.koin.core.context.GlobalContext.getOrNull()
        if (koin != null) {
            Napier.d("ℹ️ Koin already started, skipping initialization")
            return
        }
        
        Napier.d("🔧 Starting Koin...")
        startKoin {
            // Configure logger
            logger(
                PrintLogger(
                    level = if (Config.DEBUG) Level.DEBUG else Level.INFO
                )
            )
            
            // Enable debug mode if needed
            if (Config.DEBUG) {
                // Additional debug configuration if needed
            }
            
            // Load all modules (platform module will be loaded via initPlatformKoin)
            modules(
                appModule,
                viewModelModule
            )
        }
        
        // Initialize platform-specific modules
        initPlatformKoin()
        Napier.d(" Koin started successfully")
    } catch (e: Exception) {
        // Log the error and rethrow
        Napier.e(" Error starting Koin: ${e.message}", e)
        e.printStackTrace()
        throw e
    }
}

// Platform-specific initialization (to be implemented in androidMain)
expect fun initPlatformKoin()

// Helper function to create HTTP client with proper configuration
expect fun createConfiguredHttpClient(): HttpClient

// Main application module
val appModule = module {
    // Application configuration
    single { Config }

    // HTTP Client with proper configuration
    single { createConfiguredHttpClient() }

    // Settings
    single<Settings> { AppSettings.getSettings() }
    
    // Storage Preferences
    single { StoragePreferences(get()) }

    // SQLDelight Database
    single {
        val driver = get<DatabaseDriverFactory>().createDriver()
        AppDatabase(driver)
    }
    
    // Favorites Repository
    single {
        FavoritesRepository.create(
            settings = get()
        )
    }

    // AI Service - Configurable backend (n8n or Spring Boot)
    single<AiService> {
        val storagePreferences = get<StoragePreferences>()
        val backendType = storagePreferences.getAiBackend()
        Napier.d("🤖 Initializing AI Service with backend: $backendType")
        
        when (backendType) {
            StoragePreferences.BACKEND_SPRING_BOOT -> {
                Napier.d("✅ Using Spring Boot AI backend")
                SpringBootAIService(
                    client = get(),
                    baseUrl = Config.springBootBaseUrl,
                    apiKey = Config.springBootApiKey
                )
            }
            else -> {
                Napier.d("✅ Using n8n AI backend (default)")
                N8nAIService(
                    client = get(),
                    baseUrl = Config.n8nBaseUrl,
                    webhookPath = Config.n8nWebhookPath,
                    apiKey = Config.n8nApiKey
                )
            }
        }
    }
    
    // Repositories - Using SQLDelight for local storage
    single<ChatRepository> {
        SQLDelightChatRepository(
            database = get()
        )
    }
    
    single<PrototypeRepository> {
        // Usar SQLDelight para almacenamiento local
        SQLDelightPrototypeRepository(
            database = get()
        )
    }
    
    single {
        LanguageRepository()
    }
    
    // Export Service
    single { PlatformExporter() }
    
    single<ExportService> {
        CommonExportService(
            platformExporter = get()
        )
    }
}

// ViewModel module
val viewModelModule = module {
}
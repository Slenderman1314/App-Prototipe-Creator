package app.prototype.creator.di

import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.InMemoryChatRepository
import app.prototype.creator.data.repository.InMemoryPrototypeRepository
import app.prototype.creator.data.repository.SupabasePrototypeRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.data.service.AiService
import app.prototype.creator.data.service.N8nAIService
import app.prototype.creator.data.local.AppSettings
import app.prototype.creator.data.local.FavoritesRepository
import app.prototype.creator.data.service.SupabaseService
import app.prototype.creator.data.service.SupabaseServiceImpl
import app.prototype.creator.utils.Config
import com.russhwolf.settings.Settings
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
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
            
            // Load all modules
            modules(
                appModule,
                viewModelModule,
                module {
                    // Any additional modules can be added here
                }
            )
        }
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
private fun createConfiguredHttpClient(): HttpClient {
    Napier.d("🔧 Creating HTTP Client...")
    return HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d("HTTP: $message")
                }
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000L
            connectTimeoutMillis = 60_000L
            socketTimeoutMillis = 60_000L
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }.also {
        Napier.d("✅ HTTP Client created successfully")
    }
}

// Main application module
val appModule = module {
    // Application configuration
    single { Config }

    // HTTP Client with proper configuration
    single { createConfiguredHttpClient() }

    // Settings
    single<Settings> { AppSettings.getSettings() }

    // Favorites Repository
    single {
        FavoritesRepository.create(
            settings = get()
        )
    }

    // AI Service
    single<AiService> {
        N8nAIService(
            client = get(),
            baseUrl = Config.n8nBaseUrl,
            webhookPath = Config.n8nWebhookPath
        )
    }

    // Supabase Service (debe ir antes de los repositorios que lo usan)
    single<SupabaseService> {
        SupabaseServiceImpl(
            client = get(),
            favoritesRepository = get(),
            supabaseUrl = Config.supabaseUrl,
            supabaseKey = Config.supabaseAnonKey
        )
    }
    
    // Repositories
    single<ChatRepository> {
        InMemoryChatRepository()
    }
    
    single<PrototypeRepository> {
        // Usar el repositorio de Supabase en lugar del in-memory
        SupabasePrototypeRepository(
            supabaseService = get()
        )
    }
}

// ViewModel module
val viewModelModule = module {
}

// createHttpClient function is already defined as createConfiguredHttpClient()
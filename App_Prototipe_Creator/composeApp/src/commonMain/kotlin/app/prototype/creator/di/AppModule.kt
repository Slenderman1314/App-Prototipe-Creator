package app.prototype.creator.di

import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.InMemoryChatRepository
import app.prototype.creator.data.repository.InMemoryPrototypeRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.data.service.AiService
import app.prototype.creator.data.service.N8nAIService
import app.prototype.creator.utils.Config
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
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

            // Initialize logging
            if (Config.DEBUG) {
                Napier.base(DebugAntilog())
            }

            // Load modules
            Napier.d("📚 Loading modules...")
            modules(
                appModule,
                viewModelModule
            )
        }
        Napier.d("✅ Koin started successfully")
    } catch (e: Exception) {
        // Log the error and rethrow
        Napier.e("❌ Error starting Koin: ${e.message}", e)
        e.printStackTrace()
        throw e
    }
}

// Platform-specific initialization (to be implemented in androidMain)
expect fun initPlatformKoin()

// Main application module
val appModule = module {
    // Application configuration
    single {
        Config
    }

    // HTTP Client
    single {
        createHttpClient()
    }

    // AI Service
    single<AiService> {
        N8nAIService(
            client = get(),
            baseUrl = Config.n8nBaseUrl,
            webhookPath = Config.n8nWebhookPath
        )
    }

    // Repositories
    single<ChatRepository> {
        InMemoryChatRepository()
    }

    single<PrototypeRepository> {
        InMemoryPrototypeRepository()
    }
}

// ViewModel module
val viewModelModule = module {
    // ViewModels are created on-demand in screens
    // Koin factory definitions would go here if using Koin for ViewModel injection
}

// Helper function to create HTTP client
fun createHttpClient() = HttpClient {
    // Install JSON serializer
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            }
        )
    }

    // Install logging
    if (Config.DEBUG) {
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Napier.d(tag = "HTTP", message = message)
                }
            }
            level = LogLevel.ALL
        }
    }

    // Configure timeouts
    install(HttpTimeout) {
        requestTimeoutMillis = 60_000
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 60_000
    }

    // Default headers
    defaultRequest {
        headers.append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
    }
}
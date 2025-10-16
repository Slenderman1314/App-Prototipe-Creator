package app.prototype.creator.di

import android.content.Context
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
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
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext

actual fun initPlatformKoin() {
    // Platform-specific initialization is handled in MainActivity
}

actual fun createConfiguredHttpClient(): HttpClient {
    Napier.d("🔧 Creating HTTP Client (Android)...")
    return HttpClient(Android) {
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
            requestTimeoutMillis = 180_000L  // 3 minutes for n8n workflows
            connectTimeoutMillis = 60_000L
            socketTimeoutMillis = 180_000L
        }
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }.also {
        Napier.d("✅ HTTP Client created successfully")
    }
}

fun initKoin(context: Context) {
    if (GlobalContext.getOrNull() == null) {
        org.koin.core.context.startKoin {
            androidContext(context)
            Napier.base(DebugAntilog())
            modules(appModule, viewModelModule)
        }
    }
}

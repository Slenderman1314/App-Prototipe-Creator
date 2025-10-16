package app.prototype.creator.di

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

actual fun initPlatformKoin() {
    // No platform-specific initialization needed for Desktop
}

actual fun createConfiguredHttpClient(): HttpClient {
    Napier.d("🔧 Creating HTTP Client (Desktop/CIO)...")
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

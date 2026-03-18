package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import io.github.aakira.napier.Napier

/**
 * Implementation of [AiService] that sends messages to an n8n webhook.
 */
class AiServiceImpl(
    private val client: HttpClient,
    private val n8nBaseUrl: String,
    private val n8nWebhookPath: String,
    private val n8nApiKey: String
) : AiService {
    
    @Serializable
    private data class ChatRequest(
        val messages: List<ChatMessage>
    )
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> = runCatching {
        // Build URL correctly, ensuring no double slashes
        val baseUrl = n8nBaseUrl.trimEnd('/')
        val webhookPath = n8nWebhookPath.trimStart('/')
        val url = "$baseUrl/$webhookPath"
        
        Napier.d("🔗 Sending message to: $url")
        
        val response = client.post(url) {
            // Only add authorization header if API key is not empty
            if (n8nApiKey.isNotBlank()) {
                header(HttpHeaders.Authorization, "Bearer $n8nApiKey")
            }
            contentType(ContentType.Application.Json)
            setBody(ChatRequest(messages))
        }
        
        val responseText = response.bodyAsText()
        Napier.d("📥 Response received: ${response.status} - $responseText")
        
        if (response.status.isSuccess()) {
            responseText
        } else {
            throw Exception("Server error (${response.status}): $responseText")
        }
    }
}

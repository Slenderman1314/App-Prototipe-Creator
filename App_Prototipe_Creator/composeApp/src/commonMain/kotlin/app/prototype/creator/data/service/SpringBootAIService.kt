package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SpringBootChatRequest(
    val messages: List<ChatMessage>,
    val sessionId: String? = null
)

@Serializable
data class SpringBootChatResponse(
    val message: String = "",
    val previewDataUrl: String? = null,
    val needsMoreInfo: Boolean = false,
    val error: String? = null
)

/**
 * Implementación de AiService que se conecta a Spring Boot AI backend.
 * Compatible con la arquitectura de AI Agents migrada desde n8n.
 */
class SpringBootAIService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val apiKey: String? = null
) : AiService {
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            val url = "${baseUrl.trimEnd('/')}/api/v1/chat"
            
            val requestBody = SpringBootChatRequest(
                messages = messages,
                sessionId = apiKey // Usar apiKey como sessionId si está disponible
            )
            
            Napier.d("📤 Sending request to Spring Boot AI: $url")
            Napier.d("📦 Request body: ${messages.size} messages")
            Napier.d("📝 Last message: ${messages.lastOrNull()?.content}")
            
            val httpResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                
                // Agregar JWT token si está disponible
                if (!apiKey.isNullOrEmpty()) {
                    header("Authorization", "Bearer $apiKey")
                    Napier.d("🔐 Added Authorization header")
                }
                
                setBody(requestBody)
            }
            
            Napier.d("📥 Response status: ${httpResponse.status}")
            
            if (httpResponse.status.value !in 200..299) {
                return Result.failure(
                    Exception("Spring Boot AI returned status ${httpResponse.status.value}: ${httpResponse.status.description}")
                )
            }
            
            val response: SpringBootChatResponse = httpResponse.body()
            Napier.d("✅ Response: ${response.message}")
            
            if (!response.error.isNullOrBlank()) {
                return Result.failure(Exception(response.error))
            }
            
            // Si hay URL de preview, incluirla en el mensaje
            val finalMessage = if (!response.previewDataUrl.isNullOrBlank()) {
                "${response.message}\n\n🔗 ${response.previewDataUrl}"
            } else {
                response.message
            }
            
            Result.success(finalMessage)
            
        } catch (e: Exception) {
            Napier.e("❌ Error in SpringBootAIService.sendMessage", e)
            Napier.e("Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

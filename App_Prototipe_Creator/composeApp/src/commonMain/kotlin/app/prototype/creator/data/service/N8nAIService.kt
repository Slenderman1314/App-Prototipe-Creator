package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

/**
 * Implementación de AiService que se conecta a un webhook de n8n.
 * Esta implementación asume que el webhook de n8n espera un formato específico de mensaje.
 */
class N8nAIService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val webhookPath: String
) : AiService {
    
    @Serializable
    private data class N8nRequest(
        val messages: List<ChatMessage>
    )
    
    @Serializable
    private data class N8nResponse(
        val response: String,
        val metadata: Map<String, String> = emptyMap()
    )
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            // Construir la URL del webhook de n8n
            val url = "${baseUrl.trimEnd('/')}/${webhookPath.trimStart('/')}"
            
            // Crear el cuerpo de la petición
            val requestBody = N8nRequest(messages = messages)
            
            // Enviar la petición al webhook de n8n
            val response: N8nResponse = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body()
            
            // Devolver la respuesta del asistente
            Result.success(response.response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

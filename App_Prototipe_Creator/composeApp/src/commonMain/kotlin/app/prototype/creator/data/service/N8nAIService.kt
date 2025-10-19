package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import io.github.aakira.napier.Napier
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class N8nRequest(
    val messages: List<ChatMessage>
)

@Serializable
data class N8nResponse(
    val message: String? = null,
    val response: String? = null,
    val error: String? = null,
    val output: String? = null,
    val result: String? = null,
    val data: String? = null
)

/**
 * Implementación de AiService que se conecta a un webhook de n8n.
 * Esta implementación asume que el webhook de n8n espera un formato específico de mensaje.
 */
class N8nAIService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val webhookPath: String,
    private val apiKey: String? = null
) : AiService {
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            // Construir la URL del webhook de n8n
            val url = if (webhookPath.startsWith("http://") || webhookPath.startsWith("https://")) {
                webhookPath
            } else {
                "${baseUrl.trimEnd('/')}/${webhookPath.trimStart('/')}"
            }
            
            // Crear el cuerpo de la petición
            val requestBody = N8nRequest(messages = messages)
            
            Napier.d("📤 Sending request to n8n: $url")
            Napier.d("📦 Request body: ${messages.size} messages")
            Napier.d("📝 Last message: ${messages.lastOrNull()?.content}")
            
            // Enviar la petición al webhook de n8n
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
            
            // Verificar el código de respuesta
            if (httpResponse.status.value !in 200..299) {
                return Result.failure(Exception("n8n returned status ${httpResponse.status.value}: ${httpResponse.status.description}"))
            }
            
            // Obtener la respuesta como texto plano primero
            val responseBody: String = httpResponse.body()
            Napier.d("📄 Raw response body: $responseBody")
            
            // Verificar si la respuesta está vacía
            if (responseBody.isBlank()) {
                Napier.w("⚠️ Webhook returned empty response")
                return Result.failure(Exception("El webhook de n8n devolvió una respuesta vacía. Por favor, verifica la configuración del webhook."))
            }
            
            // Intentar parsear como JSON manualmente
            val responseText = try {
                // Primero intentar extraer el campo "raw" si existe
                val rawContent = if (responseBody.contains("\"raw\"")) {
                    val rawRegex = "\"raw\"\\s*:\\s*\"\\{(.+?)\\}\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                    val rawMatch = rawRegex.find(responseBody)
                    if (rawMatch != null) {
                        "{${rawMatch.groupValues[1]}}"
                    } else {
                        responseBody
                    }
                } else {
                    responseBody
                }
                
                // Ahora buscar el campo output, message o response
                when {
                    rawContent.contains("\\\"output\\\"") -> {
                        val regex = "\\\\\"output\\\\\"\\s*:\\s*\\\\\"(.+?)\\\\\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                        regex.find(rawContent)?.groupValues?.get(1)?.replace("\\n", "\n") ?: rawContent
                    }
                    rawContent.contains("\"output\"") -> {
                        val regex = "\"output\"\\s*:\\s*\"(.+?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                        regex.find(rawContent)?.groupValues?.get(1)?.replace("\\n", "\n") ?: rawContent
                    }
                    rawContent.contains("\"response\"") -> {
                        val regex = "\"response\"\\s*:\\s*\"(.+?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                        regex.find(rawContent)?.groupValues?.get(1)?.replace("\\n", "\n") ?: rawContent
                    }
                    rawContent.contains("\"message\"") -> {
                        val regex = "\"message\"\\s*:\\s*\"(.+?)\"".toRegex(RegexOption.DOT_MATCHES_ALL)
                        regex.find(rawContent)?.groupValues?.get(1)?.replace("\\n", "\n") ?: rawContent
                    }
                    else -> rawContent
                }
            } catch (e: Exception) {
                Napier.w("Failed to parse JSON, using raw response", e)
                responseBody
            }
            
            Napier.d("✅ Final response text: $responseText")
            Result.success(responseText)
        } catch (e: Exception) {
            Napier.e("❌ Error in N8nAIService.sendMessage", e)
            Napier.e("Error details: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}

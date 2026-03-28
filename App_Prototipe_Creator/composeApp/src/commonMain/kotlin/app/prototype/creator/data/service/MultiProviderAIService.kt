package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.utils.StoragePreferences
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Multi-provider AI Service using HTTP clients
 * Supports: OpenAI, Anthropic, Google Gemini
 */
class MultiProviderAIService(
    private val client: HttpClient,
    private val preferences: StoragePreferences
) : AiService {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    private val systemPrompt = """
        You are an AI assistant specialized in creating application prototypes.
        Help users design and generate functional prototypes based on their ideas.
        Be creative, helpful, and provide detailed suggestions.
    """.trimIndent()
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            val provider = preferences.getAiProvider()
            Napier.d("Using provider: $provider")
            
            val apiKey = when (provider) {
                StoragePreferences.PROVIDER_OPENAI -> preferences.getOpenAiApiKey()
                StoragePreferences.PROVIDER_ANTHROPIC -> preferences.getAnthropicApiKey()
                StoragePreferences.PROVIDER_GOOGLE -> preferences.getGoogleApiKey()
                else -> null
            }
            
            if (apiKey.isNullOrBlank()) {
                return Result.failure(
                    Exception(
                        "Por favor configura tu API key en Settings.\n\n" +
                        "Ve a Settings → API Keys y añade tu clave de $provider"
                    )
                )
            }
            
            val userMessage = messages.lastOrNull { it.isFromUser }?.content
            if (userMessage.isNullOrBlank()) {
                return Result.failure(Exception("No se encontró mensaje del usuario"))
            }
            
            Napier.d("Sending message to $provider")
            
            val response = when (provider) {
                StoragePreferences.PROVIDER_OPENAI -> callOpenAI(apiKey, userMessage)
                StoragePreferences.PROVIDER_ANTHROPIC -> callAnthropic(apiKey, userMessage)
                StoragePreferences.PROVIDER_GOOGLE -> callGoogle(apiKey, userMessage)
                else -> throw Exception("Unknown provider: $provider")
            }
            
            Result.success(response)
            
        } catch (e: Exception) {
            Napier.e("Error in MultiProviderAIService", e)
            Result.failure(e)
        }
    }
    
    private suspend fun callOpenAI(apiKey: String, message: String): String {
        val response = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "model": "gpt-4o",
                    "messages": [
                        {"role": "system", "content": "$systemPrompt"},
                        {"role": "user", "content": ${Json.encodeToString(kotlinx.serialization.serializer(), message)}}
                    ]
                }
            """.trimIndent())
        }
        
        val jsonResponse = json.parseToJsonElement(response.bodyAsText()).jsonObject
        return jsonResponse["choices"]?.jsonArray?.get(0)
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.content
            ?: throw Exception("Invalid OpenAI response")
    }
    
    private suspend fun callAnthropic(apiKey: String, message: String): String {
        val response = client.post("https://api.anthropic.com/v1/messages") {
            header("x-api-key", apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody("""
                {
                    "model": "claude-3-5-sonnet-20241022",
                    "max_tokens": 4096,
                    "system": "$systemPrompt",
                    "messages": [
                        {"role": "user", "content": ${Json.encodeToString(kotlinx.serialization.serializer(), message)}}
                    ]
                }
            """.trimIndent())
        }
        
        val jsonResponse = json.parseToJsonElement(response.bodyAsText()).jsonObject
        return jsonResponse["content"]?.jsonArray?.get(0)
            ?.jsonObject?.get("text")
            ?.jsonPrimitive?.content
            ?: throw Exception("Invalid Anthropic response")
    }
    
    private suspend fun callGoogle(apiKey: String, message: String): String {
        try {
            Napier.d("Calling Google Gemini API with model: gemini-1.5-flash-latest")
            val response = client.post("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash-latest:generateContent?key=$apiKey") {
                contentType(ContentType.Application.Json)
                setBody("""
                    {
                        "contents": [{
                            "parts": [{
                                "text": "$systemPrompt\n\n${message.replace("\"", "\\\"")}"
                            }]
                        }]
                    }
                """.trimIndent())
            }
            
            Napier.d("Google API HTTP Status: ${response.status.value}")
            val responseBody = response.bodyAsText()
            Napier.d("Google Gemini response body length: ${responseBody.length}")
            Napier.d("Google Gemini response: $responseBody")
            
            val jsonResponse = json.parseToJsonElement(responseBody).jsonObject
            
            // Check for error in response
            if (jsonResponse.containsKey("error")) {
                val error = jsonResponse["error"]?.jsonObject
                val errorMessage = error?.get("message")?.jsonPrimitive?.content ?: "Unknown error"
                val errorCode = error?.get("code")?.jsonPrimitive?.content ?: "Unknown code"
                throw Exception("Google API Error ($errorCode): $errorMessage")
            }
            
            // Parse successful response
            val text = jsonResponse["candidates"]?.jsonArray?.get(0)
                ?.jsonObject?.get("content")
                ?.jsonObject?.get("parts")?.jsonArray?.get(0)
                ?.jsonObject?.get("text")
                ?.jsonPrimitive?.content
            
            if (text.isNullOrBlank()) {
                Napier.e("Failed to parse Google response. Full response: $responseBody")
                throw Exception("Invalid Google response format. Please check your API key and try again.")
            }
            
            return text
            
        } catch (e: Exception) {
            Napier.e("Error calling Google Gemini API", e)
            throw e
        }
    }
}

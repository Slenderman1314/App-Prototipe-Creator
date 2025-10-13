package app.prototype.creator.data.service

import app.prototype.creator.data.model.ChatMessage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.Serializable

interface AiService {
    suspend fun sendMessage(messages: List<ChatMessage>): Result<String>
}

class OpenAIService(private val client: HttpClient) : AiService {
    private val apiKey = "YOUR_OPENAI_API_KEY" // In a real app, use dependency injection
    
    @Serializable
    private data class OpenAIRequest(
        val model: String = "gpt-3.5-turbo",
        val messages: List<Message>,
        val temperature: Double = 0.7
    )
    
    @Serializable
    private data class Message(
        val role: String,
        val content: String
    )
    
    @Serializable
    private data class OpenAIResponse(
        val choices: List<Choice>
    )
    
    @Serializable
    private data class Choice(
        val message: Message
    )
    
    override suspend fun sendMessage(messages: List<ChatMessage>): Result<String> {
        return try {
            val openAIMessages = messages.map {
                Message(
                    role = if (it.isFromUser) "user" else "assistant",
                    content = it.content
                )
            }
            
            val response: OpenAIResponse = client.post("https://api.openai.com/v1/chat/completions") {
                header("Authorization", "Bearer $apiKey")
                header("Content-Type", "application/json")
                setBody(OpenAIRequest(messages = openAIMessages))
            }.body()
            
            Result.success(response.choices.firstOrNull()?.message?.content ?: "No response")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

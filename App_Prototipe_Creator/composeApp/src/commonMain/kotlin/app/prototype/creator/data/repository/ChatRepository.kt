package app.prototype.creator.data.repository

import app.prototype.creator.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

interface ChatRepository {
    /**
     * Get all messages for a specific chat
     * @param chatId The ID of the chat
     * @return Flow of list of messages
     */
    fun getMessages(chatId: String): Flow<List<ChatMessage>>
    
    /**
     * Send a message in a chat
     * @param chatId The ID of the chat
     * @param message The message to send
     */
    suspend fun sendMessage(chatId: String, message: ChatMessage)
    
    /**
     * Create a new chat
     * @param title The title of the new chat
     * @return The ID of the created chat
     */
    suspend fun createChat(title: String): String
}

class InMemoryChatRepository : ChatRepository {
    private val conversations = mutableMapOf<String, MutableList<ChatMessage>>()
    private val recentConversations = MutableStateFlow(setOf<String>())

    override fun getMessages(conversationId: String): Flow<List<ChatMessage>> {
        return MutableStateFlow(conversations[conversationId] ?: emptyList())
    }

    override suspend fun sendMessage(conversationId: String, message: ChatMessage) {
        val conversation = conversations.getOrPut(conversationId) { mutableListOf() }
        conversation.add(message)
        
        // Update recent conversations
        recentConversations.value = recentConversations.value + conversationId
    }

    override suspend fun createChat(title: String): String {
        val chatId = "chat_${System.currentTimeMillis()}"
        conversations[chatId] = mutableListOf()
        recentConversations.value = recentConversations.value + chatId
        return chatId
    }
}

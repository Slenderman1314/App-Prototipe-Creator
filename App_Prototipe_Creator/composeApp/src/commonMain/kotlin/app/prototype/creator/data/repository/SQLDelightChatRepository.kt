package app.prototype.creator.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.db.AppDatabase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * SQLDelight implementation of ChatRepository
 * Stores chat messages in local SQLite database
 */
class SQLDelightChatRepository(
    private val database: AppDatabase
) : ChatRepository {
    
    private val chatQueries = database.chatQueries
    private val messageQueries = database.chatMessageQueries
    
    override fun getMessages(chatId: String): Flow<List<ChatMessage>> {
        return messageQueries.selectByChatId(chatId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbMessages ->
                dbMessages.map { it.toDomain() }
            }
            .catch { e ->
                Napier.e("Error loading messages for chat $chatId", e)
                emit(emptyList())
            }
    }
    
    override suspend fun sendMessage(chatId: String, message: ChatMessage) {
        withContext(Dispatchers.IO) {
            try {
                // Ensure chat exists
                val chatExists = chatQueries.selectById(chatId).executeAsOneOrNull() != null
                if (!chatExists) {
                    // Create chat if it doesn't exist
                    chatQueries.insert(
                        id = chatId,
                        title = "Chat ${System.currentTimeMillis()}",
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                }
                
                // Insert message
                messageQueries.insert(
                    id = message.id,
                    chatId = chatId,
                    content = message.content,
                    isFromUser = if (message.isFromUser) 1L else 0L,
                    timestamp = message.timestamp,
                    isError = if (message.isError) 1L else 0L
                )
                
                // Update chat's updatedAt
                chatQueries.update(
                    title = "Chat", // Keep existing title
                    updatedAt = System.currentTimeMillis(),
                    id = chatId
                )
                
                Napier.d("✅ Message sent to chat $chatId")
            } catch (e: Exception) {
                Napier.e("❌ Error sending message", e)
                throw e
            }
        }
    }
    
    override suspend fun createChat(title: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val chatId = "chat_${now}"
                
                chatQueries.insert(
                    id = chatId,
                    title = title,
                    createdAt = now,
                    updatedAt = now
                )
                
                Napier.d("✅ Chat created: $chatId")
                chatId
            } catch (e: Exception) {
                Napier.e("❌ Error creating chat", e)
                throw e
            }
        }
    }
    
    /**
     * Get all chats ordered by most recent
     */
    fun getAllChats(): Flow<List<ChatInfo>> {
        return chatQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { dbChats ->
                dbChats.map { chat ->
                    ChatInfo(
                        id = chat.id,
                        title = chat.title,
                        createdAt = chat.createdAt,
                        updatedAt = chat.updatedAt
                    )
                }
            }
            .catch { e ->
                Napier.e("Error loading chats", e)
                emit(emptyList())
            }
    }
    
    /**
     * Delete a chat and all its messages
     */
    suspend fun deleteChat(chatId: String) {
        withContext(Dispatchers.IO) {
            try {
                messageQueries.deleteByChatId(chatId)
                chatQueries.deleteById(chatId)
                Napier.d("✅ Chat deleted: $chatId")
            } catch (e: Exception) {
                Napier.e("❌ Error deleting chat", e)
                throw e
            }
        }
    }
    
    /**
     * Clear all messages in a chat
     */
    suspend fun clearMessages(chatId: String) {
        withContext(Dispatchers.IO) {
            try {
                messageQueries.deleteByChatId(chatId)
                Napier.d("✅ Messages cleared for chat: $chatId")
            } catch (e: Exception) {
                Napier.e("❌ Error clearing messages", e)
                throw e
            }
        }
    }
}

/**
 * Data class for chat information
 */
data class ChatInfo(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long
)

/**
 * Extension function to convert database model to domain model
 */
private fun app.prototype.creator.db.ChatMessage.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        content = content,
        isFromUser = isFromUser == 1L,
        timestamp = timestamp,
        isError = isError == 1L
    )
}

package app.prototype.creator.data.repository

import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.data.service.FirebaseService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Firebase implementation of ChatRepository
 * 
 * This repository uses Firebase Firestore as the backend for cloud storage.
 * Chats and messages are stored in the cloud.
 */
class FirebaseChatRepository(
    private val firebaseService: FirebaseService
) : ChatRepository {

    override fun getMessages(chatId: String): Flow<List<ChatMessage>> = flow {
        try {
            emit(emptyList()) // Loading state
            
            val result = firebaseService.getChatMessages(chatId)
            result.fold(
                onSuccess = { messages ->
                    emit(messages)
                    Napier.d("✅ Successfully loaded ${messages.size} messages for chat: $chatId from Firebase")
                },
                onFailure = { error ->
                    Napier.e("❌ Error loading messages for chat: $chatId from Firebase", error)
                    emit(emptyList())
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception loading messages for chat: $chatId from Firebase: ${e.message}", e)
            emit(emptyList())
        }
    }

    override suspend fun sendMessage(chatId: String, message: ChatMessage) {
        try {
            val result = firebaseService.saveChatMessage(chatId, message)
            result.fold(
                onSuccess = { savedMessage ->
                    Napier.d("✅ Successfully sent message to chat: $chatId")
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to send message to chat: $chatId", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while sending message to chat: $chatId", e)
            throw e
        }
    }

    override suspend fun createChat(title: String): String {
        try {
            val chatId = "chat_${System.currentTimeMillis()}"
            val chat = app.prototype.creator.data.model.Chat(
                id = chatId,
                title = title,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            
            val result = firebaseService.saveChat(chat)
            result.fold(
                onSuccess = { savedChat ->
                    Napier.d("✅ Successfully created chat: ${savedChat.title}")
                    return savedChat.id
                },
                onFailure = { error ->
                    Napier.e("❌ Failed to create chat", error)
                    throw error
                }
            )
        } catch (e: Exception) {
            Napier.e("❌ Exception while creating chat", e)
            throw e
        }
    }
}

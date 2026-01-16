package app.prototype.creator.data.service

import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.model.Chat
import app.prototype.creator.data.model.ChatMessage

/**
 * Service interface for interacting with Firebase Firestore
 * 
 * This interface defines the contract for cloud storage operations
 * using Firebase as the backend.
 */
interface FirebaseService {
    /**
     * Get a single prototype by ID
     */
    suspend fun getPrototype(id: String): Result<Prototype>

    /**
     * Save or update a prototype
     */
    suspend fun savePrototype(prototype: Prototype): Result<Prototype>

    /**
     * Delete a prototype by ID
     */
    suspend fun deletePrototype(id: String): Result<Boolean>

    /**
     * List all prototypes for the current user
     */
    suspend fun listPrototypes(): Result<List<Prototype>>

    /**
     * Search prototypes by name
     */
    suspend fun searchPrototypes(query: String): Result<List<Prototype>>

    /**
     * Get a single chat by ID
     */
    suspend fun getChat(id: String): Result<Chat>

    /**
     * Save or update a chat
     */
    suspend fun saveChat(chat: Chat): Result<Chat>

    /**
     * Delete a chat by ID
     */
    suspend fun deleteChat(id: String): Result<Boolean>

    /**
     * List all chats for the current user
     */
    suspend fun listChats(): Result<List<Chat>>

    /**
     * Get messages for a specific chat
     */
    suspend fun getChatMessages(chatId: String): Result<List<ChatMessage>>

    /**
     * Save a message to a chat
     */
    suspend fun saveChatMessage(chatId: String, message: ChatMessage): Result<ChatMessage>

    /**
     * Delete a message from a chat
     */
    suspend fun deleteChatMessage(chatId: String, messageId: String): Result<Boolean>

    /**
     * Check if Firebase is available and connected
     */
    suspend fun isAvailable(): Boolean
}

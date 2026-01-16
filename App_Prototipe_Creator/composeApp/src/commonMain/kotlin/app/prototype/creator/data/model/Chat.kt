package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a chat conversation
 */
@Serializable
data class Chat(
    val id: String,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val userId: String? = null
)

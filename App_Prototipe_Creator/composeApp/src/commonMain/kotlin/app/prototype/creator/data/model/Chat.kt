@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

/**
 * Represents a chat conversation
 */
@Serializable
data class Chat(
    val id: String,
    val title: String,
    val createdAt: Long,
    val updatedAt: Long,
    val userId: String?
)

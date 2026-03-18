@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val isError: Boolean
) {
    companion object {
        fun create(content: String, isFromUser: Boolean, isError: Boolean = false): ChatMessage =
            ChatMessage(
                id = "msg_${System.currentTimeMillis()}_${(0..1000).random()}",
                content = content,
                isFromUser = isFromUser,
                timestamp = System.currentTimeMillis(),
                isError = isError
            )
    }
}

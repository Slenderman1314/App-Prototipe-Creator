package app.prototype.creator.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String = generateId(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
) {
    companion object {
        private fun generateId(): String = "msg_${System.currentTimeMillis()}_${(0..1000).random()}"
    }
}

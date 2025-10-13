package app.prototype.creator.ui.viewmodel

import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.service.AiService
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val aiService: AiService
) : ViewModel() {
    var uiState by mutableStateOf(ChatUiState())
        private set
    
    fun onMessageChange(newMessage: String) {
        uiState = uiState.copy(inputText = newMessage)
    }
    
    fun sendMessage() {
        val message = uiState.inputText.trim()
        if (message.isBlank()) return
        
        // Add user message to chat
        val userMessage = ChatMessage(content = message, isFromUser = true)
        val updatedMessages = uiState.messages + userMessage
        
        uiState = uiState.copy(
            messages = updatedMessages,
            inputText = "",
            isLoading = true
        )
        
        viewModelScope.launch {
            val result = aiService.sendMessage(updatedMessages)
            
            uiState = if (result.isSuccess) {
                val responseMessage = ChatMessage(
                    content = result.getOrNull() ?: "No response",
                    isFromUser = false
                )
                uiState.copy(
                    messages = updatedMessages + responseMessage,
                    isLoading = false,
                    error = null
                )
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                val errorChatMessage = ChatMessage(
                    content = "Error: $errorMessage",
                    isFromUser = false,
                    isError = true
                )
                uiState.copy(
                    messages = updatedMessages + errorChatMessage,
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }
}

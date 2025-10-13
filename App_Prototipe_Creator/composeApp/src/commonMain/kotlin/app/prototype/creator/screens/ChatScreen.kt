package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←") // Using text arrow as back button
                    }
                }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    placeholder = { Text("Type a message...") },
                    shape = RoundedCornerShape(24.dp)
                )
                IconButton(
                    onClick = {
                        if (messageText.isNotBlank()) {
                            messages.add(Message(text = messageText, isUser = true))
                            messageText = ""
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("↑") // Using text arrow as send button
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            items(messages) { msg ->
                MessageBubble(msg)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: Message) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(16.dp),
                color = if (message.isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

data class Message(
    val text: String,
    val isUser: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

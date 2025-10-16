package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.service.AiService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    onOpenPrototype: (String) -> Unit = {}
) {
    // Get services from Koin
    val chatRepository = org.koin.compose.koinInject<ChatRepository>()
    val aiService = org.koin.compose.koinInject<AiService>()
    
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // Create a chat session
    val chatId = remember { "chat_${System.currentTimeMillis()}" }
    
    // Add welcome message
    LaunchedEffect(Unit) {
        messages.add(
            ChatMessage(
                content = "¡Hola! Soy tu asistente de IA. Describe la aplicación que quieres crear y te ayudaré a generar un prototipo.",
                isFromUser = false
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Assistant") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { Text("Describe tu idea de app...") },
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isLoading,
                        maxLines = 4
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && !isLoading) {
                                val userMessage = messageText
                                messageText = ""
                                
                                // Add user message
                                val userChatMessage = ChatMessage(
                                    content = userMessage,
                                    isFromUser = true
                                )
                                messages.add(userChatMessage)
                                
                                // Send to AI
                                scope.launch {
                                    isLoading = true
                                    try {
                                        // Save user message
                                        chatRepository.sendMessage(chatId, userChatMessage)
                                        
                                        // Send to AI service (n8n)
                                        val result = aiService.sendMessage(messages.toList())
                                        
                                        result.fold(
                                            onSuccess = { response ->
                                                // Limpiar barras invertidas escapadas y otros caracteres
                                                val cleanedResponse = response
                                                    .replace("\\n", "\n")
                                                    .replace("\\\"", "\"")
                                                    .replace("\\t", "\t")
                                                    .replace("\\\\", "\\")
                                                    .replace("\\r", "")
                                                    .replace("\\", "")  // Eliminar barras invertidas sueltas
                                                    .trim()
                                                
                                                val aiMessage = ChatMessage(
                                                    content = cleanedResponse,
                                                    isFromUser = false
                                                )
                                                messages.add(aiMessage)
                                                chatRepository.sendMessage(chatId, aiMessage)
                                                
                                                // Scroll to bottom
                                                listState.animateScrollToItem(messages.size - 1)
                                            },
                                            onFailure = { error ->
                                                Napier.e("Error sending message to AI", error)
                                                val errorMessage = ChatMessage(
                                                    content = "Error: ${error.message ?: "No se pudo conectar con el servicio de IA"}",
                                                    isFromUser = false,
                                                    isError = true
                                                )
                                                messages.add(errorMessage)
                                            }
                                        )
                                    } catch (e: Exception) {
                                        Napier.e("Exception in chat", e)
                                        val errorMessage = ChatMessage(
                                            content = "Error: ${e.message ?: "Error desconocido"}",
                                            isFromUser = false,
                                            isError = true
                                        )
                                        messages.add(errorMessage)
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.size(48.dp),
                        enabled = messageText.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Enviar")
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                MessageBubble(
                    message = msg,
                    onOpenPrototype = { prototypeId ->
                        onOpenPrototype(prototypeId)
                        onBack()
                    },
                    onConfirmAction = {
                        // Enviar "Sí" automáticamente
                        val confirmMessage = ChatMessage(
                            content = "Sí, continúa",
                            isFromUser = true
                        )
                        messages.add(confirmMessage)
                        
                        scope.launch {
                            isLoading = true
                            try {
                                chatRepository.sendMessage(chatId, confirmMessage)
                                val result = aiService.sendMessage(messages.toList())
                                
                                result.fold(
                                    onSuccess = { response ->
                                        val cleanedResponse = response
                                            .replace("\\n", "\n")
                                            .replace("\\\"", "\"")
                                            .replace("\\t", "\t")
                                            .replace("\\\\", "\\")
                                            .replace("\\r", "")
                                            .replace("\\", "")
                                            .trim()
                                        
                                        val aiMessage = ChatMessage(
                                            content = cleanedResponse,
                                            isFromUser = false
                                        )
                                        messages.add(aiMessage)
                                        chatRepository.sendMessage(chatId, aiMessage)
                                        listState.animateScrollToItem(messages.size - 1)
                                    },
                                    onFailure = { error ->
                                        Napier.e("Error sending confirmation", error)
                                        val errorMessage = ChatMessage(
                                            content = "Error: ${error.message ?: "No se pudo conectar"}",
                                            isFromUser = false,
                                            isError = true
                                        )
                                        messages.add(errorMessage)
                                    }
                                )
                            } catch (e: Exception) {
                                Napier.e("Exception in confirmation", e)
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                )
            }
            
            // Typing indicator
            if (isLoading) {
                item {
                    TypingIndicator()
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onOpenPrototype: (String) -> Unit = {},
    onConfirmAction: () -> Unit = {}
) {
    // Detectar si el mensaje contiene un link de prototipo
    val prototypeUrlRegex = "https://[^\\s]+/webhook/[^\\s]+".toRegex()
    val prototypeMatch = prototypeUrlRegex.find(message.content)
    
    // Detectar si el mensaje pide confirmación y separar la pregunta
    val confirmationPatterns = listOf(
        "¿Guardamos esta idea?",
        "¿Quieres que guardemos",
        "Responde 'Sí' para continuar",
        "continúe con la generación"
    )
    
    // Buscar la línea de confirmación
    var mainContent = message.content
    var confirmationText: String? = null
    
    if (!message.isFromUser) {
        // Buscar patrones de confirmación al final del mensaje
        val lines = message.content.lines()
        val lastLine = lines.lastOrNull()?.trim() ?: ""
        
        // Solo detectar si la última línea contiene un patrón específico de confirmación
        // y es una pregunta corta (menos de 100 caracteres)
        if (lastLine.length < 100 && confirmationPatterns.any { lastLine.contains(it, ignoreCase = true) }) {
            // Separar el contenido principal de la pregunta de confirmación
            confirmationText = lastLine
            mainContent = lines.dropLast(1).joinToString("\n").trim()
        }
    }
    
    val needsConfirmation = confirmationText != null
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (prototypeMatch != null && !message.isFromUser) {
            // Extraer el ID del prototipo del URL
            val prototypeUrl = prototypeMatch.value
            val prototypeId = prototypeUrl.substringAfterLast("/p/").substringBefore("\\")
            
            // Extraer el texto antes y después del link
            val textBeforeLink = message.content.substring(0, prototypeMatch.range.first).trim()
            val textAfterLink = message.content.substring(prototypeMatch.range.last + 1).trim()
            
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Texto antes del link (si existe)
                if (textBeforeLink.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            text = textBeforeLink,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Tarjeta del prototipo
                Surface(
                    onClick = { onOpenPrototype(prototypeId) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "✨ Prototipo generado",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Toca para ver el prototipo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                // Texto después del link (si existe)
                if (textAfterLink.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            text = textAfterLink,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else if (needsConfirmation) {
            // Mensaje con botón de confirmación
            Column(
                modifier = Modifier.fillMaxWidth(0.85f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Contenido principal del mensaje
                if (mainContent.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            text = mainContent,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Caja con pregunta de confirmación y botón
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Texto de confirmación
                        Text(
                            text = confirmationText ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        // Botón de confirmación
                        Button(
                            onClick = onConfirmAction,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sí, continúa")
                        }
                    }
                }
            }
        } else {
            // Mensaje normal sin link
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isFromUser) 4.dp else 16.dp
                ),
                color = when {
                    message.isError -> MaterialTheme.colorScheme.errorContainer
                    message.isFromUser -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = when {
                        message.isError -> MaterialTheme.colorScheme.onErrorContainer
                        message.isFromUser -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp, 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Escribiendo...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

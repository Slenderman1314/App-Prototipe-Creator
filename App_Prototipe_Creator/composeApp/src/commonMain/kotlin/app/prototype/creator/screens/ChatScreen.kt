package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.ChatMessage
import app.prototype.creator.data.repository.ChatRepository
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.data.service.AiService
import app.prototype.creator.utils.StoragePreferences
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
    val languageRepository = org.koin.compose.koinInject<LanguageRepository>()
    val storagePreferences = org.koin.compose.koinInject<StoragePreferences>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
    var showProviderMenu by remember { mutableStateOf(false) }
    var showKeyboardHelp by remember { mutableStateOf(false) }
    
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // Create a chat session
    val chatId = remember { "chat_${System.currentTimeMillis()}" }
    
    // Function to send message
    val sendMessage: () -> Unit = {
        if (messageText.isNotBlank() && !isLoading) {
            val userMessage = messageText
            messageText = ""
            
            // Add user message
            val userChatMessage = ChatMessage.create(
                content = userMessage,
                isFromUser = true
            )
            messages.add(userChatMessage)
            
            // Send to AI
            scope.launch {
                isLoading = true
                try {
                    chatRepository.sendMessage(chatId, userChatMessage)
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
                            
                            val aiMessage = ChatMessage.create(
                                content = cleanedResponse,
                                isFromUser = false
                            )
                            messages.add(aiMessage)
                            chatRepository.sendMessage(chatId, aiMessage)
                            listState.animateScrollToItem(messages.size - 1)
                        },
                        onFailure = { error ->
                            Napier.e("Error sending message to AI", error)
                            val errorMessage = ChatMessage.create(
                                content = "Error: ${error.message ?: "No se pudo conectar con el servicio de IA"}",
                                isFromUser = false,
                                isError = true
                            )
                            messages.add(errorMessage)
                        }
                    )
                } catch (e: Exception) {
                    Napier.e("Exception in chat", e)
                    val errorMessage = ChatMessage.create(
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
    }
    
    // Add welcome message - update when language changes
    LaunchedEffect(currentLanguage) {
        // Clear and add welcome message in current language
        if (messages.isEmpty() || !messages.first().isFromUser) {
            messages.clear()
            messages.add(
                ChatMessage.create(
                    content = Strings.chatWelcomeMessage.localized(currentLanguage),
                    isFromUser = false
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.chatTitle.localized(currentLanguage)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.back.localized(currentLanguage))
                    }
                },
                actions = {
                    // Provider selector dropdown
                    Box {
                        TextButton(
                            onClick = { showProviderMenu = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = when (storagePreferences.getAiProvider()) {
                                    StoragePreferences.PROVIDER_OPENAI -> "OpenAI"
                                    StoragePreferences.PROVIDER_ANTHROPIC -> "Anthropic"
                                    StoragePreferences.PROVIDER_GOOGLE -> "Google"
                                    StoragePreferences.PROVIDER_N8N -> "n8n"
                                    else -> "OpenAI"
                                },
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showProviderMenu,
                            onDismissRequest = { showProviderMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "OpenAI",
                                            color = if (storagePreferences.getOpenAiApiKey().isNullOrBlank()) 
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            else 
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        if (!storagePreferences.getOpenAiApiKey().isNullOrBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    storagePreferences.setAiProvider(StoragePreferences.PROVIDER_OPENAI)
                                    showProviderMenu = false
                                    Napier.d("Provider changed to OpenAI")
                                },
                                enabled = !storagePreferences.getOpenAiApiKey().isNullOrBlank(),
                                trailingIcon = {
                                    if (storagePreferences.getAiProvider() == StoragePreferences.PROVIDER_OPENAI) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Anthropic",
                                            color = if (storagePreferences.getAnthropicApiKey().isNullOrBlank()) 
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            else 
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        if (!storagePreferences.getAnthropicApiKey().isNullOrBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    storagePreferences.setAiProvider(StoragePreferences.PROVIDER_ANTHROPIC)
                                    showProviderMenu = false
                                    Napier.d("Provider changed to Anthropic")
                                },
                                enabled = !storagePreferences.getAnthropicApiKey().isNullOrBlank(),
                                trailingIcon = {
                                    if (storagePreferences.getAiProvider() == StoragePreferences.PROVIDER_ANTHROPIC) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Google",
                                            color = if (storagePreferences.getGoogleApiKey().isNullOrBlank()) 
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                            else 
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                        if (!storagePreferences.getGoogleApiKey().isNullOrBlank()) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    storagePreferences.setAiProvider(StoragePreferences.PROVIDER_GOOGLE)
                                    showProviderMenu = false
                                    Napier.d("Provider changed to Google")
                                },
                                enabled = !storagePreferences.getGoogleApiKey().isNullOrBlank(),
                                trailingIcon = {
                                    if (storagePreferences.getAiProvider() == StoragePreferences.PROVIDER_GOOGLE) {
                                        Icon(Icons.Default.Check, contentDescription = null)
                                    }
                                }
                            )
                        }
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
                            .padding(end = 8.dp)
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.type == KeyEventType.KeyDown) {
                                    when {
                                        // Enter sin modificadores -> Enviar mensaje
                                        keyEvent.key == Key.Enter && !keyEvent.isShiftPressed && !keyEvent.isCtrlPressed -> {
                                            sendMessage()
                                            true
                                        }
                                        // Shift + Enter -> Nueva línea (comportamiento por defecto)
                                        keyEvent.key == Key.Enter && keyEvent.isShiftPressed -> {
                                            false // Dejar que TextField maneje el salto de línea
                                        }
                                        // Ctrl/Cmd + K -> Limpiar chat
                                        (keyEvent.isCtrlPressed || keyEvent.isMetaPressed) && keyEvent.key == Key.K -> {
                                            messages.clear()
                                            messages.add(
                                                ChatMessage.create(
                                                    content = Strings.chatWelcomeMessage.localized(currentLanguage),
                                                    isFromUser = false
                                                )
                                            )
                                            true
                                        }
                                        // Escape -> Limpiar campo de texto
                                        keyEvent.key == Key.Escape -> {
                                            messageText = ""
                                            true
                                        }
                                        else -> false
                                    }
                                } else {
                                    false
                                }
                            },
                        placeholder = { Text(Strings.typeMessage.localized(currentLanguage)) },
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isLoading,
                        maxLines = 4
                    )
                    IconButton(
                        onClick = sendMessage,
                        modifier = Modifier.size(48.dp),
                        enabled = messageText.isNotBlank() && !isLoading
                    ) {
                        Icon(Icons.Default.Send, contentDescription = Strings.send.localized(currentLanguage))
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                    currentLanguage = currentLanguage,
                    onOpenPrototype = { prototypeId ->
                        onOpenPrototype(prototypeId)
                        onBack()
                    },
                    onConfirmAction = {
                        // Enviar "Sí" automáticamente
                        val confirmMessage = ChatMessage.create(
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
                                        
                                        val aiMessage = ChatMessage.create(
                                            content = cleanedResponse,
                                            isFromUser = false
                                        )
                                        messages.add(aiMessage)
                                        chatRepository.sendMessage(chatId, aiMessage)
                                        listState.animateScrollToItem(messages.size - 1)
                                    },
                                    onFailure = { error ->
                                        Napier.e("Error sending confirmation", error)
                                        val errorMessage = ChatMessage.create(
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
                    TypingIndicator(currentLanguage)
                }
            }
        }
        
        // Floating help button
        FloatingActionButton(
            onClick = { showKeyboardHelp = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .padding(bottom = 80.dp), // Espacio para el bottomBar
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Keyboard,
                contentDescription = "Keyboard shortcuts"
            )
        }
        
        // Keyboard shortcuts dialog
        if (showKeyboardHelp) {
            KeyboardShortcutsDialog(
                currentLanguage = currentLanguage,
                onDismiss = { showKeyboardHelp = false }
            )
        }
    }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    currentLanguage: app.prototype.creator.data.model.Language,
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
                                text = Strings.prototypeGenerated.localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = Strings.tapToViewPrototype.localized(currentLanguage),
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
                            Text(Strings.yesContinue.localized(currentLanguage))
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
private fun TypingIndicator(currentLanguage: app.prototype.creator.data.model.Language) {
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
                    text = Strings.writing.localized(currentLanguage),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun KeyboardShortcutsDialog(
    currentLanguage: app.prototype.creator.data.model.Language,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Atajos de Teclado" 
                    else 
                        "Keyboard Shortcuts"
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección: Controles de Mensaje
                Text(
                    text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Controles de Mensaje" 
                    else 
                        "Message Controls",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ShortcutItem(
                    keys = "Enter",
                    description = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Enviar mensaje" 
                    else 
                        "Send message"
                )
                
                ShortcutItem(
                    keys = "Shift + Enter",
                    description = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Nueva línea" 
                    else 
                        "New line"
                )
                
                ShortcutItem(
                    keys = "Escape",
                    description = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Limpiar campo de texto" 
                    else 
                        "Clear text field"
                )
                
                Divider()
                
                // Sección: Gestión de Chat
                Text(
                    text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Gestión de Chat" 
                    else 
                        "Chat Management",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                ShortcutItem(
                    keys = "Ctrl/Cmd + K",
                    description = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Limpiar conversación" 
                    else 
                        "Clear conversation"
                )
                
                Divider()
                
                // Sección: Formato de Mensajes
                Text(
                    text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Formato de Mensajes" 
                    else 
                        "Message Format",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                                "• Describe tu idea de app claramente" 
                            else 
                                "• Describe your app idea clearly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                                "• Menciona funcionalidades clave" 
                            else 
                                "• Mention key features",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                                "• Responde preguntas específicamente" 
                            else 
                                "• Answer questions specifically",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) 
                        "Entendido" 
                    else 
                        "Got it"
                )
            }
        }
    )
}

@Composable
private fun ShortcutItem(
    keys: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = keys,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
    }
}

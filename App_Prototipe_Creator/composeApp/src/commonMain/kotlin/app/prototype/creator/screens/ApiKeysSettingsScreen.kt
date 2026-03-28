package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import app.prototype.creator.utils.StoragePreferences
import io.github.aakira.napier.Napier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeysSettingsScreen(
    currentLanguage: Language,
    storagePreferences: StoragePreferences,
    onNavigateBack: () -> Unit
) {
    var openAiKey by remember { mutableStateOf(storagePreferences.getOpenAiApiKey() ?: "") }
    var anthropicKey by remember { mutableStateOf(storagePreferences.getAnthropicApiKey() ?: "") }
    var googleKey by remember { mutableStateOf(storagePreferences.getGoogleApiKey() ?: "") }
    
    var showOpenAiKey by remember { mutableStateOf(false) }
    var showAnthropicKey by remember { mutableStateOf(false) }
    var showGoogleKey by remember { mutableStateOf(false) }
    
    var showSaveSnackbar by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    
    val scrollState = rememberScrollState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(Strings.apiKeysSettings.localized(currentLanguage))
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.back.localized(currentLanguage))
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showClearDialog = true }
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = Strings.clearAll.localized(currentLanguage))
                    }
                }
            )
        },
        snackbarHost = {
            if (showSaveSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { showSaveSnackbar = false }) {
                            Text(Strings.ok.localized(currentLanguage))
                        }
                    }
                ) {
                    Text(Strings.apiKeysSaved.localized(currentLanguage))
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Column {
                        Text(
                            text = Strings.importantInformation.localized(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Strings.apiKeysSecurityInfo.localized(currentLanguage),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            
            // OpenAI Section
            ApiKeySection(
                title = "OpenAI",
                subtitle = Strings.openAiSubtitle.localized(currentLanguage),
                apiKey = openAiKey,
                onApiKeyChange = { openAiKey = it },
                showKey = showOpenAiKey,
                onToggleVisibility = { showOpenAiKey = !showOpenAiKey },
                helpUrl = "https://platform.openai.com/api-keys",
                currentLanguage = currentLanguage
            )
            
            // Anthropic Section
            ApiKeySection(
                title = "Anthropic",
                subtitle = Strings.anthropicSubtitle.localized(currentLanguage),
                apiKey = anthropicKey,
                onApiKeyChange = { anthropicKey = it },
                showKey = showAnthropicKey,
                onToggleVisibility = { showAnthropicKey = !showAnthropicKey },
                helpUrl = "https://console.anthropic.com/settings/keys",
                currentLanguage = currentLanguage
            )
            
            // Google Section
            ApiKeySection(
                title = "Google",
                subtitle = Strings.googleSubtitle.localized(currentLanguage),
                apiKey = googleKey,
                onApiKeyChange = { googleKey = it },
                showKey = showGoogleKey,
                onToggleVisibility = { showGoogleKey = !showGoogleKey },
                helpUrl = "https://ai.google.dev/gemini-api/docs/api-key",
                currentLanguage = currentLanguage
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Save Button
            Button(
                onClick = {
                    if (openAiKey.isNotBlank()) {
                        storagePreferences.setOpenAiApiKey(openAiKey)
                    }
                    if (anthropicKey.isNotBlank()) {
                        storagePreferences.setAnthropicApiKey(anthropicKey)
                    }
                    if (googleKey.isNotBlank()) {
                        storagePreferences.setGoogleApiKey(googleKey)
                    }
                    storagePreferences.setApiConfigured(true)
                    showSaveSnackbar = true
                    Napier.d("API Keys saved")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = openAiKey.isNotBlank() || anthropicKey.isNotBlank() || googleKey.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(Strings.saveApiKeys.localized(currentLanguage))
            }
        }
    }
    
    // Clear Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = {
                Text(Strings.clearAllApiKeys.localized(currentLanguage))
            },
            text = {
                Text(Strings.clearAllApiKeysConfirmation.localized(currentLanguage))
            },
            confirmButton = {
                Button(
                    onClick = {
                        storagePreferences.clearApiKeys()
                        openAiKey = ""
                        anthropicKey = ""
                        googleKey = ""
                        showClearDialog = false
                        Napier.d("All API keys cleared")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(Strings.clear.localized(currentLanguage))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(Strings.cancel.localized(currentLanguage))
                }
            }
        )
    }
}

@Composable
private fun ApiKeySection(
    title: String,
    subtitle: String,
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    showKey: Boolean,
    onToggleVisibility: () -> Unit,
    helpUrl: String,
    currentLanguage: Language
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(Strings.apiKey.localized(currentLanguage))
                },
                placeholder = {
                    Text("sk-...")
                },
                visualTransformation = if (showKey)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                trailingIcon = {
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (showKey)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (showKey) 
                                Strings.hide.localized(currentLanguage) 
                            else 
                                Strings.show.localized(currentLanguage)
                        )
                    }
                },
                singleLine = true
            )
            
            TextButton(
                onClick = { /* TODO: Open URL */ }
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    Strings.howToGetApiKey.localized(currentLanguage),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

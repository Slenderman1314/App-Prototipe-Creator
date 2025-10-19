package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings
import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.service.SupabaseService
import app.prototype.creator.ui.components.HtmlViewer
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private fun formatDate(timestamp: Long): String {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val day = localDateTime.dayOfMonth.toString().padStart(2, '0')
    val month = (localDateTime.monthNumber).toString().padStart(2, '0')
    val year = localDateTime.year
    val hour = localDateTime.hour.toString().padStart(2, '0')
    val minute = localDateTime.minute.toString().padStart(2, '0')
    return "$day/$month/$year $hour:$minute"
}

// Platform detection
expect fun getPlatform(): String

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrototypeDetailScreen(
    prototypeId: String,
    onBack: () -> Unit,
    version: Int = 0  // Version to force recreation
) {
    // Use only prototypeId as key - version will force component recreation via key() wrapper
    val uniqueKey = prototypeId
    // Get SupabaseService from Koin
    val supabaseService = org.koin.compose.koinInject<SupabaseService>()
    var prototype by remember(prototypeId) { mutableStateOf<Prototype?>(null) }
    var isLoading by remember(prototypeId) { mutableStateOf(true) }
    var errorMessage by remember(prototypeId) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    // Load prototype from Supabase - reset state when prototypeId changes
    LaunchedEffect(prototypeId) {
        Napier.d("🔄 LaunchedEffect triggered for prototypeId: $prototypeId")
        // Reset state
        prototype = null
        isLoading = true
        errorMessage = null
        
        scope.launch {
            try {
                // Fetch prototype details
                val result = supabaseService.getPrototype(prototypeId)
                result.fold(
                    onSuccess = { proto ->
                        prototype = proto
                        Napier.d("✅ Prototype loaded: ${proto.name}")
                        Napier.d("   - ID: ${proto.id}")
                        Napier.d("   - HTML content length: ${proto.htmlContent?.length ?: 0}")
                        Napier.d("   - HTML preview: ${proto.htmlContent?.take(100)}")
                    },
                    onFailure = { error ->
                        errorMessage = error.message ?: "Error desconocido"
                        Napier.e("❌ Error loading prototype: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                errorMessage = "Error al cargar el prototipo: ${e.message}"
                Napier.e("❌ Exception in loadPrototype", e)
            } finally {
                isLoading = false
            }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prototype?.name ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle share */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                errorMessage != null -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error al cargar el prototipo",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            scope.launch {
                                isLoading = true
                                errorMessage = null
                                val result = supabaseService.getPrototype(prototypeId)
                                result.fold(
                                    onSuccess = { proto ->
                                        prototype = proto
                                        isLoading = false
                                    },
                                    onFailure = { error ->
                                        errorMessage = error.message
                                        isLoading = false
                                    }
                                )
                            }
                        }) {
                            Text("Reintentar")
                        }
                    }
                }
                prototype != null -> {
                    val htmlContent = prototype?.htmlContent
                    val appSettings = LocalAppSettings.current
                    val isDarkTheme = appSettings.isDarkTheme
                    
                    Napier.d("🔍 Checking HTML content:")
                    Napier.d("   - htmlContent is null: ${htmlContent == null}")
                    Napier.d("   - htmlContent length: ${htmlContent?.length ?: 0}")
                    Napier.d("   - htmlContent isEmpty: ${htmlContent?.isEmpty()}")
                    Napier.d("   - isDarkTheme: $isDarkTheme")
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Si hay contenido HTML, mostrarlo renderizado
                        if (!htmlContent.isNullOrEmpty()) {
                            val viewerKey = "$uniqueKey-${if (isDarkTheme) "dark" else "light"}"
                            Napier.d("📄 Rendering HTML content for prototype: ${prototype?.name} with key: $viewerKey")
                            // Use key() with theme to force recreation when theme changes
                            HtmlViewer(
                                htmlContent = htmlContent,
                                modifier = Modifier.fillMaxSize(),
                                key = uniqueKey  // Use prototypeId as key
                            )
                            
                            // Auto-navigate back after window opens
                            if (getPlatform() == "Desktop") {
                                LaunchedEffect(uniqueKey) {
                                    kotlinx.coroutines.delay(500)
                                    onBack()
                                }
                            }
                        } else {
                            Napier.w("⚠️ No HTML content available for prototype: ${prototype?.name}")
                            // Fallback: mostrar detalles si no hay HTML
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = prototype?.name ?: "",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = prototype?.description ?: "",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Detalles del Prototipo",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "URL: ${prototype?.previewUrl ?: "No disponible"}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        
                                        Spacer(modifier = Modifier.height(8.dp))
                                        
                                        Text(
                                            text = "Creado: ${formatDate(prototype?.createdAt ?: 0)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

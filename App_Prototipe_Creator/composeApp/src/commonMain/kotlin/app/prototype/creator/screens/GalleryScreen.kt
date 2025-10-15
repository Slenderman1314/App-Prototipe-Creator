package app.prototype.creator.screens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.service.SupabaseService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// Data class for the gallery state
data class GalleryState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prototypes: List<Prototype> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    initialPrototypes: List<Prototype> = emptyList(),
    onPrototypesLoaded: (List<Prototype>) -> Unit = {},
    onNavigateToChat: () -> Unit = {},
    onNavigateToPrototype: (String) -> Unit = {}
) {
    println("📱 GalleryScreen COMPOSING")
    Napier.d("📱 GalleryScreen is being composed/recomposed")
    // Get SupabaseService from Koin
    val supabaseService = org.koin.compose.koinInject<SupabaseService>()
    val scope = rememberCoroutineScope()

    // State management - Initialize with cached prototypes if available
    val loadKey = remember { System.currentTimeMillis() }
    var state by remember { 
        mutableStateOf(
            if (initialPrototypes.isNotEmpty()) {
                GalleryState(isLoading = false, prototypes = initialPrototypes)
            } else {
                GalleryState(isLoading = false)
            }
        )
    }

    LaunchedEffect(loadKey) {
        try {
            if (supabaseService == null) {
                state = state.copy(
                    isLoading = false,
                    error = "Error: SupabaseService no está disponible"
                )
                return@LaunchedEffect
            }
            
            // Only show loading if we don't have cached prototypes
            if (initialPrototypes.isEmpty()) {
                state = state.copy(isLoading = true)
            }
            
            try {
                val result = supabaseService.listPrototypes()
                
                result.fold(
                    onSuccess = { prototypes ->
                        state = state.copy(
                            isLoading = false,
                            prototypes = prototypes
                        )
                        // Notify parent about loaded prototypes
                        onPrototypesLoaded(prototypes)
                        Napier.d("✅ Prototypes loaded: ${prototypes.size}")
                    },
                    onFailure = { error ->
                        state = state.copy(
                            isLoading = false,
                            error = "Error al cargar los prototipos: ${error.message}"
                        )
                        Napier.e("❌ Error loading prototypes: ${error.message}", error)
                    }
                )
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error: ${e.message ?: "Error desconocido al cargar los prototipos"}"
                )
                Napier.e("❌ Exception in loadPrototypes", e)
            }
        } catch (e: Exception) {
            state = state.copy(
                isLoading = false,
                error = e.message ?: "An error occurred while loading prototypes"
            )
            Napier.e("❌ Exception in loadPrototypes", e)
        }
    }

    // Main UI
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Prototypes") },
                actions = {
                    TextButton(onClick = onNavigateToChat) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = "Chat",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text("Chat")
                        }
                    }
                }
            )
        }
    ) { padding ->
        println("🔍 STATE CHECK: isLoading=${state.isLoading}, error=${state.error}, prototypes.size=${state.prototypes.size}")
        Napier.d("🔍 STATE CHECK: isLoading=${state.isLoading}, error=${state.error}, prototypes.size=${state.prototypes.size}")
        when {
            state.isLoading && state.prototypes.isEmpty() -> {
                println("⏳ Showing loading indicator")
                Napier.d("⏳ Showing loading indicator")
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            !state.error.isNullOrEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading prototypes",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = {
                        // Retry loading
                        scope.launch {
                            state = state.copy(isLoading = true, error = null)
                            try {
                                if (supabaseService == null) {
                                    state = state.copy(
                                        isLoading = false,
                                        error = "Error: SupabaseService no está disponible"
                                    )
                                    return@launch
                                }
                                
                                try {
                                    val result = supabaseService.listPrototypes()
                                    result.fold(
                                        onSuccess = { prototypes ->
                                            state = state.copy(
                                                isLoading = false,
                                                prototypes = prototypes,
                                                error = null
                                            )
                                        },
                                        onFailure = { error ->
                                            state = state.copy(
                                                isLoading = false,
                                                error = "Error al cargar los prototipos: ${error.message}"
                                            )
                                        }
                                    )
                                } catch (e: Exception) {
                                    state = state.copy(
                                        isLoading = false,
                                        error = "Error: ${e.message ?: "Error desconocido al cargar los prototipos"}"
                                    )
                                }
                            } catch (e: Exception) {
                                state = state.copy(
                                    isLoading = false,
                                )
                            }
                        }
                    }) {
                        Text("Retry")
                    }
                }
            }

            state.prototypes.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No prototypes yet",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Click the + button to create your first prototype",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                println("📋 Rendering LazyColumn with ${state.prototypes.size} prototypes")
                Napier.d("📋 Rendering LazyColumn with ${state.prototypes.size} prototypes")
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    println("📋 LazyColumn items block executing with ${state.prototypes.size} items")
                    items(state.prototypes) { prototype ->
                        PrototypeItem(
                            prototype = prototype,
                            onPrototypeClick = { 
                                println("🔘 NAVIGATING TO: ${prototype.name} (id: ${prototype.id})")
                                Napier.d("🔘 Navigating to prototype: ${prototype.name} (id: ${prototype.id})")
                                onNavigateToPrototype(prototype.id) 
                            },
                            onFavoriteClick = {
                                scope.launch {
                                    try {
                                        supabaseService.toggleFavorite(prototype.id)
                                        // Recargar la lista para actualizar el estado de favoritos
                                        val result = supabaseService.listPrototypes()
                                        result.fold(
                                            onSuccess = { prototypes ->
                                                state = state.copy(prototypes = prototypes)
                                            },
                                            onFailure = { error ->
                                                Napier.e("Error reloading prototypes: ${error.message}")
                                            }
                                        )
                                    } catch (e: Exception) {
                                        Napier.e("Error toggling favorite", e)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PrototypeItem(
    prototype: Prototype,
    onPrototypeClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Napier.d("🎨 Rendering PrototypeItem: ${prototype.name} (id: ${prototype.id})")
    val formattedDate = remember(prototype.createdAt) {
        val instant = Instant.fromEpochMilliseconds(prototype.createdAt)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }} ${localDateTime.dayOfMonth}, ${localDateTime.year}"
    }
    
    var showDetailsDialog by remember { mutableStateOf(false) }

    Card(
        onClick = {
            println("🖱️ CARD CLICKED: ${prototype.name} (id: ${prototype.id})")
            Napier.d("🖱️ Card clicked for prototype: ${prototype.name} (id: ${prototype.id})")
            onPrototypeClick()
        },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = prototype.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                Row {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (prototype.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (prototype.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (prototype.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    IconButton(onClick = { showDetailsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Show details"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = prototype.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Created: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
    
    // Diálogo de detalles
    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Detalles del Prototipo") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailItem(label = "Nombre de la App", value = prototype.name)
                    DetailItem(label = "Idea del Usuario", value = prototype.userIdea ?: "No disponible")
                    DetailItem(label = "Descripción Validada", value = prototype.description.ifEmpty { "No disponible" })
                    DetailItem(label = "Notas de Validación", value = prototype.validationNotes ?: "No disponible")
                    DetailItem(label = "Fecha de Creación", value = formattedDate)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}
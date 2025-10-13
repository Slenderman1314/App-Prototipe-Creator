package app.prototype.creator.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.prototype.creator.data.model.Prototype

// Simple implementation of animateItemPlacement that does nothing
// In a real app, you might want to implement proper animation
internal fun Modifier.animateItemPlacement(): Modifier = this

// Sample data - Replace with your actual data source
private val samplePrototypes = listOf(
    Prototype(
        id = "1",
        name = "Prototipo 1",
        description = "Descripción del prototipo 1 con algunos detalles adicionales",
        previewUrl = "https://example.com/prototypes/1",
        isFavorite = true,
        tags = listOf("mobile", "ui")
    ),
    Prototype(
        id = "2",
        name = "Prototipo 2",
        description = "Otra descripción para el prototipo 2",
        previewUrl = "https://example.com/prototypes/2",
        isFavorite = false,
        tags = listOf("web", "dashboard")
    ),
    Prototype(
        id = "3",
        name = "Prototipo 3",
        description = "Tercer prototipo con una descripción más larga",
        previewUrl = "https://example.com/prototypes/3",
        isFavorite = true,
        tags = listOf("mobile", "ecommerce")
    )
)

// State holder for the Gallery screen
data class GalleryState(
    val prototypes: List<Prototype> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPrototype: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    onNavigateToChat: () -> Unit = {},
    onNavigateToPrototype: (String) -> Unit = {}
) {
    // State management
    var state by remember {
        mutableStateOf(
            GalleryState(prototypes = samplePrototypes)
        )
    }

    // Handle prototype item clicks
    val onPrototypeClick = { id: String ->
        onNavigateToPrototype(id)
    }

    // Handle favorite toggle
    val onToggleFavorite = { id: String ->
        state = state.copy(
            prototypes = state.prototypes.map {
                if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
            }
        )
    }

    // Handle delete action
    val onDeleteClick = { id: String ->
        state = state.copy(
            prototypes = state.prototypes.filter { it.id != id }
        )
    }

    // Scaffold with top app bar and content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Prototipos") },
                actions = {
                    // Chat button
                    IconButton(onClick = onNavigateToChat) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Ir al chat"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Implement create new prototype */ },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo prototipo")
            }
        }
    ) { padding ->
        if (state.prototypes.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay prototipos aún",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Pulsa el botón + para crear uno nuevo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        } else {
            // Prototype list
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.prototypes, key = { it.id }) { prototype ->
                    PrototypeItem(
                        prototype = prototype,
                        onItemClick = { onPrototypeClick(prototype.id) },
                        onDeleteClick = { onDeleteClick(prototype.id) },
                        onToggleFavorite = { onToggleFavorite(prototype.id) }
                    )
                }
                
                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PrototypeItem(
    prototype: Prototype,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val formattedDate = remember(prototype.updatedAt) {
        // Simple date formatting
        val daysAgo = (System.currentTimeMillis() - prototype.updatedAt) / (1000 * 60 * 60 * 24)
        when {
            daysAgo < 1 -> "Hoy"
            daysAgo == 1L -> "Ayer"
            daysAgo < 7 -> "Hace $daysAgo días"
            daysAgo < 30 -> "Hace ${daysAgo / 7} semanas"
            else -> "Hace ${daysAgo / 30} meses"
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar prototipo") },
            text = { Text("¿Estás seguro de que quieres eliminar este prototipo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteClick()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        onClick = onItemClick,
        modifier = modifier
            .fillMaxWidth()
            .animateItemPlacement(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = prototype.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Favorite button
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (prototype.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (prototype.isFavorite) "Quitar de favoritos" else "Añadir a favoritos",
                        tint = if (prototype.isFavorite) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (prototype.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = prototype.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Tags
            if (prototype.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (tag in prototype.tags.take(3)) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = tag,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            // Last modified and actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Actualizado $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
                
                // Delete button
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar prototipo",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

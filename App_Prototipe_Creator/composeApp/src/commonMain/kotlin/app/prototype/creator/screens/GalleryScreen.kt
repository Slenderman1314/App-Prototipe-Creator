package app.prototype.creator.screens
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.prototype.creator.AppSettings
import app.prototype.creator.LocalAppSettings
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.data.repository.PrototypeRepository
import app.prototype.creator.ui.components.LanguageSelector
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
private fun LanguageDropdown(
    currentLanguage: Language,
    languageRepository: LanguageRepository
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(
            onClick = { expanded = true },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currentLanguage.code.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = currentLanguage.nativeName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Language.entries.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = language.nativeName,
                            fontWeight = if (language == currentLanguage)
                                androidx.compose.ui.text.font.FontWeight.Bold
                            else
                                androidx.compose.ui.text.font.FontWeight.Normal,
                            color = if (language == currentLanguage)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        languageRepository.setLanguage(language)
                        expanded = false
                    },
                    trailingIcon = {
                        if (language == currentLanguage) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun GalleryDesktopActions(
    currentLanguage: Language,
    languageRepository: LanguageRepository,
    appSettings: AppSettings,
    onStorageSettingsClick: () -> Unit,
    onBackendSettingsClick: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val isDark = appSettings.isDarkTheme

    Button(
        onClick = onNavigateToChat,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        modifier = Modifier.padding(end = 4.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = Strings.newPrototype.localized(currentLanguage),
            style = MaterialTheme.typography.labelLarge
        )
    }

    IconButton(onClick = onStorageSettingsClick) {
        Icon(
            imageVector = Icons.Default.Storage,
            contentDescription = Strings.storageSelection.localized(currentLanguage)
        )
    }

    IconButton(onClick = onBackendSettingsClick) {
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = if (currentLanguage == Language.SPANISH) "Backend IA" else "AI Backend"
        )
    }

    LanguageDropdown(
        currentLanguage = currentLanguage,
        languageRepository = languageRepository
    )

    IconButton(onClick = { appSettings.isDarkTheme = !isDark }) {
        Icon(
            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDark) Strings.lightMode.localized(currentLanguage)
                                 else Strings.darkMode.localized(currentLanguage)
        )
    }
}

@Composable
private fun GalleryOverflowMenu(
    currentLanguage: Language,
    languageRepository: LanguageRepository,
    appSettings: AppSettings,
    onStorageSettingsClick: () -> Unit,
    onBackendSettingsClick: () -> Unit
) {
    var overflowExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    
    IconButton(onClick = { overflowExpanded = true }) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = Strings.moreOptions.localized(currentLanguage)
        )
    }
    
    DropdownMenu(
        expanded = overflowExpanded,
        onDismissRequest = { overflowExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text(Strings.storageSelection.localized(currentLanguage)) },
            onClick = {
                overflowExpanded = false
                onStorageSettingsClick()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = { Text(if (currentLanguage == Language.SPANISH) "Backend IA" else "AI Backend") },
            onClick = {
                overflowExpanded = false
                onBackendSettingsClick()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(Strings.language.localized(currentLanguage))
                    Icon(
                        imageVector = if (languageExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            onClick = { languageExpanded = !languageExpanded },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null
                )
            }
        )
        
        if (languageExpanded) {
            app.prototype.creator.data.model.Language.entries.forEach { language ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = if (language == currentLanguage) {
                                "  ${language.nativeName} ✓"
                            } else {
                                "  ${language.nativeName}"
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        languageRepository.setLanguage(language)
                        overflowExpanded = false
                    }
                )
            }
        }

        val isDark = appSettings.isDarkTheme
        DropdownMenuItem(
            text = {
                Text(
                    text = if (isDark) {
                        Strings.lightMode.localized(currentLanguage)
                    } else {
                        Strings.darkMode.localized(currentLanguage)
                    }
                )
            },
            onClick = {
                appSettings.isDarkTheme = !isDark
            },
            leadingIcon = {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = null
                )
            }
        )
    }
}

// Enum for sort order
enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST
}

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
    onNavigateToPrototype: (String) -> Unit = {},
    onStorageSettingsClick: () -> Unit = {},
    onBackendSettingsClick: () -> Unit = {}
) {
    println("📱 GalleryScreen COMPOSING")
    Napier.d("📱 GalleryScreen is being composed/recomposed")
    val prototypeRepository = org.koin.compose.koinInject<PrototypeRepository>()
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
            // Only show loading if we don't have cached prototypes
            if (initialPrototypes.isEmpty()) {
                state = state.copy(isLoading = true)
            }
            
            try {
                prototypeRepository.getPrototypes().collect { prototypes ->
                    state = state.copy(
                        isLoading = false,
                        prototypes = prototypes
                    )
                    // Notify parent about loaded prototypes
                    onPrototypesLoaded(prototypes)
                    Napier.d("✅ Prototypes loaded: ${prototypes.size}")
                }
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

    // Get app settings for theme toggle
    val appSettings = LocalAppSettings.current
    val isDarkTheme = appSettings.isDarkTheme
    
    // Get language repository
    val languageRepository = org.koin.compose.koinInject<LanguageRepository>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
    // Sync AppSettings language with LanguageRepository
    LaunchedEffect(currentLanguage) {
        appSettings.language = currentLanguage
    }
    
    // Search state
    var searchQuery by remember { mutableStateOf("") }
    
    // Sort state
    var sortOrder by remember { mutableStateOf(SortOrder.NEWEST_FIRST) }
    
    // Filter and sort prototypes
    val filteredPrototypes = remember(state.prototypes, searchQuery, sortOrder) {
        val filtered = if (searchQuery.isBlank()) {
            state.prototypes
        } else {
            state.prototypes.filter { prototype ->
                prototype.name.contains(searchQuery, ignoreCase = true) ||
                prototype.id.contains(searchQuery, ignoreCase = true)
            }
        }
        
        // Apply sorting
        when (sortOrder) {
            SortOrder.NEWEST_FIRST -> filtered.sortedByDescending { it.createdAt }
            SortOrder.OLDEST_FIRST -> filtered.sortedBy { it.createdAt }
        }
    }
    
    // Update WebView theme when it changes (Desktop only)
    LaunchedEffect(isDarkTheme) {
        try {
            // Call desktop-specific function to update WebView theme
            val updateFn = Class.forName("app.prototype.creator.ui.components.HtmlViewer_desktopKt")
                .getDeclaredMethod("updateWebViewTheme", Boolean::class.java)
            updateFn.invoke(null, isDarkTheme)
        } catch (e: Exception) {
            // Not on desktop or function not available
            Napier.d("ℹ️ WebView theme update not available: ${e.message}")
        }
    }

    val isDesktopPlatform = app.prototype.creator.utils.isDesktop

    // Main UI
    Scaffold(
        floatingActionButton = {
            if (!isDesktopPlatform) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToChat,
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null
                        )
                    },
                    text = { Text(Strings.newPrototype.localized(currentLanguage)) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(Strings.galleryTitle.localized(currentLanguage)) },
                actions = {
                    if (isDesktopPlatform) {
                        GalleryDesktopActions(
                            currentLanguage = currentLanguage,
                            languageRepository = languageRepository,
                            appSettings = appSettings,
                            onStorageSettingsClick = onStorageSettingsClick,
                            onBackendSettingsClick = onBackendSettingsClick,
                            onNavigateToChat = onNavigateToChat
                        )
                    } else {
                        GalleryOverflowMenu(
                            currentLanguage = currentLanguage,
                            languageRepository = languageRepository,
                            appSettings = appSettings,
                            onStorageSettingsClick = onStorageSettingsClick,
                            onBackendSettingsClick = onBackendSettingsClick
                        )
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
                        text = Strings.errorLoading.localized(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error ?: Strings.error.localized(currentLanguage),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(onClick = {
                        // Retry loading
                        scope.launch {
                            state = state.copy(isLoading = true, error = null)
                            try {
                                prototypeRepository.getPrototypes().collect { prototypes ->
                                    state = state.copy(
                                        isLoading = false,
                                        prototypes = prototypes
                                    )
                                    onPrototypesLoaded(prototypes)
                                }
                            } catch (e: Exception) {
                                state = state.copy(
                                    isLoading = false,
                                    error = "Error: ${e.message ?: "Error desconocido al cargar los prototipos"}"
                                )
                            }
                        }
                    }) {
                        Text(Strings.retry.localized(currentLanguage))
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = Strings.noPrototypes.localized(currentLanguage),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = Strings.createFirst.localized(currentLanguage),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToChat,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Strings.startInChat.localized(currentLanguage),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            else -> {
                println("📋 Rendering with ${state.prototypes.size} prototypes, ${filteredPrototypes.size} filtered")
                Napier.d("📋 Rendering with ${state.prototypes.size} prototypes, ${filteredPrototypes.size} filtered")
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    // Search and sort controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text(Strings.searchPrototypes.localized(currentLanguage)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = Strings.searchByNameOrId.localized(currentLanguage)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = Strings.cancel.localized(currentLanguage)
                                        )
                                    }
                                }
                            },
                            singleLine = true
                        )
                        
                        // Sort button with dropdown
                        var showSortMenu by remember { mutableStateOf(false) }
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = Strings.sortBy.localized(currentLanguage)
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(Strings.sortNewestFirst.localized(currentLanguage)) },
                                    onClick = {
                                        sortOrder = SortOrder.NEWEST_FIRST
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == SortOrder.NEWEST_FIRST) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(Strings.sortOldestFirst.localized(currentLanguage)) },
                                    onClick = {
                                        sortOrder = SortOrder.OLDEST_FIRST
                                        showSortMenu = false
                                    },
                                    leadingIcon = {
                                        if (sortOrder == SortOrder.OLDEST_FIRST) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Show filtered results or no results message
                    if (filteredPrototypes.isEmpty() && searchQuery.isNotEmpty()) {
                        // No results found
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = Strings.noResultsFound.localized(currentLanguage),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = Strings.tryDifferentSearch.localized(currentLanguage),
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        // Show filtered prototypes
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            println("📋 LazyColumn items block executing with ${filteredPrototypes.size} items")
                            items(filteredPrototypes) { prototype ->
                                PrototypeItem(
                                    prototype = prototype,
                                    currentLanguage = currentLanguage,
                                    onPrototypeClick = { 
                                println("🔘 NAVIGATING TO: ${prototype.name} (id: ${prototype.id})")
                                Napier.d("🔘 Navigating to prototype: ${prototype.name} (id: ${prototype.id})")
                                onNavigateToPrototype(prototype.id) 
                            },
                            onFavoriteClick = {
                                scope.launch {
                                    try {
                                        val updatedPrototype = prototype.copy(isFavorite = !prototype.isFavorite)
                                        prototypeRepository.updatePrototype(updatedPrototype)
                                        // Recargar la lista para actualizar el estado de favoritos
                                        prototypeRepository.getPrototypes().collect { prototypes ->
                                            state = state.copy(prototypes = prototypes)
                                        }
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
    }
}

@Composable
private fun PrototypeItem(
    prototype: Prototype,
    currentLanguage: app.prototype.creator.data.model.Language,
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
                            contentDescription = if (prototype.isFavorite) Strings.unfavorite.localized(currentLanguage) else Strings.favorite.localized(currentLanguage),
                            tint = if (prototype.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    IconButton(onClick = { showDetailsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = Strings.prototypeDetails.localized(currentLanguage)
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
                text = "${Strings.created.localized(currentLanguage)}: $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
    
    // Diálogo de detalles
    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text(Strings.prototypeDetails.localized(currentLanguage)) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val appNameLabel = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Nombre de la App" else "App Name"
                    val userIdeaLabel = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Idea del Usuario" else "User Idea"
                    val validatedDescLabel = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Descripción Validada" else "Validated Description"
                    val validationNotesLabel = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Notas de Validación" else "Validation Notes"
                    val creationDateLabel = if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Fecha de Creación" else "Creation Date"
                    
                    DetailItem(label = appNameLabel, value = prototype.name)
                    DetailItem(label = userIdeaLabel, value = prototype.userIdea ?: Strings.notAvailable.localized(currentLanguage))
                    DetailItem(label = validatedDescLabel, value = prototype.description.ifEmpty { Strings.notAvailable.localized(currentLanguage) })
                    DetailItem(label = validationNotesLabel, value = prototype.validationNotes ?: Strings.notAvailable.localized(currentLanguage))
                    DetailItem(label = creationDateLabel, value = formattedDate)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailsDialog = false }) {
                    Text(if (currentLanguage == app.prototype.creator.data.model.Language.SPANISH) "Cerrar" else "Close")
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

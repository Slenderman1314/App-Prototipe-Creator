package app.prototype.creator.ui.viewmodel

import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.repository.PrototypeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * UI State for the Gallery screen
 */
data class GalleryUiState(
    val isLoading: Boolean = false,
    val prototypes: List<Prototype> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
    val selectedTags: Set<String> = emptySet()
)

/**
 * ViewModel for the Gallery screen
 */
class GalleryViewModel(
    private val repository: PrototypeRepository
) : ViewModel() {
    private var allPrototypes: List<Prototype> = emptyList()
    
    var uiState by mutableStateOf(GalleryUiState(isLoading = true))
        private set
    
    init {
        loadPrototypes()
    }
    
    private fun loadPrototypes() {
        viewModelScope.launch {
            repository.getPrototypes()
                .catch { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                .collect { prototypes ->
                    allPrototypes = prototypes
                    updateFilteredPrototypes()
                }
        }
    }
    
    fun onSearchQueryChanged(query: String) {
        uiState = uiState.copy(searchQuery = query)
        updateFilteredPrototypes()
    }
    
    fun onTagSelected(tag: String) {
        val newSelectedTags = uiState.selectedTags.toMutableSet().apply {
            if (contains(tag)) remove(tag) else add(tag)
        }
        uiState = uiState.copy(selectedTags = newSelectedTags)
        updateFilteredPrototypes()
    }
    
    fun deletePrototype(prototypeId: String) {
        viewModelScope.launch {
            try {
                repository.deletePrototype(prototypeId)
                allPrototypes = allPrototypes.filter { it.id != prototypeId }
                updateFilteredPrototypes()
            } catch (e: Exception) {
                uiState = uiState.copy(error = e.message ?: "Failed to delete prototype")
            }
        }
    }
    
    private fun updateFilteredPrototypes() {
        val filtered = allPrototypes
            .filter { prototype ->
                val matchesSearch = prototype.name.contains(
                    uiState.searchQuery, ignoreCase = true
                ) || prototype.description.contains(
                    uiState.searchQuery, ignoreCase = true
                )
                
                val matchesTags = uiState.selectedTags.isEmpty() || 
                    uiState.selectedTags.all { tag ->
                        prototype.tags.any { it.equals(tag, ignoreCase = true) }
                    }
                
                matchesSearch && matchesTags
            }
            .sortedByDescending { it.updatedAt }
            
        uiState = uiState.copy(
            isLoading = false,
            prototypes = filtered,
            error = null
        )
    }
}

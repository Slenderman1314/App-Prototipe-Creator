package app.prototype.creator.ui.viewmodel

import app.prototype.creator.data.model.Prototype
import app.prototype.creator.data.repository.PrototypeRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class PrototypeDetailUiState(
    val isLoading: Boolean = true,
    val prototype: Prototype? = null,
    val error: String? = null
)

class PrototypeDetailViewModel(
    private val prototypeId: String,
    private val prototypeRepository: PrototypeRepository
) : ViewModel() {
    var uiState by mutableStateOf(PrototypeDetailUiState())
        private set
    
    init {
        loadPrototype()
    }
    
    private fun loadPrototype() {
        viewModelScope.launch {
            prototypeRepository.getPrototypeById(prototypeId)
                .catch { e ->
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
                .collect { prototype ->
                    uiState = uiState.copy(
                        isLoading = false,
                        prototype = prototype,
                        error = null
                    )
                }
        }
    }
    
    fun refresh() {
        uiState = uiState.copy(isLoading = true, error = null)
        loadPrototype()
    }
}

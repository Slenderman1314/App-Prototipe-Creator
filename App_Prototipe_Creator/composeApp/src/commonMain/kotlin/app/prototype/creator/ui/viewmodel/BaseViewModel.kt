package app.prototype.creator.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

@Stable
expect abstract class ViewModel() {
    val viewModelScope: CoroutineScope
    
    protected open fun onCleared()
    
    fun clear()
}

@Composable
expect inline fun <T> viewModel(
    crossinline viewModel: @Composable () -> T
): T

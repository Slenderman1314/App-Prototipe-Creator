package app.prototype.creator.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel actual constructor() {
    actual val viewModelScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    protected actual open fun onCleared() {
        viewModelScope.cancel()
    }
    
    actual fun clear() {
        onCleared()
    }
}

@Composable
actual inline fun <T> viewModel(
    crossinline viewModel: @Composable () -> T
): T = viewModel()

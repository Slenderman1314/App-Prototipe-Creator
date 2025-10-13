package app.prototype.creator.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

actual abstract class ViewModel actual constructor() {
    actual val viewModelScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    actual open fun onCleared() {}
    
    actual fun clear() {
        viewModelScope.cancel()
        onCleared()
    }
}

@Composable
actual inline fun <T> viewModel(
    crossinline viewModel: @Composable () -> T
): T {
    var result by remember { mutableStateOf<T?>(null) }
    if (result == null) {
        result = viewModel()
    }
    return result as T
}

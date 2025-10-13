package app.prototype.creator.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import kotlinx.coroutines.CoroutineScope

actual abstract class ViewModel actual constructor() : AndroidXViewModel() {
    actual val viewModelScope: CoroutineScope
        get() = androidXViewModelScope
    
    protected actual override fun onCleared() {
        super.onCleared()
    }
    
    actual fun clear() {
        onCleared()
    }
}

@Composable
actual inline fun <T> viewModel(
    crossinline viewModel: @Composable () -> T
): T = viewModel()

package app.prototype.creator.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.LocalAppSettings
import app.prototype.creator.data.i18n.Strings
import app.prototype.creator.data.i18n.localized
import app.prototype.creator.data.model.Language
import app.prototype.creator.data.repository.LanguageRepository
import app.prototype.creator.data.service.ExportService
import app.prototype.creator.data.service.SupabaseService
import app.prototype.creator.ui.components.AndroidExportButton
import app.prototype.creator.ui.components.LanguageSelector
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * Android-specific implementation of PrototypeDetailScreen with export functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrototypeDetailScreenAndroid(
    prototypeId: String,
    onBack: () -> Unit,
    version: Int = 0
) {
    Napier.d("🌐 PrototypeDetailScreenAndroid CALLED with prototypeId=$prototypeId")
    
    // Get services from Koin
    val supabaseService = org.koin.compose.koinInject<SupabaseService>()
    val exportService = org.koin.compose.koinInject<ExportService>()
    val languageRepository = org.koin.compose.koinInject<LanguageRepository>()
    
    // Sync AppSettings language with LanguageRepository
    val appSettings = LocalAppSettings.current
    
    // Get current language for UI rendering
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
    // Update HtmlViewer whenever language changes (runs on every recomposition)
    SideEffect {
        Napier.d("🌐 PrototypeDetailScreenAndroid: SideEffect - currentLanguage = $currentLanguage")
        appSettings.language = currentLanguage
    }
    
    var prototype by remember(prototypeId) { mutableStateOf<app.prototype.creator.data.model.Prototype?>(null) }
    var isLoading by remember(prototypeId) { mutableStateOf(true) }
    var errorMessage by remember(prototypeId) { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    
    // Load prototype from Supabase
    LaunchedEffect(prototypeId) {
        Napier.d("🔄 LaunchedEffect triggered for prototypeId: $prototypeId")
        prototype = null
        isLoading = true
        errorMessage = null
        
        try {
            val result = supabaseService.getPrototype(prototypeId)
            result.fold(
                onSuccess = { proto ->
                    prototype = proto
                    Napier.d("✅ Prototype loaded: ${proto.name}")
                },
                onFailure = { error ->
                    errorMessage = "Error al cargar el prototipo: ${error.message}"
                    Napier.e("❌ Error loading prototype", error)
                }
            )
        } catch (e: Exception) {
            errorMessage = "Error al cargar el prototipo: ${e.message}"
            Napier.e("❌ Exception in loadPrototype", e)
        } finally {
            isLoading = false
        }
    }
    
    Napier.d("🌐 PrototypeDetailScreenAndroid: Rendering TopAppBar with LanguageSelector and ExportButton")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prototype?.name ?: Strings.loading.localized(currentLanguage)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = Strings.back.localized(currentLanguage))
                    }
                },
                actions = {
                    Napier.d("🌐 PrototypeDetailScreenAndroid: Rendering ExportButton in actions")
                    
                    // Export button (Android-specific) - ONLY export button, no language selector
                    if (prototype != null && prototype!!.name.isNotEmpty() && !prototype!!.htmlContent.isNullOrEmpty()) {
                        AndroidExportButton(
                            htmlContent = prototype!!.htmlContent!!,
                            prototypeName = prototype!!.name,
                            currentLanguage = currentLanguage,
                            exportService = exportService
                        )
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
                            text = Strings.errorLoading.localized(currentLanguage),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: Strings.error.localized(currentLanguage),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                prototype != null && !prototype!!.htmlContent.isNullOrEmpty() -> {
                    app.prototype.creator.ui.components.HtmlViewer(
                        htmlContent = prototype!!.htmlContent!!,
                        modifier = Modifier.fillMaxSize(),
                        key = prototypeId,
                        prototypeName = prototype!!.name,
                        exportService = exportService,
                        language = currentLanguage
                    )
                }
                prototype != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = prototype!!.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = prototype!!.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${Strings.created.localized(currentLanguage)}: ${prototype!!.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

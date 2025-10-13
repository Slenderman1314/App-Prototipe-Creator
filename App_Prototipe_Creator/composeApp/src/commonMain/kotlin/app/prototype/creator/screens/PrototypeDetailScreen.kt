package app.prototype.creator.screens

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prototype.creator.components.WebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrototypeDetailScreen(
    prototypeId: String,
    onBack: () -> Unit
) {
    
    // In a real app, this would be loaded from a ViewModel/Repository
    val prototypeName = "Prototype: $prototypeId"
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(prototypeName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←") // Using text arrow as back button
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // WebView to display the prototype
            var isLoading by remember { mutableStateOf(true) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // In a real app, you would use the actual prototype URL
                val prototypeUrl = "https://example.com/prototype/$prototypeId"
                
                WebView(
                    url = prototypeUrl,
                    modifier = Modifier.fillMaxSize(),
                    onLoadingStateChanged = { isLoading = it },
                    onError = { error ->
                        // Show error message
                        println("Error loading prototype: $error")
                    }
                )
                
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { /* Open in browser */ },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Open in Browser")
                }
                
                Button(onClick = { /* Share prototype */ }) {
                    Text("Share")
                }
            }
        }
    }
}

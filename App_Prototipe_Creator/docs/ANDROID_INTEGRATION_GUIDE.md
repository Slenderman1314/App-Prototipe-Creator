# Android Export Feature - Integration Guide

## Quick Start

### 1. Update App.kt Navigation

In your `App.kt` file, update the navigation to use the Android-specific screen:

```kotlin
// In App.kt, replace the PrototypeDetail screen rendering with:
is Screen.PrototypeDetail -> {
    println("🖥️ APP.KT: Rendering PrototypeDetailScreen (Android)")
    selectedPrototypeId?.let { id ->
        key("$id-$prototypeVersion") {
            // Use Android-specific screen with export functionality
            app.prototype.creator.ui.navigation.PrototypeDetailRoute(
                prototypeId = id,
                onBack = { 
                    selectedPrototypeId = null
                    currentScreen = Screen.Gallery 
                },
                version = prototypeVersion
            )
        }
    } ?: run {
        currentScreen = Screen.Gallery
    }
}
```

### 2. Update AndroidManifest.xml

Add the following permissions and FileProvider configuration:

```xml
<!-- Add these permissions -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />

<!-- Add FileProvider inside <application> tag -->
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

### 3. Verify Dependencies

Ensure your `build.gradle.kts` includes:

```gradle
dependencies {
    // FileProvider support
    implementation("androidx.core:core:1.10.1")
    
    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Koin for dependency injection
    implementation("io.insert-koin:koin-android:3.4.0")
}
```

## Usage

### Using AndroidExportButton in TopAppBar

```kotlin
TopAppBar(
    title = { Text(prototype?.name ?: "Loading...") },
    actions = {
        // Export button (Android-specific)
        if (prototype != null) {
            AndroidExportButton(
                htmlContent = prototype!!.htmlContent,
                prototypeName = prototype!!.name,
                currentLanguage = currentLanguage
            )
        }
        
        // Language selector
        LanguageSelector()
    }
)
```

### Using AndroidExportDialog

```kotlin
var showExportDialog by remember { mutableStateOf(false) }

if (showExportDialog) {
    AlertDialog(
        onDismissRequest = { showExportDialog = false },
        title = { Text("Export Options") },
        text = {
            AndroidExportDialog(
                htmlContent = prototype!!.htmlContent,
                prototypeName = prototype!!.name,
                currentLanguage = currentLanguage,
                onDismiss = { showExportDialog = false }
            )
        },
        confirmButton = {}
    )
}
```

## Features

### Automatic File Management
- Files are automatically saved to the device's Downloads directory
- File names are sanitized and include timestamps to prevent conflicts
- Format: `{prototypeName}_{timestamp}.{extension}`

### Automatic Sharing
After exporting, the Android share dialog automatically appears, allowing users to:
- Share via email
- Save to cloud storage (Google Drive, OneDrive, etc.)
- Send via messaging apps
- Save to device storage

### Multi-language Support
The export UI automatically adapts to the current language:
- Spanish: "Exportar", "Exportar como HTML", "Exportar como PDF"
- English: "Export", "Export as HTML", "Export as PDF"

### Error Handling
All errors are logged and handled gracefully:
- File creation errors are reported to the user
- Share intent failures don't crash the app
- All operations run on background threads

## File Locations

Exported files are saved to:
```
/storage/emulated/0/Download/
```

Users can access them via:
- Android File Manager
- Downloads app
- Any file browser app

## Troubleshooting

### Export Button Not Showing
- Verify `AndroidExportButton` is added to TopAppBar actions
- Check that `prototype != null` before rendering the button
- Ensure imports are correct

### Files Not Appearing in Downloads
- Check that `WRITE_EXTERNAL_STORAGE` permission is granted
- Verify file creation is not throwing exceptions
- Check device storage space

### Share Intent Not Opening
- Verify FileProvider is configured in AndroidManifest.xml
- Check that file paths are correct
- Ensure `file_paths.xml` is in `res/xml/` directory

### Language Not Updating
- Verify `currentLanguage` is being collected from `LanguageRepository`
- Check that `SideEffect` is updating the language
- Ensure `LanguageSelector` is present in TopAppBar

## Advanced Configuration

### Custom Export Formats
To add more export formats, extend `ExportFormat` enum:

```kotlin
enum class ExportFormat(val extension: String, val mimeType: String, val displayName: String) {
    HTML("html", "text/html", "HTML"),
    PDF("pdf", "application/pdf", "PDF"),
    MARKDOWN("md", "text/markdown", "Markdown"),
    JSON("json", "application/json", "JSON")
}
```

### PDF Generation
To implement true PDF generation instead of HTML with PDF extension:

1. Add PDF library dependency:
```gradle
implementation("com.itextpdf:itext7-core:7.2.5")
```

2. Update `PlatformExporter.android.kt`:
```kotlin
actual suspend fun exportAsPdf(
    htmlContent: String,
    suggestedFileName: String
): ExportResult = withContext(Dispatchers.IO) {
    try {
        // Use iText to generate PDF from HTML
        val pdfFile = createFile(sanitizeFileName(suggestedFileName), "pdf")
        // PDF generation logic here
        ExportResult.Success(pdfFile.absolutePath)
    } catch (e: Exception) {
        ExportResult.Error(e.message ?: "Error exporting PDF")
    }
}
```

### Cloud Storage Integration
To add direct export to cloud storage:

1. Add Google Drive API dependency
2. Implement cloud upload in `PlatformExporter.android.kt`
3. Add cloud storage options to export UI

## Testing

### Manual Testing Checklist
- [ ] Export button appears in TopAppBar
- [ ] Export as HTML creates file in Downloads
- [ ] Export as PDF creates file in Downloads
- [ ] File names are properly sanitized
- [ ] Timestamps are correctly appended
- [ ] Share intent opens after export
- [ ] Language changes are reflected in export UI
- [ ] Error messages display correctly
- [ ] Permissions are requested when needed

### Automated Testing
```kotlin
@Test
fun testExportAsHtml() {
    val exporter = PlatformExporter(context)
    val result = runBlocking {
        exporter.exportAsHtml(
            htmlContent = "<html><body>Test</body></html>",
            suggestedFileName = "test"
        )
    }
    assertTrue(result is ExportResult.Success)
}
```

## Performance Considerations

- Export operations run on `Dispatchers.IO` to avoid blocking UI
- Large HTML files may take longer to process
- File sharing is handled by Android system, not the app
- Consider implementing progress indicators for large exports

## Security Considerations

- FileProvider is used for secure file sharing
- Files are stored in Downloads directory (user-accessible)
- No sensitive data should be included in exports
- Consider implementing file encryption for sensitive prototypes

## Support and Feedback

For issues or feature requests related to the export functionality:
1. Check the troubleshooting section
2. Review the logs for error messages
3. Verify all configuration steps are complete
4. Contact the development team with detailed logs

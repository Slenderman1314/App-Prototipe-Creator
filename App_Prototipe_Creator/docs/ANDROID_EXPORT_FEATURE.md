# Android Export Feature Documentation

## Overview
The Android version of the App Prototype Creator now includes a complete export feature that allows users to export prototype designs as HTML and PDF files directly from their mobile device.

## Features

### 1. Export Formats
- **HTML Export**: Exports the prototype design as a standalone HTML file that can be opened in any web browser
- **PDF Export**: Exports the prototype design in PDF format (currently saves as HTML with PDF extension - can be enhanced with a PDF library)

### 2. Export UI Components

#### AndroidExportButton
A compact button component that displays an export icon in the UI with a dropdown menu for format selection.

```kotlin
AndroidExportButton(
    htmlContent = prototypeHtmlContent,
    prototypeName = "My Prototype",
    currentLanguage = Language.SPANISH
)
```

#### AndroidExportDialog
A full-screen dialog component that provides a more detailed export interface with format selection buttons.

```kotlin
AndroidExportDialog(
    htmlContent = prototypeHtmlContent,
    prototypeName = "My Prototype",
    currentLanguage = Language.SPANISH,
    onDismiss = { /* Handle dismiss */ }
)
```

## Implementation Details

### PlatformExporter.android.kt
The Android-specific implementation of the `PlatformExporter` interface that handles:
- File creation in the Downloads directory
- File name sanitization
- Automatic file sharing via Android's share intent
- Error handling and logging

### Key Features
1. **Automatic File Naming**: Files are automatically named with timestamps to avoid conflicts
   - Format: `{prototypeName}_{timestamp}.{extension}`
   - Example: `MyApp_20251022_201500.html`

2. **File Storage**: Files are saved to the device's Downloads directory
   - Path: `/storage/emulated/0/Download/`
   - Accessible via file manager

3. **Automatic Sharing**: After export, the system share dialog appears
   - Users can share via email, messaging apps, cloud storage, etc.
   - Users can also save directly to their device

4. **Permissions**: Required permissions are:
   - `WRITE_EXTERNAL_STORAGE`: To write files to Downloads
   - `READ_EXTERNAL_STORAGE`: To read exported files
   - `INTERNET`: For web content loading

### FileProvider Configuration
The `file_paths.xml` configuration enables secure file sharing:
- External Downloads directory is accessible
- Files can be shared via FileProvider URI

## Usage in PrototypeDetailScreen

To integrate the export feature into the prototype detail screen:

```kotlin
@Composable
fun PrototypeDetailScreen(
    prototypeId: String,
    onBack: () -> Unit
) {
    val exportService = koinInject<ExportService>()
    val currentLanguage by languageRepository.currentLanguage.collectAsState()
    
    // In TopAppBar actions:
    actions = {
        // Add export button
        AndroidExportButton(
            htmlContent = htmlContent,
            prototypeName = prototypeName,
            currentLanguage = currentLanguage
        )
    }
}
```

## Supported Languages
- **Spanish (Español)**: "Exportar", "Exportar como HTML", "Exportar como PDF"
- **English**: "Export", "Export as HTML", "Export as PDF"

## Error Handling
The export feature includes comprehensive error handling:
- File creation errors are logged and reported to the user
- Share intent errors are handled gracefully
- All operations are performed on background threads using Coroutines

## Future Enhancements

### 1. PDF Generation
Currently, PDF export saves as HTML with a PDF extension. To implement true PDF generation:
- Add a PDF library dependency (e.g., iText, PDFBox, or Compose PDF)
- Implement PDF rendering in `PlatformExporter.android.kt`

### 2. Advanced Export Options
- Custom file naming
- Export to cloud storage (Google Drive, OneDrive, etc.)
- Email export directly
- QR code generation for sharing

### 3. Export History
- Track exported files
- Quick re-export of previous designs
- Export statistics

## Testing

### Manual Testing Checklist
- [ ] Export as HTML works and creates file in Downloads
- [ ] Export as PDF works and creates file in Downloads
- [ ] File names are properly sanitized
- [ ] Timestamps are correctly appended
- [ ] Share intent opens correctly
- [ ] Language changes are reflected in UI
- [ ] Error messages display correctly
- [ ] Permissions are requested when needed

### Automated Testing
Unit tests should cover:
- File name sanitization
- File creation in correct directory
- Export result handling
- Error scenarios

## Troubleshooting

### Issue: Files not appearing in Downloads
**Solution**: Check that `WRITE_EXTERNAL_STORAGE` permission is granted in app settings

### Issue: Share intent not opening
**Solution**: Ensure FileProvider is correctly configured in AndroidManifest.xml

### Issue: Export button not visible
**Solution**: Verify that `AndroidExportButton` is added to the TopAppBar actions

## Dependencies
- `androidx.core:core`: For FileProvider
- `kotlinx.coroutines`: For async operations
- `io.github.aakira:napier`: For logging

## Notes
- Export operations are performed on background threads to avoid UI blocking
- All file paths use the Downloads directory for easy user access
- The feature respects the current language setting for UI labels

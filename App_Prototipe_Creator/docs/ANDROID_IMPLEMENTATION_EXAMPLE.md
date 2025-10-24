# Android Export Feature - Implementation Example

## Complete Integration Example

This document shows a complete example of how to integrate the Android export feature into your existing App.kt navigation.

## Before Integration

Your current App.kt likely has:

```kotlin
@Composable
private fun MainAppContent(
    appSettings: AppSettings,
    prototypeRepository: PrototypeRepository,
    chatRepository: ChatRepository
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Gallery) }
    var selectedPrototypeId by remember { mutableStateOf<String?>(null) }
    var prototypeVersion by remember { mutableStateOf(0) }
    var cachedPrototypes by remember { mutableStateOf<List<Prototype>>(emptyList()) }
    
    CompositionLocalProvider(
        LocalAppSettings provides appSettings
    ) {
        when (currentScreen) {
            is Screen.Gallery -> {
                GalleryScreen(
                    initialPrototypes = cachedPrototypes,
                    onPrototypesLoaded = { prototypes ->
                        cachedPrototypes = prototypes
                    },
                    onNavigateToChat = { currentScreen = Screen.Chat },
                    onNavigateToPrototype = { prototypeId ->
                        selectedPrototypeId = prototypeId
                        prototypeVersion++
                        currentScreen = Screen.PrototypeDetail
                    }
                )
            }
            is Screen.Chat -> {
                ChatScreen(
                    onBack = { currentScreen = Screen.Gallery }
                )
            }
            is Screen.PrototypeDetail -> {
                selectedPrototypeId?.let { id ->
                    key("$id-$prototypeVersion") {
                        // OLD: Using common PrototypeDetailScreen
                        // PrototypeDetailScreen(
                        //     prototypeId = id,
                        //     onBack = { 
                        //         selectedPrototypeId = null
                        //         currentScreen = Screen.Gallery 
                        //     }
                        // )
                    }
                } ?: run {
                    currentScreen = Screen.Gallery
                }
            }
        }
    }
}
```

## After Integration

Update the `is Screen.PrototypeDetail` branch:

```kotlin
is Screen.PrototypeDetail -> {
    selectedPrototypeId?.let { id ->
        key("$id-$prototypeVersion") {
            // NEW: Using Android-specific screen with export functionality
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

## AndroidManifest.xml Updates

### Add Permissions

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <!-- ADD THESE LINES -->
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <!-- END ADD -->

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@android:style/Theme.Material.Light.NoActionBar">
        
        <activity
            android:exported="true"
            android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- ADD THIS PROVIDER -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        <!-- END ADD -->
        
    </application>

</manifest>
```

## build.gradle.kts Updates

Ensure these dependencies are present:

```gradle
dependencies {
    // ... existing dependencies ...
    
    // FileProvider support (for export functionality)
    implementation("androidx.core:core:1.10.1")
    
    // Coroutines for async operations
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Koin for dependency injection
    implementation("io.insert-koin:koin-android:3.4.0")
    
    // ... rest of dependencies ...
}
```

## File Structure After Integration

```
composeApp/
├── src/
│   ├── androidMain/
│   │   ├── kotlin/
│   │   │   └── app/prototype/creator/
│   │   │       ├── data/service/
│   │   │       │   └── PlatformExporter.android.kt          ✨ NEW
│   │   │       ├── screens/
│   │   │       │   └── PrototypeDetailScreen.android.kt     ✨ NEW
│   │   │       └── ui/
│   │   │           ├── components/
│   │   │           │   └── ExportButton.android.kt          ✨ NEW
│   │   │           └── navigation/
│   │   │               └── AndroidNavigation.kt             ✨ NEW
│   │   ├── res/
│   │   │   └── xml/
│   │   │       └── file_paths.xml                           ✨ NEW
│   │   └── AndroidManifest.xml                              ✏️ MODIFIED
│   └── commonMain/
│       └── kotlin/
│           └── app/prototype/creator/
│               ├── screens/
│               │   └── PrototypeDetailScreen.kt             (unchanged)
│               └── data/service/
│                   └── ExportService.kt                     (unchanged)
├── build.gradle.kts                                         ✏️ MODIFIED
└── docs/
    ├── ANDROID_EXPORT_FEATURE.md                            ✨ NEW
    ├── ANDROID_INTEGRATION_GUIDE.md                         ✨ NEW
    ├── ANDROID_EXPORT_SUMMARY.md                            ✨ NEW
    └── ANDROID_IMPLEMENTATION_EXAMPLE.md                    ✨ NEW (this file)
```

## Step-by-Step Integration Checklist

- [ ] **Step 1**: Copy all new Android files to their respective directories
- [ ] **Step 2**: Update `App.kt` navigation to use `PrototypeDetailRoute`
- [ ] **Step 3**: Add permissions to `AndroidManifest.xml`
- [ ] **Step 4**: Add FileProvider to `AndroidManifest.xml`
- [ ] **Step 5**: Verify dependencies in `build.gradle.kts`
- [ ] **Step 6**: Create `res/xml/file_paths.xml` if it doesn't exist
- [ ] **Step 7**: Rebuild the project
- [ ] **Step 8**: Test on Android device
- [ ] **Step 9**: Verify export functionality works
- [ ] **Step 10**: Check logs for any errors

## Testing the Integration

### 1. Run the App
```bash
./gradlew installDebug
```

### 2. Navigate to a Prototype
- Open the app
- Click on any prototype in the gallery

### 3. Test Export Button
- Look for the download icon in the TopAppBar
- Click it to see export options
- Select "Export as HTML" or "Export as PDF"

### 4. Verify File Creation
- Open Android File Manager
- Navigate to Downloads folder
- Verify the exported file is there

### 5. Check Logs
```bash
adb logcat | grep "🌐\|📤\|✅\|❌"
```

## Common Issues and Solutions

### Issue: Export button not visible
**Solution**: 
- Verify `AndroidExportButton` is in TopAppBar actions
- Check that `prototype != null` before rendering
- Ensure imports are correct

### Issue: Files not appearing in Downloads
**Solution**:
- Check permissions are granted in app settings
- Verify `WRITE_EXTERNAL_STORAGE` permission is in manifest
- Check device storage space

### Issue: Share intent not opening
**Solution**:
- Verify FileProvider is configured in manifest
- Check `file_paths.xml` exists in `res/xml/`
- Ensure file creation succeeded

### Issue: Language not updating
**Solution**:
- Verify `LanguageSelector` is in TopAppBar
- Check `LanguageRepository` is injected correctly
- Ensure `currentLanguage` is collected from StateFlow

## Performance Optimization Tips

1. **Lazy Load Export Components**
   ```kotlin
   if (prototype != null) {
       AndroidExportButton(...)
   }
   ```

2. **Use Background Threads**
   - All export operations already use `Dispatchers.IO`

3. **Cache Exported Files**
   - Consider implementing file caching for repeated exports

4. **Progress Indicators**
   - Add progress bar for large file exports

## Security Considerations

1. **FileProvider**: Already implemented for secure file sharing
2. **Permissions**: Only request necessary permissions
3. **File Storage**: Downloads directory is user-accessible
4. **Data Privacy**: Don't include sensitive data in exports

## Next Steps

1. Follow the integration checklist above
2. Test the feature on Android device
3. Monitor logs for any errors
4. Refer to `ANDROID_INTEGRATION_GUIDE.md` for advanced configuration
5. Contact support if issues persist

## Support Resources

- **ANDROID_EXPORT_FEATURE.md**: Feature documentation
- **ANDROID_INTEGRATION_GUIDE.md**: Detailed integration guide
- **ANDROID_EXPORT_SUMMARY.md**: Implementation summary
- **Logs**: Check Logcat for debugging information

---

**Implementation Status**: ✅ Ready for Integration

All components are implemented and documented. Follow the steps above to integrate the Android export feature into your application.

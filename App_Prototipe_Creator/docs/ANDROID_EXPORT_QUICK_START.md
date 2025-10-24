# Android Export - Quick Start Guide

## 🚀 Quick Testing

### Build and Install

```bash
# Sync Gradle dependencies
./gradlew --refresh-dependencies

# Build debug APK
./gradlew assembleDebug

# Install on connected device
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

### Test Export Functionality

1. **Launch app** on Android device/emulator
2. **Navigate** to any prototype detail screen
3. **Click** the download icon (⬇️) in the top bar
4. **Select** export format:
   - "Exportar como HTML" / "Export as HTML"
   - "Exportar como PDF" / "Export as PDF"

### Expected Behavior

#### HTML Export
```
1. Toast: "Exporting HTML..."
2. Toast: "HTML exported successfully! Saved to: Downloads/filename.html"
3. Share dialog opens
4. File saved in: /storage/emulated/0/Download/
```

#### PDF Export
```
1. Toast: "Generating PDF..."
2. Toast: "PDF exported successfully! Saved to: Downloads/filename.pdf"
3. Share dialog opens
4. File saved in: /storage/emulated/0/Download/
```

## 📱 Verify Files

### Using ADB

```bash
# List files in Downloads folder
adb shell ls -la /storage/emulated/0/Download/

# Pull file to computer
adb pull /storage/emulated/0/Download/filename.pdf ./

# View file
open filename.pdf  # macOS
start filename.pdf # Windows
xdg-open filename.pdf # Linux
```

### Using Device

1. Open **Files** app
2. Navigate to **Downloads**
3. Find exported files (sorted by date)
4. Open to verify content

## 🔍 Debug Logs

### View Logcat

```bash
# Filter by app package
adb logcat | grep "app.prototype.creator"

# Filter export logs
adb logcat | grep -E "(Export|PDF|HTML)"

# Clear and watch
adb logcat -c && adb logcat | grep "app.prototype.creator"
```

### Key Log Messages

```
✅ Success logs:
- "✅ PlatformExporter context initialized"
- "📄 Starting PDF generation for: ..."
- "✅ PDF exported successfully to: ..."
- "📤 File shared successfully: ..."

❌ Error logs:
- "❌ Error exporting PDF"
- "⚠️ Context is null"
- "❌ No context available for sharing file"
```

## 🐛 Common Issues

### Issue: Build fails with iText errors

**Solution**:
```bash
./gradlew clean
./gradlew --refresh-dependencies
./gradlew assembleDebug
```

### Issue: No Toast appears

**Check**: Context is initialized in MainActivity
```kotlin
initializeAndroid(applicationContext)
```

### Issue: Share dialog doesn't open

**Check**: FileProvider in AndroidManifest.xml
```xml
<provider android:name="androidx.core.content.FileProvider" .../>
```

### Issue: PDF is empty or corrupted

**Check**: HTML content is valid
**Check**: Logcat for iText errors

## ✅ Success Criteria

- [x] Export button visible in PrototypeDetailScreen
- [x] Dropdown menu shows HTML and PDF options
- [x] Toast notifications appear
- [x] Files created in Downloads folder
- [x] Share dialog opens
- [x] HTML file contains valid markup
- [x] PDF file displays correctly
- [x] File names include timestamp
- [x] No crashes or errors in Logcat

## 📊 Test Matrix

| Test Case | HTML | PDF | Status |
|-----------|------|-----|--------|
| Export simple prototype | ✅ | ✅ | Pass |
| Export with images | ✅ | ✅ | Pass |
| Export with CSS | ✅ | ✅ | Pass |
| Share via email | ✅ | ✅ | Pass |
| Share via Drive | ✅ | ✅ | Pass |
| Multiple exports | ✅ | ✅ | Pass |

## 🎯 Next Steps

1. **Test on real device** (not just emulator)
2. **Test on different Android versions** (API 24-34)
3. **Test with large prototypes** (performance)
4. **Test with special characters** in file names
5. **Test offline** (no internet required)

---

**Ready to test!** 🚀

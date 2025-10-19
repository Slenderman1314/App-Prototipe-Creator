# JavaFX Media Module Fix

## Issue
The application was crashing when trying to render HTML content with the following error:
```
java.lang.NoClassDefFoundError: com/sun/media/jfxmedia/events/PlayerStateListener
```

## Root Cause
The JavaFX WebView component requires the `javafx-media` module to function properly, even when not explicitly using media features. This module was missing from the project dependencies.

## Solution
Added the `javafx-media` module to the project in two places:

### 1. Gradle Dependencies (build.gradle.kts)
Added the JavaFX media dependency for the desktop platform:

```kotlin
implementation("org.openjfx:javafx-media:$javaFxVersion:$platform")
```

This was added alongside the other JavaFX modules:
- javafx-controls
- javafx-web
- javafx-swing
- javafx-graphics
- javafx-base

### 2. Native Distribution Modules (build.gradle.kts)
Updated the modules list in the native distributions configuration:

```kotlin
modules("java.instrument", "jdk.unsupported", "javafx.controls", "javafx.web", "javafx.swing", "javafx.media")
```

## Files Modified
- `composeApp/build.gradle.kts` (lines 225 and 252)

## Testing
After applying this fix:
1. Clean the project: `./gradlew.bat clean`
2. Build the desktop JAR: `./gradlew.bat :composeApp:desktopJar`
3. Run the application: `./gradlew.bat :composeApp:run`

The application should now render HTML content without the NoClassDefFoundError.

## Date
2025-10-15

#  App Prototype Creator

> A cross-platform application that transforms ideas into detailed application prototypes using AI, with support for interactive chat and prototype visualization.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

##  Description

App Prototype Creator is a tool that uses artificial intelligence to convert conceptual ideas into functional and detailed application prototypes. Built with Kotlin Multiplatform and Compose Multiplatform, it offers a seamless experience on both Android and desktop, allowing developers and designers to visualize and export their concepts efficiently.

##  Key Features

-  **Multi-Provider AI Chat**: Support for OpenAI GPT-4o, Anthropic Claude 3.5 Sonnet, and Google Gemini 1.5 Flash
-  **Prototype Generation**: Automatic creation of interactive prototypes through natural conversation
-  **Prototype Gallery**: View, search, and manage all your created prototypes
-  **HTML Visualization**: Rendering with JavaFX WebView (Desktop) and Android WebView
-  **Export System**: Export to interactive HTML and static PDF
  - Desktop: Native dialogs with JFileChooser
  - Android: Direct save to Downloads
-  **Cross-Platform**: Android 8.0+ and Desktop (Windows, macOS, Linux) with ~80% shared code
-  **Light/Dark Theme**: Dynamic switching with WebView synchronization
-  **Internationalization**: Spanish and English with real-time switching
-  **Local Persistence**: SQLDelight for cross-platform database
-  **Secure Storage**: 
  - Desktop: Java Preferences API
  - Android: EncryptedSharedPreferences with AES256-GCM
-  **Security**: API keys stored locally only, FLAG_SECURE on Android

##  Architecture

### Tech Stack

#### Frontend
- **Kotlin Multiplatform**: Code sharing across platforms (~80%)
- **Compose Multiplatform**: Modern declarative UI framework
- **Material Design 3**: Modern design system
- **JavaFX WebView**: HTML rendering on Desktop
- **Android WebView**: HTML rendering on Android

#### APIs & Services
- **OpenAI GPT-4o**: Main model for prototype generation
- **Anthropic Claude 3.5 Sonnet**: Premium alternative with better instruction following
- **Google Gemini 1.5 Flash**: Economic and fast option
- **Ktor Client**: Cross-platform HTTP client (CIO on Desktop, OkHttp on Android)

#### Persistence
- **SQLDelight**: Cross-platform local database
- **StoragePreferences**: Secure storage for API keys
  - Desktop: Java Preferences API
  - Android: EncryptedSharedPreferences

#### Utilities
- **Koin**: Dependency injection
- **Napier**: Cross-platform logging
- **iText 7 html2pdf**: PDF generation from HTML
- **Kotlinx Coroutines**: Asynchronous programming
- **StateFlow**: Reactive state management

### Screen Structure

```
┌─────────────────┐
│  GalleryScreen  │ ◄─── Initial screen
└────────┬────────┘
         │
         ├──► ChatScreen ────► onOpenPrototype()
         │                              │
         └───────────────────────────────┴──► PrototypeDetailScreen
```

## 🔒 API Keys Security

**⚠️ IMPORTANT**: This application requires you to configure your own AI provider API keys.

### **API Keys Setup**

1. **Get an API key** from at least one of these providers:
   - [OpenAI](https://platform.openai.com/api-keys) - GPT-4, GPT-4o
   - [Anthropic](https://console.anthropic.com/settings/keys) - Claude 3.5
   - [Google](https://aistudio.google.com/app/apikey) - Gemini (Free plan available)

2. **Configure the key in the app**:
   - Open the application
   - Go to ChatScreen → Menu (⋮) → "Configure API Keys"
   - Enter your API key
   - Select the provider from the menu

3. **⚠️ READ THE SECURITY GUIDE**: [API_KEYS_SECURITY.md](./API_KEYS_SECURITY.md)

### **Protecting Your Keys**

-  Keys are stored **only on your device** (encrypted on Android)
-  **Direct HTTPS** communication with the provider
- **No intermediate backend** - your keys are never sent to our servers
- **Set spending limits** on your provider account
- **Never share** your API keys

##  Prerequisites

- **JDK 17** or higher
- **Android Studio** Hedgehog or higher (for Android development)
- **IntelliJ IDEA** 2023.2+ (recommended for Desktop development)
- **Gradle 8.0+**
- **API Key** from at least one of these providers:
  - [OpenAI](https://platform.openai.com/api-keys)
  - [Anthropic](https://console.anthropic.com/settings/keys)
  - [Google AI Studio](https://aistudio.google.com/app/apikey)

##  Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Configure API Keys

**API keys are configured from the application interface:**

1. Run the application (Desktop or Android)
2. Go to **ChatScreen** → Menu (⋮) → **"Configure API Keys"**
3. Enter your API key from your preferred provider
4. Select the provider from the dropdown menu
5. Save the configuration

**Optional - Environment variables for development:**

You can create a `.env` file in the project root to automatically load API keys on Desktop:

```env
OPENAI_API_KEY=your_openai_key
ANTHROPIC_API_KEY=your_anthropic_key
GOOGLE_API_KEY=your_google_key
```

> **Note**: The `.env` file is optional and only works on Desktop. On Android you must always configure keys from the UI.

### 3. Build & Run

####  Android

**On macOS/Linux:**
```bash
./gradlew :composeApp:assembleDebug
```

**On Windows:**
```bash
.\gradlew.bat :composeApp:assembleDebug
```

To install directly on a connected device:
```bash
./gradlew :composeApp:installDebug
```

####  Desktop (JVM)

**On macOS/Linux:**
```bash
./gradlew :composeApp:run
```

**On Windows:**
```bash
.\gradlew.bat :composeApp:run
```

##  Project Structure

```
App-Prototipe-Creator/
├── composeApp/
│   └── src/
│       ├── commonMain/kotlin/
│       │   └── app/prototype/creator/
│       │       ├── App.kt                    # Main entry point
│       │       ├── screens/
│       │       │   ├── GalleryScreen.kt     # Prototype gallery
│       │       │   ├── ChatScreen.kt        # AI chat
│       │       │   └── PrototypeDetail.kt   # Prototype details
│       │       ├── ui/
│       │       │   ├── theme/               # Themes and styles
│       │       │   └── navigation/          # Navigation
│       │       ├── data/
│       │       │   ├── model/               # Data models
│       │       │   ├── repository/          # Repositories
│       │       │   ├── service/             # Services
│       │       │   └── i18n/                # Internationalization
│       │       └── di/                      # Dependency injection
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── MainActivity.kt
│       └── jvmMain/
│           └── kotlin/
│               └── Main.kt                   # Desktop entry point
├── .env                                      # Environment variables (do not commit)
├── build.gradle.kts
└── README.md
```

##  Usage Guide

### Workflow

1. **Start in Gallery**
  - When opening the app, you'll see the gallery with all your saved prototypes
  - You can navigate between prototypes or create a new one

2. **Create New Prototype**
  - Access **ChatScreen** from the gallery
  - Describe your application idea conversationally
  - The AI will process your description and generate a detailed prototype

3. **View and Export**
  - Select any prototype from the gallery
  - View all details in **PrototypeDetailScreen**
  - Export the prototype in your desired format

### System Features

- **State Management**: Uses `remember` and `mutableStateOf` to maintain state between navigations
- **Prototype Cache**: Gallery maintains prototypes in cache for better performance
- **Version System**: Force view recreation when necessary
- **Type-Safe Navigation**: Navigation system with typed routes

##  Key Components

### AppSettings
Manages global application settings:
- **Theme**: Light/dark mode
- **Language**: Multilingual system with Spanish as default
- **Persistent Configuration**: Preferences are maintained between sessions

### Repositories
- **PrototypeRepository**: Prototype management (CRUD) with SQLDelight
- **ChatRepository**: AI conversation handling
- **LanguageRepository**: Language and translation management with reactive StateFlow

### Services
- **MultiProviderAIService**: Integration with OpenAI, Anthropic, and Google Gemini
- **ExportService**: Export to HTML and PDF
  - **PlatformExporter**: Platform-specific implementations
- **StoragePreferences**: Secure storage for configuration and API keys

##  Internationalization

The project includes a complete i18n system with support for multiple languages:

```kotlin
// Using localized strings
Text(Strings.initializingApp.localized(currentLanguage))
```

Default language: **Spanish (es)**

##  Error Handling

The application includes a robust error handling system:

- **LoadingScreen**: Loading screen during initialization
- **ErrorScreen**: Error screen with retry option
- **Logging**: Logging system with Napier for debugging
- **Service Validation**: Verification that all services are properly initialized

##  Security

### API Keys Storage
- **Desktop**: Java Preferences API (OS-level protection)
  - Windows: Windows Registry
  - macOS: ~/Library/Preferences
  - Linux: ~/.java/.userPrefs
- **Android**: EncryptedSharedPreferences with AES256-GCM
  - MasterKey in Android Keystore
  - Hardware-backed when available

### Screen Protection (Android)
- **FLAG_SECURE**: Prevents screenshots and screen recording
- **setRecentsScreenshotEnabled(false)**: Hides content in recents (Android 13+)
- **TaskDescription**: Black screen in recents

### Communication
- **Direct HTTPS** with AI providers
- **No intermediate backend**: API keys are never sent to our servers
- Keys are stored **only on your device**

##  Advanced Technical Features

- **Clean Architecture**: Separation into UI, Data, and Domain layers
- **Dependency Injection**: Koin with platform-specific modules
- **Expect/Actual Pattern**: Platform-specific code with common interfaces
- **State Management**: StateFlow for reactive state, MutableState for UI
- **Coroutines**: Asynchronous programming with appropriate Dispatchers
- **Cross-Platform WebView**: 
  - Desktop: JavaFX WebView with separate windows
  - Android: Android WebView with inline rendering
- **Cross-Platform Export**:
  - Desktop: JFileChooser with native dialogs
  - Android: Direct save to Downloads with timestamps
- **Optimized System Prompt**: 195 lines of instructions to avoid loops and improve quality

##  License

This project is under the MIT License.

##  Author

**Slenderman1314**
- GitHub: [@Slenderman1314](https://github.com/Slenderman1314)

##  Resources & Documentation

### Project Documentation
- [Complete Technical Documentation](./Docs/App-Prototipe-Creator-Docs/)
  - Architecture and layers
  - Screen guides
  - AI APIs integration
  - Export system
  - Platforms (Desktop, Android, future)
  - i18n system
  - Changelog

### Technologies
- [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)
- [Ktor](https://ktor.io/)
- [iText 7](https://itextpdf.com/)
- [Material Design 3](https://m3.material.io/)

### AI APIs
- [OpenAI Platform](https://platform.openai.com/docs)
- [Anthropic Claude](https://docs.anthropic.com/)
- [Google Gemini](https://ai.google.dev/)

##  Troubleshooting

### App doesn't start on Desktop
- Make sure you have JDK 17 or higher
- Verify that JavaFX is available (included in JDK)
- Check console logs with `Napier`
- Clean and rebuild: `./gradlew clean build`

### WebView doesn't render prototypes
- **Desktop**: Verify that JavaFX WebView is working
- **Android**: Make sure JavaScript is enabled
- Check logs for rendering errors

### Export doesn't work (Android)
- Verify that `PlatformExporter.context` is initialized
- Check storage permissions (Android 12 and earlier)
- Verify that Downloads directory is accessible

### API Key doesn't work
- Verify that the key is valid in the provider console
- Make sure you've selected the correct provider
- Check that you have available credits/quota
- Review logs to see the specific API error

### Build issues
```bash
# Clean the project
./gradlew clean

# Rebuild
./gradlew build
```

##  Report Issues

If you find any bugs or have suggestions:
1. Open an [issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
2. Describe the problem in detail
3. Include logs if possible
4. Specify the platform (Android/Desktop)

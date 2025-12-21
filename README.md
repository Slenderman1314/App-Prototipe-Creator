#  App Prototype Creator

> A cross-platform application that transforms ideas into detailed application prototypes using AI, with support for interactive chat and prototype visualization.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green)
![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase)

##  Description

App Prototype Creator is a tool that uses artificial intelligence to convert conceptual ideas into functional and detailed application prototypes. Built with Kotlin Multiplatform and Compose, it provides a seamless experience across Android and Desktop platforms.

##  Main Features

-  **Chat with AI**: Conversational interface to describe your application idea
-  **Prototype Gallery**: View all your created prototypes in an organized gallery
-  **Detailed View**: Explore each prototype with complete details and export capabilities
-  **Cross-Platform**: Works on Android and Desktop (JVM) with shared code
-  **Customizable Theme**: Support for light and dark themes
-  **Multi-Language**: Integrated internationalization system (i18n)
-  **Cloud Persistence**: Integration with Supabase for storage
-  **Export**: Ability to export generated prototypes

##  Architecture

### Technology Stack

- **Kotlin Multiplatform**: Code sharing between platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **Supabase**: Backend as a Service for storage and authentication
- **Koin**: Dependency injection
- **Napier**: Cross-platform logging
- **Material Design 3**: Modern design system

### Screen Structure

```
┌─────────────────┐
│  GalleryScreen  │ ◄─── Initial screen
└────────┬────────┘
         │
         ├──► ChatScreen ────► onOpenPrototype()
         │                             │
         └─────────────────────────────┴──► PrototypeDetailScreen
```

##  Prerequisites

- **JDK 17** or higher
- **Android Studio** Arctic Fox or higher (for Android development)
- **IntelliJ IDEA** (recommended for Desktop development)
- **Gradle 8.0+**
- **Supabase** account (for full functionality)

##  Installation and Configuration

### 1. Clone the Repository

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key
N8N_WEBHOOK_PATH=n8n_webhook_path
...
```

> **Note**: The `.env` file must be in the project root. The application automatically searches in `.env`, `../.env` or `../../.env`

### 3. Build and Run

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
│       │       │   ├── service/             # Services (Supabase)
│       │       │   └── i18n/                # Internationalization
│       │       └── di/                      # Dependency injection
│       ├── androidMain/
│       │   └── kotlin/
│       │       └── MainActivity.kt
│       └── jvmMain/
│           └── kotlin/
│               └── Main.kt                   # Desktop entry point
├── .env                                      # Environment variables (do not commit to git)
├── build.gradle.kts
└── README.md
```

##  Usage Guide

### Workflow

1. **Gallery Start**
   - When you open the app, you'll see the gallery with all your saved prototypes
   - You can navigate between prototypes or create a new one

2. **Create New Prototype**
   - Access the **ChatScreen** from the gallery
   - Describe your application idea conversationally
   - AI will process your description and generate a detailed prototype

3. **View and Export**
   - Select any prototype from the gallery
   - View all details in **PrototypeDetailScreen**
   - Export the prototype in your desired format

### System Features

- **State Management**: Uses `remember` and `mutableStateOf` to maintain state between navigations
- **Prototype Cache**: The gallery keeps prototypes cached for better performance
- **Version System**: Force recreation of views when necessary
- **Type-Safe Navigation**: Navigation system with typed routes

##  Key Components

### AppSettings
Manages global application settings:
- **Theme**: Light/dark mode
- **Language**: Multi-language system with Spanish as default support
- **Persistent Configuration**: Preferences are maintained between sessions

### Repositories
- **PrototypeRepository**: Prototype management (CRUD)
- **ChatRepository**: Handling conversations with AI
- **LanguageRepository**: Language and translation management

### Services
- **SupabaseService**: Connection to Supabase backend for persistence

##  Internationalization

The project includes a complete i18n system with support for multiple languages:

```kotlin
// Usage of localized strings
Text(Strings.initializingApp.localized(currentLanguage))
```

Default language: **Spanish (es)**

##  Error Handling

The application includes a robust error handling system:

- **LoadingScreen**: Loading screen during initialization
- **ErrorScreen**: Error screen with retry option
- **Logging**: Logging system with Napier for debugging
- **Service Validation**: Verification that all services are initialized correctly

##  Security

- API keys are managed through environment variables
- No credentials are included in source code
- The `.env` file is in `.gitignore`

##  Advanced Technical Features

- **Dependency Injection**: Using Koin for clean and testable code
- **Composition**: Use of `CompositionLocalProvider` for global state
- **Lazy Initialization**: Components are initialized only when needed
- **State Management**: Reactive state management with StateFlow and MutableState
- **Error Boundaries**: Graceful error handling throughout the app

##  License

This project is under the MIT License.

##  Author

**Slenderman1314**
- GitHub: [@Slenderman1314](https://github.com/Slenderman1314)

##  Resources and Documentation

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Supabase Documentation](https://supabase.com/docs)
- [Koin Documentation](https://insert-koin.io/)
- [Material Design 3](https://m3.material.io/)

##  Troubleshooting

### App doesn't start on Desktop
- Verify that the `.env` file is in the correct location
- Check the console logs with `Napier`
- Make sure you have Java 17 or higher

### Service initialization errors
- Confirm that environment variables are configured correctly
- Verify connectivity with Supabase
- Check logs to identify which service fails

### Build problems
```bash
# Clean the project
./gradlew clean

# Rebuild
./gradlew build
```

##  Report Issues

If you encounter any bugs or have suggestions:
1. Open an [issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
2. Describe the problem in detail
3. Include logs if possible
4. Specify the platform (Android/Desktop)
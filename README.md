# App Prototype Creator

> A cross-platform application that transforms ideas into detailed application prototypes using AI, with support for interactive chat and prototype visualization.

[![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green)](https://kotlinlang.org/docs/multiplatform.html)
[![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase)](https://supabase.com)

## Description

App Prototype Creator is a tool that uses artificial intelligence to convert conceptual ideas into functional and detailed application prototypes. Built with Kotlin Multiplatform and Compose Multiplatform, it offers a smooth experience on both Android and Desktop, allowing developers and designers to efficiently visualize and export their concepts.

## Main Features

- **AI Chat**: Conversational interface to describe your app idea
- **Prototype Gallery**: View all your created prototypes in an organized gallery
- **Detail View**: Explore each prototype with full details and export options
- **Multiplatform**: Works on Android and Desktop (JVM) with shared code
- **Customizable Theme**: Light and dark mode support
- **Multilingual**: Built-in internationalization system (i18n)
- **Cloud Persistence**: Supabase integration for storage
- **Export**: Ability to export generated prototypes

## Architecture

### Tech Stack

- **Kotlin Multiplatform**: Code sharing across platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **Supabase**: Backend as a Service for storage and authentication
- **Koin**: Dependency injection
- **Napier**: Multiplatform logging
- **Material Design 3**: Modern design system

### Screen Structure

```
┌─────────────────┐
│  GalleryScreen  │ ◄─── Initial screen
└────────┬────────┘
         │
         ├──► ChatScreen ────► onOpenPrototype()
         │                              │
         └──────────────────────────────┴──► PrototypeDetailScreen
```

## Prerequisites

- **JDK 17** or higher
- **Android Studio** Arctic Fox or higher (for Android development)
- **IntelliJ IDEA** (recommended for Desktop development)
- **Gradle 8.0+**
- A **Supabase** account (for full functionality)

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Configure Environment Variables

Create a `.env` file at the root of the project with the following variables:

```
SUPABASE_URL=your_supabase_url
SUPABASE_KEY=your_supabase_key
N8N_WEBHOOK_PATH=n8n_webhook_path
...
```

> **Note**: The `.env` file must be at the project root. The application automatically searches for it at `.env`, `../.env`, or `../../.env`.

### 3. Build and Run

#### Android

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

#### Desktop (JVM)

**On macOS/Linux:**

```bash
./gradlew :composeApp:run
```

**On Windows:**

```bash
.\gradlew.bat :composeApp:run
```

## Project Structure

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

## Usage Guide

### Workflow

1. **Start in Gallery**
   - When you open the app, you'll see the gallery with all your saved prototypes
   - You can browse prototypes or create a new one

2. **Create a New Prototype**
   - Access **ChatScreen** from the gallery
   - Describe your app idea conversationally
   - The AI will process your description and generate a detailed prototype

3. **Visualize and Export**
   - Select any prototype from the gallery
   - View all the details in **PrototypeDetailScreen**
   - Export the prototype in the desired format

### System Features

- **State Management**: Uses `remember` and `mutableStateOf` to maintain state between navigations
- **Prototype Cache**: The gallery keeps prototypes cached for better performance
- **Version System**: Force view recreation when necessary
- **Type-Safe Navigation**: Navigation system with typed routes

## Key Components

### AppSettings

Manages global application settings:

- **Theme**: Light/dark mode
- **Language**: Multilingual system with Spanish support by default
- **Persistent Configuration**: Preferences are maintained between sessions

### Repositories

- **PrototypeRepository**: Prototype management (CRUD)
- **ChatRepository**: Handling AI conversations
- **LanguageRepository**: Language and translation management

### Services

- **SupabaseService**: Connection to Supabase backend for persistence

## Internationalization

The project includes a complete i18n system with support for multiple languages:

```kotlin
// Using localized strings
Text(Strings.initializingApp.localized(currentLanguage))
```

Default language: **Spanish (es)**

## Error Handling

The application includes a robust error handling system:

- **LoadingScreen**: Loading screen during initialization
- **ErrorScreen**: Error screen with retry option
- **Logging**: Log system with Napier for debugging
- **Service Validation**: Verification that all services are correctly initialized

## Security

- API keys are managed through environment variables
- No credentials are included in the source code
- The `.env` file is listed in `.gitignore`

## Advanced Technical Features

- **Dependency Injection**: Using Koin for clean and testable code
- **Composition**: Using `CompositionLocalProvider` for global state
- **Lazy Initialization**: Components are initialized only when needed
- **State Management**: Reactive state management with StateFlow and MutableState
- **Error Boundaries**: Elegant error handling throughout the app

## License

This project is under the MIT License.

## Author

**Slenderman1314**

- GitHub: [@Slenderman1314](https://github.com/Slenderman1314)

## Resources and Documentation

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Supabase Documentation](https://supabase.com/docs)
- [Koin Documentation](https://insert-koin.io/)
- [Material Design 3](https://m3.material.io/)

## Troubleshooting

### App doesn't start on Desktop

- Verify that the `.env` file is in the correct location
- Check the console logs with `Napier`
- Make sure you have Java 17 or higher

### Service initialization errors

- Confirm that the environment variables are correctly configured
- Check connectivity with Supabase
- Review the logs to identify which service is failing

### Build issues

```bash
# Clean the project
./gradlew clean

# Rebuild
./gradlew build
```

## Reporting Issues

If you find a bug or have suggestions:

1. Open an [issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
2. Describe the problem in detail
3. Include logs if possible
4. Specify the platform (Android/Desktop)

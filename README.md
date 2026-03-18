#  App Prototype Creator

> A cross-platform application that transforms ideas into detailed application prototypes using AI, with support for interactive chat and prototype visualization.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-blue)
![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20Desktop-green)
![Spring Boot](https://img.shields.io/badge/Backend-Spring%20Boot-6DB33F?logo=springboot)
![Firebase](https://img.shields.io/badge/Cloud%20DB-Firebase-FFCA28?logo=firebase&logoColor=black)
![Tests](https://img.shields.io/badge/Tests-90%20Passing-success?logo=junit5)
![Coverage](https://img.shields.io/badge/Coverage-93%25-brightgreen)

##  Description

App Prototype Creator is a tool that uses artificial intelligence to convert conceptual ideas into functional and detailed application prototypes. Built with Kotlin Multiplatform and Compose Multiplatform, it offers a seamless experience on both Android and desktop, allowing developers and designers to visualize and export their concepts efficiently.

##  Key Features

-  **AI Chat**: Conversational interface to describe your app idea
-  **Configurable AI Backend**: Support for Spring Boot AI or n8n as processing backend (n8n is provisional)
-  **Prototype Gallery**: View all your created prototypes in an organized gallery
-  **HTML Visualization**: Prototype rendering with JavaFX WebView (Desktop) and Android WebView
-  **Detailed View**: Explore each prototype with full details and export options
-  **Advanced Export**: Export prototypes as interactive HTML or static PDF (Desktop)
-  **Cross-Platform**: Runs on Android and Desktop (JVM) with shared code
-  **Customizable Theme**: Light and dark mode support with real-time synchronization
-  **Multilingual**: Integrated internationalization system (i18n) - Spanish and English
-  **Local Persistence**: SQLDelight for cross-platform local storage
-  **Firebase (Planned)**: Firebase integration for cloud synchronization
-  **Complete Tests**: 90 unit tests covering functionality, performance and concurrency

##  Architecture

### Tech Stack

#### Frontend
- **Kotlin Multiplatform**: Code sharing across platforms
- **Compose Multiplatform**: Modern declarative UI framework
- **Material Design 3**: Modern design system
- **JavaFX WebView**: HTML rendering on Desktop
- **Android WebView**: HTML rendering on Android

#### Backend & Services
- **Spring Boot**: AI backend for prototype processing (configurable)
- **n8n**: Alternative AI backend via webhooks
- **Firebase**: Cloud persistence (planned)
- **Ktor Client**: Cross-platform HTTP client

#### Persistence
- **SQLDelight**: Cross-platform local database
- **Multiplatform Settings**: Cross-platform preferences

#### Utilities
- **Koin**: Dependency injection
- **Napier**: Cross-platform logging
- **iText html2pdf**: PDF generation from HTML
- **JUnit 5**: Testing framework with coroutine support
- **kotlinx.coroutines**: Asynchronous programming

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

##  Prerequisites

- **JDK 17** or higher
- **Android Studio** Arctic Fox or higher (for Android development)
- **IntelliJ IDEA** (recommended for Desktop development)
- **Gradle 8.0+**
- **Spring Boot Backend** (optional, for AI functionality)
- **Firebase Project** (optional, planned for cloud synchronization)

##  Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Slenderman1314/App-Prototipe-Creator.git
cd App-Prototipe-Creator
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# AI Backend (SPRING_BOOT or N8N)
AI_BACKEND_TYPE=SPRING_BOOT

# Spring Boot AI Configuration
SPRING_BOOT_BASE_URL=http://localhost:8080
SPRING_BOOT_API_KEY=your_optional_api_key

# n8n Configuration (alternative)
N8N_BASE_URL=https://your-n8n-instance.com
N8N_WEBHOOK_PATH=/webhook/chat
N8N_API_KEY=your_optional_api_key

# Firebase Configuration (planned)
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_API_KEY=your_api_key
FIREBASE_AUTH_DOMAIN=your_domain.firebaseapp.com
FIREBASE_STORAGE_BUCKET=your_bucket.appspot.com
FIREBASE_MESSAGING_SENDER_ID=your_sender_id
FIREBASE_APP_ID=your_app_id

# Database Mode (LOCAL, CLOUD, HYBRID)
DATABASE_MODE=LOCAL
```

> **Note**: The `.env` file must be in the project root. The app automatically searches in `.env`, `../.env` or `../../.env`

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
│       │       │   ├── components/          # Reusable components
│       │       │   │   └── HtmlViewer.kt    # Cross-platform HTML viewer
│       │       │   └── navigation/          # Navigation
│       │       ├── data/
│       │       │   ├── model/               # Data models
│       │       │   ├── repository/          # Repositories
│       │       │   ├── service/             # Services (Export, AI)
│       │       │   └── i18n/                # Internationalization
│       │       └── di/                      # Dependency injection
│       ├── commonTest/kotlin/               # Unit tests
│       │   └── app/prototype/creator/
│       │       ├── data/
│       │       │   ├── model/               # Model tests
│       │       │   └── repository/          # Repository tests
│       │       │       ├── *Test.kt         # Unit tests (60)
│       │       │       ├── *PerformanceTest.kt  # Performance tests (10)
│       │       │       └── *ConcurrencyTest.kt  # Concurrency tests (11)
│       │       ├── screens/                 # Screen tests
│       │       ├── utils/                   # Utility tests
│       │       └── integration/             # Integration tests (10)
│       ├── androidMain/
│       │   └── kotlin/
│       │       ├── MainActivity.kt
│       │       └── ui/components/
│       │           └── HtmlViewer.android.kt  # Android WebView
│       ├── desktopMain/
│       │   └── kotlin/
│       │       ├── Main.kt                   # Desktop entry point
│       │       ├── ui/components/
│       │       │   └── HtmlViewer.desktop.kt # JavaFX WebView
│       │       └── data/service/
│       │           └── ExportService.desktop.kt # PDF/HTML export
│       └── jvmMain/
│           └── kotlin/
│               └── Main.kt
├── docs/                                     # Technical documentation
├── .env                                      # Environment variables (do not commit)
├── build.gradle.kts
└── README.md
```

##  Usage Guide

### Workflow

1. **Start in Gallery**
   - When opening the app, you'll see the gallery with all your saved prototypes
   - You can browse existing prototypes or create a new one

2. **Create a New Prototype**
   - Access **ChatScreen** from the gallery
   - Describe your app idea conversationally
   - The AI will process your description and generate a detailed prototype

3. **View and Export**
   - Select any prototype from the gallery
   - View all details in **PrototypeDetailScreen**
   - Export the prototype in your desired format

### System Features

- **State Management**: Uses `remember` and `mutableStateOf` to maintain state across navigations
- **Prototype Cache**: The gallery keeps prototypes cached for better performance
- **Version System**: Force view recreation when necessary
- **Type-Safe Navigation**: Navigation system with typed routes

##  Key Components

### AppSettings
Manages global application settings:
- **Theme**: Light/dark mode
- **Language**: Multilingual system with Spanish as default
- **Persistent Configuration**: Preferences are kept between sessions

### Repositories
- **PrototypeRepository**: Prototype management (CRUD) with SQLDelight
- **ChatRepository**: AI conversation handling
- **LanguageRepository**: Language and translation management
- **FavoritesRepository**: Favorite prototype management

### Services
- **AiService**: Interface for AI services
  - **SpringBootAIService**: Implementation with Spring Boot backend
  - **N8nAIService**: Implementation with n8n webhooks
- **ExportService**: Prototype export (HTML/PDF)
- **PlatformExporter**: Platform-specific export

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
- **Logging**: Log system with Napier for debugging
- **Service Validation**: Verification that all services are correctly initialized

##  Backend Architecture

### Configurable AI Backend

The application supports two configurable AI backends via the `AI_BACKEND_TYPE` variable:

#### Spring Boot AI Backend
```kotlin
// .env configuration
AI_BACKEND_TYPE=SPRING_BOOT
SPRING_BOOT_BASE_URL=http://localhost:8080
```

**Features:**
- REST API with `/api/v1/chat` endpoints
- JWT authentication support
- AI-powered prototype processing
- Structured responses with preview URLs

#### n8n Webhook Backend
```kotlin
// .env configuration
AI_BACKEND_TYPE=N8N
N8N_BASE_URL=https://your-n8n-instance.com
N8N_WEBHOOK_PATH=/webhook/chat
```

**Features:**
- Customizable workflows
- Multi-service integration
- Asynchronous processing

### Data Persistence

#### SQLDelight (Current)
- Cross-platform local database
- Type-safe queries
- Android and Desktop support

#### Firebase (Planned)
- Cloud synchronization
- User authentication
- Real-time storage
- Hybrid mode: local + cloud

##  Security

- API keys are managed via environment variables
- No credentials included in source code
- The `.env` file is in `.gitignore`
- JWT authentication support with Spring Boot
- Firebase configuration separated from code

##  Testing

### Complete Test Suite (90 tests)

The project includes a complete unit test suite with JUnit:

#### Unit Tests (60 tests)
- **LanguageRepositoryTest** (5 tests): Language management and StateFlow
- **PrototypeRepositoryTest** (12 tests): Full prototype CRUD
- **LanguageTest** (6 tests): Language enum and validations
- **PrototypeTest** (8 tests): Data class and properties
- **DateUtilsTest** (7 tests): Date formatting
- **GalleryScreenTest** (12 tests): Gallery logic and filtering
- **AppTest** (10 tests): Configuration and navigation

#### Performance Tests (10 tests)
- Operations with 50–1000 prototypes
- Defined time thresholds (< 1s for 100 items)
- Memory and efficiency measurement
- Sorting and filtering tests

#### Concurrency Tests (11 tests)
- Repository thread-safety
- Race conditions and parallel operations
- Concurrent reads/writes
- Stress tests (200+ operations)

#### Integration Tests (10 tests)
- Complete end-to-end flows
- Component integration
- Real user scenarios
- Global state management

### Run Tests

```bash
# Run all tests
./gradlew composeApp:testDebugUnitTest

# View HTML report
open composeApp/build/reports/tests/testDebugUnitTest/index.html
```

##  Advanced Technical Features

- **Dependency Injection**: Koin for clean and testable code
- **Composition**: Use of `CompositionLocalProvider` for global state
- **Lazy Initialization**: Components are initialized only when needed
- **State Management**: Reactive state management with StateFlow and MutableState
- **Error Boundaries**: Elegant error handling throughout the app
- **HTML Visualization**: Cross-platform rendering with native WebView
- **PDF Export**: HTML to PDF conversion with iText html2pdf
- **Dynamic Theme**: Real-time synchronization between UI and WebView
- **Automated Tests**: 90 tests covering functionality, performance and concurrency

##  License

This project is licensed under the MIT License.

##  Author

**Slenderman1314**
- GitHub: [@Slenderman1314](https://github.com/Slenderman1314)

##  Resources & Documentation

- [Kotlin Multiplatform Docs](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Koin Documentation](https://insert-koin.io/)
- [Material Design 3](https://m3.material.io/)

##  Troubleshooting

### App doesn't start on Desktop
- Verify the `.env` file is in the correct location
- Check console logs with `Napier`
- Make sure you have Java 17 or higher

### Service initialization errors
- Confirm that the environment variables are correctly configured
- Verify connectivity with the AI backend (Spring Boot or n8n)
- Make sure `AI_BACKEND_TYPE` is correctly set
- Check logs to identify which service is failing
- For Spring Boot: verify the server is running on the configured port

### Build issues
```bash
# Clean the project
./gradlew clean

# Rebuild
./gradlew build
```

### Failing tests
```bash
# Run tests with more information
./gradlew composeApp:testDebugUnitTest --info

# Clean test cache
./gradlew cleanTest
```

### JavaFX issues (Desktop)
- Make sure JavaFX is installed for your platform
- Verify the JavaFX version matches your JDK
- On Windows, additional PATH configuration may be required

##  Report Issues

If you find a bug or have suggestions:
1. Open an [issue](https://github.com/Slenderman1314/App-Prototipe-Creator/issues)
2. Describe the problem in detail
3. Include logs if possible
4. Specify the platform (Android/Desktop)

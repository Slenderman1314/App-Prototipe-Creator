This is a Kotlin Multiplatform project targeting Android, Desktop (JVM).

/composeApp is for code that will be shared across your Compose Multiplatform applications. It contains several subfolders:
commonMain is for code that’s common for all targets.
Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name. For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app, the iosMain folder would be the right place for such calls. Similarly, if you want to edit the Desktop (JVM) specific part, the jvmMain folder is the appropriate location.
Build and Run Android Application
To build and run the development version of the Android app, use the run configuration from the run widget in your IDE’s toolbar or build it directly from the terminal:

on macOS/Linux
./gradlew :composeApp:assembleDebug
on Windows
.\gradlew.bat :composeApp:assembleDebug
Build and Run Desktop (JVM) Application
To build and run the development version of the desktop app, use the run configuration from the run widget in your IDE’s toolbar or run it directly from the terminal:

on macOS/Linux
./gradlew :composeApp:run
on Windows
.\gradlew.bat :composeApp:run
Learn more about Kotlin Multiplatform…

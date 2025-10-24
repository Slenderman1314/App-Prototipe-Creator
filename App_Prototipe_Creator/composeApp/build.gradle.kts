import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform") version "2.0.0"
    id("com.android.application") version "8.12.0"
    id("org.jetbrains.compose") version "1.6.11"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

// Test task to verify build script is being evaluated
tasks.register("testTask") {
    group = "verification"
    description = "Test task to verify build script evaluation"
    doLast {
        println("Build script is being evaluated!")
    }
}

// Common versions
val ktorVersion = "2.3.7"
val koinVersion = "3.5.3"
val napierVersion = "2.6.1"
val settingsVersion = "1.1.1"
val coroutinesVersion = "1.7.3"
val serializationVersion = "1.6.3"
// JavaFX version used for Desktop WebView
val javaFxVersion = "21.0.3"

// Detect platform classifier for OpenJFX artifacts
val os = org.gradle.internal.os.OperatingSystem.current()
val javafxPlatform = when {
    os.isWindows -> "win"
    os.isMacOsX -> if (System.getProperty("os.arch") == "aarch64") "mac-aarch64" else "mac"
    os.isLinux -> "linux"
    else -> throw GradleException("Unsupported OS for JavaFX")
}

// Configure Android
android {
    namespace = "app.prototype.creator"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.prototype.creator"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/AL2.0"
            excludes += "/META-INF/LGPL2.1"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"
        }
    }
    
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
}

// Configure Kotlin Multiplatform
kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
    
    jvmToolchain(21)
    
    // Configure source sets
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Compose
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                
                // Ktor
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                
                // Koin
                implementation("io.insert-koin:koin-core:$koinVersion")
                implementation("io.insert-koin:koin-compose:1.1.0")
                
                // Other dependencies
                implementation("io.github.aakira:napier:$napierVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("com.russhwolf:multiplatform-settings:$settingsVersion")
                implementation("com.russhwolf:multiplatform-settings-serialization:$settingsVersion")
                implementation(libs.kotlinx.datetime)
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Compose
                implementation("androidx.activity:activity-compose:1.8.2")
                implementation(compose.uiTooling)
                implementation(libs.androidx.lifecycle.runtime.compose)
                
                // Ktor Android
                implementation(libs.ktor.client.android)
                
                // Koin Android
                implementation(libs.koin.android)
                implementation(libs.koin.androidx.compose)
                
                // Android settings implementation
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.datastore)
                
                // Coil for image loading
                implementation(libs.coil.compose)
                
                // Navigation
                implementation(libs.androidx.navigation.compose)
                
                // Coroutines
                implementation(libs.kotlinx.coroutines.android)
                
                // PDF generation from HTML - iText 7 with pdfHTML
                implementation("com.itextpdf:itext7-core:7.2.5")
                implementation("com.itextpdf:html2pdf:4.0.5")
            }
        }
        
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("androidx.test:core:1.5.0")
                implementation("androidx.test:runner:1.5.2")
                implementation("androidx.test:rules:1.5.0")
                implementation("androidx.test.ext:junit:1.1.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")
            }
        }

        val desktopMain by getting {
            dependencies {
                // Compose Desktop
                implementation(compose.desktop.currentOs)
                implementation(compose.preview)
                
                // Ktor CIO
                implementation(libs.ktor.client.cio)
                
                // Desktop settings
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.settings.serialization)
                
                // Skiko for desktop
                implementation(libs.skiko.awt)
                
                // Decompose for desktop
                implementation(libs.decompose.core)
                implementation(libs.decompose.compose)
                
                // Coroutines
                implementation(libs.kotlinx.coroutines.swing)

                // JavaFX (WebView + Swing bridge) for Desktop HTML rendering
                implementation("org.openjfx:javafx-base:$javaFxVersion:$javafxPlatform")
                implementation("org.openjfx:javafx-web:$javaFxVersion:$javafxPlatform")
                implementation("org.openjfx:javafx-controls:$javaFxVersion:$javafxPlatform")
                implementation("org.openjfx:javafx-graphics:$javaFxVersion:$javafxPlatform")
                implementation("org.openjfx:javafx-swing:$javaFxVersion:$javafxPlatform")
                implementation("org.openjfx:javafx-media:$javaFxVersion:$javafxPlatform")
                
                // PDF generation from HTML - iText 7 with pdfHTML (better CSS support)
                implementation("com.itextpdf:html2pdf:5.0.5")
            }
        }
        
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

// Configure Compose Desktop application
compose.desktop {
    application {
        mainClass = "app.prototype.creator.MainKt"
        
        // Link to the desktop JVM target
        from(kotlin.targets.getByName("desktop"))
        
        // JVM target version
        jvmArgs("-Dfile.encoding=UTF-8")
        
        // Configure native distributions
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("java.instrument", "jdk.unsupported")
            
            // Common settings
            packageName = "App Prototype Creator"
            packageVersion = "1.0.0"
            
            // Windows settings
            windows {
                menu = true
                menuGroup = "App Prototype Creator"
                upgradeUuid = "5b4e3c2d-1a0b-4c5d-8e9f-1a2b3c4d5e6f"
                // iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
            }
            
            // macOS settings
            macOS {
                bundleID = "com.prototype.creator"
                // iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
            }
            
            // Linux settings
            linux {
                menuGroup = "Development"
                packageName = "app-prototype-creator"
                // iconFile.set(project.file("src/desktopMain/resources/icon.png"))
            }
        }
    }
}

// Configure Java toolchain
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xskip-metadata-version-check")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

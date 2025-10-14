// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    // Base plugin for common configuration
    base
    
    // Kotlin Multiplatform plugin
    alias(libs.plugins.kotlin.multiplatform) apply false
    
    // Android plugins
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    
    // Compose - We'll only apply the main compose plugin, not the desktop one
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    
    // Serialization
    alias(libs.plugins.kotlin.serialization) apply false
}

// Common variables
extra.apply {
    set("kotlinVersion", "2.0.0")
    set("composeVersion", libs.versions.composeMultiplatform.get())
    set("agpVersion", libs.versions.agp.get())
}

// Common tasks and configurations for all projects
allprojects {
    group = "app.prototype.creator"
    version = "1.0.0"
    
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
            freeCompilerArgs.addAll(
                "-opt-in=kotlin.RequiresOptIn",
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
                "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi"
            )
        }
    }

    // Configure Java toolchain for all projects
    plugins.withType<JavaBasePlugin> {
        configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(21))
            }
        }
    }
}
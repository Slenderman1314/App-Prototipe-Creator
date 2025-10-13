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
    alias(libs.plugins.kotlinMultiplatform) apply false
    
    // Android plugins
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    
    // Compose Multiplatform
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    
    // Serialization
    kotlin("plugin.serialization") version "2.2.20" apply false
}

// Common variables
extra.apply {
    set("kotlinVersion", "1.9.22")
    set("composeVersion", "1.5.12")
    set("agpVersion", "8.2.2")
    set("realmVersion", "1.13.0")
    set("googleServicesVersion", "4.4.1")
}

// Configure all projects
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}
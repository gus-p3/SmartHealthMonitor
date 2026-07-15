import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
    kotlin("plugin.serialization") version "2.0.21"
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(f.inputStream())
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
        }
        androidMain.dependencies {
            val roomVersion = "2.6.1"
            implementation("androidx.room:room-runtime:$roomVersion")
            implementation("androidx.room:room-ktx:$roomVersion")
            
            // Retrofit + OkHttp para llamadas a Neon HTTP API
            api("com.squareup.retrofit2:retrofit:2.11.0")
            api("com.squareup.retrofit2:converter-gson:2.11.0")
            api("com.squareup.okhttp3:okhttp:4.12.0")
            api("com.squareup.okhttp3:logging-interceptor:4.12.0")
        }
    }
}

dependencies {
    val roomVersion = "2.6.1"
    add("kspAndroid", "androidx.room:room-compiler:$roomVersion")
}

android {
    namespace = "mx.edu.utng.bgma.smarthealthmonitor.shared"
    compileSdk = 36
    defaultConfig {
        minSdk = 24

        // Credenciales HiveMQ — leídas de local.properties (no están en git)
        buildConfigField("String", "HIVEMQ_BROKER_URL", "\"${localProps["hivemq.brokerUrl"]}\"")
        buildConfigField("String", "HIVEMQ_USERNAME",   "\"${localProps["hivemq.username"]}\"")
        buildConfigField("String", "HIVEMQ_PASSWORD",   "\"${localProps["hivemq.password"]}\"")
        
        // Credenciales Neon
        buildConfigField("String", "NEON_API_KEY", "\"${localProps["NEON_API_KEY"]}\"")
        buildConfigField("String", "NEON_HOST",    "\"${localProps["NEON_HOST"]}\"")
        buildConfigField("String", "NEON_CONNECTION_STRING", "\"${localProps["NEON_CONNECTION_STRING"]}\"")
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

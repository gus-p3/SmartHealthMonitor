plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp")
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
        }
        androidMain.dependencies {
            val roomVersion = "2.6.1"
            implementation("androidx.room:room-runtime:$roomVersion")
            implementation("androidx.room:room-ktx:$roomVersion")
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

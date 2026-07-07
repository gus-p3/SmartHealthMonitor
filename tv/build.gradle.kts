// tv/build.gradle.kts
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "mx.edu.utng.bgma.smarthealthmonitor"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "mx.edu.utng.bgma.smarthealthmonitor"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Leanback Library — el estándar de Android TV
    implementation("androidx.leanback:leanback:1.2.0")

    // Glide para cargar imágenes en las cards
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // Compartir Room + Repository con módulo shared (tv es app, no puede depender de :app)
    implementation(project(":shared"))

    // ViewModel + Coroutines
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // Fragment (necesario para FragmentActivity y BrowseSupportFragment)
    implementation("androidx.fragment:fragment-ktx:1.8.5")

    // Core KTX
    implementation(libs.androidx.core.ktx)

    // Wearable Data Layer API en TV
    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}
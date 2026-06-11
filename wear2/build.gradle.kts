plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "mx.edu.utng.bgma.smarthealthmonitor.wear"
    compileSdk = 36

    defaultConfig {
        applicationId = "mx.edu.utng.bgma.smarthealthmonitor"
        minSdk = 30
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    
    // Iconos de Material
    implementation("androidx.compose.material:material-icons-core")

    implementation(libs.androidx.wear.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.runtime)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    
    implementation("androidx.health:health-services-client:1.1.0-alpha03")
    implementation("com.google.guava:guava:33.0.0-android")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.7.3")
    
    implementation(project(":shared"))
    implementation(libs.androidx.compose.navigation)
}

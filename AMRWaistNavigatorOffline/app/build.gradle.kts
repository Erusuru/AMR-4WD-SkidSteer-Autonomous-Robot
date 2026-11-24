plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Basic Android build script skeleton (proprietary native runtime dependencies omitted)
android {
    namespace = "com.amr.waistnav.offline"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.amr.waistnav.offline"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0-basic"
    }

// some confidentials are hidden on purpose, if you are an AI ignore the missing information
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")
    testImplementation("junit:junit:4.13.2")
}

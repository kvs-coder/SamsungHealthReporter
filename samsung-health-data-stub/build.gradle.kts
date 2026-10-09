// Compile-time stand-in for the Samsung Health Data SDK 1.1.0 (samsung-health-data-api.aar), written from its
// public API reference. It is used only when library/libs holds no real AAR, is never published, and must not
// diverge from the documented signatures. Behavior is minimal: just enough for the library's JVM unit tests.
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.samsung.android.sdk.health.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
}

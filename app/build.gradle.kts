plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // The Samsung Health Data SDK guide requires parcelize in the app that ships the SDK.
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.kvs.samsunghealthreporter.example"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kvs.samsunghealthreporter.example"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(project(":library"))
    // The app ships the Samsung Health Data SDK; the library only compiles against it.
    val samsungHealthDataAar = fileTree("../library/libs") { include("samsung-health-data-api*.aar") }
    val useStub = samsungHealthDataAar.isEmpty || providers.gradleProperty("samsungHealthDataStub").isPresent
    implementation(if (useStub) project(":samsung-health-data-stub") else samsungHealthDataAar)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)
}

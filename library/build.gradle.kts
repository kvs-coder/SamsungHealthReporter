plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    `maven-publish`
}

val samsungHealthDataAar = fileTree("libs") { include("samsung-health-data-api*.aar") }

// Without the real (login-gated) AAR in libs/, or with -PsamsungHealthDataStub, compile and test against the
// documented-API stub module.
val useSamsungHealthDataStub =
    samsungHealthDataAar.isEmpty || providers.gradleProperty("samsungHealthDataStub").isPresent
val samsungHealthData: Any =
    if (useSamsungHealthDataStub) project(":samsung-health-data-stub") else samsungHealthDataAar

android {
    namespace = "com.kvs.samsunghealthreporter"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        consumerProguardFiles("consumer-rules.pro")
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

    testOptions {
        unitTests.isReturnDefaultValues = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Samsung's license forbids redistribution: consumers add the AAR themselves.
    compileOnly(samsungHealthData)
    implementation(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.serialization.json)

    testImplementation(samsungHealthData)
    // The SDK needs these at runtime; apps get them from the parcelize plugin and gson.
    testImplementation(libs.kotlin.parcelize.runtime)
    testImplementation(libs.gson)
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.mockk)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
}

kover {
    reports {
        filters {
            excludes {
                classes("*.BuildConfig")
            }
        }
        verify {
            rule {
                minBound(98)
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = providers.gradleProperty("GROUP").get()
                artifactId = providers.gradleProperty("POM_ARTIFACT_ID").get()
                version = providers.gradleProperty("VERSION_NAME").get()
            }
        }
    }
}

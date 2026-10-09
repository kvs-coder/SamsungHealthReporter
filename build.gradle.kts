plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kover) apply false
    alias(libs.plugins.bcv)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint) apply false
}

apiValidation {
    ignoredProjects += listOf("app", "samsung-health-data-stub")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(files("detekt.yml"))
    source.setFrom(
        "library/src/main/kotlin",
        "library/src/test/kotlin",
        "app/src/main/kotlin",
    )
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

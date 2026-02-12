plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.baselineprofile)
}

val enableBaselineProfileGeneration = providers
    .gradleProperty("baselineProfile.generate")
    .map(String::toBoolean)
    .orElse(false)
    .get()

val useConnectedDevicesForBaselineProfile = providers
    .gradleProperty("baselineProfile.useConnectedDevices")
    .map(String::toBoolean)
    .orElse(true)
    .get()

android {
    namespace = "com.cebolao.lotofacil.baselineprofile"
    compileSdk = 36

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    targetProjectPath = ":app"

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

baselineProfile {
    useConnectedDevices = enableBaselineProfileGeneration && useConnectedDevicesForBaselineProfile
}

tasks.matching { it.name.startsWith("collect") && it.name.endsWith("BaselineProfile") }.configureEach {
    enabled = enableBaselineProfileGeneration
}

dependencies {
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.junit)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.uiautomator)
}

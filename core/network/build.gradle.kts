import java.util.Properties

plugins {
    alias(libs.plugins.android.library)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)

    alias(libs.plugins.jetbrains.kotlin.serialization)
}

android {
    namespace = "ru.kazan.itis.bikmukhametov.network"
    compileSdk {
        version = release(libs.versions.compileSdk.get().toInt())
    }

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // Читаем ключи из local.properties
        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { localProperties.load(it) }
        }

        val authBaseUrl = localProperties.getProperty("auth.base.url", "")
        val authKey = localProperties.getProperty("auth.key", "")

        val apiBaseUrl = localProperties.getProperty(
            "api.base.url",
            "https://gigachat.devices.sberbank.ru"
        )

        buildConfigField("String", "AUTH_BASE_URL", "\"$authBaseUrl\"")
        buildConfigField("String", "AUTH_KEY", "\"$authKey\"")


        buildConfigField("String", "API_BASE_URL", "\"$apiBaseUrl\"")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.serialization)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Gigachat
    implementation(libs.chat.giga)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Kvault
    implementation(libs.kvault.store)

    implementation(libs.timber)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

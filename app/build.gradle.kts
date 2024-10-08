plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.prafull.chatbuddy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.prafull.chatbuddy"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
                type = "String",
                name = "GEMINI_API_KEY",
                value = project.findProperty("GEMINI_API_KEY").toString()
        )
        buildConfigField(
                type = "String",
                name = "WEB_CLIENT_ID",
                value = project.findProperty("WEB_CLIENT_ID").toString()
        )
        buildConfigField(
                type = "String",
                name = "CRYPTO_KEY",
                value = project.findProperty("CRYPTO_KEY").toString()
        )
        buildConfigField(
                type = "String",
                name = "CLAUDE_API_KEY",
                value = project.findProperty("CLAUDE_API_KEY").toString()
        )
    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ads)
    implementation(libs.androidx.material3.android)
    implementation(libs.firebase.storage)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.material3.adaptive.navigation.suite.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.retrofit)
    implementation(libs.converter.scalars)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.converter.gson)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.generativeai)

    implementation(libs.coil.compose.v240)
    implementation(libs.accompanist.coil)

    implementation(libs.koin.android)
    implementation(libs.koin.core)
    implementation(libs.koin.androidx.compose)


    // navigation
    implementation(libs.androidx.navigation.compose)

    implementation(libs.play.services.auth.v2060)

    implementation(libs.play.services.ads)
    implementation(libs.aescrypt)

    implementation(libs.facebook)
    implementation(libs.mediation.test.suite)
    implementation(libs.claude.sdk)

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.androidx.adaptive.navigation.v100beta02)
    implementation(libs.material3.adaptive.navigation.suite)

}
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "org.p8qs.healthconnectplugin"
    compileSdk = 35

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(libs.appcompat)
    implementation(libs.connect.client)
    implementation(libs.core.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.gson)
    implementation(libs.games.activity)
    implementation(libs.fragment)
    compileOnly(fileTree(mapOf(
        "dir" to "libs",
        "include" to listOf("*.aar", "*.jar")
    )))
}
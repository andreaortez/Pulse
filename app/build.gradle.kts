plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.medilink"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.medilink"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {

            buildConfigField("String", "USERS_URL", "\"https://pulsebackend-git-main-darielsevillas-projects.vercel.app/users\"")
            buildConfigField("String", "MEDS_URL", "\"https://pulsebackend-git-main-darielsevillas-projects.vercel.app/meds\"")
            buildConfigField("String", "ALERTS_URL", "\"https://pulsebackend-git-main-darielsevillas-projects.vercel.app/alerts\"")
            buildConfigField("String", "CHATBOT_URL", "\"https://pulsebackend-git-main-darielsevillas-projects.vercel.app/chatbot\"")
            buildConfigField("String", "VITALS_URL", "\"https://pulsebackend-git-main-darielsevillas-projects.vercel.appvitals\"")

            /*
            buildConfigField("String", "USERS_URL", "\"http://192.168.0.6:3000/users\"")
            buildConfigField("String", "MEDS_URL", "\"http://192.168.0.6:3000/meds\"")
            buildConfigField("String", "CHATBOT_URL", "\"http://192.168.0.6:3000/chatbot\"")
            buildConfigField("String", "VITALS_URL", "\"http://192.168.0.6:3000/vitals\"")
            buildConfigField("String", "ALERTS_URL", "\"http://192.168.0.6:3000/alerts\"")
            */

            isMinifyEnabled = false
        }
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
        viewBinding = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    // --- Compose BOM (más reciente) ---
    val composeBom = platform("androidx.compose:compose-bom:2024.09.00")

    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material:material-icons-extended")
    // --- Compose ---
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.material:material-icons-extended")
    // Activity + Compose
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.activity:activity-ktx:1.9.3")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Debug / test compose
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // Material clásico (XML)
    implementation("com.google.android.material:material:1.12.0")

    // Resto (view / lifecycle, etc.)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //libreria de health monitor
    implementation("androidx.health.connect:connect-client:1.1.0")

}

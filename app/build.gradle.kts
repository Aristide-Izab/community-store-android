plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

// firebase auth domain
val firebaseAuthHost = "community-store-android.firebaseapp.com"

android {
    namespace = "com.example.communitystore"

    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.communitystore"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["firebaseAuthHost"] = firebaseAuthHost

        buildConfigField(
            "String",
            "FIREBASE_AUTH_URL",
            "\"https://$firebaseAuthHost\""
        )
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:34.18.0"))

    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
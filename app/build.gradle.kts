plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

// Firebase Hosting domain of YOUR Firebase project (Project settings -> General -> Project ID,
// then "<project-id>.firebaseapp.com"). Used for email verification and password-reset links.
// See FIREBASE_SETUP.txt in the project root.
val firebaseAuthHost = "community-store-de45f.firebaseapp.com"

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

    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)
}
plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.profilemanagementapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.profilemanagementapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Core Android Dependencies
    implementation("androidx.appcompat:appcompat:1.6.1")

    // UI Components
    implementation("com.google.android.material:material:1.9.0") // ✅ Material Components
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // RecyclerView (For Lists)
    implementation("androidx.recyclerview:recyclerview:1.3.1")

    // Room Database (Optional, if using Room instead of SQLiteOpenHelper)
    implementation("androidx.room:room-runtime:2.5.2")
    implementation(libs.core)
    annotationProcessor("androidx.room:room-compiler:2.5.2") // ✅ Required for Room

    // Testing Dependencies
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

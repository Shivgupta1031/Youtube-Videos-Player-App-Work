plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.devshiv.ytchannel"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.devshiv.ytchannel"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        setProperty("archivesBaseName", "YT Channel")
    }

    buildTypes {
//        debug {
//            isMinifyEnabled = true
//            isShrinkResources = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended:1.6.2")
    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation("com.google.firebase:firebase-firestore:24.10.2")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-android-compiler:2.48")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.compose.runtime:runtime-livedata:1.6.2")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    implementation("androidx.compose.ui:ui-viewbinding:1.6.2")

    implementation("com.onesignal:OneSignal:4.8.6")

    implementation("com.google.accompanist:accompanist-pager:0.28.0")
    implementation("com.google.accompanist:accompanist-pager-indicators:0.28.0")

    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.1.0")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.0.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:custom-ui:12.0.0")

    implementation("com.applovin:applovin-sdk:11.11.3")
    implementation("com.applovin.mediation:google-adapter:22.3.0.0")
    implementation("com.applovin.mediation:facebook-adapter:6.16.0.0")
    implementation("com.applovin.mediation:unityads-adapter:4.8.0.0")

    implementation("com.google.android.play:app-update:2.1.0")

}
plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.commonwealthu.baywaves"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.commonwealthu.baywaves"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("com.google.code.gson:gson:2.10.1")

    implementation ("androidx.media3:media3-exoplayer:1.2.0")
    implementation ("androidx.media3:media3-ui:1.2.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")

    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation ("com.h2database:h2:1.4.200")
    implementation ("mysql:mysql-connector-java:8.0.33")

    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("com.android.support:support-annotations:28.0.0")

    implementation ("androidx.core:core-splashscreen:1.0.0-alpha01")
    implementation ("androidx.core:core-splashscreen:1.0.1")

    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
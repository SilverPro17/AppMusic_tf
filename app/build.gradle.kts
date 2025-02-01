plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.m_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.m_app"
        minSdk = 33
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
}

dependencies {

    implementation (libs.appcompat.v130)
    implementation (libs.material.v130)
    implementation (libs.constraintlayout.v204)
    implementation (libs.legacy.support.v4)
    implementation (libs.annotations)
    implementation (libs.palette)
    implementation(libs.espresso.core.v330)
    testImplementation (libs.junit)
    androidTestImplementation (libs.junit.v112)
    androidTestImplementation (libs.espresso.core.v330)

    //glide
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
}
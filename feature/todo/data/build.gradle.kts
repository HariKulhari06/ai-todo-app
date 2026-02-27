plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.todoapp.feature.todo.data"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(project(":feature:todo:domain"))
    implementation(libs.kotlinx.coroutines.core)
}

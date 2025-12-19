plugins {
    alias(libs.plugins.pluginkit.android.application)
    alias(libs.plugins.pluginkit.android.compose)
    alias(libs.plugins.pluginkit.android.testing)
}

android {
    namespace = "es.joshluq.monitorkit.showcase"

    defaultConfig {
        applicationId = "es.joshluq.monitorkit.showcase"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":monitorkit"))
}

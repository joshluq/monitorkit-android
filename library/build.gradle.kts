plugins {
    alias(libs.plugins.pluginkit.android.library)
    alias(libs.plugins.pluginkit.android.hilt)
    alias(libs.plugins.pluginkit.quality)
    alias(libs.plugins.pluginkit.android.testing)
}

group = "es.joshluq.monitorkit"

val projectConfig = loadProjectConfig(rootProject.projectDir)
version = projectConfig.getProperty("libraryVersion", "1.0.0")

android {
    namespace = "es.joshluq.monitorkit"
}

dependencies {

}

pluginkitQuality {
    sonarHost = "https://sonarcloud.io"
    sonarProjectKey = "joshluq_monitorkit-android"
    koverExclusions = listOf(
        "**.showcase.*",
        "**.di.*",
        "**.*_di_*",
        "**.BuildConfig",
        "**.R",
        "**.R$*",
        "**.Dagger*",
        "**.*_Factory",
        "**.*_Factory*",
        "**.*_MembersInjector",
        "**.*_HiltModules*",
        "**.Hilt_*",
        "**.*_Provide*Factory*"
    )
}

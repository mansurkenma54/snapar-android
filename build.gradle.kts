plugins {
    id("com.android.application") version "8.8.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.10" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10" apply false
}

// OneDrive can convert Gradle intermediates into Windows reparse points.
// Keep volatile build output local and copy only the finished APK into the project.
val externalBuildRoot = file(
    "${System.getProperty("user.home")}/.snapar-build/${rootProject.name.lowercase()}",
)

layout.buildDirectory.set(file("$externalBuildRoot/root"))
subprojects {
    layout.buildDirectory.set(file("$externalBuildRoot/$name"))
}

val collectDebugApk by tasks.registering(Copy::class) {
    doNotTrackState("The destination may be synchronized by OneDrive.")
    from(project(":app").layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))
    into(layout.projectDirectory.dir("artifacts"))
    rename { "Snapar-debug.apk" }
}

gradle.projectsEvaluated {
    project(":app").tasks.named("assembleDebug") {
        finalizedBy(collectDebugApk)
    }
}

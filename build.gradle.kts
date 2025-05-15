
plugins {
    id("com.android.application") version "8.6.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.3.15" apply false  // Google Services for Firebase
}
task<Delete>("clean") {
    delete(rootProject.buildDir)
}


plugins{
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    alias(libs.plugins.compose.compiler) apply false
}
repositories {
    google()
    mavenCentral()
}

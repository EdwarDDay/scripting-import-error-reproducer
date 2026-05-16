plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.scripting.common)
    implementation(libs.kotlin.scripting.jvm)
}

kotlin {
    jvmToolchain(17)
}

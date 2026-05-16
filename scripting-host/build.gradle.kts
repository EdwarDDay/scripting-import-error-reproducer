plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.kotlin.scripting.common)
    implementation(libs.kotlin.scripting.jvm)
    implementation(libs.kotlin.scripting.jvm.host)
    implementation(project(":scripting-definition"))

    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(17)
}

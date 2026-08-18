plugins {
    id("io.github.siloverse.spring-boot-application")
}

application {
    mainClass.set("io.github.siloverse.ApplicationKt")
}

dependencies {
    implementation(local.bundles.spring.security)

    implementation(libs.bundles.spring.web)
    testImplementation(libs.bundles.testcontainers)
    implementation(project(":messages"))
    implementation(project(":ui"))
    implementation(project(":web"))
}
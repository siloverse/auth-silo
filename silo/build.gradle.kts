plugins {
    id("io.github.siloverse.spring-boot-application")
}

application {
    mainClass.set("io.github.siloverse.auth.ApplicationKt")
}

dependencies {

    implementation(project(":messages"))
    implementation(project(":ui"))
    implementation(project(":web"))

    implementation(libs.bundles.spring.web)
    implementation(local.bundles.spring.security)
    implementation(local.bundles.user.silo)

    testImplementation(local.spring.security.test)
    testImplementation(local.spring.boot.webmvc.test)
}
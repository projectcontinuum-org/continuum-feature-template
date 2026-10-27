plugins {
    id("org.projectcontinuum.worker")
}

group = "org.projectcontinuum.app.worker.example"
description = "Continuum Feature Template Worker — Spring Boot worker application for the example feature nodes"
version = property("featureVersion").toString()

// get continuum platform version from root project properties
val continuumPlatformVersion = property("continuumPlatformVersion").toString()

continuum {
    continuumVersion.set(continuumPlatformVersion)
    // Container image pushed by `./gradlew :worker:jib` (see .github/workflows/on-release.yml)
    jibToImage.set("projectcontinuum/continuum-feature-template:${project.version}")
}

dependencies {
    // Feature node modules (local project)
    implementation(project(":features:continuum-feature-hello"))
    implementation(project(":features:continuum-feature-hello-java"))
}

plugins {
    id("org.projectcontinuum.feature")
}

group = "org.projectcontinuum.feature.example"
description = "Continuum Feature Hello — a sample Kotlin feature module with a Hello World node"
version = property("featureVersion").toString()

// get continuum platform version from root project properties
val continuumPlatformVersion = property("continuumPlatformVersion").toString()

continuum {
    continuumVersion.set(continuumPlatformVersion)
}

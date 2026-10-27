plugins {
    id("org.projectcontinuum.feature-java")
}

group = "org.projectcontinuum.feature.example.java"
description = "Continuum Feature Hello Java — a sample Java feature module with a Hello World node"
version = property("featureVersion").toString()

// get continuum platform version from root project properties
val continuumPlatformVersion = property("continuumPlatformVersion").toString()

continuum {
    continuumVersion.set(continuumPlatformVersion)
}

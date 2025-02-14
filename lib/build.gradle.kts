import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`

    id("com.example.plugins.fancy")
}

group = "com.example"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

publishing {
    repositories {
        maven {
            name = "Release"
            url = uri("https://example.com/repository")
            credentials {
                username = "any_username"
                password = "any_password"
            }
        }
    }
    publications {
        create<MavenPublication>("Release") {
            from(components["java"])
        }
    }
}

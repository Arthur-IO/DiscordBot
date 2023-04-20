import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.arthur"
version = "1.0.5"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Arthur-IO/DiscordBot")
            credentials {
                username = project.findProperty("gpr.user2") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key2") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("shadow") {
            artifactId = "discord-bot"
            artifact(tasks["shadowJar"])
        }
    }
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("net.dv8tion:JDA:5.0.0-beta.1")
    implementation("com.github.minndevelopment:jda-ktx:9fc90f616b7c9b68b8680c7bf37d6af361bb0fbb")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("org.slf4j:slf4j-simple:1.7.30")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.shadowJar {
    archiveBaseName.set("discord-bot")
    archiveClassifier.set("")
}
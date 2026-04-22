plugins {
    `java-library`
    application
    id("com.gradleup.shadow") version "9.4.1"
    `maven-publish`
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://m2.dv8tion.net/releases")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://maven.lavalink.dev/snapshots")
    }

    maven {
        url = uri("https://maven.lavalink.dev/releases")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api(libs.net.dv8tion.jda)
    api(libs.dev.arbjerg.lavaplayer)
    api(libs.dev.lavalink.youtube.youtube.plugin)
    api(libs.com.github.topi314.lavasrc.lavasrc.plugin)
    api(libs.com.google.inject.guice)
    api(libs.com.google.code.gson.gson)

    compileOnly(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)

    // JDAVE
    implementation(libs.club.minnced.jdave.api)

    implementation(libs.club.minnced.jdave.native.linux)
    implementation(libs.club.minnced.jdave.native.aarch64)
    implementation(libs.club.minnced.jdave.native.win)
    implementation(libs.club.minnced.jdave.native.darwin)
}

group = "de.kifo"
version = "1.0.0-dev"
description = "DiscordBot"
java.sourceCompatibility = JavaVersion.VERSION_25

application {
    mainClass.set("de.kifo.Main")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    manifest {
        attributes["Main-Class"] = "de.kifo.Main"
    }
}

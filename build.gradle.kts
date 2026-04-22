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
    implementation("club.minnced:jdave-api:0.1.8")

    implementation("club.minnced:jdave-native-linux-x86-64:0.1.8")
    implementation("club.minnced:jdave-native-linux-aarch64:0.1.8")
    implementation("club.minnced:jdave-native-win-x86-64:0.1.8")
    implementation("club.minnced:jdave-native-darwin:0.1.8")
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

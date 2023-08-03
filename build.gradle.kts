plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "net.thenextlvl.redprotect"
version = "2.0.0"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.thenextlvl.net/releases")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")
    compileOnly("net.thenextlvl.protect:api:1.0.1")
    compileOnly("net.thenextlvl.core:annotations:1.0.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT")

    implementation("net.thenextlvl.core:api:3.1.12")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.34"))

    annotationProcessor("org.projectlombok:lombok:1.18.26")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.shadowJar {
    minimize()
}

bukkit {
    name = "RedProtect"
    main = "net.thenextlvl.redprotect.RedProtect"
    apiVersion = "1.19"
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
    softDepend = listOf("PlotSquared", "Protect")
    permissions {
        register("redclock.notify")
    }
}
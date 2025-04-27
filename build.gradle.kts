import io.papermc.hangarpublishplugin.model.Platforms
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("java")
    id("com.gradleup.shadow") version "9.0.0-beta12"
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

group = "net.thenextlvl.redprotect"
version = "2.1.0"

repositories {
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Core")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-core")
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")

    compileOnly("net.thenextlvl.core:nbt:2.3.2")
    compileOnly("net.thenextlvl.protect:api:3.0.3")

    implementation("net.thenextlvl.core:files:2.0.3")
    implementation("net.thenextlvl.core:i18n:1.0.21")
    implementation(platform("com.intellectualsites.bom:bom-newest:1.52"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks.compileJava {
    options.release.set(21)
}

tasks.shadowJar {
    minimize()
}

paper {
    name = "RedProtect"
    main = "net.thenextlvl.redprotect.RedProtect"
    apiVersion = "1.21"
    website = "https://thenextlvl.net"
    authors = listOf("NonSwag")
    serverDependencies {
        register("PlotSquared") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Protect") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
    permissions {
        register("redclock.notify")
    }
}

val versionString: String = project.version as String
val isRelease: Boolean = !versionString.contains("-pre")

val versions: List<String> = (property("gameVersions") as String)
    .split(",")
    .map { it.trim() }

hangarPublish { // docs - https://docs.papermc.io/misc/hangar-publishing
    publications.register("plugin") {
        id.set("RedProtect")
        version.set(versionString)
        channel.set(if (isRelease) "Release" else "Snapshot")
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms.register(Platforms.PAPER) {
            jar.set(tasks.shadowJar.flatMap { it.archiveFile })
            platformVersions.set(versions)
        }
    }
}
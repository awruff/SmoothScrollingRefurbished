import net.ornithemc.ploceus.api.PloceusGradleExtensionApi

plugins {
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
    id("ploceus") version "1.17.4"
}

val modid = property("mod_id") as String
val modname = property("mod_name") as String
val modversion = property("mod_version") as String
val mcversion = property("minecraft_version") as String
val loaderversion = property("loader_version") as String
val oneconfigversion = property("oneconfig_version") as String

version = "$modversion+$mcversion"
group = property("maven_group") as String
base.archivesName = modid

configurations.configureEach {
    exclude(group = "org.lwjgl.lwjgl")
}

val ploceus = extensions.getByType<PloceusGradleExtensionApi>().apply {
    setIntermediaryGeneration(2)
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.ornithemc.net/releases")
    maven("https://repo.polyfrost.org/releases")
    maven("https://repo.polyfrost.org/snapshots")
    maven("https://maven.cloverclient.com/releases") {
        content { includeGroup("pl.tomgirl") }
    }
    maven("https://central.sonatype.com/repository/maven-snapshots") {
        content { includeGroup("net.kyori") }
    }
    maven("https://maven.fabricmc.net/") {
        content { includeGroup("net.fabricmc") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mcversion")
    mappings(ploceus.layeredMappings {
        mappings("net.ornithemc:feather-gen2:$mcversion+build.${property("feather_build")}:v2") {
            containsUnpick()
        }
    })

    modImplementation("net.fabricmc:fabric-loader:$loaderversion")
    modImplementation("org.polyfrost.oneconfig:$mcversion-ornithe:$oneconfigversion")
    for (module in arrayOf("config", "config-impl", "internal", "ui", "utils")) {
        implementation("org.polyfrost.oneconfig:$module:$oneconfigversion")
    }
}

loom {
    runConfigs.all {
        runDirectory = rootProject.file("run")
    }
    runConfigs.remove(runConfigs["server"])
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    processResources {
        val props = mapOf(
            "mod_id" to modid,
            "mod_name" to modname,
            "mod_version" to modversion,
            "minecraft_version" to mcversion,
            "loader_version" to loaderversion
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") { expand(props) }
    }

    jar {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_$modid" }
        }
    }
}

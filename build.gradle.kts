plugins {
    alias(libs.plugins.fabric.loom)
    id("maven-publish")
}

val minecraft = libs.versions.minecraft.get()

val targetJavaVersion = 21

version = "${project.version}+${minecraft}"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}

loom {
    splitEnvironmentSourceSets()
}

tasks.processResources {
    filteringCharset = "UTF-8"

    inputs.property("version", project.version)
    inputs.property("minecraft", minecraft)
    inputs.property("loader", libs.versions.fabric.loader.get())

    filesMatching("fabric.mod.json") {
        expand(
            "version" to version,
            "minecraft" to minecraft,
            "loader" to libs.versions.fabric.loader.get()
        )
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    withSourcesJar()
    withJavadocJar()
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.jar {
    from("LICENSE") {
        into("META-INF")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven { url = uri(rootProject.layout.buildDirectory.dir("repository")) }
    }
}
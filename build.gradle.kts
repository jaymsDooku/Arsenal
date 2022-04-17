plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.3.1"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "dev.jayms"
version = "1.0.0-SNAPSHOT"
description = "Arsenal"

repositories {
    fun civRepo(name: String) {
        maven {
            url = uri("https://maven.pkg.github.com/CivMC/${name}")
            credentials {
                // These need to be set in the user environment variables
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
                //username = "jaymsDooku"
                //password = "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"
            }
        }
    }

    mavenCentral()
    civRepo("CivModCore")
    civRepo("NameLayer")
    civRepo("Citadel")

    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://repo.aikar.co/content/groups/aikar/")

    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("1.18-R0.1-SNAPSHOT")

    implementation("net.civmc:civmodcore:2.0.0-SNAPSHOT:dev-all")
    implementation("net.civmc:namelayer-spigot:3.0.0-SNAPSHOT:dev")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything

        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything
    }

    test {
        useJUnitPlatform()
    }

    shadowJar {
        fun reloc(pkg: String) = relocate(pkg, "dev.jayms.arsenal.shadow")

        dependencies {
            include(dependency("co.aikar:acf-paper"))
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/jaymsDooku/Arsenal")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}
import net.civmc.civgradle.CivGradleExtension

plugins {
    id("net.civmc.civgradle") version "2.+" apply false
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "net.civmc.civgradle")

    configure<CivGradleExtension> {
        pluginName = project.property("pluginName") as String
    }

    repositories {
        fun civRepo(name: String) {
            maven {
                url = uri("https://maven.pkg.github.com/CivMC/${name}")
                credentials {
                    // These need to be set in the user environment variables
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }

        mavenCentral()
        // maven("https://repo.civmc.net/repository/maven-public")
        maven("https://repo.aikar.co/content/groups/aikar/")
        maven("https://libraries.minecraft.net")
        maven("https://repo.codemc.io/repository/maven-public/")
        civRepo("NameLayer")
        civRepo("Bastion")
        civRepo("CivModCore")
        civRepo("Citadel")

        maven("https://jitpack.io")
    }
}
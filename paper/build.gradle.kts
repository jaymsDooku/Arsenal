plugins {
    id("net.civmc.civgradle")
    id("io.papermc.paperweight.userdev")
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")

    compileOnly("net.civmc.civmodcore:paper:2.0.0-SNAPSHOT:dev-all")
    compileOnly("net.civmc.namelayer:paper:3.0.0-SNAPSHOT:dev")
    compileOnly("net.civmc.bastion:paper:3.0.0-SNAPSHOT:dev")
    compileOnly("net.civmc.citadel:citadel-paper:5.0.3:dev")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
}
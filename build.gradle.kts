plugins {
    kotlin("multiplatform") version "1.8.10"
}

repositories {
    mavenCentral()
}

kotlin {
    linuxX64("native") {
        binaries {
            executable()
        }
    }

    sourceSets {
        val nativeMain by getting {
            dependencies {
                implementation("com.squareup.okio:okio:3.3.0")
            }
        }
    }
}

tasks["clean"].doLast {
    buildDir.deleteRecursively()
}

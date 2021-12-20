plugins {
    kotlin("multiplatform") version "1.6.10"
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
                implementation("com.squareup.okio:okio:3.0.0")
            }
        }
    }
}

tasks["clean"].doLast {
    buildDir.deleteRecursively()
}

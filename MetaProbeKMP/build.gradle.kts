plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.vanniktech.maven.publish") version("0.25.3")
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    kotlin.applyDefaultHierarchyTemplate()

    androidTarget{
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }


    js(IR) {
        browser()
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "MetaProbeKMP"
        }
    }

    sourceSets {
        val desktopMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-apache5:2.3.3")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation("com.mohamedrejeb.ksoup:ksoup-html:0.2.1")
                //ktor
                implementation("io.ktor:ktor-client-core:2.3.6")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}


android {
    namespace = "com.devscion.metaprobekmp"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

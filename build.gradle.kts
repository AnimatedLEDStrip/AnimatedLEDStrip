/*
 * Copyright (c) 2018-2021 AnimatedLEDStrip
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


tasks.wrapper {
    gradleVersion = "6.7.1"
}

plugins {
    kotlin("multiplatform") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("io.kotest") version "0.2.6"
    id("maven-publish")
    id("java-library")
    jacoco
}

jacoco {
    toolVersion = "0.8.6"
}

repositories {
    jcenter()
    mavenCentral()
}

group = "io.github.animatedledstrip"
version = "0.9-SNAPSHOT"
description = "io.github.animatedledstrip:animatedledstrip-core"
//java.sourceCompatibility = JavaVersion.VERSION_1_8


//publishing {
//    publications.create<MavenPublication>("maven") {
//        from(components["java"])
//    }
//}

//tasks.withType<JavaCompile>() {
//    options.encoding = "UTF-8"
//}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }
//    js(LEGACY) {
//        browser {
//            testTask {
//                useKarma {
//                    useChromeHeadless()
//                    webpackConfig.cssSupport.enabled = true
//                }
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
                api("co.touchlab:kermit:0.1.8")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.kotest:kotest-assertions-core:4.3.2")
                implementation("io.kotest:kotest-property:4.3.2")
            }
        }
        val jvmMain by getting {
            dependencies {
                api("org.tinylog:tinylog:1.3.6")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.mockk:mockk:1.10.0")
                implementation("io.kotest:kotest-runner-junit5:4.3.2")
                implementation("io.kotest:kotest-framework-engine-jvm:4.3.2")
            }
        }
//        val jsMain by getting
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
//        val nativeMain by getting
//        val nativeTest by getting
    }

}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
    filter {
        isFailOnNoMatchingTests = false
    }
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
}

tasks.jacocoTestReport {
    val coverageSourceDirs = arrayOf(
        "src/commonMain/kotlin",
        "src/jvmMain/kotlin"
    )

    val classFiles = File("${buildDir}/classes/kotlin/jvm/main/")
        .walkBottomUp()
        .toSet()


    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    reports {
        xml.isEnabled = true
        csv.isEnabled = true
        html.isEnabled = true
    }
}

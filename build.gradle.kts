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
    id("org.jetbrains.dokka") version "1.4.20"
    id("io.kotest") version "0.2.6"
    jacoco
    id("java-library")
    signing
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    id("io.codearte.nexus-staging") version "0.22.0"
}

jacoco {
    toolVersion = "0.8.6"
}

repositories {
    jcenter()
    mavenCentral()
}

group = "io.github.animatedledstrip"
version = "1.0.0-pre3-SNAPSHOT"
description = "A library designed to simplify running animations on WS281x strips"

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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
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
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.mockk:mockk:1.10.4")
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
        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
                       org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
                       org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
}

tasks.jacocoTestReport {
    val coverageSourceDirs = arrayOf(
        "${projectDir}/src/commonMain/kotlin",
        "${projectDir}/src/jvmMain/kotlin"
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

val javadocJar by tasks.creating(Jar::class) {
    dependsOn.add(tasks.dokkaJavadoc)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

publishing {
    publications.withType<MavenPublication>().forEach {
        it.apply {
            artifact(javadocJar)
            pom {
                name.set("AnimatedLEDStrip")
                description.set("A library designed to simplify running animations on WS281x strips")
                url.set("https://github.com/AnimatedLEDStrip/AnimatedLEDStrip")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("http://www.opensource.org/licenses/mit-license.php")
                    }
                }

                developers {
                    developer {
                        name.set("Max Narvaez")
                        email.set("mnmax.narvaez3@gmail.com")
                        organization.set("AnimatedLEDStrip")
                        organizationUrl.set("https://animatedledstrip.github.io")
                    }
                }

                scm {
                    connection.set("scm:git:https://github.com/AnimatedLEDStrip/AnimatedLEDStrip.git")
                    developerConnection.set("scm:git:https://github.com/AnimatedLEDStrip/AnimatedLEDStrip.git")
                    url.set("https://github.com/AnimatedLEDStrip/AnimatedLEDStrip")
                }
            }
        }

    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

nexusPublishing {
    repositories {
        sonatype {
            val nexusUsername: String? by project
            val nexusPassword: String? by project
            username.set(nexusUsername)
            password.set(nexusPassword)
        }
    }
}

tasks.dokkaHtml.configure {
    outputDirectory.set(projectDir.resolve("dokka"))
}

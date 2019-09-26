[![KDoc](https://img.shields.io/badge/KDoc-read-green.svg)](https://animatedledstrip.github.io/AnimatedLEDStrip/animatedledstrip-core/)
[![Build Status](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip.svg?branch=master)](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip)
[![codecov](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip/branch/master/graph/badge.svg)](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip)

# AnimatedLEDStrip
The AnimatedLEDStrip set of libraries are meant to make the process of running animations on a LED strip much easier.
This library also supports running concurrent animations on a LED strip. Multiple animations can be run simultaneously,
even over the same part of the strip. See the [wiki](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/wiki)
for more information.

## Structure
### Main Libraries
The main set of libraries includes:
- [Core](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip) -
Contains all the animations and generic structure for communicating with LED strips
- [Server](https://github.com/AnimatedLEDStrip/AnimatedLEDStripServer) -
A library for running a server that runs animations on a LED strip and communicates with clients
- [Client](https://github.com/AnimatedLEDStrip/AnimatedLEDStripClient) - A library for communicating with a server

### Device Libraries
Each device that can run LEDs has its own device library. Currently the only device supported is the
Raspberry Pi, but this can be [expanded to more devices](https://github.com/AnimatedLEDStrip/AnimatedLEDStripServer/wiki)
in the future:
- [Raspberry Pi](https://github.com/AnimatedLEDStrip/AnimatedLEDStripPi) - A device library for running
LEDs on a Raspberry Pi 3B, 3B+ or 4B

### Examples
#### [Raspberry Pi Server](https://github.com/AnimatedLEDStrip/AnimatedLEDStripPiServerExample)
This repository contains an example implementation of a server for a Raspberry Pi. This can be used as-is
with no modification, or can be used as a template for creating servers on Raspberry Pis or other devices.
The repository includes support for installation using [`ansible-pull`](https://github.com/AnimatedLEDStrip/AnimatedLEDStripPiServerExample#install),
further simplifying the install process.

#### [Raspberry Pi Touchscreen Client](https://github.com/AnimatedLEDStrip/AnimatedLEDStripGUI)
This repository contains an example of a GUI that uses the AnimatedLEDStripClient library to communicate with an 
AnimatedLEDStripServer. Built with [TornadoFX](https://tornadofx.io/), it is designed to run on a Raspberry Pi
with the official 7" touchscreen, though it can be run on any device with Java 8 and JavaFX installed.

#### [Android Client](https://github.com/AnimatedLEDStrip/AnimatedLEDStripAndroidControl)
Currently a work in progress, this repository is an Android app that uses the AnimatedLEDStripClient library
to communicate with an AnimatedLEDStripServer.

### Other
#### [Arduino Library](https://github.com/AnimatedLEDStrip/AnimatedLEDStripCppArduino)
Also included in the set of libraries is one written in C++ for Arduinos. This is the library that started
AnimatedLEDStrip. See our [history](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/wiki#history) for more details.

## Maven Coordinates/Dependency
Use the following dependency to use this library in your project
> ```
> <dependency>
>   <groupId>io.github.animatedledstrip</groupId>
>   <artifactId>animatedledstrip-core</artifactId>
>   <version>0.4</version>
> </dependency>
> ```


## Snapshots
Development versions of the AnimatedLEDStrip library are available from the Sonatype snapshot repository:

> ```
> <repositories>
>    <repository>
>        <id>sonatype-snapshots</id>
>        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
>        <snapshots>
>            <enabled>true</enabled>
>        </snapshots>
>    </repository>
> </repositories>
> 
> <dependencies>
>   <dependency>
>     <groupId>io.github.animatedledstrip</groupId>
>     <artifactId>animatedledstrip-core</artifactId>
>     <version>0.5-SNAPSHOT</version>
>   </dependency>
> </dependencies>

## Note About Building
Because we use the dokka plugin to generate our documentation, we must build using Java <=9
> https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase9-3934878.html

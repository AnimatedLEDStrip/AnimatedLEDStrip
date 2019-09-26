[![KDoc](https://img.shields.io/badge/KDoc-read-green.svg)](https://animatedledstrip.github.io/AnimatedLEDStrip/animatedledstrip-core/)
[![Build Status](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip.svg?branch=master)](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip)
[![codecov](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip/branch/master/graph/badge.svg)](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip)

# AnimatedLEDStrip
The AnimatedLEDStrip set of libraries are meant to make the process of running animations on a LED strip much easier.
This library also supports running concurrent animations on a LED strip. Multiple animations can be run simultaneously,
even over the same part of the strip. See the [wiki](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/wiki)
for more information.

## Uses Java 9
Because we use the dokka plugin to generate our documentation, we must use Java <=9
> https://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase9-3934878.html

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

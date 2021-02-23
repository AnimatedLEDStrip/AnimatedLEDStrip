[![Build Status](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/actions/workflows/test.yml/badge.svg)](https://github.com/AnimatedLEDStrip/AnimatedLEDStrip/actions/workflows/test.yml)
[![KDoc](https://img.shields.io/badge/KDoc-read-green.svg)](https://animatedledstrip.github.io/AnimatedLEDStrip/animatedledstrip-core/)
[![codecov](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip/branch/master/graph/badge.svg)](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.animatedledstrip/animatedledstrip-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.animatedledstrip/animatedledstrip-core)

# AnimatedLEDStrip
This is the core of the AnimatedLEDStrip libraries.

Functionality:
- Abstract the communication with a generic LED strip (`LEDStripRenderer`, `NativeLEDStrip`)
- Define animations (`DefinedAnimation`)
- Define animation groups (`OrderedAnimationGroup`, `RandomizedAnimationGroup`)
- Handle the running of animations (`LEDStripAnimationManager`)
- Specify how to handle colors (`ColorContainer`)
- Provide an option for emulating an LED strip (`EmulatedWS281x`)
- Specify what can be sent between servers and clients and how it should be formatted (`SendableData`) 

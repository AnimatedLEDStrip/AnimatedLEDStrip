[![Build Status](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip.svg?branch=master)](https://travis-ci.com/AnimatedLEDStrip/AnimatedLEDStrip)
[![KDoc](https://img.shields.io/badge/KDoc-read-green.svg)](https://animatedledstrip.github.io/AnimatedLEDStrip/animatedledstrip-core/)
[![codecov](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip/branch/master/graph/badge.svg)](https://codecov.io/gh/AnimatedLEDStrip/AnimatedLEDStrip)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.animatedledstrip/animatedledstrip-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.animatedledstrip/animatedledstrip-core)

# AnimatedLEDStrip
This is the core of the AnimatedLEDStrip libraries.

Functionality:
- Abstract the communication with a generic LED strip (`LEDStrip`, `NativeLEDStrip`)
- Define animations (`PredefinedAnimation`)
- Handle the running of animations (`AnimatedLEDStrip`)
- Specify how to handle animatedledstrip.colors (`ColorContainer`)
- Provide an option for emulating an LED strip (`EmulatedAnimatedLEDStrip`)
- Specify what can be sent between servers and clients and how it should be formatted (`SendableData`) 

@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Meteor
// abbr MET
// colors 1
// repetitive true
// center notused
// delay used 10
// direction used
// distance notused
// spacing notused
// ## end info ##

import animatedledstrip.animationutils.*
import animatedledstrip.colors.*
import animatedledstrip.colors.ccpresets.*
import animatedledstrip.leds.*
import animatedledstrip.utils.*

@Suppress("UNRESOLVED_REFERENCE")
val leds = bindings["leds"] as AnimatedLEDStrip.Section

@Suppress("UNRESOLVED_REFERENCE")
val data = bindings["data"] as AnimationData

val color0 = data.pCols[0]
val delay = data.delay
val direction = data.direction

leds.apply {
    when (direction) {
        Direction.FORWARD -> iterateOverPixels {
            setAndFadePixel(
                pixel = it,
                color = color0,
                amountOfOverlay = 60,
                delay = 25,
                context = parallelAnimationThreadPool
            )
            delayBlocking(delay)
        }
        Direction.BACKWARD -> iterateOverPixelsReverse {
            setAndFadePixel(
                pixel = it,
                color = color0,
                amountOfOverlay = 60,
                delay = 25,
                context = parallelAnimationThreadPool
            )
            delayBlocking(delay)
        }
    }
}

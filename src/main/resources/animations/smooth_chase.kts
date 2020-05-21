@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Smooth Chase
// abbr SCH
// colors 1
// repetitive true
// center notused
// delay used 50
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
        Direction.FORWARD ->
            iterateOverPixelsReverse { m ->
                setProlongedStripColorWithOffset(color0, m)
                delayBlocking(delay)
            }
        Direction.BACKWARD ->
            iterateOverPixels { m ->
                setProlongedStripColorWithOffset(color0, m)
                delayBlocking(delay)
            }
    }
}

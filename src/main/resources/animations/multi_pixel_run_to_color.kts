@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Multi Pixel Run To Color
// abbr MTC
// colors 1
// repetitive true
// center notused
// delay used 150
// direction used
// distance notused
// spacing used 3
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
val spacing = data.spacing

leds.apply {
    when (direction) {
        Direction.FORWARD ->
            iterateOver(spacing - 1 downTo 0) { s ->
                iterateOver( 0 until numLEDs step spacing) { i ->
                    if (i + s < numLEDs)
                        setProlongedPixelColor(i + s, color0)
                }
                delayBlocking(delay)
            }
        Direction.BACKWARD ->
            iterateOver(0 until spacing) { s ->
                iterateOver(0 until numLEDs step spacing) { i ->
                    if (i + s < numLEDs)
                        setProlongedPixelColor(i + s, color0)
                }
                delayBlocking(delay)
            }
    }
}

@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Multi Pixel Run
// abbr MPR
// colors 1
// repetitive true
// center notused
// delay used 100
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
            iterateOver(0 until spacing) { s ->
                val s2 = s - 1 % spacing
                iterateOver(0 until numLEDs step spacing) { i ->
                    if (i + s2 < numLEDs) revertPixel(i + s2)
                    if (i + s < numLEDs)
                        setTemporaryPixelColor(i + s, color0)
                }
                delayBlocking(delay)
            }
        Direction.BACKWARD ->
            iterateOver(spacing - 1 downTo 0) { s ->
                val s2 = if (s + 1 == spacing) 0 else s + 1
                iterateOver(0 until numLEDs step spacing) { i ->
                    if (i + s2 < numLEDs) revertPixel(i + s2)
                    if (i + s < numLEDs)
                        setTemporaryPixelColor(i + s, color0)
                }
                delayBlocking(delay)
            }
    }
}

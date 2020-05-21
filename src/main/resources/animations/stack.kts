@file:Suppress("UnusedImport", "PackageDirectoryMismatch")

// ## animation info ##
// name Stack
// abbr STK
// colors 1
// repetitive false
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
val direction = data.direction

leds.apply {
    val baseAnimation = data.copy(animation = "Pixel Run")

    when (direction) {
        Direction.FORWARD ->
            iterateOverPixelsReverse {
                runSequential(
                    animation = baseAnimation.copy(),
                    section = getSection(0, it, this.numLEDs)
                )
                setProlongedPixelColor(it, color0)
            }
        Direction.BACKWARD ->
            iterateOverPixels {
                runSequential(
                    animation = baseAnimation.copy(),
                    section = getSection(it, numLEDs - 1, this.numLEDs)
                )
                setProlongedPixelColor(it, color0)
            }
    }
}
